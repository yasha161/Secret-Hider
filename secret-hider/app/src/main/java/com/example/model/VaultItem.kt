package com.example.model

data class VaultItem(
  val id: String = java.util.UUID.randomUUID().toString(),
  val title: String,
  val type: StegoType,
  val carrierSnippet: String,
  val hiddenPayload: String,
  val timestamp: Long = System.currentTimeMillis(),
  val isLockedWithPasscode: Boolean = false,
  val passcodeHint: String = ""
)

enum class StegoType(val label: String, val badge: String) {
  TEXT("Hide in Text", "Stego"),
  EMOJI("Hide in Emoji", "Unicode Tags"),
  IMAGE("Hide in Image", "LSB Crypto")
}
