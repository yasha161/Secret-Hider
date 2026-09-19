package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardHide
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Automatically dismisses the soft keyboard and clears focus on tapping any background area.
 * Uses PointerEventPass.Initial without consuming gestures so child buttons can receive clicks freely.
 */
fun Modifier.dismissKeyboardOnTap(
  focusManager: FocusManager,
  keyboardController: SoftwareKeyboardController?
): Modifier = this.pointerInput(Unit) {
  awaitPointerEventScope {
    while (true) {
      val event = awaitPointerEvent(PointerEventPass.Initial)
      val down = event.changes.firstOrNull { it.pressed }
      if (down != null) {
        focusManager.clearFocus()
        keyboardController?.hide()
      }
    }
  }
}

/**
 * Standard keyboard options that automatically dismiss the keyboard on pressing the IME Action key.
 */
fun autoDismissKeyboardOptions(imeAction: ImeAction = ImeAction.Done): KeyboardOptions {
  return KeyboardOptions(imeAction = imeAction)
}

fun autoDismissKeyboardActions(
  focusManager: FocusManager,
  keyboardController: SoftwareKeyboardController?,
  onDoneExtra: (() -> Unit)? = null
): KeyboardActions {
  return KeyboardActions(
    onDone = {
      focusManager.clearFocus()
      keyboardController?.hide()
      onDoneExtra?.invoke()
    },
    onGo = {
      focusManager.clearFocus()
      keyboardController?.hide()
      onDoneExtra?.invoke()
    },
    onSearch = {
      focusManager.clearFocus()
      keyboardController?.hide()
      onDoneExtra?.invoke()
    }
  )
}

/**
 * A sleek Gen-Z quick-dismiss bar providing 1-tap Keyboard Drop and 1-tap Clear.
 */
@Composable
fun KeyboardToolRow(
  onDismiss: () -> Unit,
  onClear: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Surface(
      shape = RoundedCornerShape(10.dp),
      color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
      modifier = Modifier
        .height(28.dp)
        .clickable { onDismiss() }
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.KeyboardHide,
          contentDescription = "Hide Keyboard",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "Hide Keyboard",
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.primary
        )
      }
    }

    if (onClear != null) {
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier
          .height(28.dp)
          .clickable { onClear() }
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "Clear text",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Clear",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}
