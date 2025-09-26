package com.alperen.spendcraft.feature.settings

import androidx.lifecycle.ViewModel
import com.alperen.spendcraft.core.ai.AIKeyManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AISettingsViewModel @Inject constructor(
    val aiKeyManager: AIKeyManager
) : ViewModel()
