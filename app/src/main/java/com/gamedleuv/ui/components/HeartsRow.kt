package com.gamedleuv.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gamedleuv.R

@Composable
fun HeartsRow(
    total: Int = 5,
    filled: Int = 2, // cuantos están "llenos"
    size: Int = 20,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(total) { index ->
            val icon = if (index < filled) {
                R.drawable.heart_filled
            } else {
                R.drawable.heart_outline
            }

            Icon(
                painter = painterResource(id = icon),
                contentDescription = "heart",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(size.dp)
            )
        }
    }
}