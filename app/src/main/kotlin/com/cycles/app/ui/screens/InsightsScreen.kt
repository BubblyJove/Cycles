package com.cycles.app.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cycles.app.ui.viewmodel.InsightsViewModel
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun InsightsScreen(
    modifier: Modifier = Modifier,
    viewModel: InsightsViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Handle share intent
    LaunchedEffect(state.shareIntent) {
        state.shareIntent?.let { intent ->
            context.startActivity(Intent.createChooser(intent, "Export"))
            viewModel.clearShareIntent()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = "Insights",
                style = MaterialTheme.typography.headlineSmall,
            )
        }

        // Stats card
        item {
            val stats = state.stats
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Cycle Statistics", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (stats != null) {
                        StatRow("Average cycle", "${String.format("%.1f", stats.averageCycleLength)} days")
                        StatRow("Shortest", "${stats.shortestCycle} days")
                        StatRow("Longest", "${stats.longestCycle} days")
                        StatRow("Cycles tracked", "${stats.totalCycles}")
                        stats.averagePeriodLength?.let {
                            StatRow("Avg. period", "${String.format("%.1f", it)} days")
                        }
                    } else {
                        Text(
                            "Log at least 2 complete cycles to see statistics.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        // Prediction card
        item {
            val prediction = state.prediction
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Next Period Prediction", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (prediction != null) {
                        val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                        Text(
                            "${prediction.windowStart.format(dateFormatter)} – ${prediction.windowEnd.format(dateFormatter)}",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Confidence: ${(prediction.confidence * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                        LinearProgressIndicator(
                            progress = { prediction.confidence },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            prediction.rationale,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        )
                    } else {
                        Text(
                            "Log at least 2 complete cycles to get predictions.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        )
                    }
                }
            }
        }

        // Export buttons
        item {
            Text("Export Data", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = { viewModel.exportCsv() },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Filled.TableChart, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                    Text("CSV")
                }
                OutlinedButton(
                    onClick = { viewModel.exportPdf() },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Filled.Description, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                    Text("PDF")
                }
            }
        }

        // Cycle history
        if (state.cycles.isNotEmpty()) {
            item {
                Text("Cycle History", style = MaterialTheme.typography.titleMedium)
            }
            items(state.cycles) { cycle ->
                val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Started: ${cycle.startDate.format(dateFormatter)}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        cycle.endDate?.let {
                            Text(
                                "Period ended: ${it.format(dateFormatter)}",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        cycle.cycleLength?.let {
                            Text(
                                "Cycle length: $it days",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        cycle.periodLength?.let {
                            Text(
                                "Period length: $it days",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        if (cycle.cycleLength == null) {
                            Text(
                                "Current cycle",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }

        // Bottom padding
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
