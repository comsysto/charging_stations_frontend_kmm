package com.example.emobilitychargingstations.android.ui.composables.reusables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import com.example.emobilitychargingstations.android.models.ToggleInfo

@Composable
fun RadioButtonsComposable(socketTypeButtons: SnapshotStateList<ToggleInfo>) {
    socketTypeButtons.forEach { toggleInfo ->
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
            RadioButton(selected = toggleInfo.isChecked, onClick = {
                socketTypeButtons.replaceAll {
                    it.copy(isChecked = it.title == toggleInfo.title)
                }
            })
            Text(text = toggleInfo.title)
        }
    }
}