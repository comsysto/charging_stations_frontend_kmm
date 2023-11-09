package com.example.emobilitychargingstations.data.users

import com.emobilitychargingstations.database.UserInfoEntity
import com.example.emobilitychargingstations.models.UserInfo

fun UserInfoEntity.toUserInfo() = UserInfo(
    chargerType = this.chargerType,
    favoriteStations = this.favoriteStations?.toMutableList()
)