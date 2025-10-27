package com.alperen.spendcraft.feature.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alperen.spendcraft.LocaleHelper
import com.alperen.spendcraft.core.ui.R as CoreR

data class Language(
    val code: String,
    val name: String,
    val nativeName: String,
    val flag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagePickerScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedLanguage by remember { 
        mutableStateOf(LocaleHelper.getLanguage(context))
    }
    
    val languages = listOf(
        Language("tr", "Turkish", "Türkçe", "🇹🇷"),
        Language("en", "English", "English", "🇬🇧")
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(if (selectedLanguage == "tr") "Dil Seçimi" else "Language Selection")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = if (selectedLanguage == "tr") "Geri" else "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                Text(
                    text = if (selectedLanguage == "tr") "Uygulamada kullanılacak dili seçin" else "Select the language for the app",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            items(languages) { language ->
                LanguageListItem(
                    language = language,
                    isSelected = selectedLanguage == language.code,
                    onClick = {
                        selectedLanguage = language.code
                        LocaleHelper.setLocale(context, language.code)
                    }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (selectedLanguage == "tr") "Uygulama yeniden başlatıldığında değişiklikler uygulanacaktır." else "Changes will be applied after the app restarts.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun LanguageListItem(
    language: Language,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = language.flag,
            fontSize = 32.sp
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = language.nativeName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = language.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
    
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}
