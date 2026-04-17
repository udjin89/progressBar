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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.progressbar.ui.theme.Forest40
import com.example.progressbar.ui.theme.GreenGrey80
import com.example.progressbar.ui.theme.GreenGrey90
import com.example.progressbar.ui.theme.GreenLight80
import com.example.progressbar.utils.formatDuration

@Composable
fun TimeCard(
    title: String,
    timeMillis: Long,
    modifier: Modifier = Modifier
) {
        Box(
            modifier = modifier
                .background(GreenLight80, RoundedCornerShape(12.dp))  // GreenLight80
                .border(1.dp, GreenGrey80, RoundedCornerShape(12.dp)) // GreenGrey80
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
                    color = GreenGrey90
                )
                Text(
                    text = formatDuration(timeMillis),
                    style = MaterialTheme.typography.titleMedium,
                    color = Forest40, // Forest40
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
}