package com.cycles.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cycles.app.ui.viewmodel.TodayViewModel
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TodayScreen(
    modifier: Modifier = Modifier,
    viewModel: TodayViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var notesText by remember(state.log?.notes) { mutableStateOf(state.log?.notes.orEmpty()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        // Date header
        Text(
            text = state.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)),
            style = MaterialTheme.typography.headlineSmall,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Period toggle
        Button(
            onClick = { viewModel.togglePeriod() },
            colors = if (state.isInPeriod) {
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                )
            } else {
                ButtonDefaults.buttonColors()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (state.isInPeriod) "Period ended" else "Period started")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bleeding intensity
        SectionLabel("Bleeding")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val bleedingOptions = listOf(1 to "Light", 2 to "Medium", 3 to "Heavy", 4 to "Very Heavy")
            bleedingOptions.forEach { (level, label) ->
                FilterChip(
                    selected = state.log?.bleedingIntensity == level,
                    onClick = {
                        val newLevel = if (state.log?.bleedingIntensity == level) 0 else level
                        viewModel.updateBleeding(newLevel)
                    },
                    label = { Text(label) },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pain level
        SectionLabel("Pain")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val painOptions = listOf(1 to "Mild", 2 to "Moderate", 3 to "Severe", 4 to "Very Severe")
            painOptions.forEach { (level, label) ->
                FilterChip(
                    selected = state.log?.painLevel == level,
                    onClick = {
                        val newLevel = if (state.log?.painLevel == level) 0 else level
                        viewModel.updatePain(newLevel)
                    },
                    label = { Text(label) },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mood
        SectionLabel("Mood")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val moods = listOf("Happy", "Calm", "Anxious", "Sad", "Irritable", "Energetic")
            moods.forEach { mood ->
                FilterChip(
                    selected = state.log?.mood == mood,
                    onClick = {
                        val newMood = if (state.log?.mood == mood) null else mood
                        viewModel.updateMood(newMood)
                    },
                    label = { Text(mood) },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Medications
        SectionLabel("Medications")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = state.log?.medications == "Yes",
                onClick = {
                    val newMeds = if (state.log?.medications == "Yes") null else "Yes"
                    viewModel.updateMedications(newMeds)
                },
                label = { Text("Took medication") },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notes
        SectionLabel("Notes")
        OutlinedTextField(
            value = notesText,
            onValueChange = {
                notesText = it
                viewModel.updateNotes(it)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Add notes...") },
            minLines = 2,
            maxLines = 4,
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 4.dp),
    )
}
