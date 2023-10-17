package com.example.emobilitychargingstations.android.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.emobilitychargingstations.android.StationsViewModel
import com.example.emobilitychargingstations.models.ChargerTypesEnum
import com.example.emobilitychargingstations.android.models.ToggleInfo

@Composable
fun ChargerTypeSelectionScreen(proceedToNextScreen: () -> Unit, viewModel: StationsViewModel) {
    val listOfButtonsInfo = mutableListOf<ToggleInfo>()
    val chargerType = viewModel.getUserInfo()?.chargerType
    ChargerTypesEnum.values().forEach {
        listOfButtonsInfo.add(ToggleInfo(chargerType == it.displayName, it.displayName))
    }
    val socketTypeButtons = remember {
        val mutableStateList = mutableStateListOf<ToggleInfo>()
        mutableStateList.addAll(listOfButtonsInfo)
        mutableStateList
    }

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Please select charger type")
            ChargerTypeRadioButtons(socketTypeButtons)
            Button(onClick = {
                viewModel.setUserInfo(socketTypeButtons.filter { toggleInfo -> toggleInfo.isChecked }.first().title)
                proceedToNextScreen()
            }) {
                Text("Proceed")
            }
        }
    }
}
@Composable
fun ChargerTypeRadioButtons(socketTypeButtons: SnapshotStateList<ToggleInfo>) {
    socketTypeButtons.forEachIndexed { index, toggleInfo ->
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = toggleInfo.isChecked, onClick = {
                socketTypeButtons.replaceAll {
                    it.copy(isChecked = it.title == toggleInfo.title)
                }
            })
            Text(text = toggleInfo.title)
        }
    }
}