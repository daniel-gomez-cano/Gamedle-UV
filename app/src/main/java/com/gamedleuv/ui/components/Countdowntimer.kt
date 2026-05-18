package com.gamedleuv.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gamedleuv.R


@Composable
fun CountdownTimer(
    seconds: Int,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    val displayColor = if (color == Color.Unspecified)
        MaterialTheme.colorScheme.onBackground
    else
        color


    Text(
        text = "$seconds s",
        style = MaterialTheme.typography.bodyMedium,
        color = displayColor
    )

}