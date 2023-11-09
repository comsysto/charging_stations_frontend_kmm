package com.example.emobilitychargingstations.domain.user

import com.example.emobilitychargingstations.data.users.UsersRepositoryImpl
import com.example.emobilitychargingstations.models.UserInfo
import kotlinx.coroutines.flow.Flow

class UserUseCase(val usersRepository: UsersRepositoryImpl) {
    fun getUserInfo(): UserInfo? {
        return usersRepository.getUserInfo()
    }

    suspend fun getUserInfoAsFlow(): Flow<UserInfo?> {
        return usersRepository.getUserInfoAsFlow()
    }

    suspend fun setUserInfo(userInfo: UserInfo) {
        usersRepository.setUserInfo(userInfo)
    }
}