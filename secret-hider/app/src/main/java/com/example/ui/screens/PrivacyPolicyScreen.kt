package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PrivacyPolicyScreen(
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  LegalScreen(
    title = "Privacy Policy",
    emoji = "\uD83D\uDD12",
    lastUpdated = "September 18, 2026",
    summaryBadge = "\uD83D\uDD12 Short version: Your messages never leave your device. We don\u2019t collect, store, or see what you encode or decode. The app works 100% on-device.",
    sections = listOf(
      LegalSection(
        heading = "Who We Are",
        body = "Secret Hider is a free steganography tool that lets you hide secret messages inside text, emojis, and images. It is available as an Android app and via Google AI Studio."
      ),
      LegalSection(
        heading = "What We Do NOT Collect",
        body = "We do not collect, transmit, store, or process the messages, images, or any content you encode or decode.\n\nEverything \u2014 encoding, decoding, encryption \u2014 happens entirely on your device (on-device / client-side). Your secrets never travel to any server.\n\nWe do not require you to create an account. We do not collect your name, email address, or any personal identifying information to use the core tool."
      ),
      LegalSection(
        heading = "Vault / Local Storage",
        body = "The Secret Vault feature saves your hidden messages locally on your device (in the app\u2019s private storage). This data never leaves your device. If you uninstall the app or clear app data, this locally stored data will be permanently deleted."
      ),
      LegalSection(
        heading = "Analytics & Advertising",
        body = "At this time, Secret Hider does not use any analytics services or advertising networks. If we add these in the future, this policy will be updated before those services go live, and we will clearly disclose what data those third-party services may collect."
      ),
      LegalSection(
        heading = "Children\u2019s Privacy",
        body = "Secret Hider is not directed at children under 13. We do not knowingly collect any information from children."
      ),
      LegalSection(
        heading = "Changes to This Policy",
        body = "We may update this Privacy Policy from time to time. The \u201cLast Updated\u201d date at the top of this page will reflect the most recent changes. Continued use of the app after updates means you accept the revised policy."
      ),
      LegalSection(
        heading = "Contact",
        body = "Questions about this Privacy Policy? You can reach us via the Contact section in the app\u2019s About page."
      )
    ),
    onBack = onBack,
    modifier = modifier
  )
}
