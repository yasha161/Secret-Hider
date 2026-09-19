package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TermsScreen(
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  LegalScreen(
    title = "Terms of Service",
    emoji = "\uD83D\uDCCB",
    lastUpdated = "September 18, 2026",
    summaryBadge = "\uD83D\uDCCB Short version: Secret Hider is free for personal, lawful, fun use. Please don\u2019t use it for anything that\u2019s illegal or harmful. You\u2019re responsible for what you hide.",
    sections = listOf(
      LegalSection(
        heading = "1. Acceptance of Terms",
        body = "By using Secret Hider (the \u201cService\u201d), you agree to these Terms of Service. If you don\u2019t agree, please don\u2019t use the app."
      ),
      LegalSection(
        heading = "2. What Secret Hider Is",
        body = "Secret Hider is a free, personal steganography tool designed for fun, privacy, and harmless secret-sharing \u2014 inside jokes, surprise messages, personal notes, and other lawful personal communication between consenting people."
      ),
      LegalSection(
        heading = "3. Permitted Use",
        body = "The Service is provided free of charge for personal, non-commercial, lawful use. You may use it to hide messages for fun, surprise a friend, keep a personal journal, or any other lawful personal purpose."
      ),
      LegalSection(
        heading = "4. Prohibited Use",
        body = "You may NOT use Secret Hider for:\n\u2022 Fraud, scams, phishing, or financial deception of any kind\n\u2022 Harassment, threats, stalking, or abuse of any person\n\u2022 Hiding illegal content or content that violates any applicable law\n\u2022 Distributing malware or any other prohibited content\n\u2022 Any communication that violates another person\u2019s rights\n\nViolating these terms may result in revocation of your right to use the Service."
      ),
      LegalSection(
        heading = "5. No Warranty",
        body = "The Service is provided \u201cas-is\u201d without any warranty. We do not guarantee that encoding/decoding will work with every app, platform, or character encoding environment. Since all processing is on-device, you are responsible for saving and backing up anything important."
      ),
      LegalSection(
        heading = "6. Data and Local Storage",
        body = "Since everything runs on your device, you are responsible for your locally stored data. Clearing app data, uninstalling the app, or clearing browser storage may permanently delete your Vault contents. We have no ability to recover this data."
      ),
      LegalSection(
        heading = "7. Limitation of Liability",
        body = "To the maximum extent permitted by law, Secret Hider and its developer shall not be liable for any direct, indirect, incidental, or consequential damages arising from your use or inability to use the Service."
      ),
      LegalSection(
        heading = "8. Governing Law",
        body = "These Terms are governed by the laws of India. Any disputes will be subject to the jurisdiction of courts in India."
      ),
      LegalSection(
        heading = "9. Changes to Terms",
        body = "We may update these Terms. The \u201cLast Updated\u201d date above will reflect changes. Continued use after updates means you accept the revised Terms."
      )
    ),
    onBack = onBack,
    modifier = modifier
  )
}
