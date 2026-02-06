package com.cycles.app.domain

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.cycles.app.data.entity.DailyLog
import java.io.File
import java.time.format.DateTimeFormatter

class DataExporter(private val context: Context) {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun generateCsv(logs: List<DailyLog>): String {
        val header = "Date,Bleeding,Pain,Mood,Discharge,Medications,Sex,BBT,OPK,Notes"
        val rows = logs.sortedBy { it.date }.map { log ->
            listOf(
                log.date.format(dateFormatter),
                bleedingLabel(log.bleedingIntensity),
                painLabel(log.painLevel),
                log.mood.orEmpty(),
                log.discharge.orEmpty(),
                log.medications.orEmpty(),
                log.sexActivity.orEmpty(),
                log.bbtTemp?.toString().orEmpty(),
                log.opkResult.orEmpty(),
                escapeCsv(log.notes.orEmpty()),
            ).joinToString(",")
        }
        return (listOf(header) + rows).joinToString("\n")
    }

    fun generatePdf(logs: List<DailyLog>, stats: CycleStats?): File {
        val document = PdfDocument()
        val pageWidth = 595 // A4
        val pageHeight = 842
        val margin = 40f
        val lineHeight = 16f
        var currentY = margin + 20f
        var pageNumber = 1

        val titlePaint = Paint().apply { textSize = 18f; isFakeBoldText = true }
        val headerPaint = Paint().apply { textSize = 12f; isFakeBoldText = true }
        val bodyPaint = Paint().apply { textSize = 10f }

        var currentPage: PdfDocument.Page? = null

        fun startNewPage(): PdfDocument.Page {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber++).create()
            val page = document.startPage(pageInfo)
            currentY = margin + 20f
            currentPage = page
            return page
        }

        var page = startNewPage()
        var canvas = page.canvas

        canvas.drawText("Cycles \u2014 Health Report", margin, currentY, titlePaint)
        currentY += lineHeight * 2

        if (stats != null) {
            canvas.drawText("Summary", margin, currentY, headerPaint)
            currentY += lineHeight * 1.5f
            canvas.drawText("Average cycle length: ${String.format("%.1f", stats.averageCycleLength)} days", margin, currentY, bodyPaint)
            currentY += lineHeight
            canvas.drawText("Cycle range: ${stats.shortestCycle}\u2013${stats.longestCycle} days", margin, currentY, bodyPaint)
            currentY += lineHeight
            canvas.drawText("Completed cycles: ${stats.totalCycles}", margin, currentY, bodyPaint)
            currentY += lineHeight
            if (stats.averagePeriodLength != null) {
                canvas.drawText("Average period length: ${String.format("%.1f", stats.averagePeriodLength)} days", margin, currentY, bodyPaint)
                currentY += lineHeight
            }
            currentY += lineHeight
        }

        canvas.drawText("Daily Logs", margin, currentY, headerPaint)
        currentY += lineHeight * 1.5f

        val sortedLogs = logs.sortedByDescending { it.date }
        for (log in sortedLogs) {
            if (currentY > pageHeight - margin - lineHeight * 4) {
                document.finishPage(currentPage!!)
                page = startNewPage()
                canvas = page.canvas
            }

            canvas.drawText(log.date.format(dateFormatter), margin, currentY, headerPaint)
            currentY += lineHeight

            val details = buildList {
                log.bleedingIntensity?.let { if (it > 0) add("Bleeding: ${bleedingLabel(it)}") }
                log.painLevel?.let { if (it > 0) add("Pain: ${painLabel(it)}") }
                log.mood?.let { add("Mood: $it") }
                log.medications?.let { add("Meds: $it") }
                log.notes?.let { if (it.isNotBlank()) add("Notes: $it") }
            }
            for (detail in details) {
                val displayText = if (detail.length > 80) detail.take(80) + "..." else detail
                canvas.drawText("  $displayText", margin, currentY, bodyPaint)
                currentY += lineHeight
            }
            currentY += lineHeight * 0.5f
        }

        document.finishPage(currentPage!!)

        val file = File(context.cacheDir, "cycles_report.pdf")
        file.outputStream().use { document.writeTo(it) }
        document.close()
        return file
    }

    fun shareCsv(csv: String): Intent {
        val file = File(context.cacheDir, "cycles_export.csv")
        file.writeText(csv)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    fun sharePdf(pdfFile: File): Intent {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", pdfFile)
        return Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }

    companion object {
        fun bleedingLabel(level: Int?): String = when (level) {
            1 -> "Light"
            2 -> "Medium"
            3 -> "Heavy"
            4 -> "Very Heavy"
            else -> ""
        }

        fun painLabel(level: Int?): String = when (level) {
            1 -> "Mild"
            2 -> "Moderate"
            3 -> "Severe"
            4 -> "Very Severe"
            else -> ""
        }
    }
}
