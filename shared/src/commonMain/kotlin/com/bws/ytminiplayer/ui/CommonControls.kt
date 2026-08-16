package com.bws.ytminiplayer.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.bws.ytminiplayer.ui.theme.PlayerColors

/** Botón pulsable con un icono dibujado en Canvas. */
@Composable
internal fun CanvasIconButton(
    onClick: () -> Unit,
    iconSize: Int = 18,
    draw: DrawScope.() -> Unit
) {
    IconButton(onClick = onClick, modifier = Modifier.size(28.dp)) {
        Canvas(Modifier.size(iconSize.dp)) { draw() }
    }
}

/** Botón circular tenue (repetir / aleatorio). */
@Composable
internal fun CircleControl(onClick: () -> Unit, content: @Composable () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(39.dp)
            .clip(CircleShape)
            .background(PlayerColors.ControlCircle)
    ) { content() }
}
