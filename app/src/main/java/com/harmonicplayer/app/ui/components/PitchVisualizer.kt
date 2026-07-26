package com.harmonicplayer.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Pitch-Reactive Real-time Audio Visualizer Canvas Composable.
 * Renders 8 dynamic vertical bars with rounded caps and gradient colors
 * corresponding to frequency spectrum pitch magnitudes.
 */
@Composable
fun PitchVisualizer(
    barHeights: FloatArray,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary,
    accentColor: Color = MaterialTheme.colorScheme.secondary,
    height: Dp = 120.dp
) {
    val numBars = barHeights.size.coerceAtMost(8)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val width = size.width
        val canvasHeight = size.height

        // Calculate dynamic bar widths and spacing
        val gapRatio = 0.3f
        val totalGapUnits = (numBars - 1) * gapRatio + numBars
        val barWidth = width / totalGapUnits
        val gapWidth = barWidth * gapRatio
        val cornerRadiusPx = barWidth / 2.5f

        for (i in 0 until numBars) {
            val rawHeight = barHeights.getOrElse(i) { 0f }.coerceIn(0.0f, 1.0f)
            
            // Ensure minimum baseline height (4dp equivalent) so bars are visible when idle
            val minHeightPx = 8.dp.toPx()
            val calculatedHeight = (rawHeight * canvasHeight).coerceAtLeast(minHeightPx)

            val x = i * (barWidth + gapWidth)
            val y = canvasHeight - calculatedHeight

            // Reactive gradient: shifts towards accent color as magnitude increases
            val topColor = Color(
                red = barColor.red * (1f - rawHeight) + accentColor.red * rawHeight,
                green = barColor.green * (1f - rawHeight) + accentColor.green * rawHeight,
                blue = barColor.blue * (1f - rawHeight) + accentColor.blue * rawHeight,
                alpha = 1.0f
            )

            val gradientBrush = Brush.verticalGradient(
                colors = listOf(topColor, barColor),
                startY = y,
                endY = canvasHeight
            )

            drawRoundRect(
                brush = gradientBrush,
                topLeft = Offset(x, y),
                size = Size(barWidth, calculatedHeight),
                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
            )
        }
    }
}
