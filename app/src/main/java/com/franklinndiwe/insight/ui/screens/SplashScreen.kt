package com.franklinndiwe.insight.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.utils.unit
import com.franklinndiwe.insight.viewmodel.AppViewModelProvider
import com.franklinndiwe.insight.viewmodel.SplashScreenViewModel

@Composable
fun AppSplashScreen(
    isFirstLaunch: Boolean = false,
    onTimeout: unit = {},
    splashScreenViewModel: SplashScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    // This will always refer to the latest onTimeout function that
    // LandingScreen was recomposed with
    val currentOnTimeout by rememberUpdatedState(onTimeout)
    // Create an effect that matches the lifecycle of LandingScreen.
    // If LandingScreen recomposes or onTimeout changes,
    // the delay shouldn't start again.
    LaunchedEffect(true) {
        if (isFirstLaunch) splashScreenViewModel.setupApp().await()
        currentOnTimeout()
    }
    Surface {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                Modifier.size(150.dp)
            )
        }
    }
}
