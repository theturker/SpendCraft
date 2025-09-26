package com.alperen.spendcraft.feature.ai

import androidx.lifecycle.ViewModel
import com.alperen.spendcraft.core.billing.BillingRepository
import com.alperen.spendcraft.data.repository.AIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AIViewModel @Inject constructor(
    val aiRepository: AIRepository,
    val billingRepository: BillingRepository
) : ViewModel()
