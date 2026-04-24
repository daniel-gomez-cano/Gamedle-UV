package com.gamedleuv.ui.components



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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.gamedleuv.ui.theme.GamedleUVTheme

@Composable
fun AppPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    visualTransformation: PasswordVisualTransformation
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
        visualTransformation = visualTransformation,

        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.secondary
        ),

        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFE5E5E5),
            unfocusedContainerColor = Color(0xFFE5E5E5),

            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,

            cursorColor = MaterialTheme.colorScheme.secondary
        ),

        shape = RoundedCornerShape(50.dp),

        modifier = modifier
            .fillMaxWidth()
            .border(
                2.dp,
                MaterialTheme.colorScheme.secondary,
                RoundedCornerShape(50.dp)
            )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewAppPasswordField() {
    GamedleUVTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            AppPasswordField(
                value = "",
                onValueChange = {},
                placeholder = "Escribe aquí...",
                visualTransformation = PasswordVisualTransformation()
            )
        }
    }
}