package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Secret Hider", appName)
  }

  @Test
  fun `test stego engine text`() {
    val cover = "Hey! Hope you are having a wonderful day and staying productive."
    val secret = "Yoo bro"
    val encoded = com.example.stego.StegoEngine.encodeInText(cover, secret)
    println("ENCODED LENGTH: ${encoded.length} cover length: ${cover.length}")
    val decoded = com.example.stego.StegoEngine.decodeFromText(encoded)
    assertEquals(secret, decoded)

    val secretWithPass = com.example.stego.StegoEngine.encodeInText(cover, secret, "mypass")
    val decodedWithPass = com.example.stego.StegoEngine.decodeFromText(secretWithPass, "mypass")
    assertEquals(secret, decodedWithPass)

    val unicodeSecret = "Secret 🔐 नमस्ते"
    val encUni = com.example.stego.StegoEngine.encodeInText(cover, unicodeSecret)
    val decUni = com.example.stego.StegoEngine.decodeFromText(encUni)
    println("UNICODE DECODED: '$decUni' expected: '$unicodeSecret'")
    assertEquals(unicodeSecret, decUni)
  }

  @Test
  fun `test stego engine emoji`() {
    val carrier = "🔥"
    val secret = "Secret 🔐 नमस्ते"
    val encoded = com.example.stego.StegoEngine.encodeInEmoji(carrier, secret, "pass123")
    val decoded = com.example.stego.StegoEngine.decodeFromEmoji(encoded, "pass123")
    assertEquals(secret, decoded)
  }

  @Test
  fun `test image stego engine bitmap encode and decode`() {
    val sampleBmp = com.example.stego.ImageStegoEngine.createSampleBitmap("Cyber Sunset", 200, 200)
    val secret = "Super Top Secret Coordinates 📍: 40.7128° N, 74.0060° W"

    // 1. Plain steganography
    val encodedBmp = com.example.stego.ImageStegoEngine.encodeMessageInBitmap(sampleBmp, secret)
    val decodedResult = com.example.stego.ImageStegoEngine.decodeMessageFromBitmap(encodedBmp)
    org.junit.Assert.assertTrue(decodedResult.isSuccess)
    assertEquals(secret, decodedResult.getOrNull())

    // 2. Encrypted steganography
    val encBmpWithPass = com.example.stego.ImageStegoEngine.encodeMessageInBitmap(sampleBmp, secret, "secretKey99")
    val decResultWithPass = com.example.stego.ImageStegoEngine.decodeMessageFromBitmap(encBmpWithPass, "secretKey99")
    org.junit.Assert.assertTrue(decResultWithPass.isSuccess)
    assertEquals(secret, decResultWithPass.getOrNull())

    // 3. Encrypted decoded with wrong key should fail
    val wrongPassResult = com.example.stego.ImageStegoEngine.decodeMessageFromBitmap(encBmpWithPass, "")
    org.junit.Assert.assertTrue(wrongPassResult.isFailure)
  }
}
