package com.gmail.cristiandeives.myswitch.common.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.navigation.compose.rememberNavController
import com.gmail.cristiandeives.myswitch.common.ui.theme.MySwitchTheme
import com.gmail.cristiandeives.myswitch.navigation.MySwitchNavHost
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

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

                val navController = rememberNavController()
                MySwitchNavHost(navController)
            }
        }
    }
}
