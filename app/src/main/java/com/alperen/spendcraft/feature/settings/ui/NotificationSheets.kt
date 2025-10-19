package com.alperen.spendcraft.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alperen.spendcraft.core.ui.IOSColors
import com.alperen.spendcraft.feature.notifications.CustomNotification
import com.alperen.spendcraft.feature.notifications.NotificationTemplate

/**
 * iOS NotificationSettingsView sheet'lerinin Android karşılıkları
 * BottomSheet kullanarak iOS sheet davranışını taklit ediyor
 */

// MARK: - Edit Template Sheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTemplateSheet(
    template: NotificationTemplate,
    onDismiss: () -> Unit,
    onSave: (NotificationTemplate) -> Unit
) {
    var hour by remember { mutableIntStateOf(template.hour) }
    var minute by remember { mutableIntStateOf(template.minute) }
    var isEnabled by remember { mutableStateOf(template.isEnabled) }
    var selectedMonthDays by remember { mutableStateOf(template.daysOfMonth?.toSet() ?: emptySet()) }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Header
            item {
                Text(
                    text = "Bildirimi Düzenle",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            // Template Info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = template.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = template.body,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Settings Header
            item {
                Text(
                    text = "Ayarlar",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
            
            // Enable Toggle
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Aktif",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { isEnabled = it }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Time Picker
            item {
                TimePickerCard(
                    hour = hour,
                    minute = minute,
                    onHourChange = { hour = it },
                    onMinuteChange = { minute = it }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Month Days Picker (if applicable)
            if (template.daysOfMonth != null) {
                item {
                    Text(
                        text = "Ayın Hangi Günlerinde?",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                
                // Quick select buttons
                item {
                    QuickSelectRow(
                        selectedDays = selectedMonthDays,
                        onSelectRange = { selectedMonthDays = it }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                // Day grid
                item {
                    MonthDaysGrid(
                        selectedDays = selectedMonthDays,
                        onDayToggle = { day ->
                            selectedMonthDays = if (selectedMonthDays.contains(day)) {
                                selectedMonthDays - day
                            } else {
                                selectedMonthDays + day
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            
            // Week Days Info (if applicable)
            if (template.daysOfWeek != null) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Haftanın Günleri",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = daysText(template.daysOfWeek!!),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            
            // Save Button
            item {
                Button(
                    onClick = {
                        val updated = template.copy(
                            hour = hour,
                            minute = minute,
                            isEnabled = isEnabled,
                            daysOfMonth = if (template.daysOfMonth != null) {
                                selectedMonthDays.ifEmpty { null }?.sorted()
                            } else null
                        )
                        onSave(updated)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Kaydet", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

// MARK: - Add/Edit Custom Notification Sheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomNotificationSheet(
    onDismiss: () -> Unit,
    onAdd: (CustomNotification) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var hour by remember { mutableIntStateOf(9) }
    var minute by remember { mutableIntStateOf(0) }
    var selectedDays by remember { mutableStateOf<Set<Int>>(emptySet()) }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Text(
                    text = "Özel Bildirim",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            // Title
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Başlık") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Body
            item {
                OutlinedTextField(
                    value = body,
                    onValueChange = { body = it },
                    label = { Text("Mesaj") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3,
                    maxLines = 5
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Time
            item {
                Text(
                    text = "Zaman",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
            
            item {
                TimePickerCard(
                    hour = hour,
                    minute = minute,
                    onHourChange = { hour = it },
                    onMinuteChange = { minute = it }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Days
            item {
                Text(
                    text = "Tekrarlama",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = if (selectedDays.isEmpty()) "Her gün" else daysText(selectedDays.toList()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
            
            item {
                WeekDaysSelector(
                    selectedDays = selectedDays,
                    onDayToggle = { day ->
                        selectedDays = if (selectedDays.contains(day)) {
                            selectedDays - day
                        } else {
                            selectedDays + day
                        }
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Create Button
            item {
                Button(
                    onClick = {
                        val notification = CustomNotification(
                            title = title,
                            body = body,
                            hour = hour,
                            minute = minute,
                            isEnabled = true,
                            daysOfWeek = selectedDays.ifEmpty { null }?.sorted()
                        )
                        onAdd(notification)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = title.isNotEmpty() && body.isNotEmpty(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Oluştur", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCustomNotificationSheet(
    notification: CustomNotification,
    onDismiss: () -> Unit,
    onSave: (CustomNotification) -> Unit
) {
    var title by remember { mutableStateOf(notification.title) }
    var body by remember { mutableStateOf(notification.body) }
    var hour by remember { mutableIntStateOf(notification.hour) }
    var minute by remember { mutableIntStateOf(notification.minute) }
    var isEnabled by remember { mutableStateOf(notification.isEnabled) }
    var selectedDays by remember { mutableStateOf(notification.daysOfWeek?.toSet() ?: emptySet()) }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Text(
                    text = "Bildirimi Düzenle",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            // Title
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Başlık") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Body
            item {
                OutlinedTextField(
                    value = body,
                    onValueChange = { body = it },
                    label = { Text("Mesaj") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3,
                    maxLines = 5
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Enable Toggle
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Aktif",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { isEnabled = it }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Time
            item {
                Text(
                    text = "Saat",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
            
            item {
                TimePickerCard(
                    hour = hour,
                    minute = minute,
                    onHourChange = { hour = it },
                    onMinuteChange = { minute = it }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Days
            item {
                Text(
                    text = "Tekrarlama",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = if (selectedDays.isEmpty()) "Her gün" else daysText(selectedDays.toList()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
            
            item {
                WeekDaysSelector(
                    selectedDays = selectedDays,
                    onDayToggle = { day ->
                        selectedDays = if (selectedDays.contains(day)) {
                            selectedDays - day
                        } else {
                            selectedDays + day
                        }
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Save Button
            item {
                Button(
                    onClick = {
                        val updated = notification.copy(
                            title = title,
                            body = body,
                            hour = hour,
                            minute = minute,
                            isEnabled = isEnabled,
                            daysOfWeek = selectedDays.ifEmpty { null }?.sorted()
                        )
                        onSave(updated)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = title.isNotEmpty() && body.isNotEmpty(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Kaydet", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

// MARK: - Helper Components

@Composable
private fun TimePickerCard(
    hour: Int,
    minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hour Picker
            NumberPicker(
                value = hour,
                onValueChange = onHourChange,
                range = 0..23,
                label = "Saat"
            )
            
            Text(
                text = ":",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            // Minute Picker
            NumberPicker(
                value = minute,
                onValueChange = onMinuteChange,
                range = 0..59,
                label = "Dakika"
            )
        }
    }
}

@Composable
private fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = { if (value > range.first) onValueChange(value - 1) }
            ) {
                Icon(Icons.Filled.Clear, "Azalt")
            }
            
            Text(
                text = String.format("%02d", value),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(60.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            IconButton(
                onClick = { if (value < range.last) onValueChange(value + 1) }
            ) {
                Icon(Icons.Filled.Add, "Artır")
            }
        }
    }
}

@Composable
private fun MonthDaysGrid(
    selectedDays: Set<Int>,
    onDayToggle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = modifier.height(240.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items((1..31).toList()) { day ->
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (selectedDays.contains(day)) IOSColors.Blue
                        else MaterialTheme.colorScheme.surface
                    )
                    .clickable { onDayToggle(day) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$day",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (selectedDays.contains(day)) Color.White
                    else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun QuickSelectRow(
    selectedDays: Set<Int>,
    onSelectRange: (Set<Int>) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickSelectChip("1-5", (1..5).toSet(), selectedDays, onSelectRange)
        QuickSelectChip("5-15", (5..15).toSet(), selectedDays, onSelectRange)
        QuickSelectChip("15-20", (15..20).toSet(), selectedDays, onSelectRange)
        QuickSelectChip("20-31", (20..31).toSet(), selectedDays, onSelectRange)
        QuickSelectChip("Her Gün", (1..31).toSet(), selectedDays, onSelectRange)
    }
}

@Composable
private fun QuickSelectChip(
    label: String,
    days: Set<Int>,
    selectedDays: Set<Int>,
    onSelect: (Set<Int>) -> Unit
) {
    val isSelected = selectedDays == days
    FilterChip(
        selected = isSelected,
        onClick = { onSelect(days) },
        label = { Text(label) }
    )
}

@Composable
private fun WeekDaysSelector(
    selectedDays: Set<Int>,
    onDayToggle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val dayNames = listOf("Paz", "Pzt", "Sal", "Çar", "Per", "Cum", "Cmt")
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.height(120.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(dayNames.size) { index ->
            val day = index + 1
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (selectedDays.contains(day)) IOSColors.Blue
                        else MaterialTheme.colorScheme.surface
                    )
                    .border(
                        width = 1.dp,
                        color = if (selectedDays.contains(day)) IOSColors.Blue
                        else MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onDayToggle(day) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dayNames[index],
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (selectedDays.contains(day)) Color.White
                    else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun daysText(days: List<Int>): String {
    val dayNames = listOf("Pazar", "Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi")
    return days.map { dayNames.getOrNull(it - 1) ?: "" }.joinToString(", ")
}

