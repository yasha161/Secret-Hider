package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VaultRepository
import com.example.model.AppLanguage
import com.example.model.VaultItem
import com.example.ui.components.AppBottomNavBar
import com.example.ui.components.LanguageSelectionDialog
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EmojiStegoScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.ImageStegoScreen
import com.example.ui.screens.PrivacyPolicyScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TermsScreen
import com.example.ui.screens.TextStegoScreen
import com.example.ui.screens.VaultScreen
import com.example.ui.screens.WelcomeOnboardingScreen
import com.example.ui.theme.AppThemeStyle
import com.example.ui.theme.SecretHiderTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MainAppContent()
    }
  }
}

@Composable
fun MainAppContent() {
  // Default to Ocean Royal (Figma Mockup primary), easily switchable to Pink Peach or Midnight Black
  var selectedTheme by remember { mutableStateOf(AppThemeStyle.OCEAN_BLUE) }
  var selectedLanguage by remember { mutableStateOf(AppLanguage.ENGLISH) }
  var showLanguageDialog by remember { mutableStateOf(false) }

  // Initial screen: start on onboarding (or dashboard)
  var currentScreen by remember { mutableStateOf("onboarding") }
  var userName by remember { mutableStateOf("Maya") }
  var showEditUserDialog by remember { mutableStateOf(false) }
  var editedUserName by remember { mutableStateOf(userName) }

  val vaultItems by VaultRepository.items.collectAsState()
  val focusManager = LocalFocusManager.current
  val keyboardController = LocalSoftwareKeyboardController.current

  SecretHiderTheme(themeStyle = selectedTheme) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      bottomBar = {
        // Persistent Bottom Navigation Bar (Screens 4-10 in Figma mockup)
        if (currentScreen != "onboarding" && currentScreen != "about" &&
            currentScreen != "privacy" && currentScreen != "terms") {
          AppBottomNavBar(
            currentTab = currentScreen,
            onTabSelected = { tab ->
              focusManager.clearFocus()
              keyboardController?.hide()
              currentScreen = tab
            }
          )
        }
      }
    ) { innerPadding ->
      val contentModifier = Modifier.padding(innerPadding)

      AnimatedContent(
        targetState = currentScreen,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "screen_navigation"
      ) { screen ->
        when (screen) {
          "onboarding" -> {
            WelcomeOnboardingScreen(
              initialUserName = userName,
              selectedLanguage = selectedLanguage,
              onLanguageSelected = { selectedLanguage = it },
              onFinishOnboarding = { updatedName, targetScreen ->
                userName = updatedName
                currentScreen = targetScreen
              },
              modifier = contentModifier
            )
          }

          "dashboard", "home" -> {
            DashboardScreen(
              userName = userName,
              onEditUserName = {
                editedUserName = userName
                showEditUserDialog = true
              },
              savedCount = vaultItems.size,
              onOpenTool = { tool ->
                currentScreen = tool
              },
              modifier = contentModifier
            )
          }

          "vault" -> {
            VaultScreen(
              onBack = { currentScreen = "dashboard" },
              modifier = contentModifier
            )
          }

          "history" -> {
            HistoryScreen(
              onOpenItem = { /* Handled in history card */ },
              modifier = contentModifier
            )
          }

          "settings" -> {
            SettingsScreen(
              userName = userName,
              onEditUserName = {
                editedUserName = userName
                showEditUserDialog = true
              },
              selectedTheme = selectedTheme,
              onSelectTheme = { selectedTheme = it },
              selectedLanguage = selectedLanguage,
              onChangeLanguage = { showLanguageDialog = true },
              onOpenAbout = { currentScreen = "about" },
              onOpenPrivacy = { currentScreen = "privacy" },
              onOpenTerms = { currentScreen = "terms" },
              modifier = contentModifier
            )
          }

          "about" -> {
            AboutScreen(
              onBack = { currentScreen = "settings" },
              onOpenPrivacy = { currentScreen = "privacy" },
              onOpenTerms = { currentScreen = "terms" },
              modifier = contentModifier
            )
          }

          "privacy" -> {
            PrivacyPolicyScreen(
              onBack = { currentScreen = "settings" },
              modifier = contentModifier
            )
          }

          "terms" -> {
            TermsScreen(
              onBack = { currentScreen = "settings" },
              modifier = contentModifier
            )
          }

          "text" -> {
            TextStegoScreen(
              onBack = { currentScreen = "dashboard" },
              modifier = contentModifier
            )
          }

          "emoji" -> {
            EmojiStegoScreen(
              onBack = { currentScreen = "dashboard" },
              modifier = contentModifier
            )
          }

          "image" -> {
            ImageStegoScreen(
              onBack = { currentScreen = "dashboard" },
              modifier = contentModifier
            )
          }

          else -> {
            DashboardScreen(
              userName = userName,
              onEditUserName = {
                editedUserName = userName
                showEditUserDialog = true
              },
              savedCount = vaultItems.size,
              onOpenTool = { tool ->
                currentScreen = tool
              },
              modifier = contentModifier
            )
          }
        }
      }

      // Dialog: Language switcher (openable from Settings)
      if (showLanguageDialog) {
        LanguageSelectionDialog(
          currentLanguage = selectedLanguage,
          onLanguageConfirmed = { chosenLanguage ->
            selectedLanguage = chosenLanguage
            showLanguageDialog = false
          }
        )
      }

      // Dialog: Edit User Profile Name
      if (showEditUserDialog) {
        AlertDialog(
          onDismissRequest = { showEditUserDialog = false },
          shape = RoundedCornerShape(22.dp),
          containerColor = MaterialTheme.colorScheme.surface,
          title = {
            Text(
              text = "Edit Name 👋",
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
          },
          text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Text(
                text = "What should Secret Hider call you?",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              OutlinedTextField(
                value = editedUserName,
                onValueChange = { editedUserName = it },
                label = { Text("Display Name") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("dialog_edit_name_input")
              )
            }
          },
          confirmButton = {
            Button(
              onClick = {
                if (editedUserName.isNotBlank()) {
                  userName = editedUserName.trim()
                }
                showEditUserDialog = false
              },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
              modifier = Modifier.testTag("dialog_save_name_button")
            ) {
              Text("Save", fontWeight = FontWeight.SemiBold)
            }
          },
          dismissButton = {
            TextButton(onClick = { showEditUserDialog = false }) {
              Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }
        )
      }
    }
  }
}
