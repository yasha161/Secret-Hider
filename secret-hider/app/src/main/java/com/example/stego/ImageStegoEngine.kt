package com.example.stego

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.ByteBuffer

object ImageStegoEngine {

  private val HEADER_MAGIC = "STEG".toByteArray(Charsets.US_ASCII)
  private const val FLAG_PLAIN: Byte = 0x00
  private const val FLAG_ENCRYPTED: Byte = 0x01

  /**
   * Calculates maximum secret payload capacity in bytes for a given image dimension.
   * 3 bits per pixel (LSB of R, G, B).
   */
  fun calculateCapacityBytes(width: Int, height: Int): Int {
    val totalBits = width.toLong() * height.toLong() * 3
    val totalBytes = (totalBits / 8).toInt()
    val headerSize = HEADER_MAGIC.size + 1 + 4 // 9 bytes overhead
    return (totalBytes - headerSize).coerceAtLeast(0)
  }

  /**
   * Embeds a secret text into a Bitmap using Least Significant Bit (LSB) steganography.
   * Modifies only the 1st bit of R, G, and B color channels.
   */
  fun encodeMessageInBitmap(
    sourceBitmap: Bitmap,
    secretMessage: String,
    password: String = ""
  ): Bitmap {
    val rawBytes = secretMessage.toByteArray(Charsets.UTF_8)
    val hasPassword = password.isNotBlank()

    val payloadBytes = if (hasPassword) {
      val keyBytes = password.toByteArray(Charsets.UTF_8)
      ByteArray(rawBytes.size) { i ->
        (rawBytes[i].toInt() xor keyBytes[i % keyBytes.size].toInt()).toByte()
      }
    } else {
      rawBytes
    }

    val capacity = calculateCapacityBytes(sourceBitmap.width, sourceBitmap.height)
    if (payloadBytes.size > capacity) {
      throw IllegalArgumentException("Secret message exceeds carrier capacity ($capacity bytes)")
    }

    // Packet structure:
    // [4 bytes: "STEG"] + [1 byte: flag] + [4 bytes: payload length] + [N bytes: payload]
    val totalPacketSize = HEADER_MAGIC.size + 1 + 4 + payloadBytes.size
    val packet = ByteBuffer.allocate(totalPacketSize).apply {
      put(HEADER_MAGIC)
      put(if (hasPassword) FLAG_ENCRYPTED else FLAG_PLAIN)
      putInt(payloadBytes.size)
      put(payloadBytes)
    }.array()

    // Create ARGB_8888 mutable bitmap
    val mutableBitmap = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true)
    val width = mutableBitmap.width
    val height = mutableBitmap.height

    var packetByteIdx = 0
    var packetBitIdx = 0 // 0 to 7 (MSB to LSB)

    val totalBitsToWrite = packet.size * 8
    var bitsWritten = 0

    outer@ for (y in 0 until height) {
      for (x in 0 until width) {
        if (bitsWritten >= totalBitsToWrite) break@outer

        val pixel = mutableBitmap.getPixel(x, y)
        val a = (pixel ushr 24) and 0xFF
        var r = (pixel ushr 16) and 0xFF
        var g = (pixel ushr 8) and 0xFF
        var b = pixel and 0xFF

        // Red LSB
        if (bitsWritten < totalBitsToWrite) {
          val bit = (packet[packetByteIdx].toInt() ushr (7 - packetBitIdx)) and 1
          r = (r and 0xFE) or bit
          bitsWritten++
          packetBitIdx++
          if (packetBitIdx == 8) {
            packetBitIdx = 0
            packetByteIdx++
          }
        }

        // Green LSB
        if (bitsWritten < totalBitsToWrite) {
          val bit = (packet[packetByteIdx].toInt() ushr (7 - packetBitIdx)) and 1
          g = (g and 0xFE) or bit
          bitsWritten++
          packetBitIdx++
          if (packetBitIdx == 8) {
            packetBitIdx = 0
            packetByteIdx++
          }
        }

        // Blue LSB
        if (bitsWritten < totalBitsToWrite) {
          val bit = (packet[packetByteIdx].toInt() ushr (7 - packetBitIdx)) and 1
          b = (b and 0xFE) or bit
          bitsWritten++
          packetBitIdx++
          if (packetBitIdx == 8) {
            packetBitIdx = 0
            packetByteIdx++
          }
        }

        mutableBitmap.setPixel(x, y, Color.argb(a, r, g, b))
      }
    }

    return mutableBitmap
  }

  /**
   * Extracts a secret text embedded using encodeMessageInBitmap.
   */
  fun decodeMessageFromBitmap(
    bitmap: Bitmap,
    password: String = ""
  ): Result<String> {
    return try {
      val width = bitmap.width
      val height = bitmap.height
      val totalPixels = width * height

      // Read enough bits to parse header: 4 (magic) + 1 (flag) + 4 (length) = 9 bytes = 72 bits
      val headerBitsNeeded = 9 * 8
      if (totalPixels * 3 < headerBitsNeeded) {
        return Result.failure(Exception("Image is too small to contain stego header."))
      }

      var currentByte = 0
      var bitCount = 0
      val extractedBytes = mutableListOf<Byte>()

      var headerParsed = false
      var isEncrypted = false
      var payloadLength = 0
      var targetTotalBytes = 9

      outer@ for (y in 0 until height) {
        for (x in 0 until width) {
          val pixel = bitmap.getPixel(x, y)
          val channels = intArrayOf(
            (pixel ushr 16) and 1, // Red LSB
            (pixel ushr 8) and 1,  // Green LSB
            pixel and 1            // Blue LSB
          )

          for (bit in channels) {
            currentByte = (currentByte shl 1) or bit
            bitCount++

            if (bitCount == 8) {
              extractedBytes.add(currentByte.toByte())
              currentByte = 0
              bitCount = 0

              // Check magic header once 4 bytes collected
              if (extractedBytes.size == 4) {
                val magic = extractedBytes.take(4).toByteArray()
                if (!magic.contentEquals(HEADER_MAGIC)) {
                  return Result.failure(Exception("No hidden stego payload detected in this image."))
                }
              }

              // Parse header once 9 bytes collected
              if (extractedBytes.size == 9 && !headerParsed) {
                headerParsed = true
                isEncrypted = extractedBytes[4] == FLAG_ENCRYPTED
                val lengthBuffer = ByteBuffer.wrap(extractedBytes.subList(5, 9).toByteArray())
                payloadLength = lengthBuffer.int

                val maxPossiblePayload = (totalPixels * 3 / 8) - 9
                if (payloadLength <= 0 || payloadLength > maxPossiblePayload) {
                  return Result.failure(Exception("Invalid stego payload metadata found."))
                }
                targetTotalBytes = 9 + payloadLength
              }

              if (headerParsed && extractedBytes.size >= targetTotalBytes) {
                break@outer
              }
            }
          }
        }
      }

      if (!headerParsed || extractedBytes.size < targetTotalBytes) {
        return Result.failure(Exception("Incomplete stego payload in image data."))
      }

      val payloadBytes = extractedBytes.subList(9, targetTotalBytes).toByteArray()
      val decryptedBytes = if (isEncrypted) {
        if (password.isBlank()) {
          return Result.failure(Exception("This secret is protected by a passphrase. Please enter the passphrase."))
        }
        val keyBytes = password.toByteArray(Charsets.UTF_8)
        ByteArray(payloadBytes.size) { i ->
          (payloadBytes[i].toInt() xor keyBytes[i % keyBytes.size].toInt()).toByte()
        }
      } else {
        payloadBytes
      }

      val message = String(decryptedBytes, Charsets.UTF_8)
      Result.success(message)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  /**
   * Safely loads and scales a Bitmap from a content Uri to avoid OutOfMemory.
   */
  fun loadBitmapFromUri(context: Context, uri: Uri, maxDim: Int = 1600): Bitmap? {
    return try {
      val boundsStream = context.contentResolver.openInputStream(uri) ?: return null
      val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
      BitmapFactory.decodeStream(boundsStream, null, options)
      boundsStream.close()

      val origW = options.outWidth
      val origH = options.outHeight
      if (origW <= 0 || origH <= 0) return null

      var sampleSize = 1
      while ((origW / sampleSize) > maxDim || (origH / sampleSize) > maxDim) {
        sampleSize *= 2
      }

      val decodeOptions = BitmapFactory.Options().apply {
        inSampleSize = sampleSize
        inPreferredConfig = Bitmap.Config.ARGB_8888
      }

      val decodeStream = context.contentResolver.openInputStream(uri)
      val bitmap = BitmapFactory.decodeStream(decodeStream, null, decodeOptions)
      decodeStream?.close()
      bitmap
    } catch (_: Exception) {
      null
    }
  }

  /**
   * Saves bitmap as lossless PNG file in cache directory.
   */
  fun saveBitmapToPngFile(context: Context, bitmap: Bitmap, prefix: String = "stego_img"): File {
    val dir = File(context.cacheDir, "stego_images")
    if (!dir.exists()) dir.mkdirs()
    val file = File(dir, "${prefix}_${System.currentTimeMillis()}.png")
    FileOutputStream(file).use { out ->
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
      out.flush()
    }
    return file
  }

  /**
   * Gets a shareable content Uri for FileProvider.
   */
  fun getShareUri(context: Context, file: File): Uri {
    return FileProvider.getUriForFile(
      context,
      "${context.packageName}.fileprovider",
      file
    )
  }

  /**
   * Saves image to device MediaStore (Pictures/StegoVault) without requiring dangerous storage permissions.
   */
  fun saveImageToGallery(context: Context, file: File): Boolean {
    return try {
      val contentValues = android.content.ContentValues().apply {
        put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, file.name)
        put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/png")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
          put(
            android.provider.MediaStore.Images.Media.RELATIVE_PATH,
            android.os.Environment.DIRECTORY_PICTURES + "/StegoVault"
          )
          put(android.provider.MediaStore.Images.Media.IS_PENDING, 1)
        }
      }
      val resolver = context.contentResolver
      val uri = resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
      if (uri != null) {
        resolver.openOutputStream(uri)?.use { out ->
          file.inputStream().use { input -> input.copyTo(out) }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
          contentValues.clear()
          contentValues.put(android.provider.MediaStore.Images.Media.IS_PENDING, 0)
          resolver.update(uri, contentValues, null, null)
        }
        true
      } else {
        false
      }
    } catch (_: Exception) {
      false
    }
  }

  /**
   * Retrieves readable file name from Uri.
   */
  fun getFileNameFromUri(context: Context, uri: Uri): String {
    var name = "custom_image.png"
    try {
      context.contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
          val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
          if (nameIndex != -1) {
            name = cursor.getString(nameIndex)
          }
        }
      }
    } catch (_: Exception) {}
    return name
  }

  /**
   * Generates a procedural artistic gradient carrier bitmap for sample templates.
   */
  fun createSampleBitmap(name: String, width: Int = 800, height: Int = 800): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val (color1, color2) = when {
      name.contains("Sunset") -> Color.parseColor("#FF4E50") to Color.parseColor("#F9D423")
      name.contains("Cherry") -> Color.parseColor("#FF9A8B") to Color.parseColor("#FF6A88")
      name.contains("Starry") || name.contains("Cosmos") -> Color.parseColor("#2E0854") to Color.parseColor("#0B1B4F")
      name.contains("Tokyo") -> Color.parseColor("#8A2387") to Color.parseColor("#E94057")
      name.contains("Matrix") || name.contains("Garden") -> Color.parseColor("#0F2027") to Color.parseColor("#2C5364")
      else -> Color.parseColor("#3A1C71") to Color.parseColor("#FFAF7B")
    }

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), color1, color2, Shader.TileMode.CLAMP)
    }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

    // Draw some subtle procedural decorative shapes
    val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = Color.WHITE
      alpha = 25
    }
    canvas.drawCircle(width * 0.75f, height * 0.25f, width * 0.35f, circlePaint)
    canvas.drawCircle(width * 0.25f, height * 0.8f, width * 0.45f, circlePaint)

    return bitmap
  }
}
