package com.example.furnitures_app.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.furnitures_app.R

@Composable
fun CircularIconButton(
    @DrawableRes icon: Int,
    color: Color,
    contentDescription: String,
    padding: Dp = 0.dp,
    onClick: () -> Unit,
){
    IconButton(onClick = { onClick() }) {
        Icon(
            painterResource(icon),
            tint = color,
            modifier = Modifier
                .clip(shape = RoundedCornerShape(50.dp))
                .background(color = colorResource(R.color.black_transparent))
                .padding(padding),
            contentDescription = contentDescription
        )
    }
}