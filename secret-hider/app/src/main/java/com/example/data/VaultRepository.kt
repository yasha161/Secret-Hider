package com.example.data

import com.example.model.StegoType
import com.example.model.VaultItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object VaultRepository {
  private val _items = MutableStateFlow<List<VaultItem>>(
    listOf(
      VaultItem(
        title = "Demo Secret Note",
        type = StegoType.EMOJI,
        carrierSnippet = "🤫",
        hiddenPayload = "Meet at the coffee shop at 5PM",
        isLockedWithPasscode = false
      ),
      VaultItem(
        title = "Project Access Key",
        type = StegoType.TEXT,
        carrierSnippet = "The quick brown fox jumps over the lazy dog.",
        hiddenPayload = "TOKEN_8928_SECURE_ALPHA",
        isLockedWithPasscode = true,
        passcodeHint = "Default: secret"
      )
    )
  )
  val items: StateFlow<List<VaultItem>> = _items.asStateFlow()

  fun addItem(item: VaultItem) {
    _items.value = listOf(item) + _items.value
  }

  fun deleteItem(id: String) {
    _items.value = _items.value.filterNot { it.id == id }
  }
}
