package com.example.progressbar.dialogs

sealed interface DialogButtonConfig {
    object None : DialogButtonConfig                      // No buttons (custom handling)
    object OkOnly : DialogButtonConfig                     // Single "OK" button
    object OkCancel : DialogButtonConfig                   // "OK" + "Cancel" buttons

    // 🔹 Advanced: Custom button labels/actions
    data class Custom(
        val confirmLabel: String = "OK",
        val confirmAction: () -> Unit,
        val dismissLabel: String = "Cancel",
        val dismissAction: (() -> Unit)? = null
    ) : DialogButtonConfig
}