package com.whatsapptracker.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.whatsapptracker.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val InterFont = GoogleFont("Inter")
val PlayfairDisplayFont = GoogleFont("Playfair Display")

val InterFontFamily = FontFamily(
    Font(googleFont = InterFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = InterFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = InterFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = InterFont, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = InterFont, fontProvider = provider, weight = FontWeight.Black),
)

val PlayfairFontFamily = FontFamily(
    Font(googleFont = PlayfairDisplayFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = PlayfairDisplayFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = PlayfairDisplayFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = PlayfairDisplayFont, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = PlayfairDisplayFont, fontProvider = provider, weight = FontWeight.Black),
)

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = PlayfairFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = (-1.5).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = PlayfairFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.5).sp,
    ),
    displaySmall = TextStyle(
        fontFamily = PlayfairFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.25).sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = PlayfairFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.25).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = PlayfairFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 26.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
    ),
)
