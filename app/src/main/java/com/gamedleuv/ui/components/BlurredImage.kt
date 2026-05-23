package com.gamedleuv.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Scale

@Composable
fun BlurredImage(
    imageUrl: String?,
    revealedSectors: List<Int>,
    modifier: Modifier = Modifier,
    columns: Int = 3,
    rows: Int = 3,
) {
    val context = LocalContext.current
    val totalSectors = columns * rows

    // ── Painter de la imagen pixelada (32x32, sin caché de memoria ni disco) ─
    // memoryCachePolicy DISABLED + diskCachePolicy DISABLED garantiza que Coil
    // siempre decodifique a 32x32, sin importar si la imagen ya estuvo en RAM.
    val pixelatedPainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .size(32, 32)
            .scale(Scale.FILL)
            .memoryCachePolicy(CachePolicy.DISABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
            .build()
    )

    // ── Painter de la imagen nítida (caché normal) ────────────────────────────
    val sharpPainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .build()
    )

    Box(modifier = modifier) {
        // Canvas único que dibuja ambas capas de forma controlada y síncrona.
        // Al estar en el mismo Canvas, el orden de pintado está garantizado:
        // 1. Primero pixelado (todo el fondo)
        // 2. Después nítido solo en los sectores revelados (clip exacto)
        // 3. Rejilla encima
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // ── 1. Fondo pixelado completo ────────────────────────────────────
            with(pixelatedPainter) {
                draw(
                    size = Size(canvasWidth, canvasHeight),
                    colorFilter = null
                )
            }

            // ── 2. Sectores nítidos (solo los revelados) ──────────────────────
            if (revealedSectors.isNotEmpty()) {
                val sectorW = canvasWidth / columns
                val sectorH = canvasHeight / rows

                revealedSectors.forEach { index ->
                    val col = index % columns
                    val row = index / columns

                    drawIntoCanvas { canvas ->
                        canvas.save()
                        canvas.clipRect(
                            androidx.compose.ui.geometry.Rect(
                                topLeft = Offset(col * sectorW, row * sectorH),
                                bottomRight = Offset(
                                    col * sectorW + sectorW,
                                    row * sectorH + sectorH
                                )
                            )
                        )
                        with(sharpPainter) {
                            draw(
                                size = Size(canvasWidth, canvasHeight),
                                colorFilter = null
                            )
                        }
                        canvas.restore()
                    }
                }
            }

            // ── 3. Rejilla sobre todos los sectores ───────────────────────────
            val sectorW = canvasWidth / columns
            val sectorH = canvasHeight / rows
            val gridPaint = androidx.compose.ui.graphics.Paint().apply {
                color = Color.Black.copy(alpha = 0.30f)
                style = PaintingStyle.Stroke
                strokeWidth = 1.5f
            }
            drawIntoCanvas { canvas ->
                (0 until totalSectors).forEach { index ->
                    val col = index % columns
                    val row = index / columns
                    canvas.drawRect(
                        left = col * sectorW,
                        top = row * sectorH,
                        right = col * sectorW + sectorW,
                        bottom = row * sectorH + sectorH,
                        paint = gridPaint
                    )
                }
            }
        }
    }
}