package com.example.stego

import android.util.Base64

object StegoEngine {

  // =========================================================================
  // 1. Unicode Tag Steganography (Hide in Emoji)
  // Maps ASCII/UTF-8 code points to Unicode Tag block (U+E0000 to U+E007F)
  // =========================================================================
  private const val TAG_OFFSET = 0xE0000
  private const val TAG_CANCEL = "\uDB40\uDC7F" // U+E007F in surrogate pair
  private const val B64_PREFIX = "B64:"

  fun encodeInEmoji(carrierEmoji: String, secretMessage: String, password: String = ""): String {
    val rawBytes = secretMessage.toByteArray(Charsets.UTF_8)
    val payloadBytes = if (password.isNotEmpty()) {
      val keyBytes = password.toByteArray(Charsets.UTF_8)
      ByteArray(rawBytes.size) { i ->
        (rawBytes[i].toInt() xor keyBytes[i % keyBytes.size].toInt()).toByte()
      }
    } else {
      rawBytes
    }

    // Base64 encode so any Unicode/binary data maps cleanly to ASCII tag range (0x20..0x7E)
    val b64 = B64_PREFIX + Base64.encodeToString(payloadBytes, Base64.NO_WRAP)

    val sb = StringBuilder(carrierEmoji)
    for (char in b64) {
      val codePoint = char.code
      if (codePoint in 1..127) {
        val tagCodePoint = TAG_OFFSET + codePoint
        sb.append(Character.toChars(tagCodePoint))
      }
    }
    sb.append(TAG_CANCEL)
    return sb.toString()
  }

  fun decodeFromEmoji(carrierWithSecret: String, password: String = ""): String {
    val decodedChars = StringBuilder()
    var i = 0
    while (i < carrierWithSecret.length) {
      val codePoint = Character.codePointAt(carrierWithSecret, i)
      val charCount = Character.charCount(codePoint)
      if (codePoint in (TAG_OFFSET + 1)..(TAG_OFFSET + 127) && codePoint != (TAG_OFFSET + 0x7F)) {
        val originalChar = (codePoint - TAG_OFFSET).toChar()
        decodedChars.append(originalChar)
      }
      i += charCount
    }

    val extracted = decodedChars.toString()
    if (extracted.isEmpty()) return ""

    return if (extracted.startsWith(B64_PREFIX)) {
      val b64Data = extracted.substring(B64_PREFIX.length)
      try {
        val payloadBytes = Base64.decode(b64Data, Base64.NO_WRAP)
        val finalBytes = if (password.isNotEmpty()) {
          val keyBytes = password.toByteArray(Charsets.UTF_8)
          if (keyBytes.isNotEmpty()) {
            ByteArray(payloadBytes.size) { idx ->
              (payloadBytes[idx].toInt() xor keyBytes[idx % keyBytes.size].toInt()).toByte()
            }
          } else {
            payloadBytes
          }
        } else {
          payloadBytes
        }
        String(finalBytes, Charsets.UTF_8)
      } catch (_: Exception) {
        ""
      }
    } else {
      // Fallback for legacy plain ASCII tag payloads
      if (password.isNotEmpty()) {
        xorEncrypt(extracted, password)
      } else {
        extracted
      }
    }
  }

  // =========================================================================
  // 2. Zero-Width Characters (Hide in Text)
  // =========================================================================
  const val ZW_0 = '\u200B' // Zero-width space -> bit 0
  const val ZW_1 = '\u200C' // Zero-width non-joiner -> bit 1
  const val ZW_SEP = '\u200D' // Zero-width joiner -> byte separator

  fun hasHiddenText(text: String): Boolean {
    return text.any { it == ZW_0 || it == ZW_1 }
  }

  fun countHiddenBits(text: String): Int {
    return text.count { it == ZW_0 || it == ZW_1 }
  }

  fun countHiddenBytes(text: String): Int {
    return text.count { it == ZW_SEP }
  }

  fun encodeInText(coverText: String, secretMessage: String, password: String = ""): String {
    if (secretMessage.isEmpty()) return coverText

    val rawBytes = secretMessage.toByteArray(Charsets.UTF_8)
    val payloadBytes = if (password.isNotEmpty()) {
      val keyBytes = password.toByteArray(Charsets.UTF_8)
      ByteArray(rawBytes.size) { i ->
        (rawBytes[i].toInt() xor keyBytes[i % keyBytes.size].toInt()).toByte()
      }
    } else {
      rawBytes
    }

    val binary = StringBuilder()
    for (b in payloadBytes) {
      val bin = Integer.toBinaryString(b.toInt() and 0xFF).padStart(8, '0')
      for (bit in bin) {
        binary.append(if (bit == '0') ZW_0 else ZW_1)
      }
      binary.append(ZW_SEP)
    }

    val spaceIdx = coverText.indexOf(' ')
    return if (spaceIdx != -1) {
      coverText.substring(0, spaceIdx) + binary.toString() + coverText.substring(spaceIdx)
    } else {
      coverText + binary.toString()
    }
  }

  fun decodeFromText(encodedText: String, password: String = ""): String {
    val zwChars = StringBuilder()
    for (c in encodedText) {
      if (c == ZW_0 || c == ZW_1 || c == ZW_SEP) {
        zwChars.append(c)
      }
    }
    if (zwChars.isEmpty()) return ""

    val tokens = zwChars.toString().split(ZW_SEP)
    val byteList = mutableListOf<Byte>()
    for (token in tokens) {
      if (token.length == 8) {
        val binStr = token.map { if (it == ZW_0) '0' else '1' }.joinToString("")
        try {
          val byteVal = binStr.toInt(2).toByte()
          byteList.add(byteVal)
        } catch (_: Exception) {}
      }
    }
    if (byteList.isEmpty()) return ""

    val extractedBytes = byteList.toByteArray()
    val finalBytes = if (password.isNotEmpty()) {
      val keyBytes = password.toByteArray(Charsets.UTF_8)
      if (keyBytes.isNotEmpty()) {
        ByteArray(extractedBytes.size) { i ->
          (extractedBytes[i].toInt() xor keyBytes[i % keyBytes.size].toInt()).toByte()
        }
      } else {
        extractedBytes
      }
    } else {
      extractedBytes
    }

    return try {
      val decodedStr = String(finalBytes, Charsets.UTF_8)
      decodedStr
    } catch (_: Exception) {
      ""
    }
  }

  fun visualizeZeroWidth(text: String): String {
    val sb = StringBuilder()
    for (c in text) {
      when (c) {
        ZW_0 -> sb.append("[0]")
        ZW_1 -> sb.append("[1]")
        ZW_SEP -> sb.append("[|]")
        else -> sb.append(c)
      }
    }
    return sb.toString()
  }

  // Legacy fallback
  private fun xorEncrypt(text: String, key: String): String {
    if (key.isEmpty()) return text
    val out = StringBuilder()
    for (i in text.indices) {
      val k = key[i % key.length]
      out.append((text[i].code xor k.code).toChar())
    }
    return out.toString()
  }
}
