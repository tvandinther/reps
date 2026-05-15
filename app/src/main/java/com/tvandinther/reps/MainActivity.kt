package com.tvandinther.reps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tvandinther.reps.ui.RepsApp
import com.tvandinther.reps.ui.theme.RepsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RepsTheme {
                RepsApp()
            }
        }
    }
}
