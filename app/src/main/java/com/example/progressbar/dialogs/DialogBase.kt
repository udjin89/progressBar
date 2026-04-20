package com.example.progressbar.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun DialogBase(
    show: MutableState<Boolean>,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // ✅ Guard: only compose when visible
    if (!show.value) return

    Dialog(
        onDismissRequest = {
            show.value = false
            onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false // 🔑 Critical: lets your content control size
        )
    ) {
        // ✅ Blank canvas for your UI
        content()
    }
}