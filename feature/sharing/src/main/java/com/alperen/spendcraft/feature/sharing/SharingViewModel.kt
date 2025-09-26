package com.alperen.spendcraft.feature.sharing

import androidx.lifecycle.ViewModel
import com.alperen.spendcraft.core.billing.BillingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharingViewModel @Inject constructor(
    val billingRepository: BillingRepository
) : ViewModel()
