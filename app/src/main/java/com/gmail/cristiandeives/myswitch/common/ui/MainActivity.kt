package com.gmail.cristiandeives.myswitch.common.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import com.gmail.cristiandeives.myswitch.common.ui.theme.MySwitchTheme
import com.gmail.cristiandeives.myswitch.navigation.MySwitchNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val systemUiController = rememberSystemUiController()

            MySwitchTheme(dynamicColor = true) {
                val primaryColor = MaterialTheme.colorScheme.primary
                SideEffect {
                    systemUiController.setSystemBarsColor(primaryColor)
                }

                val navController = rememberAnimatedNavController()
                MySwitchNavHost(navController)
            }
        }
    }
}
