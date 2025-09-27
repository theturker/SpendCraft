package com.alperen.spendcraft.feature.sharing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alperen.spendcraft.core.billing.BillingRepository
import com.alperen.spendcraft.data.db.entities.SharingMemberEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharingViewModel @Inject constructor(
    val billingRepository: BillingRepository
) : ViewModel() {
    
    private val _members = MutableStateFlow<List<SharingMemberEntity>>(emptyList())
    val members: StateFlow<List<SharingMemberEntity>> = _members.asStateFlow()
    
    fun inviteMember(email: String) {
        viewModelScope.launch {
            // TODO: Implement invite member functionality
            // This would typically call a backend service
        }
    }
    
    fun updateMemberRole(member: SharingMemberEntity, newRole: String) {
        viewModelScope.launch {
            // TODO: Implement update member role functionality
        }
    }
    
    fun removeMember(member: SharingMemberEntity) {
        viewModelScope.launch {
            // TODO: Implement remove member functionality
        }
    }
}