package com.gmail.cristiandeives.myswitch.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleObserver

@Composable
fun ObserveLifecycle(observer: LifecycleObserver) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(observer) {
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}