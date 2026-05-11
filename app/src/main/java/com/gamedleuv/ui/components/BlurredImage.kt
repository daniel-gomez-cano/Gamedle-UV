package com.gamedleuv.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import coil.size.Scale

@Composable
fun BlurredImage(
    imageUrl: String?,
    revealedSectors: List<Int>,
    modifier: Modifier = Modifier,
    columns: Int = 3,
    rows: Int = 3,
) {
    android.util.Log.d("BLUR_DEBUG", "imageUrl recibida: $imageUrl")
    val totalSectors = columns * rows
    val context = LocalContext.current

    Box(modifier = modifier) {


        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .size(32, 32)   // descarga en 32x32 para efecto pixelado
                .scale(Scale.FILL)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            filterQuality = FilterQuality.None, 
            modifier = Modifier.fillMaxSize()
        )
        AsyncImage(
            model = imageUrl,
            contentDescription = "game image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    val sectorW = size.width / columns
                    val sectorH = size.height / rows

                    // Solo dibuja los sectores revelados usando clipRect
                    revealedSectors.forEach { index ->
                        val col = index % columns
                        val row = index / columns

                        drawContext.canvas.save()
                        drawContext.canvas.clipRect(
                            androidx.compose.ui.geometry.Rect(
                                topLeft = Offset(col * sectorW, row * sectorH),
                                bottomRight = Offset(
                                    col * sectorW + sectorW,
                                    row * sectorH + sectorH
                                )
                            )
                        )
                        drawContent()
                        drawContext.canvas.restore()
                    }

                    // Borde entre sectores
                    (0 until totalSectors).forEach { index ->
                        if (index !in revealedSectors) {
                            val col = index % columns
                            val row = index / columns
                            drawRect(
                                color = Color.Black.copy(alpha = 0.15f),
                                topLeft = Offset(col * sectorW + 1f, row * sectorH + 1f),
                                size = Size(sectorW - 2f, sectorH - 2f)
                            )
                        }
                    }
                }
        )
    }
}
