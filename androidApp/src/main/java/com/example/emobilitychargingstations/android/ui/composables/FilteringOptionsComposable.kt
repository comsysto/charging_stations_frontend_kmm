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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.comsystoreply.emobilitychargingstations.android.R
import com.example.emobilitychargingstations.android.StationsViewModel
import com.example.emobilitychargingstations.android.models.ChargerTypeToggleInfo
import com.example.emobilitychargingstations.android.ui.composables.reusables.getActivityViewModel
import com.example.emobilitychargingstations.android.ui.utilities.getStringIdFromChargerType
import com.example.emobilitychargingstations.models.ChargerTypesEnum

@Composable
fun FilteringOptionsComposable(proceedToNextScreen: () -> Unit, viewModel: StationsViewModel = getActivityViewModel()) {
    val listOfButtonsInfo = mutableListOf<ChargerTypeToggleInfo>()
    val chargerType = viewModel.getUserInfo()?.filterProperties?.chargerType
    ChargerTypesEnum.values().forEach {
        listOfButtonsInfo.add(ChargerTypeToggleInfo(chargerType == it, it))
    }
    val socketTypeButtons = remember {
        val mutableStateList = mutableStateListOf<ChargerTypeToggleInfo>()
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
            ChargerTypeButtonsComposable(socketTypeButtons)
            Row {
                Button(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    enabled = socketTypeButtons.any { toggleInfo -> toggleInfo.isChecked },
                    onClick = {
                        socketTypeButtons.firstOrNull { toggleInfo -> toggleInfo.isChecked }?.chargerType?.let {
                            viewModel.setChargerType(it)
                        }
                        proceedToNextScreen()
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            }
        }
    }
}

@Composable
fun ChargerTypeButtonsComposable(socketTypeButtons: SnapshotStateList<ChargerTypeToggleInfo>) {
    Text(stringResource(R.string.android_charger_type_selection))
    Column(modifier = Modifier.padding(vertical = 10.dp), horizontalAlignment = Alignment.Start) {
        socketTypeButtons.forEach { toggleInfo ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
                RadioButton(selected = toggleInfo.isChecked, onClick = {
                    socketTypeButtons.replaceAll {
                        it.copy(isChecked = it.chargerType == toggleInfo.chargerType)
                    }
                })
                Text(text = stringResource(id = toggleInfo.chargerType.getStringIdFromChargerType()))
            }
        }
    }
}

