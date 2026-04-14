package com.example.progressbar.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.progressbar.dial.ProgressBar

@Composable
fun HomeScreen(name: String, modifier : Modifier){
    Column {
        Text(
            text = " $name",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = Color.Red,
            modifier = modifier
                .fillMaxWidth()
                .background(Color.DarkGray)
        )
        ProgressBar(modifier = Modifier.padding(16.dp), )
        TimerScreen()
    }
}





