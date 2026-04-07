package com.gamedleuv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gamedleuv.R
import com.gamedleuv.ui.theme.GamedleUVTheme

@Composable
fun ProfileButton(
    img: Int = R.drawable.profile,
    transparent: Boolean = false,
    color: Color = MaterialTheme.colorScheme.secondary,
    iconSize: androidx.compose.ui.unit.Dp = 24.dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (transparent) Color.Transparent
                else MaterialTheme.colorScheme.background
            )
            .border(
                4.dp,
                color,
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(img),
            contentDescription = "icono",
            tint = color,
            modifier = Modifier.size(iconSize)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewProfileButton() {
    GamedleUVTheme {
        ProfileButton(onClick = {})
    }
}