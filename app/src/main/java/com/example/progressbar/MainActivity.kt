package com.example.progressbar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.progressbar.screen.HomeScreen
import com.example.progressbar.ui.theme.ProgressBarTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true//false // White icons for dark background
        }

        setContent {
            ProgressBarTheme(
                darkTheme = false,
                dynamicColor = false
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Scaffold(
                        modifier = Modifier,
                            //.fillMaxSize().border(5.dp, Yellow40),
                           // .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Bottom)),
                        contentWindowInsets = WindowInsets(0)
                    ) { innerPadding ->
                        HomeScreen(
                            name = "ADHD progress bar",
                            modifier = Modifier
                                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
                                .padding(innerPadding)

                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), // side padding for content
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress bar with TOP margin
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp) // ← Top margin here
        )

        Spacer(modifier = Modifier.height(16.dp)) // spacing between progress and text

        Text(
            text = "Hello $name! Yeah this is Sparta!",
            modifier = Modifier.padding(top = 8.dp),
            color = Color.Cyan
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProgressBarTheme {
        Greeting("Android")
    }
}