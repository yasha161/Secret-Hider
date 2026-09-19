package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.ui.components.dismissKeyboardOnTap
import com.example.ui.theme.AmbientScreenGlow
import com.example.ui.theme.DesignTokens
import com.example.ui.theme.GlassSecondaryButton
import com.example.ui.theme.OvershootCheckBadge
import com.example.ui.theme.SegmentedProgressBar
import com.example.ui.theme.SignalGradientButton
import com.example.ui.theme.bouncyClick

@Composable
fun WelcomeOnboardingScreen(
  initialUserName: String,
  selectedLanguage: AppLanguage,
  onLanguageSelected: (AppLanguage) -> Unit,
  onFinishOnboarding: (userName: String, targetScreen: String) -> Unit,
  modifier: Modifier = Modifier
) {
  var currentStep by remember { mutableIntStateOf(1) } // 1, 2, or 3
  var nameInput by remember { mutableStateOf(if (initialUserName.isBlank() || initialUserName == "User") "Maya" else initialUserName) }
  var chosenLang by remember { mutableStateOf(selectedLanguage) }
  val selectedTools = remember { mutableStateListOf("text", "image") }
  val focusManager = LocalFocusManager.current
  val keyboardController = LocalSoftwareKeyboardController.current

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(DesignTokens.DriftGradient)
      .imePadding()
  ) {
    // Subtle top ambient blue aura from design plan
    AmbientScreenGlow(
      modifier = Modifier
        .fillMaxWidth()
        .height(280.dp)
        .align(Alignment.TopCenter)
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // 1. Top Area: Segmented Progress Bar & Skip
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 12.dp, bottom = 12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // 3-segment pill progress indicator with signal glow
          SegmentedProgressBar(
            currentStep = currentStep,
            totalSteps = 3,
            modifier = Modifier.weight(1f)
          )

          Spacer(modifier = Modifier.width(16.dp))

          Text(
            text = "Skip",
            color = DesignTokens.InkMuted,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            modifier = Modifier
              .bouncyClick {
                onFinishOnboarding(nameInput.ifBlank { "Maya" }, "dashboard")
              }
              .padding(horizontal = 8.dp, vertical = 6.dp)
              .testTag("onboarding_skip_button")
          )
        }
      }

      // 2. Middle Content Area: Fills the remaining screen space gracefully
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth(),
        contentAlignment = Alignment.Center
      ) {
        AnimatedContent(
          targetState = currentStep,
          transitionSpec = {
            if (targetState > initialState) {
              (slideInHorizontally(tween(320, easing = FastOutSlowInEasing)) { it / 2 } + fadeIn(tween(280)))
                .togetherWith(slideOutHorizontally(tween(280, easing = FastOutSlowInEasing)) { -it / 2 } + fadeOut(tween(220)))
            } else {
              (slideInHorizontally(tween(320, easing = FastOutSlowInEasing)) { -it / 2 } + fadeIn(tween(280)))
                .togetherWith(slideOutHorizontally(tween(280, easing = FastOutSlowInEasing)) { it / 2 } + fadeOut(tween(220)))
            }
          },
          label = "onboarding_step"
        ) { step ->
          when (step) {
            1 -> OnboardingStepLanguage(
              selectedLang = chosenLang,
              onSelect = {
                chosenLang = it
                onLanguageSelected(it)
              }
            )
            2 -> OnboardingStepName(
              name = nameInput,
              onNameChange = { nameInput = it }
            )
            3 -> OnboardingStepTools(
              selectedTools = selectedTools,
              onToggleTool = { toolId ->
                if (selectedTools.contains(toolId)) {
                  selectedTools.remove(toolId)
                } else {
                  selectedTools.add(toolId)
                }
              }
            )
          }
        }
      }

      // 3. Bottom Action Bar: Always anchored cleanly above system navigation bar
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 16.dp, bottom = 20.dp)
          .navigationBarsPadding()
      ) {
        if (currentStep < 3) {
          SignalGradientButton(
            text = "Continue",
            onClick = { currentStep++ },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
              )
            },
            testTag = "onboarding_continue_button"
          )
        } else {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            GlassSecondaryButton(
              text = "Skip",
              onClick = {
                onFinishOnboarding(nameInput.ifBlank { "Maya" }, "dashboard")
              },
              modifier = Modifier.weight(1f),
              testTag = "onboarding_final_skip"
            )

            SignalGradientButton(
              text = "Get Started",
              onClick = {
                val nextScreen = if (selectedTools.contains("text")) "text"
                else if (selectedTools.contains("emoji")) "emoji"
                else if (selectedTools.contains("image")) "image"
                else "dashboard"
                onFinishOnboarding(nameInput.ifBlank { "Maya" }, nextScreen)
              },
              modifier = Modifier.weight(1f),
              trailingIcon = {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(18.dp)
                )
              },
              testTag = "onboarding_final_continue"
            )
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// Step 1: Pick your language (Glass Cards, 2-Column Responsive)
// -------------------------------------------------------------
private data class OnboardingLangItem(val lang: AppLanguage, val label: String)

@Composable
private fun OnboardingStepLanguage(
  selectedLang: AppLanguage,
  onSelect: (AppLanguage) -> Unit
) {
  val languages = listOf(
    OnboardingLangItem(AppLanguage.ENGLISH, "🇺🇸 English"),
    OnboardingLangItem(AppLanguage.SPANISH, "🇪🇸 Español"),
    OnboardingLangItem(AppLanguage.FRENCH, "🇫🇷 Français"),
    OnboardingLangItem(AppLanguage.GERMAN, "🇩🇪 Deutsch"),
    OnboardingLangItem(AppLanguage.PORTUGUESE, "🇧🇷 Português"),
    OnboardingLangItem(AppLanguage.HINDI, "🇮🇳 हिन्दी"),
    OnboardingLangItem(AppLanguage.JAPANESE, "🇯🇵 日本語"),
    OnboardingLangItem(AppLanguage.KOREAN, "🇰🇷 한국어")
  )

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = "Pick your language 🌍",
      fontSize = 26.sp,
      fontWeight = FontWeight.Bold,
      color = DesignTokens.InkPrimary,
      letterSpacing = (-0.4).sp
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = "You can change this anytime in settings.",
      fontSize = 14.sp,
      color = DesignTokens.InkMuted
    )

    Spacer(modifier = Modifier.height(20.dp))

    // 2-column clean responsive grid
    for (i in languages.indices step 2) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        LanguageGlassCard(
          label = languages[i].label,
          selected = selectedLang == languages[i].lang,
          onClick = { onSelect(languages[i].lang) },
          modifier = Modifier.weight(1f)
        )

        if (i + 1 < languages.size) {
          LanguageGlassCard(
            label = languages[i + 1].label,
            selected = selectedLang == languages[i + 1].lang,
            onClick = { onSelect(languages[i + 1].lang) },
            modifier = Modifier.weight(1f)
          )
        } else {
          Spacer(modifier = Modifier.weight(1f))
        }
      }
    }
  }
}

@Composable
private fun LanguageGlassCard(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val shape = RoundedCornerShape(16.dp)

  Box(
    modifier = modifier
      .bouncyClick(onClick = onClick)
      .shadow(
        elevation = if (selected) 6.dp else 2.dp,
        shape = shape,
        ambientColor = if (selected) DesignTokens.SignalGlowColor else Color.Black.copy(alpha = 0.05f),
        spotColor = if (selected) DesignTokens.SignalBlue else Color.Transparent
      )
      .clip(shape)
      .background(
        if (selected) Color(0xFFEFF6FF)
        else Color.White.copy(alpha = 0.88f)
      )
      .border(
        width = if (selected) 2.dp else 1.dp,
        color = if (selected) DesignTokens.SignalBlue else Color(0xFFE2E8F0),
        shape = shape
      )
      .padding(horizontal = 14.dp, vertical = 15.dp),
    contentAlignment = Alignment.CenterStart
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(
        text = label,
        fontSize = 15.sp,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        color = if (selected) DesignTokens.SignalBlue else DesignTokens.InkPrimary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      if (selected) {
        Box(
          modifier = Modifier
            .size(9.dp)
            .clip(CircleShape)
            .background(DesignTokens.SignalBlue)
        )
      }
    }
  }
}

// -------------------------------------------------------------
// Step 2: What should we call you? (Fancy 3D Hero + Frosted Input)
// -------------------------------------------------------------
@Composable
private fun OnboardingStepName(
  name: String,
  onNameChange: (String) -> Unit
) {
  val focusManager = LocalFocusManager.current
  val keyboardController = LocalSoftwareKeyboardController.current

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.Start
  ) {
    // 1. Fancy 3D Hero Container with Glowing Holographic Privacy Shield Art
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(210.dp)
        .shadow(
          elevation = 8.dp,
          shape = RoundedCornerShape(24.dp),
          ambientColor = DesignTokens.SkyGlowColor,
          spotColor = DesignTokens.SignalBlue.copy(alpha = 0.35f)
        )
        .clip(RoundedCornerShape(24.dp))
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color(0xFFE0EFFF),
              Color(0xFFEBF5FE),
              Color.White
            )
          )
        )
        .border(
          width = 1.5.dp,
          brush = Brush.verticalGradient(
            listOf(
              Color.White,
              Color(0xFF93C5FD).copy(alpha = 0.6f)
            )
          ),
          shape = RoundedCornerShape(24.dp)
        )
    ) {
      // 3D Illustration
      Image(
        painter = painterResource(id = R.drawable.img_security_hero),
        contentDescription = "Holographic Privacy Shield",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )

      // Floating Frosted Status Badge at bottom-start
      Box(
        modifier = Modifier
          .align(Alignment.BottomStart)
          .padding(14.dp)
          .shadow(4.dp, RoundedCornerShape(20.dp))
          .clip(RoundedCornerShape(20.dp))
          .background(Color.White.copy(alpha = 0.92f))
          .border(BorderStroke(1.dp, Color(0xFFBFDBFE)), RoundedCornerShape(20.dp))
          .padding(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            tint = DesignTokens.SignalBlue,
            modifier = Modifier.size(16.dp)
          )
          Text(
            text = "ZERO-KNOWLEDGE VAULT",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = DesignTokens.SignalBlue,
            letterSpacing = 0.4.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(22.dp))

    // 2. Heading and Subtitle
    Text(
      text = "What should we call you? 👋",
      fontSize = 26.sp,
      fontWeight = FontWeight.Bold,
      color = DesignTokens.InkPrimary,
      letterSpacing = (-0.4).sp
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = "Your name stays local on your device. Never shared.",
      fontSize = 14.sp,
      color = DesignTokens.InkMuted
    )

    Spacer(modifier = Modifier.height(18.dp))

    // 3. Ultra-Sleek Frosted Glass Name Input Field
    OutlinedTextField(
      value = name,
      onValueChange = { if (it.length <= 25) onNameChange(it) },
      placeholder = {
        Text(
          "Enter your name...",
          color = Color(0xFF94A3B8),
          fontSize = 16.sp
        )
      },
      leadingIcon = {
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(Color(0xFFDBEAFE)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "User Icon",
            tint = DesignTokens.SignalBlue,
            modifier = Modifier.size(18.dp)
          )
        }
      },
      trailingIcon = {
        if (name.isNotEmpty()) {
          IconButton(onClick = { onNameChange("") }) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Clear name",
              tint = Color(0xFF94A3B8),
              modifier = Modifier.size(18.dp)
            )
          }
        }
      },
      singleLine = true,
      shape = RoundedCornerShape(20.dp),
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
      keyboardActions = KeyboardActions(onDone = {
        focusManager.clearFocus()
        keyboardController?.hide()
      }),
      colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color(0xFFF1F6FD),
        focusedBorderColor = DesignTokens.SignalBlue,
        unfocusedBorderColor = Color(0xFFCBD5E1),
        focusedTextColor = DesignTokens.InkPrimary,
        unfocusedTextColor = DesignTokens.InkPrimary
      ),
      modifier = Modifier
        .fillMaxWidth()
        .shadow(2.dp, RoundedCornerShape(20.dp), ambientColor = Color.Black.copy(alpha = 0.04f))
        .testTag("onboarding_name_input")
    )

    Spacer(modifier = Modifier.height(14.dp))

    // 4. Reactive Pop Coral Welcome Preview Chip
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = Color(0xFFFFECEF),
      border = BorderStroke(1.dp, Color(0xFFFECDD3)),
      modifier = Modifier
        .fillMaxWidth()
        .bouncyClick {
          onNameChange(name.ifBlank { "Maya" })
        }
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Box(
          modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(DesignTokens.PopCoral),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = if (name.isNotBlank()) name.first().uppercase() else "M",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Hey, ${name.ifBlank { "Maya" }} 👋",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = DesignTokens.PopCoral
          )
          Text(
            text = "Your private vault is configured and ready.",
            fontSize = 12.sp,
            color = Color(0xFFE11D48).copy(alpha = 0.8f)
          )
        }

        Text(
          text = "✨",
          fontSize = 16.sp
        )
      }
    }
  }
}

// -------------------------------------------------------------
// Step 3: How do you want to hide your secrets? (2x2 Glass Grid)
// -------------------------------------------------------------
private data class OnboardingToolItem(
  val id: String,
  val title: String,
  val subtitle: String,
  val icon: ImageVector,
  val iconTint: Color,
  val iconBg: Color
)

@Composable
private fun OnboardingStepTools(
  selectedTools: List<String>,
  onToggleTool: (String) -> Unit
) {
  val tools = listOf(
    OnboardingToolItem(
      id = "text",
      title = "Hide in Text",
      subtitle = "Zero-width spaces",
      icon = Icons.Default.TextFields,
      iconTint = DesignTokens.SignalBlue,
      iconBg = Color(0xFFDBEAFE)
    ),
    OnboardingToolItem(
      id = "image",
      title = "Hide in Image",
      subtitle = "Pixel LSB stealth",
      icon = Icons.Default.Image,
      iconTint = DesignTokens.PopSky,
      iconBg = DesignTokens.PopSkyBg
    ),
    OnboardingToolItem(
      id = "emoji",
      title = "Hide in Emoji",
      subtitle = "Emoji byte mapping",
      icon = Icons.Default.Mood,
      iconTint = DesignTokens.PopMint,
      iconBg = DesignTokens.PopMintBg
    ),
    OnboardingToolItem(
      id = "unsure",
      title = "Not sure yet",
      subtitle = "Explore everything",
      icon = Icons.Default.Psychology,
      iconTint = DesignTokens.PopAmber,
      iconBg = DesignTokens.PopAmberBg
    )
  )

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = "How do you want to hide your secrets?",
      fontSize = 24.sp,
      fontWeight = FontWeight.Bold,
      color = DesignTokens.InkPrimary,
      lineHeight = 32.sp,
      letterSpacing = (-0.4).sp
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = "Choose any tools you want to start with.",
      fontSize = 14.sp,
      color = DesignTokens.InkMuted
    )

    Spacer(modifier = Modifier.height(20.dp))

    // 2x2 Grid
    for (i in tools.indices step 2) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        ToolGlassCard(
          item = tools[i],
          selected = selectedTools.contains(tools[i].id),
          onClick = { onToggleTool(tools[i].id) },
          modifier = Modifier.weight(1f)
        )

        if (i + 1 < tools.size) {
          ToolGlassCard(
            item = tools[i + 1],
            selected = selectedTools.contains(tools[i + 1].id),
            onClick = { onToggleTool(tools[i + 1].id) },
            modifier = Modifier.weight(1f)
          )
        } else {
          Spacer(modifier = Modifier.weight(1f))
        }
      }
    }
  }
}

@Composable
private fun ToolGlassCard(
  item: OnboardingToolItem,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val shape = RoundedCornerShape(20.dp)

  Box(
    modifier = modifier
      .height(130.dp)
      .bouncyClick(onClick = onClick)
      .shadow(
        elevation = if (selected) 6.dp else 2.dp,
        shape = shape,
        ambientColor = if (selected) DesignTokens.SignalGlowColor else Color.Black.copy(alpha = 0.05f),
        spotColor = if (selected) DesignTokens.SignalBlue else Color.Transparent
      )
      .clip(shape)
      .background(
        if (selected) Color(0xFFEFF6FF)
        else Color.White.copy(alpha = 0.88f)
      )
      .border(
        width = if (selected) 2.dp else 1.dp,
        color = if (selected) DesignTokens.SignalBlue else Color(0xFFE2E8F0),
        shape = shape
      )
      .padding(14.dp)
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(item.iconBg),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = item.iconTint,
            modifier = Modifier.size(24.dp)
          )
        }

        // Animated checkmark badge with overshoot bounce
        OvershootCheckBadge(selected = selected)
      }

      Column {
        Text(
          text = item.title,
          fontSize = 15.sp,
          fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
          color = if (selected) DesignTokens.SignalBlue else DesignTokens.InkPrimary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = item.subtitle,
          fontSize = 11.sp,
          color = DesignTokens.InkMuted,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}
