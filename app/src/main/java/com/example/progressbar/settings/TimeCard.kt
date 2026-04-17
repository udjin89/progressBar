package com.example.progressbar.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.util.TimeUtils.formatDuration
import com.example.progressbar.utils.formatDuration

@Composable
fun TimeCard(
    title: String,
    timeSeconds: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
        Box(
            modifier = modifier
                .background(Color(0xFFECFDF5), RoundedCornerShape(12.dp))  // GreenLight80
                .border(1.dp, Color(0xFFC1C7C0), RoundedCornerShape(12.dp)) // GreenGrey80
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF3A4A42)
                )
                Text(
                    text = formatDuration(timeSeconds),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF059669), // Forest40
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
}