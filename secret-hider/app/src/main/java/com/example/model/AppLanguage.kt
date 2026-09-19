package com.example.model

enum class AppLanguage(
  val code: String,
  val displayName: String,
  val nativeName: String,
  val flagEmoji: String,
  val subtitle: String
) {
  ENGLISH(
    code = "en",
    displayName = "English",
    nativeName = "English",
    flagEmoji = "🇺🇸",
    subtitle = "Default global language"
  ),
  SPANISH(
    code = "es",
    displayName = "Spanish",
    nativeName = "Español",
    flagEmoji = "🇪🇸",
    subtitle = "Idioma global esteganografía"
  ),
  FRENCH(
    code = "fr",
    displayName = "French",
    nativeName = "Français",
    flagEmoji = "🇫🇷",
    subtitle = "Stéganographie sécurisée"
  ),
  GERMAN(
    code = "de",
    displayName = "German",
    nativeName = "Deutsch",
    flagEmoji = "🇩🇪",
    subtitle = "Sichere Steganographie"
  ),
  PORTUGUESE(
    code = "pt",
    displayName = "Portuguese",
    nativeName = "Português",
    flagEmoji = "🇧🇷",
    subtitle = "Esteganografia segura"
  ),
  HINDI(
    code = "hi",
    displayName = "Hindi",
    nativeName = "हिन्दी",
    flagEmoji = "🇮🇳",
    subtitle = "भारत की आधिकारिक भाषा"
  ),
  JAPANESE(
    code = "ja",
    displayName = "Japanese",
    nativeName = "日本語",
    flagEmoji = "🇯🇵",
    subtitle = "安全なステガノグラフィ"
  ),
  KOREAN(
    code = "ko",
    displayName = "Korean",
    nativeName = "한국어",
    flagEmoji = "🇰🇷",
    subtitle = "안전한 스테가노그래피"
  ),
  ARABIC(
    code = "ar",
    displayName = "Arabic",
    nativeName = "العربية",
    flagEmoji = "🇸🇦",
    subtitle = "إخفاء الرسائل المشفرة"
  )
}
