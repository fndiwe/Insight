package com.franklinndiwe.insight.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.utils.AppUtils.listOfFonts

val Montserrat = FontFamily(
    Font(R.font.montserrat),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold)
)
val GolosText = FontFamily(
    Font(R.font.golos_text),
    Font(R.font.golos_text_medium, FontWeight.Medium),
    Font(R.font.golos_text_semibold, FontWeight.SemiBold)
)
val MerriweatherSans = FontFamily(
    Font(R.font.merriweather_sans),
    Font(R.font.merriweather_sans_medium, FontWeight.Medium),
    Font(R.font.merriweather_sans_semibold, FontWeight.SemiBold)
)
val NotoSans = FontFamily(
    Font(R.font.noto_sans),
    Font(R.font.noto_sans_medium, FontWeight.Medium),
    Font(R.font.noto_sans_semibold, FontWeight.SemiBold)
)
val OpenSans = FontFamily(
    Font(R.font.open_sans),
    Font(R.font.open_sans_medium, FontWeight.Medium),
    Font(R.font.open_sans_semibold, FontWeight.SemiBold)
)

fun typography(index: Int): Typography {
    val appFont = try {
        listOfFonts[index]
    } catch (e: IndexOutOfBoundsException) {
        listOfFonts[0]
    }.second
    return Typography(
        displayLarge = Typography().displayLarge.copy(fontFamily = appFont),
        displayMedium = Typography().displayMedium.copy(fontFamily = appFont),
        displaySmall = Typography().displaySmall.copy(fontFamily = appFont),
        headlineLarge = Typography().headlineLarge.copy(fontFamily = appFont),
        headlineMedium = Typography().headlineMedium.copy(fontFamily = appFont),
        headlineSmall = Typography().headlineSmall.copy(fontFamily = appFont),
        titleLarge = Typography().titleLarge.copy(fontFamily = appFont),
        titleMedium = Typography().titleMedium.copy(fontFamily = appFont),
        titleSmall = Typography().titleSmall.copy(fontFamily = appFont),
        bodyLarge = Typography().bodyLarge.copy(fontFamily = appFont),
        bodyMedium = Typography().bodyMedium.copy(fontFamily = appFont),
        bodySmall = Typography().bodySmall.copy(fontFamily = appFont),
        labelMedium = Typography().labelMedium.copy(fontFamily = appFont),
        labelLarge = Typography().labelLarge.copy(fontFamily = appFont),
        labelSmall = Typography().labelSmall.copy(fontFamily = appFont)
    )
}