package com.example.emobilitychargingstations.android.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
                .padding()
                .background(Color.LightGray),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Please select charger type:")
            Column(modifier = Modifier.padding(vertical = 10.dp), horizontalAlignment = Alignment.Start) {
                ChargerTypeRadioButtons(socketTypeButtons)
            }
            Row {
                Button(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    onClick = {
                        socketTypeButtons.replaceAll { it.copy(isChecked = false) }
                        viewModel.setUserInfo(null)
                        proceedToNextScreen()
                    }
                ) {
                    Text("Remove selection")
                }
                Button(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    enabled = socketTypeButtons.any { toggleInfo -> toggleInfo.isChecked },
                    onClick = {
                        viewModel.setUserInfo(socketTypeButtons.firstOrNull { toggleInfo -> toggleInfo.isChecked }?.title)
                        proceedToNextScreen()
                    }
                ) {
                    Text("Proceed")
                }
            }
        }
    }
}

@Composable
fun ChargerTypeRadioButtons(socketTypeButtons: SnapshotStateList<ToggleInfo>) {
    socketTypeButtons.forEachIndexed { index, toggleInfo ->
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