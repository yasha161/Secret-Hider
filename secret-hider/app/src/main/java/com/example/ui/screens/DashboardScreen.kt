package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AmbientScreenGlow
import com.example.ui.theme.DesignTokens
import com.example.ui.theme.bouncyClick

private data class ExtraordinaryEmojiPreset(
  val emoji: String,
  val name: String,
  val glowColor: Color
)

@Composable
fun DashboardScreen(
  userName: String,
  onEditUserName: () -> Unit,
  savedCount: Int,
  onOpenTool: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()

  // Next-Gen Theme Mode State: Light Luxe Neo vs Dark Cyber Deck
  var isCyberDarkMode by remember { mutableStateOf(false) }

  val bgBrush = if (isCyberDarkMode) {
    Brush.verticalGradient(
      colors = listOf(
        Color(0xFF060913),
        Color(0xFF0B1120),
        Color(0xFF030712)
      )
    )
  } else {
    Brush.verticalGradient(
      colors = listOf(
        Color(0xFFEBF5FF),
        Color(0xFFF1F6FE),
        Color(0xFFF8FAFC)
      )
    )
  }

  // Pulsing animation for neon status dots
  val infiniteTransition = rememberInfiniteTransition(label = "cyber_pulse")
  val pulseGlow by infiniteTransition.animateFloat(
    initialValue = 0.5f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_glow"
  )

  // Extraordinary Emojis Deck collection
  val extraordinaryEmojis = remember {
    listOf(
      ExtraordinaryEmojiPreset("⚡", "Quantum Shock", Color(0xFF00F5D4)),
      ExtraordinaryEmojiPreset("🚀", "Hyper Drive", Color(0xFF3B82F6)),
      ExtraordinaryEmojiPreset("🪐", "Cosmic Orbit", Color(0xFF8B5CF6)),
      ExtraordinaryEmojiPreset("🔮", "Cyber Oracle", Color(0xFFA855F7)),
      ExtraordinaryEmojiPreset("👾", "Matrix Entity", Color(0xFF10B981)),
      ExtraordinaryEmojiPreset("💎", "Quantum Prism", Color(0xFF38BDF8)),
      ExtraordinaryEmojiPreset("🤫", "Silent Ghost", Color(0xFF64748B)),
      ExtraordinaryEmojiPreset("🔥", "Plasma Fire", Color(0xFFF43F5E)),
      ExtraordinaryEmojiPreset("🧬", "Cipher DNA", Color(0xFF10B981)),
      ExtraordinaryEmojiPreset("✨", "Star Spark", Color(0xFFF59E0B)),
      ExtraordinaryEmojiPreset("💀", "Chaos Skull", Color(0xFFE2E8F0)),
      ExtraordinaryEmojiPreset("🛸", "Stealth UFO", Color(0xFF06B6D4))
    )
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(bgBrush)
  ) {
    // Top Ambient Holographic Glow Aura
    AmbientScreenGlow(
      modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)
        .align(Alignment.TopCenter)
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 18.dp, vertical = 14.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Top Bar: Header & Next-Gen Theme Switcher
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "Hey, ${userName.ifBlank { "Maya" }}",
              fontSize = 26.sp,
              fontWeight = FontWeight.ExtraBold,
              color = if (isCyberDarkMode) Color.White else DesignTokens.InkPrimary,
              letterSpacing = (-0.5).sp
            )

            // Animated pulsing badge
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                  if (isCyberDarkMode) Color(0xFF1E293B) else Color(0xFFEFF6FF)
                )
                .border(
                  width = 1.dp,
                  color = Color(0xFF38BDF8).copy(alpha = pulseGlow),
                  shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "NEXT-GEN",
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0284C7),
                letterSpacing = 0.5.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(3.dp))

          // System stealth status
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(Color(0xFF10B981).copy(alpha = pulseGlow))
            )
            Text(
              text = "100% Zero-Leak Stealth Engine Ready",
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              color = if (isCyberDarkMode) Color(0xFF94A3B8) else DesignTokens.InkMuted
            )
          }
        }

        // Action Pod: Theme Toggle + User Avatar
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Quick Next-Gen Cyber Mode Switcher Pill
          Box(
            modifier = Modifier
              .bouncyClick { isCyberDarkMode = !isCyberDarkMode }
              .clip(RoundedCornerShape(16.dp))
              .background(
                if (isCyberDarkMode) Color(0xFF1E293B) else Color.White.copy(alpha = 0.9f)
              )
              .border(
                BorderStroke(
                  1.dp,
                  if (isCyberDarkMode) Color(0xFF38BDF8).copy(alpha = 0.5f) else Color(0xFFE2E8F0)
                ),
                RoundedCornerShape(16.dp)
              )
              .padding(horizontal = 10.dp, vertical = 8.dp)
              .testTag("theme_mode_toggle")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(
                text = if (isCyberDarkMode) "🌌 Cyber" else "✨ Neo",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCyberDarkMode) Color(0xFF38BDF8) else DesignTokens.SignalBlue
              )
            }
          }

          // User Avatar with Iridescent Border
          Box(
            modifier = Modifier
              .size(46.dp)
              .bouncyClick(onClick = onEditUserName)
              .shadow(
                elevation = 6.dp,
                shape = CircleShape,
                ambientColor = DesignTokens.PopCoral.copy(alpha = 0.3f),
                spotColor = DesignTokens.PopCoral.copy(alpha = 0.5f)
              )
              .clip(CircleShape)
              .background(DesignTokens.PopCoralBg)
              .border(
                BorderStroke(2.dp, DesignTokens.CyberIridescentBrush),
                CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (userName.isNotBlank()) userName.first().uppercase() else "M",
              color = DesignTokens.PopCoral,
              fontSize = 19.sp,
              fontWeight = FontWeight.ExtraBold
            )
          }
        }
      }

      // 2. EXTRAORDINARY EMOJI MATRIX ROW (Interactive Deck)
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(22.dp))
          .background(
            if (isCyberDarkMode) Color(0xFF0F172A).copy(alpha = 0.85f)
            else Color.White.copy(alpha = 0.88f)
          )
          .border(
            BorderStroke(1.dp, DesignTokens.CyberIridescentBrush),
            RoundedCornerShape(22.dp)
          )
          .padding(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = Color(0xFFA855F7),
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = "Extraordinary Emoji Lab",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = if (isCyberDarkMode) Color.White else DesignTokens.InkPrimary
            )
          }

          Text(
            text = "Tap to load ➔",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0284C7),
            modifier = Modifier.bouncyClick { onOpenTool("emoji") }
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal scrolling extraordinary emojis
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          extraordinaryEmojis.forEach { item ->
            Box(
              modifier = Modifier
                .bouncyClick {
                  Toast.makeText(context, "Loaded ${item.emoji} (${item.name}) into Stego Matrix!", Toast.LENGTH_SHORT).show()
                  onOpenTool("emoji")
                }
                .shadow(
                  elevation = 3.dp,
                  shape = RoundedCornerShape(16.dp),
                  spotColor = item.glowColor.copy(alpha = 0.35f)
                )
                .clip(RoundedCornerShape(16.dp))
                .background(
                  if (isCyberDarkMode) Color(0xFF1E293B)
                  else Color(0xFFF1F6FE)
                )
                .border(
                  BorderStroke(1.dp, item.glowColor.copy(alpha = 0.4f)),
                  RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
              ) {
                Text(
                  text = item.emoji,
                  fontSize = 24.sp
                )
                Text(
                  text = item.name,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isCyberDarkMode) Color(0xFFCBD5E1) else DesignTokens.InkSecondary
                )
              }
            }
          }
        }
      }

      // 3. STEGANOGRAPHY TOOLS
      Text(
        text = "Steganography Tools",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = if (isCyberDarkMode) Color(0xFF94A3B8) else DesignTokens.InkSecondary,
        letterSpacing = 0.2.sp
      )

      // CARD 1: Hide in Text 📝
      NextGenToolCard(
        imageRes = R.drawable.emoji_cyber_text,
        title = "Hide in Text 📝",
        glowTag = "INVISIBLE TEXT",
        badgeText = "AES-256",
        subtitle = "Hide a message inside normal-looking text — no one can tell it's there.",
        pillTags = listOf("Invisible Text", "Stealth"),
        accentColor = Color(0xFF00F5D4),
        isDark = isCyberDarkMode,
        pulseGlow = pulseGlow,
        onClick = { onOpenTool("text") },
        testTag = "card_hide_in_text"
      )

      // CARD 2: Hide in Emoji 🤫
      NextGenToolCard(
        imageRes = R.drawable.emoji_cyber_mask,
        title = "Hide in Emoji 🤫",
        glowTag = "HIDDEN IN EMOJI",
        badgeText = "STEALTH",
        subtitle = "Your message disappears inside a single emoji. Looks normal, isn't.",
        pillTags = listOf("Emoji Carrier", "Stealth"),
        accentColor = Color(0xFF10B981),
        isDark = isCyberDarkMode,
        pulseGlow = pulseGlow,
        onClick = { onOpenTool("emoji") },
        testTag = "card_hide_in_emoji"
      )

      // CARD 3: Hide in Image 🖼️
      NextGenToolCard(
        imageRes = R.drawable.emoji_cyber_image,
        title = "Hide in Image 🖼️",
        glowTag = "HIDDEN IN IMAGE",
        badgeText = "LOSSLESS PNG",
        subtitle = "Hide your message inside a photo's pixels — the image looks exactly the same.",
        pillTags = listOf("Pixel Hidden", "Lossless PNG"),
        accentColor = Color(0xFFA855F7),
        isDark = isCyberDarkMode,
        pulseGlow = pulseGlow,
        onClick = { onOpenTool("image") },
        testTag = "card_hide_in_image"
      )

      // CARD 4: Secret Vault 🔐
      NextGenToolCard(
        imageRes = R.drawable.emoji_cyber_vault,
        title = "Secret Vault 🔐",
        glowTag = "YOUR VAULT",
        badgeText = "$savedCount SAVED",
        subtitle = "All your hidden messages, saved and locked, right on your phone.",
        pillTags = listOf("Offline Only", "Encrypted"),
        accentColor = Color(0xFFF59E0B),
        isDark = isCyberDarkMode,
        pulseGlow = pulseGlow,
        onClick = { onOpenTool("vault") },
        testTag = "card_secret_vault"
      )

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}

/**
 * Next-Generation Tool Card featuring high-res 3D Emoji art, holographic gradient borders,
 * glowing cyber matrix status badges, and spring tactile feedback.
 */
@Composable
private fun NextGenToolCard(
  imageRes: Int,
  title: String,
  glowTag: String,
  badgeText: String,
  subtitle: String,
  pillTags: List<String>,
  accentColor: Color,
  isDark: Boolean,
  pulseGlow: Float,
  onClick: () -> Unit,
  testTag: String
) {
  val shape = RoundedCornerShape(24.dp)

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .bouncyClick(onClick = onClick)
      .shadow(
        elevation = 8.dp,
        shape = shape,
        ambientColor = accentColor.copy(alpha = 0.2f),
        spotColor = accentColor.copy(alpha = 0.45f)
      )
      .clip(shape)
      .background(
        if (isDark) {
          Brush.verticalGradient(
            listOf(
              Color(0xFF0F172A).copy(alpha = 0.94f),
              Color(0xFF1E293B).copy(alpha = 0.88f)
            )
          )
        } else {
          Brush.verticalGradient(
            listOf(
              Color.White,
              Color(0xFFF8FAFC)
            )
          )
        }
      )
      .border(
        width = 1.5.dp,
        brush = Brush.horizontalGradient(
          listOf(
            accentColor.copy(alpha = 0.85f),
            Color(0xFF38BDF8).copy(alpha = 0.6f),
            Color(0xFFA855F7).copy(alpha = 0.5f)
          )
        ),
        shape = shape
      )
      .padding(16.dp)
      .testTag(testTag)
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Top Row: 3D Extraordinary Emoji Avatar + Titles + Action Arrow
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // 3D Extraordinary Emoji Avatar container with glowing aura
        Box(
          modifier = Modifier
            .size(68.dp)
            .shadow(
              elevation = 6.dp,
              shape = RoundedCornerShape(20.dp),
              spotColor = accentColor.copy(alpha = 0.6f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
              Brush.radialGradient(
                colors = listOf(
                  accentColor.copy(alpha = 0.35f),
                  Color(0xFF0F172A)
                )
              )
            )
            .border(
              BorderStroke(1.5.dp, accentColor.copy(alpha = 0.7f)),
              RoundedCornerShape(20.dp)
            ),
          contentAlignment = Alignment.Center
        ) {
          Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )
        }

        // Title and Subtitle info
        Column(modifier = Modifier.weight(1f)) {
          // Status Pill with pulsing dot
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = pulseGlow))
            )

            Text(
              text = glowTag,
              fontSize = 10.sp,
              fontWeight = FontWeight.ExtraBold,
              color = accentColor,
              letterSpacing = 0.5.sp
            )
          }

          Spacer(modifier = Modifier.height(2.dp))

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = title,
              fontSize = 18.sp,
              fontWeight = FontWeight.ExtraBold,
              color = if (isDark) Color.White else DesignTokens.InkPrimary,
              letterSpacing = (-0.3).sp
            )
          }

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = subtitle,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            color = if (isDark) Color(0xFF94A3B8) else DesignTokens.InkSecondary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        }

        // Holographic Action Capsule Arrow
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(
              if (isDark) Color(0xFF1E293B) else Color(0xFFEFF6FF)
            )
            .border(
              BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
              CircleShape
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Open $title",
            tint = accentColor,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      // Bottom Row: Tech Feature Pills & Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          pillTags.take(2).forEach { tag ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                  if (isDark) Color(0xFF1E293B).copy(alpha = 0.7f)
                  else Color(0xFFF1F5F9)
                )
                .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
              Text(
                text = tag,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDark) Color(0xFFCBD5E1) else DesignTokens.InkMuted
              )
            }
          }
        }

        // Badge pill on right
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(accentColor.copy(alpha = 0.15f))
            .border(
              BorderStroke(1.dp, accentColor.copy(alpha = 0.5f)),
              RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = badgeText,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = accentColor,
            letterSpacing = 0.4.sp
          )
        }
      }
    }
  }
}
