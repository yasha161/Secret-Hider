package com.example.ui.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Design Plan Tokens:
 * ink, drift, signal, glass, pop, glow
 */
object DesignTokens {
  // 1. INK: Deep, crisp typography and obsidian accents
  val InkPrimary = Color(0xFF0F172A)
  val InkSecondary = Color(0xFF334155)
  val InkMuted = Color(0xFF64748B)
  val InkBorder = Color(0xFFE2E8F0)
  val ObsidianVault = Color(0xFF0F172A)
  val ObsidianVaultBorder = Color(0xFF1E293B)

  // 2. DRIFT: Airy light background canvas with subtle vertical gradient and glowing auras
  val DriftGradient = Brush.verticalGradient(
    colors = listOf(
      Color(0xFFEBF5FF),
      Color(0xFFF1F6FE),
      Color(0xFFF8FAFC)
    )
  )
  val DriftCanvas = Color(0xFFF8FAFC)

  // 3. SIGNAL: Electric royal blue for focal interactive points
  val SignalBlue = Color(0xFF2563EB)
  val SignalBlueDark = Color(0xFF1D4ED8)
  val SignalBlueLight = Color(0xFF3B82F6)
  val SignalGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))
  )

  // 4. GLASS: Frosted translucent white surfaces with luminous fine borders
  val GlassBackground = Color.White.copy(alpha = 0.86f)
  val GlassCardGradient = Brush.verticalGradient(
    colors = listOf(
      Color.White.copy(alpha = 0.94f),
      Color.White.copy(alpha = 0.82f)
    )
  )
  val GlassBorder = BorderStroke(
    1.dp,
    Brush.verticalGradient(
      listOf(
        Color.White.copy(alpha = 0.95f),
        Color(0xFF93C5FD).copy(alpha = 0.35f)
      )
    )
  )
  val GlassBorderSubtle = BorderStroke(1.dp, Color(0xFFE2E8F0).copy(alpha = 0.8f))

  // 5. POP: Vibrant accents & avatars
  val PopCoral = Color(0xFFFB7185)
  val PopCoralBg = Color(0xFFFFECEF)
  val PopAmber = Color(0xFFF59E0B)
  val PopAmberBg = Color(0xFFFEF3C7)
  val PopMint = Color(0xFF0D9488)
  val PopMintBg = Color(0xFFCCFBF1)
  val PopSky = Color(0xFF0284C7)
  val PopSkyBg = Color(0xFFE0F2FE)

  // 6. GLOW: Ambient light drops and button glows
  val SignalGlowColor = Color(0xFF2563EB).copy(alpha = 0.35f)
  val SkyGlowColor = Color(0xFF38BDF8).copy(alpha = 0.25f)

  // 7. NEXT-GEN CYBER & EXTRAORDINARY EMOJI TOKENS
  val CyberNeonCyan = Color(0xFF00F5D4)
  val CyberElectricBlue = Color(0xFF0070F3)
  val CyberNeonPurple = Color(0xFF7928CA)
  val CyberNeonPink = Color(0xFFFF0080)
  val CyberNeonMint = Color(0xFF10B981)
  val CyberNeonAmber = Color(0xFFF59E0B)

  val CyberIridescentBrush = Brush.linearGradient(
    colors = listOf(
      Color(0xFF00F5D4),
      Color(0xFF38BDF8),
      Color(0xFF818CF8),
      Color(0xFFC084FC),
      Color(0xFFF472B6)
    )
  )

  val CyberDarkCanvasGradient = Brush.verticalGradient(
    colors = listOf(
      Color(0xFF070B14),
      Color(0xFF0D1424),
      Color(0xFF0A0F1D)
    )
  )

  val CyberLuxeCardGradient = Brush.verticalGradient(
    colors = listOf(
      Color.White.copy(alpha = 0.98f),
      Color(0xFFF0F7FF).copy(alpha = 0.92f)
    )
  )
}

/**
 * Tactile scale bounce animation for interactive elements.
 * Squishes smoothly to ~0.95 on press, then springs back with satisfying tactile snap.
 */
@Composable
fun Modifier.bouncyClick(
  enabled: Boolean = true,
  scaleDown: Float = 0.95f,
  onClick: () -> Unit
): Modifier {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed && enabled) scaleDown else 1.0f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessMediumLow
    ),
    label = "bouncy_scale"
  )

  return this
    .graphicsLayer {
      scaleX = scale
      scaleY = scale
    }
    .clickable(
      interactionSource = interactionSource,
      indication = null,
      enabled = enabled,
      onClick = onClick
    )
}

/**
 * Frosted glass container with luminous subtle gradient and fine border.
 */
@Composable
fun FrostedGlassPanel(
  modifier: Modifier = Modifier,
  shape: Shape = RoundedCornerShape(22.dp),
  border: BorderStroke? = DesignTokens.GlassBorder,
  elevation: Dp = 4.dp,
  content: @Composable () -> Unit
) {
  Surface(
    modifier = modifier
      .shadow(
        elevation = elevation,
        shape = shape,
        ambientColor = DesignTokens.SkyGlowColor,
        spotColor = DesignTokens.SignalGlowColor
      ),
    shape = shape,
    color = Color.Transparent,
    border = border
  ) {
    Box(
      modifier = Modifier
        .background(DesignTokens.GlassCardGradient)
    ) {
      content()
    }
  }
}

/**
 * 5-Segment Progress Bar with glowing active segment.
 */
@Composable
fun SegmentedProgressBar(
  currentStep: Int,
  totalSteps: Int = 5,
  modifier: Modifier = Modifier
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(6.dp),
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
  ) {
    for (i in 1..totalSteps) {
      val isActive = i <= currentStep
      val isCurrent = i == currentStep

      val animProgress by animateFloatAsState(
        targetValue = if (isActive) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy),
        label = "segment_progress_$i"
      )

      Box(
        modifier = Modifier
          .weight(1f)
          .height(5.dp)
          .clip(RoundedCornerShape(3.dp))
          .background(
            if (isActive) {
              DesignTokens.SignalBlue
            } else {
              DesignTokens.InkBorder.copy(alpha = 0.6f)
            }
          )
          .then(
            if (isCurrent) {
              Modifier.shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(3.dp),
                spotColor = DesignTokens.SignalBlue
              )
            } else Modifier
          )
      )
    }
  }
}

/**
 * Primary Signal Button with gradient fill, glow shadow, and scale bounce on press.
 */
@Composable
fun SignalGradientButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  isLoading: Boolean = false,
  trailingIcon: @Composable (() -> Unit)? = null,
  testTag: String = ""
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed && enabled && !isLoading) 0.95f else 1.0f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessMediumLow
    ),
    label = "btn_scale"
  )

  Box(
    modifier = modifier
      .graphicsLayer {
        scaleX = scale
        scaleY = scale
      }
      .shadow(
        elevation = if (enabled) 10.dp else 0.dp,
        shape = RoundedCornerShape(16.dp),
        ambientColor = DesignTokens.SignalGlowColor,
        spotColor = DesignTokens.SignalBlueDark.copy(alpha = 0.5f)
      )
      .clip(RoundedCornerShape(16.dp))
      .background(
        if (enabled) DesignTokens.SignalGradient
        else Brush.linearGradient(listOf(Color(0xFF94A3B8), Color(0xFF64748B)))
      )
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        enabled = enabled && !isLoading,
        onClick = onClick
      )
      .padding(vertical = 16.dp, horizontal = 20.dp)
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Text(
        text = text,
        color = Color.White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.2.sp
      )
      if (trailingIcon != null) {
        Spacer(modifier = Modifier.width(8.dp))
        trailingIcon()
      }
    }
  }
}

/**
 * Secondary Glass Button with soft border and scale bounce.
 */
@Composable
fun GlassSecondaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String = ""
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.95f else 1.0f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessMediumLow
    ),
    label = "sec_btn_scale"
  )

  Box(
    modifier = modifier
      .graphicsLayer {
        scaleX = scale
        scaleY = scale
      }
      .clip(RoundedCornerShape(16.dp))
      .background(Color.White.copy(alpha = 0.75f))
      .border(BorderStroke(1.dp, DesignTokens.InkBorder), RoundedCornerShape(16.dp))
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
      .padding(vertical = 16.dp, horizontal = 20.dp)
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      color = DesignTokens.InkSecondary,
      fontSize = 15.sp,
      fontWeight = FontWeight.SemiBold
    )
  }
}

/**
 * Animated check badge with energetic overshoot bounce.
 */
@Composable
fun OvershootCheckBadge(
  selected: Boolean,
  modifier: Modifier = Modifier
) {
  val scale = remember { Animatable(if (selected) 1f else 0.4f) }

  LaunchedEffect(selected) {
    if (selected) {
      scale.animateTo(
        targetValue = 1f,
        animationSpec = spring(
          dampingRatio = Spring.DampingRatioHighBouncy,
          stiffness = Spring.StiffnessMediumLow
        )
      )
    } else {
      scale.snapTo(0.4f)
    }
  }

  Box(
    modifier = modifier
      .size(24.dp)
      .graphicsLayer {
        scaleX = if (selected) scale.value else 1f
        scaleY = if (selected) scale.value else 1f
      }
      .clip(CircleShape)
      .background(
        if (selected) DesignTokens.SignalBlue
        else Color.Transparent
      )
      .border(
        width = 1.5.dp,
        color = if (selected) DesignTokens.SignalBlue else DesignTokens.InkBorder,
        shape = CircleShape
      ),
    contentAlignment = Alignment.Center
  ) {
    if (selected) {
      Text(
        text = "✓",
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.ExtraBold
      )
    }
  }
}

/**
 * Pulsing ambient glow decoration at top corner of screens.
 */
@Composable
fun AmbientScreenGlow(
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "ambient_glow")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.45f,
    targetValue = 0.75f,
    animationSpec = infiniteRepeatable(
      animation = tween(2800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "glow_alpha"
  )

  Box(
    modifier = modifier
      .graphicsLayer { alpha = pulseAlpha }
      .drawBehind {
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0xFF93C5FD).copy(alpha = 0.5f),
              Color(0xFFE0F2FE).copy(alpha = 0.25f),
              Color.Transparent
            ),
            center = Offset(size.width * 0.5f, 0f),
            radius = size.width * 0.75f
          )
        )
      }
  )
}
