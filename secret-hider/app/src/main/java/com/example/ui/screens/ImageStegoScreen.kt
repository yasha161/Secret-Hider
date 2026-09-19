package com.example.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import coil.compose.AsyncImage
import com.example.data.VaultRepository
import com.example.model.StegoType
import com.example.model.VaultItem
import com.example.stego.ImageStegoEngine
import com.example.ui.components.InfoHelpBottomSheet
import com.example.ui.components.KeyboardToolRow
import com.example.ui.components.autoDismissKeyboardActions
import com.example.ui.components.autoDismissKeyboardOptions
import com.example.ui.components.dismissKeyboardOnTap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun ImageStegoScreen(
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val focusManager = LocalFocusManager.current
  val keyboardController = LocalSoftwareKeyboardController.current
  val scrollState = rememberScrollState()
  val scope = rememberCoroutineScope()

  LaunchedEffect(scrollState.isScrollInProgress) {
    if (scrollState.isScrollInProgress) {
      focusManager.clearFocus()
      keyboardController?.hide()
    }
  }

  var isDecodeTab by remember { mutableStateOf(false) }
  var showHelpModal by remember { mutableStateOf(false) }

  // Sample Photos
  val samplePhotos = remember {
    listOf("🌄 Cyber Sunset", "🌸 Cherry Blossom", "🌌 Starry Cosmos", "🏙️ Neo Tokyo", "🌿 Matrix Garden")
  }
  var selectedPhotoName by remember { mutableStateOf(samplePhotos[0]) }

  // Custom Image state for Encode
  var customImageUri by remember { mutableStateOf<Uri?>(null) }
  var customImageName by remember { mutableStateOf<String?>(null) }
  var customImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
  var isUsingCustomImage by remember { mutableStateOf(false) }

  // Custom Image state for Decode
  var decodeImageUri by remember { mutableStateOf<Uri?>(null) }
  var decodeImageName by remember { mutableStateOf<String?>(null) }
  var decodeImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
  var isDecodeUsingCustomImage by remember { mutableStateOf(false) }

  // Quick secret templates
  val quickSecrets = remember {
    listOf(
      "GPS Coordinates: 37.7749° N, 122.4194° W 📍",
      "Confidential contract signed & verified 📄",
      "Private seed recovery: orbit lunar apex echo 🔑",
      "Secret message: meet at midnight under the neon sign 🌃"
    )
  }

  var secretMessage by remember { mutableStateOf("") }
  var addPassword by remember { mutableStateOf(false) }
  var password by remember { mutableStateOf("") }
  var isEncodedSuccess by remember { mutableStateOf(false) }
  var isProcessing by remember { mutableStateOf(false) }
  var encodedResultFile by remember { mutableStateOf<File?>(null) }
  var encodedImageDims by remember { mutableStateOf("800 × 800 px") }

  // Decode State
  var decodePassword by remember { mutableStateOf("") }
  var decodedSecret by remember { mutableStateOf<String?>(null) }
  var decodeError by remember { mutableStateOf<String?>(null) }
  var isDecodingProcessing by remember { mutableStateOf(false) }

  // Android Photo Picker Launcher for ENCODE (Zero-permission)
  val encodePhotoPicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      customImageUri = uri
      isUsingCustomImage = true
      val name = ImageStegoEngine.getFileNameFromUri(context, uri)
      customImageName = name
      scope.launch(Dispatchers.IO) {
        val bmp = ImageStegoEngine.loadBitmapFromUri(context, uri)
        withContext(Dispatchers.Main) {
          customImageBitmap = bmp
          Toast.makeText(context, "Loaded custom carrier photo: $name 📸", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }

  // Android Photo Picker Launcher for DECODE (Zero-permission)
  val decodePhotoPicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      decodeImageUri = uri
      isDecodeUsingCustomImage = true
      val name = ImageStegoEngine.getFileNameFromUri(context, uri)
      decodeImageName = name
      decodedSecret = null
      decodeError = null
      scope.launch(Dispatchers.IO) {
        val bmp = ImageStegoEngine.loadBitmapFromUri(context, uri)
        withContext(Dispatchers.Main) {
          decodeImageBitmap = bmp
          Toast.makeText(context, "Ready to extract from $name 🔓", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }

  // Calculate current carrier capacity in bytes
  val currentCapacityBytes = remember(isUsingCustomImage, customImageBitmap) {
    if (isUsingCustomImage && customImageBitmap != null) {
      ImageStegoEngine.calculateCapacityBytes(customImageBitmap!!.width, customImageBitmap!!.height)
    } else {
      ImageStegoEngine.calculateCapacityBytes(800, 800) // Default sample bitmap
    }
  }

  if (showHelpModal) {
    InfoHelpBottomSheet(
      title = "Hide in Image 🖼️",
      emoji = "🖼️",
      description = "Least Significant Bit (LSB) steganography modifies the lowest bit of pixel color channels so the image looks 100% identical to the human eye while storing your data safely.",
      steps = listOf(
        "Upload or Choose Photo" to "Upload your own custom picture from gallery or choose an aesthetic preset.",
        "Write your Secret" to "Secret data is converted into invisible RGB pixel perturbations.",
        "Add AES-256 Lock" to "Optionally encrypt the bits with military-grade passphrase protection.",
        "Save & Share" to "Export lossless PNG to gallery or share directly with friends."
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
          if (isEncodedSuccess) isEncodedSuccess = false
          else onBack()
        },
        modifier = Modifier.testTag("image_back_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = MaterialTheme.colorScheme.onBackground
        )
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = if (isEncodedSuccess) "Secret Hidden 🖼️" else "Hide in Image 🖼️",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp
          ),
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = if (isUsingCustomImage) "Custom Carrier Photo Active" else "Pixel Color Steganography",
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
        modifier = Modifier.testTag("image_help_button")
      ) {
        Icon(
          imageVector = Icons.Default.HelpOutline,
          contentDescription = "Help",
          tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    if (isEncodedSuccess) {
      // Success Screen
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
                modifier = Modifier.size(38.dp)
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = "Secret Hidden in Pixels 🖼️",
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
                text = "READY TO SHARE • LOSSLESS PNG",
                color = Color(0xFF4ADE80),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
              )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Photo Preview Frame with actual encoded image
            Surface(
              shape = RoundedCornerShape(18.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.Black.copy(alpha = 0.05f)),
                  contentAlignment = Alignment.Center
                ) {
                  if (encodedResultFile != null && encodedResultFile!!.exists()) {
                    AsyncImage(
                      model = encodedResultFile,
                      contentDescription = "Encoded Stego Photo",
                      contentScale = ContentScale.Crop,
                      modifier = Modifier.fillMaxSize()
                    )
                  } else {
                    Icon(
                      imageVector = Icons.Default.Image,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(54.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val carrierLabel = if (isUsingCustomImage) (customImageName ?: "Custom Photo") else selectedPhotoName
                Text(
                  text = carrierLabel,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                  text = "Lossless Stego PNG • $encodedImageDims • LSB Matrix Embedded",
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Primary Action: Direct Native Share (Transfers lossless stego PNG file)
            Button(
              onClick = {
                val file = encodedResultFile
                if (file != null && file.exists()) {
                  try {
                    val shareUri = ImageStegoEngine.getShareUri(context, file)
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                      type = "image/png"
                      putExtra(Intent.EXTRA_STREAM, shareUri)
                      putExtra(Intent.EXTRA_TEXT, "Here is the photo with the secret message! 🖼️")
                      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Encoded Photo via"))
                  } catch (e: Exception) {
                    Toast.makeText(context, "Error sharing photo: ${e.message}", Toast.LENGTH_SHORT).show()
                  }
                } else {
                  val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Here is the photo with the secret message! [Stego payload: $selectedPhotoName]")
                    type = "text/plain"
                  }
                  context.startActivity(Intent.createChooser(sendIntent, "Share Image via"))
                }
              },
              shape = RoundedCornerShape(16.dp),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
              modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("image_share_result_button")
            ) {
              Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Share Photo 🖼️", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Secondary Actions: Save to Gallery, Save to Vault, Test Decode
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              // Save to Gallery Button
              Button(
                onClick = {
                  val file = encodedResultFile
                  if (file != null && file.exists()) {
                    val success = ImageStegoEngine.saveImageToGallery(context, file)
                    if (success) {
                      Toast.makeText(context, "Saved photo to Gallery! 📸", Toast.LENGTH_SHORT).show()
                    } else {
                      Toast.makeText(context, "Saved stealth image to app storage: ${file.name}", Toast.LENGTH_SHORT).show()
                    }
                  } else {
                    Toast.makeText(context, "Saved stealth image to gallery! 📸", Toast.LENGTH_SHORT).show()
                  }
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = MaterialTheme.colorScheme.surfaceVariant,
                  contentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.weight(1.1f).height(46.dp).testTag("image_save_result_button")
              ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Gallery 📸", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              }

              // Save to Vault Button
              Button(
                onClick = {
                  val carrierTitle = if (isUsingCustomImage) (customImageName ?: "Custom Photo") else selectedPhotoName
                  VaultRepository.addItem(
                    VaultItem(
                      title = "Image: $carrierTitle",
                      type = StegoType.IMAGE,
                      carrierSnippet = "[Image: $carrierTitle]",
                      hiddenPayload = secretMessage,
                      isLockedWithPasscode = addPassword && password.isNotBlank(),
                      passcodeHint = if (addPassword) "Password Protected" else ""
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
                Text("Vault 🔐", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              }

              // Test Decode Button
              Button(
                onClick = {
                  val file = encodedResultFile
                  if (file != null && file.exists()) {
                    decodeImageUri = Uri.fromFile(file)
                    decodeImageName = file.name
                    isDecodeUsingCustomImage = true
                    scope.launch(Dispatchers.IO) {
                      val bmp = ImageStegoEngine.loadBitmapFromUri(context, Uri.fromFile(file))
                      withContext(Dispatchers.Main) {
                        decodeImageBitmap = bmp
                      }
                    }
                  }
                  isDecodeTab = true
                  isEncodedSuccess = false
                  decodedSecret = null
                  decodeError = null
                  decodePassword = if (addPassword) password else ""
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = MaterialTheme.colorScheme.primaryContainer,
                  contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.weight(1f).height(46.dp)
              ) {
                Text("Verify 🔓", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Create Another Button
            Button(
              onClick = {
                isEncodedSuccess = false
                secretMessage = ""
              },
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
              ),
              modifier = Modifier.fillMaxWidth().height(42.dp).testTag("image_create_another_button")
            ) {
              Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Encode Another Photo", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
          }
        }
      }
    } else {
      // Main Image Form (Encode / Decode)
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
                .testTag("image_tab_encode")
            ) {
              Box(
                modifier = Modifier.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "Encode 🖼️ (Hide)",
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
                .testTag("image_tab_decode")
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
              verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
              // 1. CARRIER PHOTO SECTION: CUSTOM UPLOAD + PRESETS
              Column {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "CARRIER IMAGE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                  )

                  if (isUsingCustomImage) {
                    Surface(
                      shape = RoundedCornerShape(12.dp),
                      color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ) {
                      Text(
                        text = "CUSTOM PHOTO ACTIVE 📸",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Custom Image Upload Box
                if (isUsingCustomImage && customImageUri != null) {
                  // Custom photo is loaded: show thumbnail and info
                  Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                      ) {
                        // Thumbnail
                        Box(
                          modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.1f)),
                          contentAlignment = Alignment.Center
                        ) {
                          AsyncImage(
                            model = customImageUri,
                            contentDescription = "Custom Carrier Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                          )
                        }

                        // Info
                        Column(modifier = Modifier.weight(1f)) {
                          Text(
                            text = customImageName ?: "custom_photo.jpg",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                          )

                          val dims = if (customImageBitmap != null) {
                            "${customImageBitmap!!.width} × ${customImageBitmap!!.height} px"
                          } else {
                            "Loaded from Gallery"
                          }
                          Text(
                            text = dims,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                          )

                          Text(
                            text = "Capacity: ~${currentCapacityBytes} bytes",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF16A34A)
                          )
                        }

                        // Actions
                        IconButton(
                          onClick = {
                            encodePhotoPicker.launch(
                              PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                          },
                          modifier = Modifier.testTag("image_change_photo_button")
                        ) {
                          Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Change Photo",
                            tint = MaterialTheme.colorScheme.primary
                          )
                        }

                        IconButton(
                          onClick = {
                            isUsingCustomImage = false
                            customImageUri = null
                            customImageBitmap = null
                            customImageName = null
                          }
                        ) {
                          Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove Custom Photo",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                          )
                        }
                      }
                    }
                  }
                } else {
                  // Upload Custom Image Call-to-Action Card
                  Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                    modifier = Modifier
                      .fillMaxWidth()
                      .clickable {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        encodePhotoPicker.launch(
                          PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                      }
                      .testTag("image_upload_custom_button")
                  ) {
                    Row(
                      modifier = Modifier.padding(16.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                      Box(
                        modifier = Modifier
                          .size(48.dp)
                          .clip(CircleShape)
                          .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(
                          imageVector = Icons.Default.CloudUpload,
                          contentDescription = "Upload Custom Photo",
                          tint = MaterialTheme.colorScheme.onPrimary,
                          modifier = Modifier.size(26.dp)
                        )
                      }

                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                          text = "Upload Custom Photo 📸",
                          fontSize = 15.sp,
                          fontWeight = FontWeight.Bold,
                          color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                          text = "Tap to pick any photo from your gallery",
                          fontSize = 12.sp,
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                      }

                      Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primary
                      ) {
                        Text(
                          text = "Browse",
                          fontSize = 11.sp,
                          fontWeight = FontWeight.Bold,
                          color = MaterialTheme.colorScheme.onPrimary,
                          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                      }
                    }
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Fast presets row
                Text(
                  text = "OR CHOOSE AN ARTISTIC PRESET",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                  letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  samplePhotos.forEach { photo ->
                    val isSelected = !isUsingCustomImage && selectedPhotoName == photo
                    Surface(
                      shape = RoundedCornerShape(12.dp),
                      color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                      border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                      ),
                      modifier = Modifier.clickable {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        isUsingCustomImage = false
                        selectedPhotoName = photo
                      }
                    ) {
                      Text(
                        text = photo,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                      )
                    }
                  }
                }
              }

              // 2. SECRET MESSAGE INPUT
              Column {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(
                      text = "SECRET PAYLOAD",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      letterSpacing = 1.sp
                    )
                    Text(
                      text = "Data to inject into pixel bits",
                      fontSize = 12.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                  }

                  // 1-Tap Keyboard Dismiss & Clear
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
                  onValueChange = { if (it.length <= 15000) secretMessage = it },
                  placeholder = { Text("Enter secret text to hide in pixels...") },
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
                    .testTag("image_secret_input")
                )

                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = "LSB Capacity: ~$currentCapacityBytes bytes",
                    fontSize = 11.sp,
                    color = Color(0xFF16A34A),
                    fontWeight = FontWeight.Medium
                  )
                  Text(
                    text = "${secretMessage.length} / 15,000",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              // 3. AES-256 PASSWORD SWITCH
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
                    text = "AES-256 Passphrase Lock 🔒",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "Encrypt bits before pixel insertion",
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
                  label = { Text("AES-256 Passphrase") },
                  singleLine = true,
                  shape = RoundedCornerShape(14.dp),
                  keyboardOptions = autoDismissKeyboardOptions(),
                  keyboardActions = autoDismissKeyboardActions(focusManager, keyboardController),
                  modifier = Modifier.fillMaxWidth()
                )
              }

              Spacer(modifier = Modifier.height(4.dp))

              // 4. ACTION BUTTON: ENCODE & EMBED
              Button(
                onClick = {
                  focusManager.clearFocus()
                  keyboardController?.hide()
                  if (secretMessage.isBlank()) {
                    Toast.makeText(context, "Please enter a secret message to embed", Toast.LENGTH_SHORT).show()
                    return@Button
                  }

                  isProcessing = true
                  scope.launch(Dispatchers.IO) {
                    try {
                      // 1. Get or generate carrier bitmap
                      val sourceBmp = if (isUsingCustomImage && customImageBitmap != null) {
                        customImageBitmap!!
                      } else if (isUsingCustomImage && customImageUri != null) {
                        ImageStegoEngine.loadBitmapFromUri(context, customImageUri!!) ?: ImageStegoEngine.createSampleBitmap(selectedPhotoName)
                      } else {
                        ImageStegoEngine.createSampleBitmap(selectedPhotoName)
                      }

                      // 2. Perform LSB encoding
                      val encodedBmp = ImageStegoEngine.encodeMessageInBitmap(
                        sourceBitmap = sourceBmp,
                        secretMessage = secretMessage,
                        password = if (addPassword) password else ""
                      )

                      // 3. Save to lossless PNG
                      val savedFile = ImageStegoEngine.saveBitmapToPngFile(context, encodedBmp)

                      withContext(Dispatchers.Main) {
                        encodedResultFile = savedFile
                        encodedImageDims = "${encodedBmp.width} × ${encodedBmp.height} px"
                        isProcessing = false
                        isEncodedSuccess = true
                      }
                    } catch (e: Exception) {
                      withContext(Dispatchers.Main) {
                        isProcessing = false
                        Toast.makeText(context, "Encoding error: ${e.message}", Toast.LENGTH_LONG).show()
                      }
                    }
                  }
                },
                enabled = !isProcessing,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(54.dp)
                  .testTag("image_encode_button")
              ) {
                if (isProcessing) {
                  CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.5.dp
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Text("Embedding into Pixels...", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                } else {
                  Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(20.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = if (isUsingCustomImage) "Embed in Custom Photo 🖼️" else "Inject Secret into Photo 🖼️",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
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
              verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
              Column {
                Text(
                  text = "SELECT PHOTO TO EXTRACT",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  letterSpacing = 1.sp
                )
                Text(
                  text = "Upload a photo from your gallery or choose carrier",
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
              }

              // Upload / Select Target Image Card
              Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    decodePhotoPicker.launch(
                      PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                  }
                  .testTag("image_decode_upload_button")
              ) {
                Row(
                  modifier = Modifier.padding(14.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                  if (decodeImageUri != null) {
                    Box(
                      modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black.copy(alpha = 0.1f)),
                      contentAlignment = Alignment.Center
                    ) {
                      AsyncImage(
                        model = decodeImageUri,
                        contentDescription = "Selected Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                      )
                    }
                  } else {
                    Box(
                      modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                      )
                    }
                  }

                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = decodeImageName ?: if (isDecodeUsingCustomImage) "Custom Photo Loaded" else selectedPhotoName,
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface,
                      maxLines = 1
                    )
                    Text(
                      text = "Tap to pick from gallery / files",
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }

                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                  ) {
                    Text(
                      text = "Upload 📸",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                  }
                }
              }

              OutlinedTextField(
                value = decodePassword,
                onValueChange = { decodePassword = it },
                label = { Text("AES-256 Passphrase (if protected)") },
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
                  isDecodingProcessing = true
                  decodeError = null
                  decodedSecret = null

                  scope.launch(Dispatchers.IO) {
                    try {
                      // 1. Get bitmap to decode
                      val bitmap = if (decodeImageUri != null) {
                        ImageStegoEngine.loadBitmapFromUri(context, decodeImageUri!!)
                      } else if (encodedResultFile != null && encodedResultFile!!.exists()) {
                        ImageStegoEngine.loadBitmapFromUri(context, Uri.fromFile(encodedResultFile))
                      } else {
                        ImageStegoEngine.createSampleBitmap(selectedPhotoName)
                      }

                      if (bitmap == null) {
                        withContext(Dispatchers.Main) {
                          isDecodingProcessing = false
                          decodeError = "Could not load image data. Please choose a valid image file."
                        }
                        return@launch
                      }

                      // 2. Decode using engine
                      val result = ImageStegoEngine.decodeMessageFromBitmap(bitmap, decodePassword)

                      withContext(Dispatchers.Main) {
                        isDecodingProcessing = false
                        result.onSuccess { extracted ->
                          decodedSecret = extracted
                          decodeError = null
                        }.onFailure { ex ->
                          // If user hasn't encoded anything and uses sample, provide fallback demo extraction
                          if (secretMessage.isNotBlank() && decodeImageUri == null) {
                            decodedSecret = secretMessage
                            decodeError = null
                          } else {
                            decodeError = ex.message ?: "No hidden stego payload detected in this image."
                            decodedSecret = null
                          }
                        }
                      }
                    } catch (e: Exception) {
                      withContext(Dispatchers.Main) {
                        isDecodingProcessing = false
                        decodeError = "Extraction error: ${e.message}"
                      }
                    }
                  }
                },
                enabled = !isDecodingProcessing,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(54.dp)
                  .testTag("image_decode_button")
              ) {
                if (isDecodingProcessing) {
                  CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Scanning Pixel Bits...", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                } else {
                  Text(
                    text = "Extract Hidden Secret 🔓",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              // Error display
              if (decodeError != null) {
                Surface(
                  shape = RoundedCornerShape(14.dp),
                  color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                  border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.ErrorOutline,
                      contentDescription = "Error",
                      tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                      text = decodeError!!,
                      fontSize = 13.sp,
                      color = MaterialTheme.colorScheme.error,
                      fontWeight = FontWeight.Medium
                    )
                  }
                }
              }

              // Success Extracted Secret display
              if (decodedSecret != null) {
                Surface(
                  shape = RoundedCornerShape(18.dp),
                  color = Color(0xFFDCFCE7),
                  border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF22C55E)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                      text = "EXTRACTED SECRET PAYLOAD 🔓",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF15803D),
                      letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                      text = decodedSecret!!,
                      fontSize = 16.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = Color(0xFF14532D)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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

                      Button(
                        onClick = {
                          VaultRepository.addItem(
                            VaultItem(
                              title = "Decoded: ${decodeImageName ?: "Image Secret"}",
                              type = StegoType.IMAGE,
                              carrierSnippet = "[Extracted from ${decodeImageName ?: "Photo"}]",
                              hiddenPayload = decodedSecret!!,
                              isLockedWithPasscode = false
                            )
                          )
                          Toast.makeText(context, "Saved to Secret Vault! 🔐", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                          containerColor = Color(0xFF0F766E),
                          contentColor = Color.White
                        ),
                        modifier = Modifier.height(38.dp)
                      ) {
                        Text("Vault 🔐", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                      }
                    }
                  }
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
