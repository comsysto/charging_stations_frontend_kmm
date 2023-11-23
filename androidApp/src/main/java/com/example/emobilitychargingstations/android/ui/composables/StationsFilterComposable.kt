package com.example.emobilitychargingstations.android.ui.composables

import android.util.Log
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.comsystoreply.emobilitychargingstations.android.R
import com.example.emobilitychargingstations.android.StationsViewModel
import com.example.emobilitychargingstations.android.models.ChargerTypeToggleInfo
import com.example.emobilitychargingstations.android.models.ChargingTypeToggleInfo
import com.example.emobilitychargingstations.android.models.ToggleInfo
import com.example.emobilitychargingstations.android.ui.composables.reusables.RadioButtonsComposable
import com.example.emobilitychargingstations.android.ui.composables.reusables.getActivityViewModel
import com.example.emobilitychargingstations.android.ui.utilities.getStringIdFromChargerType
import com.example.emobilitychargingstations.android.ui.utilities.getStringIdFromChargingType
import com.example.emobilitychargingstations.models.ChargingTypeEnum
import org.koin.androidx.compose.koinViewModel


@Composable
fun StationsFilterComposable(viewModel: StationsViewModel = getActivityViewModel()) {
    val userInfo = viewModel.getUserInfo()
    Box {
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()) {
//            AvailabilityFilterComposable()
            ChargingTypeFilterComposable(userInfo?.filterProperties?.chargingType)
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Change Charger Type")
            }
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Edit Favorites")
            }
        }
    }
}

@Composable
fun AvailabilityFilterComposable() {
    val socketTypeButtons = remember {
        val mutableStateList = mutableStateListOf<ToggleInfo>()
        mutableStateList.add(ToggleInfo(false, "Yes"))
        mutableStateList.add(ToggleInfo(true, "No"))
        mutableStateList
    }
        Text(text = "Display only currently available stations")
        RadioButtonsComposable(socketTypeButtons = socketTypeButtons)

}

@Composable
fun ChargingTypeFilterComposable(chargingTypeEnum: ChargingTypeEnum?) {
    val listOfButtonsInfo = mutableListOf<ChargingTypeToggleInfo>()
    ChargingTypeEnum.values().forEach {
        listOfButtonsInfo.add(ChargingTypeToggleInfo((chargingTypeEnum ?: ChargingTypeEnum.ANY) == it, it))
    }
    val socketTypeButtons = remember {
        val mutableStateList = mutableStateListOf<ChargingTypeToggleInfo>()
        mutableStateList.addAll(listOfButtonsInfo)
        mutableStateList
    }
    Text(stringResource(R.string.android_charging_type_selection))
    ChargingTypeButtonsComposable(socketTypeButtons = socketTypeButtons)
}

@Composable
fun ChargingTypeButtonsComposable(socketTypeButtons: SnapshotStateList<ChargingTypeToggleInfo>, viewModel: StationsViewModel = getActivityViewModel()) {
    socketTypeButtons.forEachIndexed { index, toggleInfo ->
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
            RadioButton(selected = toggleInfo.isChecked, onClick = {
                viewModel.setChargingType(toggleInfo.chargingType)
                socketTypeButtons.replaceAll {
                    it.copy(isChecked = it.chargingType == toggleInfo.chargingType)
                }
            })
            Text( stringResource(toggleInfo.chargingType.getStringIdFromChargingType()))
        }
    }
}