package com.cycles.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cycles.app.ui.components.CalendarGrid
import com.cycles.app.ui.components.DayDetailSheet
import com.cycles.app.ui.viewmodel.CalendarViewModel
import java.time.format.DateTimeFormatter

@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        // Month navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { viewModel.navigateMonth(-1) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous month")
            }
            Text(
                text = state.currentMonth.format(monthFormatter),
                style = MaterialTheme.typography.titleLarge,
            )
            IconButton(onClick = { viewModel.navigateMonth(1) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next month")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        CalendarGrid(
            month = state.currentMonth,
            logs = state.logs,
            cycles = state.cycles,
            prediction = state.prediction,
            selectedDate = state.selectedDate,
            onDateClick = { viewModel.selectDate(it) },
        )
    }

    // Day detail bottom sheet
    if (state.selectedDate != null) {
        DayDetailSheet(
            date = state.selectedDate!!,
            log = state.selectedLog,
            onDismiss = { viewModel.clearSelection() },
        )
    }
}
