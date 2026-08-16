package com.bws.ytminiplayer.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import kotlinytminiplayer.shared.generated.resources.Res
import kotlinytminiplayer.shared.generated.resources.roboto_black
import kotlinytminiplayer.shared.generated.resources.roboto_bold
import kotlinytminiplayer.shared.generated.resources.roboto_medium
import kotlinytminiplayer.shared.generated.resources.roboto_regular
import org.jetbrains.compose.resources.Font as ResourceFont

/* ------------------------------------------------------------------ */
/*  Tipografía Roboto                                                  */
/*  Requiere los .ttf en composeResources/font.                        */
/* ------------------------------------------------------------------ */
@Composable
internal fun robotoFamily(): FontFamily = FontFamily(
    ResourceFont(Res.font.roboto_regular, FontWeight.Normal),
    ResourceFont(Res.font.roboto_medium, FontWeight.Medium),
    ResourceFont(Res.font.roboto_bold, FontWeight.Bold),
    ResourceFont(Res.font.roboto_black, FontWeight.Black),
)
