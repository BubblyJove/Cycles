package com.cycles.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cycles.app.data.entity.Cycle
import com.cycles.app.data.entity.DailyLog
import com.cycles.app.data.entity.Prediction
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarGrid(
    month: YearMonth,
    logs: Map<LocalDate, DailyLog>,
    cycles: List<Cycle>,
    prediction: Prediction?,
    selectedDate: LocalDate?,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val firstOfMonth = month.atDay(1)
    val daysInMonth = month.lengthOfMonth()
    // Monday = 1, offset to start week on Monday
    val startDayOfWeek = firstOfMonth.dayOfWeek.value // 1=Mon..7=Sun

    Column(modifier = modifier.fillMaxWidth()) {
        // Day-of-week headers
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Calendar cells: 6 rows x 7 cols
        var dayCounter = 1
        for (week in 0 until 6) {
            if (dayCounter > daysInMonth) break
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 1..7) {
                    val cellIndex = week * 7 + col
                    if (cellIndex < startDayOfWeek || dayCounter > daysInMonth) {
                        // Empty cell
                        Box(modifier = Modifier.weight(1f).height(44.dp))
                    } else {
                        val date = month.atDay(dayCounter)
                        DayCell(
                            date = date,
                            log = logs[date],
                            isSelected = date == selectedDate,
                            isToday = date == LocalDate.now(),
                            isPeriodDay = isPeriodDay(date, cycles),
                            isPredicted = isPredictedDay(date, prediction),
                            onClick = { onDateClick(date) },
                            modifier = Modifier.weight(1f),
                        )
                        dayCounter++
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    log: DailyLog?,
    isSelected: Boolean,
    isToday: Boolean,
    isPeriodDay: Boolean,
    isPredicted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasBleeding = log?.bleedingIntensity != null && log.bleedingIntensity > 0

    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        hasBleeding || isPeriodDay -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        isPredicted -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
        else -> Color.Transparent
    }

    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
        isToday -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = modifier
            .height(44.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = if (isToday) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodySmall,
            color = textColor,
            textAlign = TextAlign.Center,
        )
        if (hasBleeding) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        } else if (isPredicted) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f))
            )
        }
    }
}

private fun isPeriodDay(date: LocalDate, cycles: List<Cycle>): Boolean {
    return cycles.any { cycle ->
        val end = cycle.endDate ?: cycle.startDate
        !date.isBefore(cycle.startDate) && !date.isAfter(end)
    }
}

private fun isPredictedDay(date: LocalDate, prediction: Prediction?): Boolean {
    if (prediction == null) return false
    return !date.isBefore(prediction.predictedStart) && !date.isAfter(prediction.predictedEnd)
}
