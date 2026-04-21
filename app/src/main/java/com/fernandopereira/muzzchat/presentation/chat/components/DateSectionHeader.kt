package com.fernandopereira.muzzchat.presentation.chat.components

import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.withStyle
import com.fernandopereira.muzzchat.ui.common.ThemePreviews
import com.fernandopereira.muzzchat.ui.theme.MuzzChatTheme
import com.fernandopereira.muzzchat.ui.theme.Dimen8

@Composable
fun DateSectionHeader(
    day: String,
    time: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = Dimen8),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Center,
    ) {
        Text(
            text =
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = Bold)) {
                        append(day)
                    }
                    append(" $time")
                },
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
        )
    }
}

@ThemePreviews
@Composable
private fun DateSectionHeaderPreview() {
    MuzzChatTheme {
        Surface {
            DateSectionHeader(day = "Thursday", time = "11:59")
        }
    }
}
