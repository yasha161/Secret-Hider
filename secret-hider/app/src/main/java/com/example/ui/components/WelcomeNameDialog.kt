package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardHide
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppLanguage

@Composable
fun WelcomeNameDialog(
  initialUserName: String,
  selectedLanguage: AppLanguage,
  onChangeLanguage: () -> Unit,
  onProceed: (userName: String, targetScreen: String) -> Unit,
  onDismissToDashboard: (userName: String) -> Unit
) {
  var nameInput by remember { mutableStateOf(initialUserName) }
  var selectedChoice by remember { mutableStateOf("emoji") }
  val focusManager = LocalFocusManager.current
  val keyboardController = LocalSoftwareKeyboardController.current

  Dialog(
    onDismissRequest = {
      focusManager.clearFocus()
      keyboardController?.hide()
      val finalName = if (nameInput.isNotBlank()) nameInput.trim() else initialUserName
      onDismissToDashboard(finalName)
    },
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .clip(RoundedCornerShape(26.dp))
        .border(
          width = 1.2.dp,
          brush = Brush.linearGradient(
            listOf(
              MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
              MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
              MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
          ),
          shape = RoundedCornerShape(26.dp)
        ),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 8.dp,
      shadowElevation = 18.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
          ) {
            focusManager.clearFocus()
            keyboardController?.hide()
          }
          .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Top row with Language indicator tag
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF10B981))
              )
              Text(
                text = "100% Client-Side Privacy",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.primary
                )
              )
            }
          }

          // Language button to change language if desired
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .clickable { onChangeLanguage() }
              .testTag("name_dialog_language_btn"),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
            )
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(text = selectedLanguage.flagEmoji, fontSize = 12.sp)
              Text(
                text = selectedLanguage.code.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              )
              Icon(
                imageVector = Icons.Default.Translate,
                contentDescription = "Language",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(12.dp)
              )
            }
          }
        }

        // Header matching the user screenshot: Lock icon + Welcome to Secret Hider
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Box(
            modifier = Modifier
              .size(50.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(
                Brush.linearGradient(
                  listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.tertiary
                  )
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = "Secret Hider Lock",
              tint = Color.White,
              modifier = Modifier.size(28.dp)
            )
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Welcome to Secret Hider",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Zero-trace steganography & encryption suite",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        HorizontalDivider(
          color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
          thickness = 1.dp
        )

        // 1. What is your name?
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "1",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  fontSize = 11.sp
                )
              )
            }
            Text(
              text = "What is your name?",
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.5.sp
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            placeholder = { Text("Enter your name (e.g., Alex, Yash)") },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("name_dialog_input"),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
              focusManager.clearFocus()
              keyboardController?.hide()
            }),
            trailingIcon = {
              if (nameInput.isNotEmpty()) {
                IconButton(
                  onClick = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                  }
                ) {
                  Icon(
                    imageVector = Icons.Default.KeyboardHide,
                    contentDescription = "Hide Keyboard",
                    tint = MaterialTheme.colorScheme.primary
                  )
                }
              }
            },
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = MaterialTheme.colorScheme.background,
              unfocusedContainerColor = MaterialTheme.colorScheme.background
            )
          )
        }

        // 2. Where would you like to start?
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "2",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  fontSize = 11.sp
                )
              )
            }
            Text(
              text = "Choose tool to start with:",
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          // Compact Tool Choice Cards
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            CompactChoiceCard(
              title = "Hide in Emoji",
              subtitle = "Unicode tag steganography",
              badge = "Popular",
              icon = Icons.Default.Mood,
              iconColor = Color(0xFFCA8A04),
              iconBg = Color(0xFFFEF08A),
              isSelected = selectedChoice == "emoji",
              onClick = {
                focusManager.clearFocus()
                keyboardController?.hide()
                selectedChoice = "emoji"
              },
              tag = "choice_emoji"
            )

            CompactChoiceCard(
              title = "Hide in Plain Text",
              subtitle = "Zero-width space cipher",
              badge = "Stealth",
              icon = Icons.Default.TextFields,
              iconColor = Color(0xFFDC2626),
              iconBg = Color(0xFFFEE2E2),
              isSelected = selectedChoice == "text",
              onClick = {
                focusManager.clearFocus()
                keyboardController?.hide()
                selectedChoice = "text"
              },
              tag = "choice_text"
            )

            CompactChoiceCard(
              title = "Hide in Image Pixels",
              subtitle = "LSB RGB pixel encryption",
              badge = "LSB Bit",
              icon = Icons.Default.Image,
              iconColor = Color(0xFF16A34A),
              iconBg = Color(0xFFDCFCE7),
              isSelected = selectedChoice == "image",
              onClick = {
                focusManager.clearFocus()
                keyboardController?.hide()
                selectedChoice = "image"
              },
              tag = "choice_image"
            )
          }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Action Buttons
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Button(
            onClick = {
              focusManager.clearFocus()
              keyboardController?.hide()
              val finalName = if (nameInput.isNotBlank()) nameInput.trim() else initialUserName
              onProceed(finalName, selectedChoice)
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("name_dialog_continue_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary
            )
          ) {
            Text(
              text = when (selectedChoice) {
                "emoji" -> "Open Emoji Steganography 👉"
                "text" -> "Open Text Steganography 👉"
                "image" -> "Open Image Steganography 👉"
                else -> "Get Started 👉"
              },
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            )
          }

          // Direct go to dashboard button
          TextButton(
            onClick = {
              val finalName = if (nameInput.isNotBlank()) nameInput.trim() else initialUserName
              onDismissToDashboard(finalName)
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("name_dialog_dashboard_button")
          ) {
            Icon(
              imageVector = Icons.Default.Dashboard,
              contentDescription = "Dashboard",
              modifier = Modifier.size(16.dp),
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Skip Directly to Dashboard 🏠",
              style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
        }
      }
    }
  }
}

@Composable
private fun CompactChoiceCard(
  title: String,
  subtitle: String,
  badge: String,
  icon: ImageVector,
  iconColor: Color,
  iconBg: Color,
  isSelected: Boolean,
  onClick: () -> Unit,
  tag: String
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .testTag(tag),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
      else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ),
    border = androidx.compose.foundation.BorderStroke(
      width = if (isSelected) 1.5.dp else 1.dp,
      color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(iconBg),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconColor,
            modifier = Modifier.size(18.dp)
          )
        }

        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = title,
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = MaterialTheme.colorScheme.surfaceVariant
            ) {
              Text(
                text = badge,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
          Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Box(
        modifier = Modifier
          .size(18.dp)
          .clip(CircleShape)
          .border(
            width = if (isSelected) 4.5.dp else 1.5.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
            shape = CircleShape
          )
          .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
      )
    }
  }
}
