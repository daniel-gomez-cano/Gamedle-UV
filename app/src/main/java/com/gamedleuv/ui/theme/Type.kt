package com.gamedleuv.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import androidx.compose.ui.text.font.*
import com.gamedleuv.R


val Play = FontFamily(
    Font(R.font.play_regular, FontWeight.Normal),
    Font(R.font.play_bold, FontWeight.Bold)
)

val Inter = FontFamily(
    Font(R.font.inter_18pt_medium, FontWeight.Medium),
    Font(R.font.inter_18pt_regular, FontWeight.Normal)
)

val Typography = Typography(

    //title
    titleLarge = TextStyle(
        fontFamily = Play,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),

    titleMedium = TextStyle(
        fontFamily = Play,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    titleSmall = TextStyle(
        fontFamily = Play,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),

    //label
    labelLarge = TextStyle(
        fontFamily = Play,
        fontWeight = FontWeight.Bold,
        fontSize = 25.sp
    ),

    labelMedium = TextStyle(
        fontFamily = Play,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),

    labelSmall = TextStyle(
        fontFamily = Play,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),


    //Body
    bodyLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),

    bodyMedium = TextStyle(
        fontFamily = Play,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),

    bodySmall = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),



    //display

    displayMedium = TextStyle(
        fontFamily = Play,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    )


)
/*

    PANTALLAS PRINCIPALES

displayLarge
displayMedium
displaySmall

headlineLarge
headlineMedium
headlineSmall

titleLarge
titleMedium
titleSmall

    CUERPO BASE

bodyLarge
bodyMedium
bodySmall

    UI/BOTONES

labelLarge
labelMedium
labelSmall


 */



    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
