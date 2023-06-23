package ir.amirroid.qrcodescanner.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun QrcodeCover() {
    val displayMatrix = LocalContext.current.resources.displayMetrics
    val infiniteTransition = rememberInfiniteTransition(label = "cover")
    val height = displayMatrix.heightPixels
    val width = displayMatrix.widthPixels
    val startY = height * 0.15f
    val startX = width * .15f
    val endX = width * .85f
    val endY = startY + (endX - startX)
    val lineY by infiniteTransition.animateFloat(
        initialValue = startY + 8,
        targetValue = endY - 8,
        animationSpec = infiniteRepeatable(
            tween(2000),
            repeatMode = RepeatMode.Reverse
        ), label = "y"
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            Color.Black.copy(0.5f),
            size = size
        )
        drawRect(
            Color.Transparent,
            Offset(startX, startY),
            size = Size(
                endX - startX,
                endY - startY
            ),
            blendMode = BlendMode.DstIn
        )
        drawRect(
            color = Color.White.copy(0.3f),
            topLeft = Offset(startX, startY),
            size = Size(
                endX - startX,
                endY - startY
            ),
            style = Stroke(
                8f
            )
        )
        drawLine(
            Brush.horizontalGradient(
                listOf(
                    Color.Red.copy(0.1f),
                    Color.Red,
                    Color.Red.copy(0.1f),
                )
            ),
            Offset(
                startX,
                lineY
            ),
            Offset(
                endX,
                lineY
            ),
            strokeWidth = 8f
        )
    }
}