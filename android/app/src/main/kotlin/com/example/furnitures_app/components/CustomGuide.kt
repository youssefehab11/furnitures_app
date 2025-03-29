package com.example.furnitures_app.components

import androidx.annotation.RawRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.furnitures_app.R

@Composable
fun CustomGuide(
    modifier: Modifier,
    @RawRes lottieResId: Int,
    message: String,
    animationSize: Dp = 300.dp,
    onConfirm: (() -> Unit?)? = null
) {
    val preloaderLottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            lottieResId
        )
    )

    val preloaderProgress by animateLottieCompositionAsState(
        preloaderLottieComposition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .width(450.dp)
                .height(450.dp)
                .clip(shape = RoundedCornerShape(16.dp))
                .background(color = colorResource(R.color.black_transparent))
        ) {
            Column {

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(0.8f).fillMaxWidth()
                ) {
                    LottieAnimation(
                        composition = preloaderLottieComposition,
                        progress = preloaderProgress,
                        modifier = Modifier
                            .size(animationSize)
                    )
                }
                Text(
                    message,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(0.2f)
                )
                if (onConfirm != null) {
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier.weight(0.1f).fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                onConfirm()
                            }
                        ) {
                            Text("Got It")
                        }
                    }
                }

            }

        }
    }
}