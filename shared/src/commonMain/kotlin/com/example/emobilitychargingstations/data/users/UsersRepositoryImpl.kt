package com.example.emobilitychargingstations.data.users

import com.example.emobilitychargingstations.models.UserInfo
import kotlinx.coroutines.flow.Flow

interface UsersRepositoryImpl {
    fun getUserInfo(): UserInfo?

    suspend fun getUserInfoAsFlow(): Flow<UserInfo?>

    suspend fun setUserInfo(userInfo: UserInfo)
}