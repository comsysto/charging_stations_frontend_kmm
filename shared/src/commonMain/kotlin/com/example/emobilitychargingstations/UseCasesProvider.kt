package com.example.emobilitychargingstations

import com.example.emobilitychargingstations.domain.stations.StationsUseCase
import com.example.emobilitychargingstations.domain.user.UserUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UseCasesProvider(): KoinComponent {
    val userUseCase: UserUseCase by inject()
    val stationsUseCase: StationsUseCase by inject()
}