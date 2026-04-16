package com.example.progressbar.test

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SwapableScreen() {
    // 🔹 State drives which composable is shown
    var currentView by remember { mutableStateOf("list") }

    Column(
        modifier = Modifier.fillMaxSize().padding(10.dp, 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 🔹 Control buttons
        Row {
            Button(onClick = { currentView = "list" }) { Text("List") }
            Button(onClick = { currentView = "grid" }) { Text("Grid") }
            Button(onClick = { currentView = "detail" }) { Text("Detail") }
        }

        // 🔥 Animated swap
        AnimatedContent(
            targetState = currentView,
            transitionSpec = { getTransitionSpec(AnimationType.SwipeRight)  },
            label = "SwipeTransition"
        ){ currentView ->
            // 🔹 Swap content based on state
            when (currentView) {
                "list" -> ListView()
                "grid" -> GridView()
                "detail" -> DetailView()
            }
        }
    }
}
//AnimatedContent(
//targetState = currentView,
//transitionSpec = {
//    ContentTransform(
//        targetContentEnter = fadeIn(animationSpec = tween(250)),
//        initialContentExit = fadeOut(animationSpec = tween(250))
//    )
//},
//label = "ViewTransition"
//)



//AnimatedContent(
//targetState = currentIndex,
//transitionSpec = {
//    ContentTransform(
//        targetContentEnter = slideInHorizontally(
//            initialOffsetX = { fullWidth -> -fullWidth }, // Start off-screen left
//            animationSpec = tween(300, easing = FastOutSlowInEasing)
//        ),
//        initialContentExit = slideOutHorizontally(
//            targetOffsetX = { fullWidth -> fullWidth }, // Exit off-screen right
//            animationSpec = tween(300, easing = FastOutSlowInEasing)
//        )
//    )
//},
//label = "SwipeTransition"
//)
@Composable
fun ListView() { Text("📋 List View") }
@Composable
fun GridView() { Text("🔲 Grid View") }
@Composable
fun DetailView() { Text("🔍 Detail View") }