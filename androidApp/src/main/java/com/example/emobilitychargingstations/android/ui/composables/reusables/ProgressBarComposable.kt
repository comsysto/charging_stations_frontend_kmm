package com.example.emobilitychargingstations.android.ui.composables.reusables

import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProgressBarComposable(modifier: Modifier = Modifier) = CircularProgressIndicator(
    modifier = modifier.width(78.dp),
    color = MaterialTheme.colorScheme.surfaceVariant,
    trackColor = MaterialTheme.colorScheme.secondary
)