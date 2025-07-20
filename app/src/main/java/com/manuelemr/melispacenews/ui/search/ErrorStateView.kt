package com.manuelemr.melispacenews.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.manuelemr.melispacenews.R

@Composable
fun ErrorStateView(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    ctaTitle: String,
    onCtaClick: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge
        )

        Image(
            modifier = Modifier.size(50.dp),
            painter = painterResource(R.drawable.ic_warning),
            contentDescription = null
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge
        )

        OutlinedButton(
            modifier = Modifier.defaultMinSize(minWidth = 80.dp),
            contentPadding = PaddingValues(horizontal = 8.dp),
            onClick = onCtaClick
        ) {
            Text(text = ctaTitle)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorStateViewPreview() {
    ErrorStateView(
        title = "Error",
        message = "Something went wrong",
        ctaTitle = "Retry"
    ) {
    }
}