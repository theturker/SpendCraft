package com.alperen.spendcraft.feature.recurrence

import androidx.lifecycle.ViewModel
import com.alperen.spendcraft.core.billing.BillingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecurringViewModel @Inject constructor(
    val billingRepository: BillingRepository
) : ViewModel()
