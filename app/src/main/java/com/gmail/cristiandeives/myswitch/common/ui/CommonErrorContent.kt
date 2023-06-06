package com.gmail.cristiandeives.myswitch.common.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gmail.cristiandeives.myswitch.common.ui.theme.MySwitchTheme

@Composable
fun CommonErrorContent(
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        modifier = modifier,
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp),
        )
    }
}

// region Previews
@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MySwitchTheme {
        CommonErrorContent(
            text = "Something went wrong.",
            modifier = Modifier.padding(16.dp),
        )
    }
}
// endregion
