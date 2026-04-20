package com.example.progressbar.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun WarningDialog(
    show: MutableState<Boolean>,
    title: String,
    description: String,
    onConfirm: (() -> Unit)? = null
) {
    if (!show.value) return

    AlertDialog(
        // All 3 dismissal paths auto-set show.value = false
        onDismissRequest = { show.value = false },
        title = { Text(title, style = MaterialTheme.typography.headlineSmall) },
        text = { Text(description, style = MaterialTheme.typography.bodyMedium) },

        confirmButton = {
            TextButton(onClick = {
                show.value = false // ✅ Auto-closes
                onConfirm?.invoke() // 🟢 Optional extra action
            }) {
                Text("OK")
            }
        },

//        dismissButton = {
//            TextButton(onClick = { show.value = false }) { // ✅ Auto-closes
//                Text("Cancel")
//            }
//        }
    )
}
// --- using --
//WarningDialog(
//show = showTotalDurationDialog,
//title = "Fucking Alert !!!",
//description = "Test for my dialog",
//onConfirm = {
//    // ✅ Optional: Run code when user taps OK
////                        viewModel.reset()
//    // Dialog auto-closes via show.value = false inside WarningDialog
//}
//)