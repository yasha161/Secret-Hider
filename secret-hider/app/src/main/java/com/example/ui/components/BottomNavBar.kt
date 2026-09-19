package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DesignTokens

enum class NavTab(val id: String, val label: String) {
  HOME("dashboard", "Home"),
  VAULT("vault", "Vault"),
  HISTORY("history", "History"),
  SETTINGS("settings", "Settings")
}

@Composable
fun AppBottomNavBar(
  currentTab: String,
  onTabSelected: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  // Frosted glass bottom bar with fine top border and soft shadow
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .shadow(
        elevation = 12.dp,
        ambientColor = DesignTokens.SkyGlowColor,
        spotColor = DesignTokens.SignalGlowColor
      ),
    color = Color.White.copy(alpha = 0.94f),
    border = BorderStroke(1.dp, Color(0xFFE2E8F0).copy(alpha = 0.8f))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(64.dp)
        .padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      NavTabItem(
        label = "Home",
        selected = currentTab == "dashboard" || currentTab == "home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        onClick = { onTabSelected("dashboard") },
        testTag = "nav_home"
      )

      NavTabItem(
        label = "Vault",
        selected = currentTab == "vault",
        selectedIcon = Icons.Filled.Lock,
        unselectedIcon = Icons.Outlined.Lock,
        onClick = { onTabSelected("vault") },
        testTag = "nav_vault"
      )

      NavTabItem(
        label = "History",
        selected = currentTab == "history",
        selectedIcon = Icons.Filled.Refresh,
        unselectedIcon = Icons.Outlined.Refresh,
        onClick = { onTabSelected("history") },
        testTag = "nav_history"
      )

      NavTabItem(
        label = "Settings",
        selected = currentTab == "settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        onClick = { onTabSelected("settings") },
        testTag = "nav_settings"
      )
    }
  }
}

@Composable
private fun NavTabItem(
  label: String,
  selected: Boolean,
  selectedIcon: ImageVector,
  unselectedIcon: ImageVector,
  onClick: () -> Unit,
  testTag: String
) {
  val primaryColor = DesignTokens.SignalBlue
  val unselectedColor = DesignTokens.InkMuted
  val tintColor by animateColorAsState(
    targetValue = if (selected) primaryColor else unselectedColor,
    label = "tab_tint"
  )

  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.92f else 1.0f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessMediumLow
    ),
    label = "tab_scale"
  )

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = Modifier
      .graphicsLayer {
        scaleX = scale
        scaleY = scale
      }
      .clip(CircleShape)
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
      .padding(horizontal = 14.dp, vertical = 6.dp)
      .testTag(testTag)
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .size(36.dp)
        .then(
          if (selected) Modifier
            .background(
              color = primaryColor.copy(alpha = 0.12f),
              shape = CircleShape
            )
          else Modifier
        )
    ) {
      Icon(
        imageVector = if (selected) selectedIcon else unselectedIcon,
        contentDescription = label,
        tint = tintColor,
        modifier = Modifier.size(22.dp)
      )
    }

    Text(
      text = label,
      color = tintColor,
      fontSize = 11.sp,
      fontWeight = if (selected) androidx.compose.ui.text.font.FontWeight.SemiBold else androidx.compose.ui.text.font.FontWeight.Normal
    )
  }
}
