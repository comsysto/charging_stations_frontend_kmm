package com.example.emobilitychargingstations.data.stations.database

import com.emobilitychargingstations.database.UserInfoEntity
import com.example.emobilitychargingstations.models.UserInfo

fun UserInfoEntity.toUserInfo() = UserInfo(
    chargerType = this.chargerType
)