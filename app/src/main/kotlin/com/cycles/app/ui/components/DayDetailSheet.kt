package com.cycles.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cycles.app.data.entity.DailyLog
import com.cycles.app.domain.DataExporter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailSheet(
    date: LocalDate,
    log: DailyLog?,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)),
                style = MaterialTheme.typography.titleLarge,
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (log == null) {
                Text(
                    text = "No data logged for this day.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                log.bleedingIntensity?.let {
                    if (it > 0) DetailRow("Bleeding", DataExporter.bleedingLabel(it))
                }
                log.painLevel?.let {
                    if (it > 0) DetailRow("Pain", DataExporter.painLabel(it))
                }
                log.mood?.let { DetailRow("Mood", it) }
                log.discharge?.let { DetailRow("Discharge", it) }
                log.medications?.let { DetailRow("Medications", it) }
                log.sexActivity?.let { DetailRow("Sex", it) }
                log.bbtTemp?.let { DetailRow("BBT", "${it}°") }
                log.opkResult?.let { DetailRow("OPK", it) }
                log.notes?.let {
                    if (it.isNotBlank()) DetailRow("Notes", it)
                }

                // If all fields are null/empty, show generic message
                val hasData = listOfNotNull(
                    log.bleedingIntensity?.takeIf { it > 0 },
                    log.painLevel?.takeIf { it > 0 },
                    log.mood,
                    log.discharge,
                    log.medications,
                    log.notes?.takeIf { it.isNotBlank() },
                ).isNotEmpty()

                if (!hasData) {
                    Text(
                        text = "No data logged for this day.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
