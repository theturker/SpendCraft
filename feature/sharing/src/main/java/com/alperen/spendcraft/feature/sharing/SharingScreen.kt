package com.alperen.spendcraft.feature.sharing

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import com.alperen.spendcraft.core.ui.AppScaffold
import com.alperen.spendcraft.core.ui.ModernCard
import com.alperen.spendcraft.core.ui.PremiumGate
import com.alperen.spendcraft.data.db.entities.SharingMemberEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SharingScreen(
    membersFlow: Flow<List<SharingMemberEntity>>,
    onInviteMember: (String) -> Unit,
    onUpdateRole: (SharingMemberEntity, String) -> Unit,
    onRemoveMember: (SharingMemberEntity) -> Unit,
    isPremium: Boolean,
    onUpgrade: () -> Unit,
    onBack: () -> Unit = {}
) {
    val members by membersFlow.collectAsState(initial = emptyList())
    var showInviteDialog by remember { mutableStateOf(false) }
    var inviteEmail by remember { mutableStateOf("") }
    
    AppScaffold(
        title = "Aile/Ortak Bütçe",
        onBack = onBack
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!isPremium) {
                item {
                    PremiumGate(
                        title = "Premium Özellik",
                        description = "Aile ve ortak bütçe paylaşımı Premium üyeler için özel bir özelliktir.",
                        onUpgrade = onUpgrade
                    )
                }
            }
            
            // Invite Member Section
            if (isPremium) {
                item {
                    ModernCard {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Üye Davet Et",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = inviteEmail,
                                    onValueChange = { inviteEmail = it },
                                    label = { Text("E-posta adresi") },
                                    modifier = Modifier.weight(1f)
                                )
                                
                                Button(
                                    onClick = {
                                        if (inviteEmail.isNotBlank()) {
                                            onInviteMember(inviteEmail)
                                            inviteEmail = ""
                                        }
                                    },
                                    enabled = inviteEmail.isNotBlank()
                                ) {
                                    Icon(
                                        painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_person_add_vector),
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Davet Et")
                                }
                            }
                        }
                    }
                }
            }
            
            // Members List
            if (members.isEmpty()) {
                item {
                    ModernCard {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_group_vector),
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Henüz üye yok",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Aile üyelerinizi davet ederek bütçeyi paylaşın",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(members) { member ->
                    SharingMemberItem(
                        member = member,
                        onUpdateRole = { newRole -> onUpdateRole(member, newRole) },
                        onRemove = { onRemoveMember(member) }
                    )
                }
            }
            
            // Info Card
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Paylaşım Hakkında",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "• Owner: Tüm yetkilere sahip\n• Editor: İşlem ekleyebilir/düzenleyebilir\n• Viewer: Sadece görüntüleyebilir",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SharingMemberItem(
    member: SharingMemberEntity,
    onUpdateRole: (String) -> Unit,
    onRemove: () -> Unit
) {
    var showRoleDialog by remember { mutableStateOf(false) }
    var showRemoveDialog by remember { mutableStateOf(false) }
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    ModernCard {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Column {
                        Text(
                            text = member.userId,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = getRoleDisplayName(member.role),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (member.role != "OWNER") {
                        IconButton(onClick = { showRoleDialog = true }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Rol değiştir",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        IconButton(onClick = { showRemoveDialog = true }) {
                            Icon(
                                painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_remove_vector),
                                contentDescription = "Üyeyi çıkar",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Davet tarihi:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = dateFormatter.format(Date(member.invitedAt)),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                if (member.joinedAt != null) {
                    Column {
                        Text(
                            text = "Katılım tarihi:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = dateFormatter.format(Date(member.joinedAt ?: 0L)),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "Bekliyor",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
    
    // Role Selection Dialog
    if (showRoleDialog) {
        AlertDialog(
            onDismissRequest = { showRoleDialog = false },
            title = { Text("Rol Değiştir") },
            text = { Text("${member.userId} kullanıcısının rolünü değiştirin") },
            confirmButton = {
                // Role selection would be implemented here
                TextButton(
                    onClick = { showRoleDialog = false }
                ) {
                    Text("Tamam")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRoleDialog = false }
                ) {
                    Text("İptal")
                }
            }
        )
    }
    
    // Remove Member Dialog
    if (showRemoveDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveDialog = false },
            title = { Text("Üyeyi Çıkar") },
            text = { Text("${member.userId} kullanıcısını gruptan çıkarmak istediğinizden emin misiniz?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRemove()
                        showRemoveDialog = false
                    }
                ) {
                    Text("Çıkar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRemoveDialog = false }
                ) {
                    Text("İptal")
                }
            }
        )
    }
}

private fun getRoleDisplayName(role: String): String {
    return when (role) {
        "OWNER" -> "Sahip"
        "EDITOR" -> "Düzenleyici"
        "VIEWER" -> "Görüntüleyici"
        else -> role
    }
}
