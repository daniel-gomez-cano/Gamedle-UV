package com.gamedleuv.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.hazeEffect


@Composable
fun BlurredImage(
    imageUrl: String?,
    revealedSectors: List<Int>,
    modifier: Modifier = Modifier,
    columns: Int = 3,
    rows: Int = 3,
) {
    val hazeState = remember { HazeState() }
    val totalSectors = columns * rows

    Box(modifier = modifier) {

        // imagen base
        AsyncImage(
            model = imageUrl,
            contentDescription = "game image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .haze(hazeState)
        )

        //Overlay: sectores NO revelados con blur encima
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()

                    val sectorW = size.width / columns
                    val sectorH = size.height / rows

                    (0 until totalSectors).forEach { index ->
                        if (index !in revealedSectors) {
                            val col = index % columns
                            val row = index / columns
                            // borde sutil entre sectores
                            drawRect(
                                color = Color.Black.copy(alpha = 0.08f),
                                topLeft = Offset(col * sectorW + 1f, row * sectorH + 1f),
                                size = Size(sectorW - 2f, sectorH - 2f)
                            )
                        }
                    }
                }
                .hazeEffect(
                    state = hazeState,
                    style = HazeDefaults.style(
                        backgroundColor = Color.Black,
                        blurRadius = 18.dp,
                        noiseFactor = 0f
                    )
                )
                ) {
                    // Excluye del blur los sectores ya revelados
                }

        // Sectores revelados: dibuja un recuadro transparente encima
        // Esto "cancela" el blur en los sectores que ya se revelaron
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    val sectorW = size.width / columns
                    val sectorH = size.height / rows

                    revealedSectors.forEach { index ->
                        val col = index % columns
                        val row = index / columns
                        // limpia el sector revelado
                        drawRect(
                            color = Color.Transparent,
                            topLeft = Offset(col * sectorW, row * sectorH),
                            size = Size(sectorW, sectorH),
                            blendMode = androidx.compose.ui.graphics.BlendMode.Clear
                        )
                    }
                }
        )
    }


}