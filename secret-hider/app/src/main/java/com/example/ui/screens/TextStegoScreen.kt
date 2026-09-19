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
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.font.FontFamily
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
import kotlin.random.Random

@Composable
fun TextStegoScreen(
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

  // Quick Gen-Z & realistic covers
  val quickCovers = remember {
    listOf(
      "bro look at this syllabus 💀",
      "did u see what just dropped? 👀",
      "fav study playlist on spotify 🎧",
      "mom said dinner is ready 🍕",
      "running late, be there in 5! 🏃💨",
      "hey did u finish question 4 on homework? 📚",
      "that movie was actually so good omg 🍿",
      "let me know when u reach home safely ✨",
      "sending the updated meeting deck for review 📊",
      "can you check this link really quick? 🔗"
    )
  }

  // Quick secret templates
  val quickSecrets = remember {
    listOf(
      "Lowkey have feelings for u... 💌",
      "Wi-Fi Password: SuperGuest#2026 📶",
      "Seed Phrase: river neon delta eagle sunset 🔑",
      "Surprise birthday party tonight @ 8:30pm 🎁",
      "Private Discord invite: /stealth-drop 👾"
    )
  }

  // Encode state
  var coverSentence by remember { mutableStateOf(quickCovers[0]) }
  var secretMessage by remember { mutableStateOf("") }
  var addPassword by remember { mutableStateOf(false) }
  var password by remember { mutableStateOf("") }

  // Result state
  var encodedOutput by remember { mutableStateOf("") }
  var showResultScreen by remember { mutableStateOf(false) }
  var showXRay by remember { mutableStateOf(false) }

  // Decode state
  var textToDecode by remember { mutableStateOf("") }
  var decodePassword by remember { mutableStateOf("") }
  var decodedSecret by remember { mutableStateOf<String?>(null) }
  var decodeError by remember { mutableStateOf<String?>(null) }

  if (showHelpModal) {
    InfoHelpBottomSheet(
      title = "Hide in Text 📝",
      emoji = "📝",
      description = "Zero-Width Steganography embeds secret text invisibly between normal words using invisible Unicode characters (U+200B, U+200C). Completely invisible on WhatsApp, Discord, iMessage, and Instagram.",
      steps = listOf(
        "Pick or Roll a Cover Text" to "This is what anyone reading the message sees normally.",
        "Type your secret" to "The secret message is converted to invisible zero-width bits.",
        "Optional Password" to "Adds AES-style key protection for top-secret drops.",
        "Copy & Send" to "Paste directly into any chat. Recipient uses Decode tab to reveal in 1 tap!"
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
    // Top Bar
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
        modifier = Modifier.testTag("text_back_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = MaterialTheme.colorScheme.onBackground
        )
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = if (showResultScreen) "Secret Hidden 📝" else "Hide in Text 📝",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp
          ),
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = "Invisible Text Steganography",
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
        modifier = Modifier.testTag("text_help_button")
      ) {
        Icon(
          imageVector = Icons.Default.HelpOutline,
          contentDescription = "Help",
          tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // Main Content
    if (showResultScreen) {
      // Message Hidden Result Screen (Gen-Z High Polish)
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
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Neon Success Badge
            Box(
              modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(Color(0xFFDCFCE7)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = Color(0xFF16A34A),
                modifier = Modifier.size(40.dp)
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = "Secret Invisibly Embedded 📝",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Stealth Status Badge
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = Color(0xFF052E16),
              modifier = Modifier.padding(vertical = 4.dp)
            ) {
              Text(
                text = "READY TO SHARE • INVISIBLE",
                color = Color(0xFF4ADE80),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
              )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "CARRIER TEXT (LOOKS NORMAL):",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
              )
              Text(
                text = "Invisible",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
              shape = RoundedCornerShape(16.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = encodedOutput,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(16.dp)
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // X-Ray Visual Inspection toggle
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "X-Ray Mode (inspect invisible bits)",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              TextButton(onClick = { showXRay = !showXRay }) {
                Text(
                  text = if (showXRay) "Hide Matrix ✕" else "Matrix X-Ray 🔍",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }

            if (showXRay) {
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF090D14),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22C55E)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Text(
                    text = "ZERO-WIDTH BINARY INSPECTOR:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF22C55E)
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = StegoEngine.visualizeZeroWidth(encodedOutput),
                    color = Color(0xFF38BDF8),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 16.sp
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Primary Action: Direct Native Share
            Button(
              onClick = {
                val sendIntent = android.content.Intent().apply {
                  action = android.content.Intent.ACTION_SEND
                  putExtra(android.content.Intent.EXTRA_TEXT, encodedOutput)
                  type = "text/plain"
                }
                context.startActivity(android.content.Intent.createChooser(sendIntent, "Share Message via"))
              },
              shape = RoundedCornerShape(16.dp),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
              modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("text_share_result_button")
            ) {
              Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text("Share Message 📝", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                  Toast.makeText(context, "Copied text! Ready to paste 📋", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = MaterialTheme.colorScheme.surfaceVariant,
                  contentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.weight(1f).height(46.dp).testTag("text_copy_result_button")
              ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Copy", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
              }

              Button(
                onClick = {
                  VaultRepository.addItem(
                    VaultItem(
                      title = "Text: ${coverSentence.take(24)}...",
                      type = StegoType.TEXT,
                      carrierSnippet = encodedOutput,
                      hiddenPayload = secretMessage,
                      isLockedWithPasscode = addPassword && password.isNotBlank(),
                      passcodeHint = if (addPassword) "Passcode protected" else ""
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
                modifier = Modifier.weight(1f).height(46.dp).testTag("text_create_another_button")
              ) {
                Text("New ↺", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
              }
            }
          }
        }
      }
    } else {
      // Main Form View with Tab Switch
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(scrollState)
          .dismissKeyboardOnTap(focusManager, keyboardController)
          .padding(horizontal = 20.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Gen-Z Segmented Switch (Encode 🔒 | Decode 🔓)
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
                .testTag("text_tab_encode")
            ) {
              Box(
                modifier = Modifier.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "Encode 🔒 (Hide)",
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
                .testTag("text_tab_decode")
            ) {
              Box(
                modifier = Modifier.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "Decode 🔓 (Reveal)",
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
              // COVER TEXT SECTION
              Column {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(
                      text = "COVER TEXT (WHAT PEOPLE SEE)",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      letterSpacing = 1.sp
                    )
                    Text(
                      text = "The harmless cover message",
                      fontSize = 12.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                  }

                  // 1-Tap Roll Dice button
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    modifier = Modifier
                      .clickable {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        val randomIndex = Random.nextInt(quickCovers.size)
                        coverSentence = quickCovers[randomIndex]
                        Toast.makeText(context, "Rolled fresh cover text! 🎲", Toast.LENGTH_SHORT).show()
                      }
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = "Roll",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                      Text(
                        text = "Roll 🎲",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Cover Horizontal Chips (Gen-Z instant presets)
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  quickCovers.take(5).forEach { sample ->
                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = if (coverSentence == sample) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                      border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (coverSentence == sample) MaterialTheme.colorScheme.primary else Color.Transparent
                      ),
                      modifier = Modifier.clickable {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        coverSentence = sample
                      }
                    ) {
                      Text(
                        text = sample,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                  value = coverSentence,
                  onValueChange = { coverSentence = it },
                  placeholder = { Text("Enter or select cover sentence...") },
                  singleLine = true,
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
                    .testTag("text_cover_input")
                )
              }

              // SECRET MESSAGE SECTION
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
                      text = "What will be hidden invisibly",
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

                // Quick Secret Preset Chips
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
                  onValueChange = { if (it.length <= 5000) secretMessage = it },
                  placeholder = { Text("Type your secret drop here...") },
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
                    .testTag("text_secret_input")
                )

                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  // Live Stealth Meter
                  if (secretMessage.isNotBlank()) {
                    Text(
                      text = "⚡ Stealth Score: S-Tier (100% undetected)",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF16A34A)
                    )
                  } else {
                    Spacer(modifier = Modifier.width(1.dp))
                  }

                  Text(
                    text = "${secretMessage.length} / 5,000",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              // Password Protection Switch
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(14.dp))
                  .clickable {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    addPassword = !addPassword
                  }
                  .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Text(
                    text = "Add secret password 🔒",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "Optional passphrase lock for extra stealth",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                Switch(
                  checked = addPassword,
                  onCheckedChange = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    addPassword = it
                  },
                  colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MaterialTheme.colorScheme.primary
                  )
                )
              }

              if (addPassword) {
                OutlinedTextField(
                  value = password,
                  onValueChange = { password = it },
                  label = { Text("Secret Password") },
                  placeholder = { Text("Enter passphrase...") },
                  singleLine = true,
                  shape = RoundedCornerShape(14.dp),
                  keyboardOptions = autoDismissKeyboardOptions(),
                  keyboardActions = autoDismissKeyboardActions(focusManager, keyboardController),
                  modifier = Modifier.fillMaxWidth()
                )
              }

              Spacer(modifier = Modifier.height(4.dp))

              // Big Button: Hide My Message
              Button(
                onClick = {
                  focusManager.clearFocus()
                  keyboardController?.hide()
                  if (secretMessage.isBlank()) {
                    Toast.makeText(context, "Please enter a secret message to hide", Toast.LENGTH_SHORT).show()
                    return@Button
                  }
                  val cover = coverSentence.ifBlank { "Hello friend" }
                  val pass = if (addPassword) password.trim() else ""
                  encodedOutput = StegoEngine.encodeInText(cover, secretMessage, pass)
                  showResultScreen = true
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(54.dp)
                  .testTag("text_encode_button")
              ) {
                Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Hide My Message 🔒",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        } else {
          // DECODE TAB
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
                    text = "PASTE TEXT TO REVEAL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                  )
                  Text(
                    text = "Instant extraction of zero-width secret",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                  )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  Button(
                    onClick = {
                      focusManager.clearFocus()
                      keyboardController?.hide()
                      val clip = clipboardManager.getText()?.text
                      if (!clip.isNullOrBlank()) {
                        textToDecode = clip
                        Toast.makeText(context, "Pasted from clipboard! 📋", Toast.LENGTH_SHORT).show()
                      } else {
                        Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
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
              }

              OutlinedTextField(
                value = textToDecode,
                onValueChange = {
                  textToDecode = it
                  decodedSecret = null
                  decodeError = null
                },
                placeholder = { Text("Paste the received text message here...") },
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
                  .testTag("text_decode_input")
              )

              OutlinedTextField(
                value = decodePassword,
                onValueChange = { decodePassword = it },
                label = { Text("Password (if passcode protected)") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = autoDismissKeyboardOptions(),
                keyboardActions = autoDismissKeyboardActions(focusManager, keyboardController),
                modifier = Modifier.fillMaxWidth()
              )

              Button(
                onClick = {
                  focusManager.clearFocus()
                  keyboardController?.hide()
                  if (textToDecode.isBlank()) {
                    Toast.makeText(context, "Please paste text to decode", Toast.LENGTH_SHORT).show()
                    return@Button
                  }
                  val extracted = StegoEngine.decodeFromText(textToDecode, decodePassword.trim())
                  if (extracted.isNotBlank()) {
                    decodedSecret = extracted
                    decodeError = null
                  } else {
                    decodedSecret = null
                    decodeError = "No hidden secret found. If passcode protected, check password."
                  }
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(54.dp)
                  .testTag("text_decode_button")
              ) {
                Text(
                  text = "Reveal Secret 🔓",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              // Decoded output display (Gen-Z Success Card)
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
                        text = "REVEALED SECRET 🔓",
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
                        Toast.makeText(context, "Secret copied to clipboard!", Toast.LENGTH_SHORT).show()
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
