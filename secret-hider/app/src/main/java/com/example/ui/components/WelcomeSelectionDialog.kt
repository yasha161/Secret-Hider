package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun WelcomeSelectionDialog(
  initialUserName: String,
  onDismissToDashboard: (String) -> Unit,
  onOptionSelected: (targetScreen: String, updatedName: String) -> Unit
) {
  var nameInput by remember { mutableStateOf(initialUserName) }
  var selectedChoice by remember { mutableStateOf<String?>("emoji") }

  Dialog(
    onDismissRequest = {
      val finalName = if (nameInput.isNotBlank()) nameInput.trim() else initialUserName
      onDismissToDashboard(finalName)
    },
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .clip(RoundedCornerShape(26.dp)),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      shadowElevation = 8.dp,
      border = androidx.compose.foundation.BorderStroke(
        1.dp,
        MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
      )
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Dialog Header with Icon
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "Welcome",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp)
            )
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Welcome to Secret Hider! ✨",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Aapka stealth privacy assistant",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Name input prompt
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = "Aapka Name:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            placeholder = { Text("Apna naam likhiye (e.g. Yash)") },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
              )
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("welcome_name_input"),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = MaterialTheme.colorScheme.background,
              unfocusedContainerColor = MaterialTheme.colorScheme.background
            )
          )
        }

        // Question: Message kisme chupana chahoge?
        Text(
          text = "Aapko kya chahiye? Message kisme chupana chahoge?",
          style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          ),
          color = MaterialTheme.colorScheme.primary
        )

        // 3 Options to choose from
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          WelcomeOptionCard(
            title = "Hide in Emoji 🤫",
            subtitle = "Unicode tags se emoji ke peeche message chupayein",
            icon = Icons.Default.Mood,
            badge = "Popular",
            isSelected = selectedChoice == "emoji",
            iconTint = Color(0xFFCA8A04),
            iconBg = Color(0xFFFEF08A),
            onClick = { selectedChoice = "emoji" },
            tag = "option_emoji"
          )

          WelcomeOptionCard(
            title = "Hide in Plain Text 📝",
            subtitle = "Casual sentence ke andar zero-width space se hide karein",
            icon = Icons.Default.TextFields,
            badge = "Stealth",
            isSelected = selectedChoice == "text",
            iconTint = Color(0xFFDC2626),
            iconBg = Color(0xFFFEE2E2),
            onClick = { selectedChoice = "text" },
            tag = "option_text"
          )

          WelcomeOptionCard(
            title = "Hide in Image Pixels 🖼️",
            subtitle = "PNG photos ke RGB pixels me LSB se encrypt karein",
            icon = Icons.Default.Image,
            badge = "LSB Bit",
            isSelected = selectedChoice == "image",
            iconTint = Color(0xFF16A34A),
            iconBg = Color(0xFFDCFCE7),
            onClick = { selectedChoice = "image" },
            tag = "option_image"
          )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Action button: Open Chosen Option
        Button(
          onClick = {
            val finalName = if (nameInput.isNotBlank()) nameInput.trim() else initialUserName
            val target = selectedChoice ?: "dashboard"
            onOptionSelected(target, finalName)
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("welcome_proceed_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
          Text(
            text = when (selectedChoice) {
              "emoji" -> "Continue with Emoji Tool 👉"
              "text" -> "Continue with Text Tool 👉"
              "image" -> "Continue with Image Tool 👉"
              else -> "Continue 👉"
            },
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
          )
        }

        HorizontalDivider(
          color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f),
          thickness = 1.dp
        )

        // Bottom option: Direct goes in dashboard
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          TextButton(
            onClick = {
              val finalName = if (nameInput.isNotBlank()) nameInput.trim() else initialUserName
              onDismissToDashboard(finalName)
            },
            modifier = Modifier.testTag("welcome_direct_dashboard_button")
          ) {
            Icon(
              imageVector = Icons.Default.Dashboard,
              contentDescription = "Dashboard",
              modifier = Modifier.size(16.dp),
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Direct Go to Dashboard 🏠",
              style = MaterialTheme.typography.bodyMedium.copy(
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
private fun WelcomeOptionCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  badge: String,
  isSelected: Boolean,
  iconTint: Color,
  iconBg: Color,
  onClick: () -> Unit,
  tag: String
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag(tag),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
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
        .padding(horizontal = 12.dp, vertical = 10.dp),
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
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(iconBg),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
          )
        }

        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = title,
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.surfaceVariant
            ) {
              Text(
                text = badge,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
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
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
          )
        }
      }

      Box(
        modifier = Modifier
          .size(20.dp)
          .clip(CircleShape)
          .border(
            width = if (isSelected) 5.dp else 1.5.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            shape = CircleShape
          )
          .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
      )
    }
  }
}
