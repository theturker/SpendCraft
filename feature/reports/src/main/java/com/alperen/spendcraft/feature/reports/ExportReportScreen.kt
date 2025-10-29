package com.alperen.spendcraft.feature.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.alperen.spendcraft.core.ui.AppScaffold
import com.alperen.spendcraft.core.ui.ModernCard
import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.model.Category
import com.alperen.spendcraft.core.model.TransactionType
import java.text.SimpleDateFormat
import java.util.*

enum class DateRange(val labelResId: Int) {
    LAST_WEEK(com.alperen.spendcraft.feature.reports.R.string.export_date_range_last_week),
    LAST_MONTH(com.alperen.spendcraft.feature.reports.R.string.export_date_range_last_month),
    LAST_3_MONTHS(com.alperen.spendcraft.feature.reports.R.string.export_date_range_last_3_months),
    LAST_YEAR(com.alperen.spendcraft.feature.reports.R.string.export_date_range_last_year),
    CUSTOM(com.alperen.spendcraft.feature.reports.R.string.export_date_range_custom)
}

enum class ExportFormat(val displayName: String, val icon: ImageVector, val descriptionResId: Int) {
    CSV("CSV", CsvIcon, com.alperen.spendcraft.feature.reports.R.string.export_format_csv_description),
    PDF("PDF", PdfIcon, com.alperen.spendcraft.feature.reports.R.string.export_format_pdf_description),
    EXCEL("Excel", ExcelIcon, com.alperen.spendcraft.feature.reports.R.string.export_format_excel_description)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportReportScreen(
    transactions: List<Transaction>,
    categories: List<Category>,
    onNavigateBack: () -> Unit,
    onExport: (format: ExportFormat, dateRange: DateRange, customStart: Long, customEnd: Long) -> Unit,
    onPreview: (uri: android.net.Uri) -> Unit = {}
) {
    val context = LocalContext.current
    var selectedFormat by remember { mutableStateOf<ExportFormat?>(null) }
    var selectedDateRange by remember { mutableStateOf<DateRange?>(null) }
    var customStartDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var customEndDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var isExporting by remember { mutableStateOf(false) }
    var exportMessage by remember { mutableStateOf<String?>(null) }
    
    // iOS scroll behavior
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val collapsedFraction = scrollBehavior.state.collapsedFraction
    val titleFontSize = lerp(start = 34.sp, stop = 17.sp, fraction = collapsedFraction)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Box {
                LargeTopAppBar(
                    title = { Spacer(modifier = Modifier) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                painter = painterResource(com.alperen.spendcraft.core.ui.R.drawable.ic_chevron_left),
                                contentDescription = context.getString(com.alperen.spendcraft.feature.reports.R.string.back)
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    )
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (collapsedFraction > 0.5f) 64.dp else 152.dp)
                        .align(Alignment.BottomCenter),
                    contentAlignment = if (collapsedFraction > 0.5f) {
                        Alignment.Center
                    } else {
                        Alignment.BottomStart
                    }
                ) {
                    Text(
                        text = context.getString(com.alperen.spendcraft.feature.reports.R.string.export_title),
                        fontSize = titleFontSize,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = if (collapsedFraction > 0.5f) {
                            Modifier
                        } else {
                            Modifier.padding(start = 16.dp, bottom = 8.dp)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Format Selection
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = context.getString(com.alperen.spendcraft.feature.reports.R.string.export_format_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        ExportFormat.values().forEach { format ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = selectedFormat == format,
                                        onClick = { selectedFormat = format }
                                    )
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedFormat == format,
                                    onClick = { selectedFormat = format }
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Icon(
                                    imageVector = format.icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = format.displayName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = context.getString(format.descriptionResId),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Date Range Selection
            item {
                ModernCard {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = context.getString(com.alperen.spendcraft.feature.reports.R.string.export_date_range_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        DateRange.values().forEach { range ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = selectedDateRange == range,
                                        onClick = { selectedDateRange = range }
                                    )
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedDateRange == range,
                                    onClick = { selectedDateRange = range }
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = context.getString(range.labelResId),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }

            // Export Button
            item {
                Button(
                    onClick = {
                        if (selectedFormat != null && selectedDateRange != null) {
                            isExporting = true
                            exportMessage = null
                            
                            try {
                                val exportService = ExportService(context)
                                val uri = when (selectedFormat!!) {
                                    ExportFormat.CSV -> exportService.exportToCsv(transactions, categories, selectedDateRange!!, customStartDate, customEndDate)
                                    ExportFormat.PDF -> exportService.exportToPdf(
                                        transactions, categories, selectedDateRange!!, customStartDate, customEndDate,
                                        aiAnalysis = null, // İleride AI özetini burada geçebiliriz
                                        aiRecommendation = null
                                    )
                                    ExportFormat.EXCEL -> exportService.exportToExcel(transactions, categories, selectedDateRange!!, customStartDate, customEndDate)
                                }
                                
                                if (uri != null) {
                                    val mime = when (selectedFormat!!) {
                                        ExportFormat.PDF -> "application/pdf"
                                        ExportFormat.CSV -> "text/csv"
                                        ExportFormat.EXCEL -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                                    }
                                    if (selectedFormat == ExportFormat.PDF) {
                                        onPreview(uri)
                                    } else {
                                        exportService.shareFile(uri, mime)
                                    }
                                    // Bildirim: rapor oluşturuldu – tıklayınca dosya açılsın
                                    try {
                                        val notifier = com.alperen.spendcraft.core.notifications.NotificationManager(context)
                                        notifier.showFileNotification(
                                            title = context.getString(com.alperen.spendcraft.feature.reports.R.string.export_notification_title),
                                            message = if (selectedFormat == ExportFormat.PDF) context.getString(com.alperen.spendcraft.feature.reports.R.string.export_notification_pdf_created) else context.getString(com.alperen.spendcraft.feature.reports.R.string.export_notification_created),
                                            uri = uri,
                                            mimeType = mime
                                        )
                                    } catch (_: Exception) {}
                                    exportMessage = context.getString(com.alperen.spendcraft.feature.reports.R.string.export_success_message)
                                } else {
                                    exportMessage = context.getString(com.alperen.spendcraft.feature.reports.R.string.export_format_not_supported)
                                }
                            } catch (e: Exception) {
                                exportMessage = context.getString(com.alperen.spendcraft.feature.reports.R.string.export_error_message, e.message ?: context.getString(com.alperen.spendcraft.feature.reports.R.string.unknown_error))
                            } finally {
                                isExporting = false
                            }
                        }
                    },
                    enabled = selectedFormat != null && selectedDateRange != null && !isExporting,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isExporting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(context.getString(com.alperen.spendcraft.feature.reports.R.string.export_creating))
                    } else {
                        Icon(
                            imageVector = DownloadIcon,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(context.getString(com.alperen.spendcraft.feature.reports.R.string.export_download))
                    }
                }
            }
            
            // Export Message
            exportMessage?.let { message ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (message == context.getString(com.alperen.spendcraft.feature.reports.R.string.export_success_message)) 
                                MaterialTheme.colorScheme.primaryContainer 
                            else 
                                MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = message,
                            modifier = Modifier.padding(16.dp),
                            color = if (message == context.getString(com.alperen.spendcraft.feature.reports.R.string.export_success_message)) 
                                MaterialTheme.colorScheme.onPrimaryContainer 
                            else 
                                MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}

// Custom Vector Icons
val DownloadIcon: ImageVector
    get() {
        if (_downloadIcon != null) {
            return _downloadIcon!!
        }
        _downloadIcon = Builder(
            name = "Download",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(
                fill = null,
                stroke = androidx.compose.ui.graphics.SolidColor(androidx.compose.ui.graphics.Color(0xFF000000)),
                strokeLineWidth = 2.0f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 4.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(21.0f, 15.0f)
                verticalLineToRelative(4.0f)
                arcToRelative(2.0f, 2.0f, 0.0f, false, true, -2.0f, 2.0f)
                horizontalLineTo(5.0f)
                arcToRelative(2.0f, 2.0f, 0.0f, false, true, -2.0f, -2.0f)
                verticalLineToRelative(-4.0f)
            }
            path(
                fill = null,
                stroke = androidx.compose.ui.graphics.SolidColor(androidx.compose.ui.graphics.Color(0xFF000000)),
                strokeLineWidth = 2.0f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 4.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(7.0f, 10.0f)
                lineToRelative(5.0f, 5.0f)
                lineToRelative(5.0f, -5.0f)
            }
            path(
                fill = null,
                stroke = androidx.compose.ui.graphics.SolidColor(androidx.compose.ui.graphics.Color(0xFF000000)),
                strokeLineWidth = 2.0f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 4.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(12.0f, 15.0f)
                verticalLineTo(3.0f)
            }
        }.build()
        return _downloadIcon!!
    }

private var _downloadIcon: ImageVector? = null

val PdfIcon: ImageVector
    get() {
        if (_pdfIcon != null) {
            return _pdfIcon!!
        }
        _pdfIcon = Builder(
            name = "Pdf",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(
                fill = androidx.compose.ui.graphics.SolidColor(androidx.compose.ui.graphics.Color(0xFFE53E3E)),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(4.0f, 4.0f)
                horizontalLineToRelative(16.0f)
                verticalLineToRelative(16.0f)
                horizontalLineTo(4.0f)
                close()
            }
            path(
                fill = androidx.compose.ui.graphics.SolidColor(androidx.compose.ui.graphics.Color(0xFFFFFFFF)),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(8.0f, 8.0f)
                horizontalLineToRelative(8.0f)
                verticalLineToRelative(8.0f)
                horizontalLineTo(8.0f)
                close()
            }
            path(
                fill = androidx.compose.ui.graphics.SolidColor(androidx.compose.ui.graphics.Color(0xFFE53E3E)),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(10.0f, 10.0f)
                horizontalLineToRelative(4.0f)
                verticalLineToRelative(1.0f)
                horizontalLineTo(10.0f)
                close()
            }
            path(
                fill = androidx.compose.ui.graphics.SolidColor(androidx.compose.ui.graphics.Color(0xFFE53E3E)),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(10.0f, 12.0f)
                horizontalLineToRelative(4.0f)
                verticalLineToRelative(1.0f)
                horizontalLineTo(10.0f)
                close()
            }
            path(
                fill = androidx.compose.ui.graphics.SolidColor(androidx.compose.ui.graphics.Color(0xFFE53E3E)),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(10.0f, 14.0f)
                horizontalLineToRelative(4.0f)
                verticalLineToRelative(1.0f)
                horizontalLineTo(10.0f)
                close()
            }
        }.build()
        return _pdfIcon!!
    }

private var _pdfIcon: ImageVector? = null

val ExcelIcon: ImageVector
    get() {
        if (_excelIcon != null) {
            return _excelIcon!!
        }
        _excelIcon = Builder(
            name = "Excel",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(
                fill = androidx.compose.ui.graphics.SolidColor(androidx.compose.ui.graphics.Color(0xFF217346)),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(4.0f, 4.0f)
                horizontalLineToRelative(16.0f)
                verticalLineToRelative(16.0f)
                horizontalLineTo(4.0f)
                close()
            }
            path(
                fill = androidx.compose.ui.graphics.SolidColor(androidx.compose.ui.graphics.Color(0xFFFFFFFF)),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(8.0f, 8.0f)
                horizontalLineToRelative(8.0f)
                verticalLineToRelative(8.0f)
                horizontalLineTo(8.0f)
                close()
            }
            path(
                fill = androidx.compose.ui.graphics.SolidColor(androidx.compose.ui.graphics.Color(0xFF217346)),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(10.0f, 10.0f)
                horizontalLineToRelative(1.0f)
                verticalLineToRelative(4.0f)
                horizontalLineTo(10.0f)
                close()
            }
            path(
                fill = androidx.compose.ui.graphics.SolidColor(androidx.compose.ui.graphics.Color(0xFF217346)),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(12.0f, 10.0f)
                horizontalLineToRelative(1.0f)
                verticalLineToRelative(4.0f)
                horizontalLineTo(12.0f)
                close()
            }
            path(
                fill = androidx.compose.ui.graphics.SolidColor(androidx.compose.ui.graphics.Color(0xFF217346)),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(14.0f, 10.0f)
                horizontalLineToRelative(1.0f)
                verticalLineToRelative(4.0f)
                horizontalLineTo(14.0f)
                close()
            }
        }.build()
        return _excelIcon!!
    }

private var _excelIcon: ImageVector? = null

val CsvIcon: ImageVector
    get() {
        if (_csvIcon != null) {
            return _csvIcon!!
        }
        _csvIcon = Builder(
            name = "Csv",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(
                fill = androidx.compose.ui.graphics.SolidColor(androidx.compose.ui.graphics.Color(0xFF6B7280)),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(4.0f, 4.0f)
                horizontalLineToRelative(16.0f)
                verticalLineToRelative(16.0f)
                horizontalLineTo(4.0f)
                close()
            }
            path(
                fill = androidx.compose.ui.graphics.SolidColor(androidx.compose.ui.graphics.Color(0xFFFFFFFF)),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(8.0f, 8.0f)
                horizontalLineToRelative(8.0f)
                verticalLineToRelative(8.0f)
                horizontalLineTo(8.0f)
                close()
            }
            path(
                fill = androidx.compose.ui.graphics.SolidColor(androidx.compose.ui.graphics.Color(0xFF6B7280)),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(10.0f, 10.0f)
                horizontalLineToRelative(4.0f)
                verticalLineToRelative(1.0f)
                horizontalLineTo(10.0f)
                close()
            }
            path(
                fill = androidx.compose.ui.graphics.SolidColor(androidx.compose.ui.graphics.Color(0xFF6B7280)),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(10.0f, 12.0f)
                horizontalLineToRelative(4.0f)
                verticalLineToRelative(1.0f)
                horizontalLineTo(10.0f)
                close()
            }
            path(
                fill = androidx.compose.ui.graphics.SolidColor(androidx.compose.ui.graphics.Color(0xFF6B7280)),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(10.0f, 14.0f)
                horizontalLineToRelative(4.0f)
                verticalLineToRelative(1.0f)
                horizontalLineTo(10.0f)
                close()
            }
        }.build()
        return _csvIcon!!
    }

private var _csvIcon: ImageVector? = null
