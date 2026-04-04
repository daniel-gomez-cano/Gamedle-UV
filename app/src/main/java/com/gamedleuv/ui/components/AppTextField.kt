package com.gamedleuv.ui.components



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.gamedleuv.ui.theme.GamedleUVTheme

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,

        placeholder = {
            Text(
                text = placeholder,
                color = Color(0xFF3E3E3E)
            )
        },

        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onBackground
        ),

        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFE5E5E5),
            unfocusedContainerColor = Color(0xFFE5E5E5),

            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,

            cursorColor = MaterialTheme.colorScheme.secondary
        ),

        shape = RoundedCornerShape(20.dp),

        modifier = modifier
            .fillMaxWidth()
            .border(
                2.dp,
                MaterialTheme.colorScheme.secondary,
                RoundedCornerShape(20.dp)
            )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewAppTextField() {
    GamedleUVTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            AppTextField(
                value = "",
                onValueChange = {},
                placeholder = "Escribe aquí..."
            )
        }
    }
}