package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VaultRepository
import com.example.model.StegoType
import com.example.model.VaultItem
import com.example.stego.StegoEngine
import com.example.ui.components.InfoHelpBottomSheet
import com.example.ui.components.KeyboardToolRow
import com.example.ui.components.autoDismissKeyboardActions
import com.example.ui.components.autoDismissKeyboardOptions
import com.example.ui.components.dismissKeyboardOnTap

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.R
import com.example.ui.theme.DesignTokens

private data class EmojiVibePreset(val name: String, val emojis: List<String>)

@Composable
fun EmojiStegoScreen(
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val focusManager = LocalFocusManager.current
  val keyboardController = LocalSoftwareKeyboardController.current
  val scrollState = rememberScrollState()

  // Auto-remove keyboard whenever scrolling begins
  LaunchedEffect(scrollState.isScrollInProgress) {
    if (scrollState.isScrollInProgress) {
      focusManager.clearFocus()
      keyboardController?.hide()
    }
  }

  var isDecodeTab by remember { mutableStateOf(false) }
  var showHelpModal by remember { mutableStateOf(false) }

  // 1 to 3 Carrier Emoji combo list (Mockup Screens 6 & 7)
  val availableEmojis = remember {
    listOf(
      "⚡", "🚀", "🪐", "🔮", "👾", "💎", "🤫", "🔥", "🧬", "✨",
      "💀", "🛸", "🕶️", "🥷", "🌌", "❤️‍🔥", "🍓", "🌸", "🍕", "🍀"
    )
  }
  val selectedEmojis = remember { mutableStateListOf("⚡", "🚀", "🔮") }

  // Gen-Z & Next-Gen Cyber Vibe Presets
  val vibePresets = remember {
    listOf(
      EmojiVibePreset("Quantum Cyber ⚡🚀", listOf("⚡", "🚀", "🪐")),
      EmojiVibePreset("Cyber Oracle 🔮✨", listOf("🔮", "✨", "💎")),
      EmojiVibePreset("Deep Stealth 🕶️🤫", listOf("🕶️", "🤫", "🥷")),
      EmojiVibePreset("Neon Drip 🔥❤️‍🔥", listOf("🔥", "❤️‍🔥", "🍓")),
      EmojiVibePreset("Matrix Glitch 👾🧬", listOf("👾", "🧬", "💀")),
      EmojiVibePreset("Alien Zone 🛸🪐", listOf("🛸", "🪐", "🌌"))
    )
  }

  // Quick secret templates
  val quickSecrets = remember {
    listOf(
      "Meet me at 9PM behind the café 🤫",
      "Lowkey have a massive crush on u 💌",
      "Discord secret drop link: /ghost-drop 👾",
      "Wi-Fi key: NeonSky#8821 📶"
    )
  }

  var secretMessage by remember { mutableStateOf("") }

  // Result state (Screen 10)
  var encodedOutput by remember { mutableStateOf("") }
  var showResultScreen by remember { mutableStateOf(false) }

  // Decode state
  var emojiToDecode by remember { mutableStateOf("") }
  var decodedSecret by remember { mutableStateOf<String?>(null) }
  var decodeError by remember { mutableStateOf<String?>(null) }

  if (showHelpModal) {
    InfoHelpBottomSheet(
      title = "Hide in Emoji 🤫",
      emoji = "🤫",
      description = "Emoji Steganography embeds secret text invisibly behind 1 to 3 standard emojis using Unicode Variation Selectors and Unicode Tag Blocks. It looks like standard emojis to everyone!",
      steps = listOf(
        "Select 1 to 3 Emojis" to "Pick an aesthetic combo or choose a 1-tap Vibe Preset.",
        "Type your Secret" to "Your message is attached invisibly into the emoji's Unicode payload.",
        "Copy Anywhere" to "Send via WhatsApp, Discord, or Instagram DM.",
        "Decode Instantly" to "Recipient pastes the emojis into the Decode tab to reveal."
      ),
      onDismiss = { showHelpModal = false }
    )
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .dismissKeyboardOnTap(focusManager, keyboardController)
  ) {
    // Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = {
          focusManager.clearFocus()
          keyboardController?.hide()
          if (showResultScreen) {
            showResultScreen = false
          } else {
            onBack()
          }
        },
        modifier = Modifier.testTag("emoji_back_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = MaterialTheme.colorScheme.onBackground
        )
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = if (showResultScreen) "Secret Hidden 🤫" else "Hide in Emoji 🤫",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp
          ),
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = "Emoji Carrier Steganography",
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.primary
        )
      }

      IconButton(
        onClick = {
          focusManager.clearFocus()
          keyboardController?.hide()
          showHelpModal = true
        },
        modifier = Modifier.testTag("emoji_help_button")
      ) {
        Icon(
          imageVector = Icons.Default.HelpOutline,
          contentDescription = "Help",
          tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    if (showResultScreen) {
      // Result Screen (Screen 10)
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(scrollState)
          .dismissKeyboardOnTap(focusManager, keyboardController)
          .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Card(
          shape = RoundedCornerShape(26.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Hidden",
                tint = Color.White,
                modifier = Modifier.size(38.dp)
              )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
              text = "Secret Hidden in Emoji 🤫",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
              shape = RoundedCornerShape(20.dp),
              color = Color(0xFF052E16),
              modifier = Modifier.padding(vertical = 4.dp)
            ) {
              Text(
                text = "READY TO SHARE • STEALTH",
                color = Color(0xFF4ADE80),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
              )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Large Carrier Preview
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Box(
                modifier = Modifier.padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = encodedOutput,
                  fontSize = 44.sp,
                  letterSpacing = 8.sp,
                  modifier = Modifier.testTag("emoji_result_display")
                )
              }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Action: Direct Native Share
            Button(
              onClick = {
                val sendIntent = android.content.Intent().apply {
                  action = android.content.Intent.ACTION_SEND
                  putExtra(android.content.Intent.EXTRA_TEXT, encodedOutput)
                  type = "text/plain"
                }
                context.startActivity(android.content.Intent.createChooser(sendIntent, "Share Emoji via"))
              },
              shape = RoundedCornerShape(16.dp),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
              modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("emoji_share_result_button")
            ) {
              Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Share Emoji 🤫", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Secondary Actions: Copy, Vault, New
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Button(
                onClick = {
                  clipboardManager.setText(AnnotatedString(encodedOutput))
                  Toast.makeText(context, "Copied carrier emoji! Ready to paste 📋", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = MaterialTheme.colorScheme.surfaceVariant,
                  contentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.weight(1f).height(46.dp).testTag("emoji_copy_result_button")
              ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Copy", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
              }

              Button(
                onClick = {
                  VaultRepository.addItem(
                    VaultItem(
                      title = "Emoji: ${selectedEmojis.joinToString("")}",
                      type = StegoType.EMOJI,
                      carrierSnippet = encodedOutput,
                      hiddenPayload = secretMessage
                    )
                  )
                  Toast.makeText(context, "Saved to Secret Vault 🔐", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = MaterialTheme.colorScheme.surfaceVariant,
                  contentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.weight(1f).height(46.dp)
              ) {
                Text("Vault 🔐", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
              }

              Button(
                onClick = {
                  showResultScreen = false
                  secretMessage = ""
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = MaterialTheme.colorScheme.surfaceVariant,
                  contentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.weight(1f).height(46.dp).testTag("emoji_create_another_button")
              ) {
                Text("New ↺", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
              }
            }
          }
        }
      }
    } else {
      // Main Form View
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(scrollState)
          .dismissKeyboardOnTap(focusManager, keyboardController)
          .padding(horizontal = 20.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Tab Switcher
        Surface(
          shape = RoundedCornerShape(24.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = if (!isDecodeTab) MaterialTheme.colorScheme.surface else Color.Transparent,
              shadowElevation = if (!isDecodeTab) 3.dp else 0.dp,
              modifier = Modifier
                .weight(1f)
                .clickable {
                  focusManager.clearFocus()
                  keyboardController?.hide()
                  isDecodeTab = false
                }
                .testTag("emoji_tab_encode")
            ) {
              Box(
                modifier = Modifier.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "Encode 🤫 (Hide)",
                  fontSize = 14.sp,
                  fontWeight = if (!isDecodeTab) FontWeight.Bold else FontWeight.Medium,
                  color = if (!isDecodeTab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(20.dp),
              color = if (isDecodeTab) MaterialTheme.colorScheme.surface else Color.Transparent,
              shadowElevation = if (isDecodeTab) 3.dp else 0.dp,
              modifier = Modifier
                .weight(1f)
                .clickable {
                  focusManager.clearFocus()
                  keyboardController?.hide()
                  isDecodeTab = true
                }
                .testTag("emoji_tab_decode")
            ) {
              Box(
                modifier = Modifier.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "Decode 🔓 (Extract)",
                  fontSize = 14.sp,
                  fontWeight = if (isDecodeTab) FontWeight.Bold else FontWeight.Medium,
                  color = if (isDecodeTab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }

        if (!isDecodeTab) {
          // ENCODE CARD
          Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(20.dp),
              verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              // 1. Vibe Presets Row
              Column {
                Text(
                  text = "GEN-Z VIBE COMBOS (1-TAP)",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  vibePresets.forEach { preset ->
                    Surface(
                      shape = RoundedCornerShape(12.dp),
                      color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                      modifier = Modifier.clickable {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        selectedEmojis.clear()
                        selectedEmojis.addAll(preset.emojis)
                      }
                    ) {
                      Text(
                        text = preset.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                      )
                    }
                  }
                }
              }

              // 2. Emoji Picker Grid
              Column {
                Text(
                  text = "CARRIER EMOJIS (PICK 1 TO 3)",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                // 2 rows of 10 extraordinary emojis with horizontal scroll
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                  verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableEmojis.take(10).forEach { emoji ->
                      val isSelected = selectedEmojis.contains(emoji)
                      Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(
                          width = if (isSelected) 2.dp else 1.dp,
                          color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                        ),
                        modifier = Modifier
                          .size(42.dp)
                          .clickable {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            if (isSelected) {
                              if (selectedEmojis.size > 1) selectedEmojis.remove(emoji)
                            } else {
                              if (selectedEmojis.size < 3) selectedEmojis.add(emoji)
                              else {
                                selectedEmojis.removeAt(0)
                                selectedEmojis.add(emoji)
                              }
                            }
                          }
                      ) {
                        Box(contentAlignment = Alignment.Center) {
                          Text(text = emoji, fontSize = 22.sp)
                        }
                      }
                    }
                  }

                  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableEmojis.drop(10).forEach { emoji ->
                      val isSelected = selectedEmojis.contains(emoji)
                      Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(
                          width = if (isSelected) 2.dp else 1.dp,
                          color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                        ),
                        modifier = Modifier
                          .size(42.dp)
                          .clickable {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            if (isSelected) {
                              if (selectedEmojis.size > 1) selectedEmojis.remove(emoji)
                            } else {
                              if (selectedEmojis.size < 3) selectedEmojis.add(emoji)
                              else {
                                selectedEmojis.removeAt(0)
                                selectedEmojis.add(emoji)
                              }
                            }
                          }
                      ) {
                        Box(contentAlignment = Alignment.Center) {
                          Text(text = emoji, fontSize = 22.sp)
                        }
                      }
                    }
                  }
                }
              }

              // Selected Combo Pill
              Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Text(
                      text = "Active Carrier:",
                      fontSize = 13.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = selectedEmojis.joinToString(" "),
                      fontSize = 22.sp
                    )
                  }

                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary
                  ) {
                    Text(
                      text = "${selectedEmojis.size}/3 ready",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color.White,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                  }
                }
              }

              // Secret message input
              Column {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(
                      text = "SECRET MESSAGE",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      letterSpacing = 1.sp
                    )
                    Text(
                      text = "Invisibly bundled with the emojis",
                      fontSize = 12.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                  }

                  // 1-Tap Keyboard Dismiss & Clear Bar
                  KeyboardToolRow(
                    onDismiss = {
                      focusManager.clearFocus()
                      keyboardController?.hide()
                    },
                    onClear = if (secretMessage.isNotEmpty()) {
                      { secretMessage = "" }
                    } else null
                  )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Secret Templates
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  quickSecrets.forEach { preset ->
                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                      modifier = Modifier.clickable {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        secretMessage = preset
                      }
                    ) {
                      Text(
                        text = preset,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                  value = secretMessage,
                  onValueChange = { if (it.length <= 2000) secretMessage = it },
                  placeholder = { Text("Type your secret message here...") },
                  minLines = 4,
                  shape = RoundedCornerShape(16.dp),
                  keyboardOptions = autoDismissKeyboardOptions(),
                  keyboardActions = autoDismissKeyboardActions(focusManager, keyboardController),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                  ),
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("emoji_secret_input")
                )

                Text(
                  text = "${secretMessage.length} / 2,000",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 4.dp)
                )
              }

              // Big Button: Hide in Emoji
              Button(
                onClick = {
                  focusManager.clearFocus()
                  keyboardController?.hide()
                  if (secretMessage.isBlank()) {
                    Toast.makeText(context, "Please enter a secret message to hide", Toast.LENGTH_SHORT).show()
                    return@Button
                  }
                  val carrier = selectedEmojis.joinToString("")
                  encodedOutput = StegoEngine.encodeInEmoji(carrier, secretMessage)
                  showResultScreen = true
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(54.dp)
                  .testTag("emoji_encode_button")
              ) {
                Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Hide in Emoji 🤫",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        } else {
          // DECODE CARD
          Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(20.dp),
              verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = "PASTE EMOJI TO EXTRACT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                  )
                  Text(
                    text = "Extract the hidden secret from carrier emojis",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                  )
                }

                Button(
                  onClick = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    val clip = clipboardManager.getText()?.text
                    if (!clip.isNullOrBlank()) {
                      emojiToDecode = clip
                      Toast.makeText(context, "Pasted from clipboard! 📋", Toast.LENGTH_SHORT).show()
                    }
                  },
                  shape = RoundedCornerShape(12.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    contentColor = MaterialTheme.colorScheme.primary
                  ),
                  modifier = Modifier.height(36.dp)
                ) {
                  Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Paste 📋", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              }

              OutlinedTextField(
                value = emojiToDecode,
                onValueChange = {
                  emojiToDecode = it
                  decodedSecret = null
                  decodeError = null
                },
                placeholder = { Text("Paste the emoji string here...") },
                minLines = 3,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = autoDismissKeyboardOptions(),
                keyboardActions = autoDismissKeyboardActions(focusManager, keyboardController),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                  unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                  focusedBorderColor = MaterialTheme.colorScheme.primary,
                  unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("emoji_decode_input")
              )

              Button(
                onClick = {
                  focusManager.clearFocus()
                  keyboardController?.hide()
                  if (emojiToDecode.isBlank()) {
                    Toast.makeText(context, "Please paste emoji to decode", Toast.LENGTH_SHORT).show()
                    return@Button
                  }
                  val extracted = StegoEngine.decodeFromEmoji(emojiToDecode)
                  if (extracted.isNotBlank()) {
                    decodedSecret = extracted
                    decodeError = null
                  } else {
                    decodedSecret = null
                    decodeError = "No secret found in this emoji carrier"
                  }
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(54.dp)
                  .testTag("emoji_decode_button")
              ) {
                Text(
                  text = "Extract Secret 🔓",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              if (decodedSecret != null) {
                Surface(
                  shape = RoundedCornerShape(18.dp),
                  color = Color(0xFFDCFCE7),
                  border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF22C55E)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(
                        text = "EXTRACTED SECRET 🔓",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF15803D),
                        letterSpacing = 1.sp
                      )
                      Text(
                        text = "100% Extracted ✨",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF16A34A)
                      )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                      text = decodedSecret!!,
                      fontSize = 16.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = Color(0xFF14532D)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                      onClick = {
                        clipboardManager.setText(AnnotatedString(decodedSecret!!))
                        Toast.makeText(context, "Secret copied!", Toast.LENGTH_SHORT).show()
                      },
                      shape = RoundedCornerShape(12.dp),
                      colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF15803D),
                        contentColor = Color.White
                      ),
                      modifier = Modifier.height(38.dp)
                    ) {
                      Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("Copy Secret", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }

              if (decodeError != null) {
                Surface(
                  shape = RoundedCornerShape(14.dp),
                  color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                  border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Text(
                    text = decodeError!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(14.dp)
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}
