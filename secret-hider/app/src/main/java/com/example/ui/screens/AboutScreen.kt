package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AboutScreen(
  onBack: () -> Unit,
  onOpenPrivacy: () -> Unit,
  onOpenTerms: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      IconButton(onClick = onBack) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = MaterialTheme.colorScheme.onBackground
        )
      }
      Text(
        text = "About Secret Hider",
        style = MaterialTheme.typography.titleLarge.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 19.sp
        ),
        color = MaterialTheme.colorScheme.onBackground
      )
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 20.dp, vertical = 4.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

      // Hero section
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(text = "\uD83E\uDD2B", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "Secret Hider",
          fontSize = 28.sp,
          fontWeight = FontWeight.ExtraBold,
          color = MaterialTheme.colorScheme.onBackground,
          textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Hide messages where no one thinks to look.",
          fontSize = 15.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
          lineHeight = 22.sp
        )
      }

      // What is this
      AboutCard(
        emoji = "\uD83D\uDD0D",
        title = "What is this?",
        body = "Secret Hider is a free steganography tool that lets you embed secret messages inside normal-looking text, everyday emojis, and ordinary photos.\n\nIt's real steganography, made approachable. No PhD required."
      )

      // Why it was built
      AboutCard(
        emoji = "\u2764\uFE0F",
        title = "Why we built it",
        body = "Not everything needs to be a corporate secret. Sometimes you just want to slip a surprise into a boring text, send an inside joke hidden in a photo, or keep a private note that looks like nothing at all.\n\nWe built Secret Hider for fun, personal moments, and the joy of having a secret."
      )

      // The 3 methods
      Text(
        text = "How it works",
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )

      MethodCard(emoji = "\uD83D\uDCDD", title = "Hide in Text", description = "Zero-width invisible Unicode characters embed your secret between the words of any normal sentence.")
      MethodCard(emoji = "\uD83E\uDD2B", title = "Hide in Emoji", description = "Unicode Tag characters attach your secret invisibly to any emoji. It looks completely normal.")
      MethodCard(emoji = "\uD83D\uDDBC\uFE0F", title = "Hide in Image", description = "LSB pixel steganography hides your secret in the color data of a photo. The image looks identical.")

      // Privacy promise card
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.Top,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp).padding(top = 2.dp)
          )
          Column {
            Text(
              text = "Your data stays with you",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Nothing you encode or decode ever leaves your device. No uploads, no servers, no logging. We genuinely cannot see what you hide.",
              fontSize = 13.sp,
              lineHeight = 20.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // One request
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFFFF3E0),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = "\uD83D\uDE4F One request: Use this for fun, inside jokes, personal privacy, and harmless secrets between consenting people. Please don\u2019t use it to hurt or deceive anyone, or for anything illegal.",
          fontSize = 13.sp,
          lineHeight = 20.sp,
          color = Color(0xFF5D4037),
          modifier = Modifier.padding(16.dp)
        )
      }

      // Legal links
      Text(
        text = "Legal",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )

      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column {
          LegalLinkRow(label = "\uD83D\uDD12 Privacy Policy", onClick = onOpenPrivacy)
          LegalLinkRow(label = "\uD83D\uDCCB Terms of Service", onClick = onOpenTerms, showDivider = false)
        }
      }

      // Version
      Text(
        text = "Version 1.0 \u2022 Made with \u2764\uFE0F in India",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun AboutCard(emoji: String, title: String, body: String) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = emoji, fontSize = 22.sp)
        Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
      }
      Text(text = body, fontSize = 14.sp, lineHeight = 21.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}

@Composable
private fun MethodCard(emoji: String, title: String, description: String) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.Top,
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Text(text = emoji, fontSize = 26.sp)
      Column {
        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(3.dp))
        Text(text = description, fontSize = 13.sp, lineHeight = 19.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
  }
}

@Composable
private fun LegalLinkRow(label: String, onClick: () -> Unit, showDivider: Boolean = true) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(horizontal = 16.dp, vertical = 14.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    Icon(
      imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.size(18.dp)
    )
  }
  if (showDivider) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .padding(horizontal = 16.dp)
        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    )
  }
}
