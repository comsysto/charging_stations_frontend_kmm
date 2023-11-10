import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import com.emobilitychargingstations.database.StationEntity
import com.emobilitychargingstations.database.StationsDatabase
import com.emobilitychargingstations.database.UserInfoEntity
import com.example.emobilitychargingstations.data.stations.StationsRepository
import com.example.emobilitychargingstations.data.stations.StationsRepositoryImpl
import com.example.emobilitychargingstations.data.stations.api.StationsApi
import com.example.emobilitychargingstations.data.users.UsersRepository
import com.example.emobilitychargingstations.data.users.UsersRepositoryImpl
import com.example.emobilitychargingstations.domain.stations.StationsUseCase
import com.example.emobilitychargingstations.domain.user.UserUseCase
import com.example.emobilitychargingstations.models.Station
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.dsl.module

fun repositoryModule() = module {
    single { provideApi() }
    single { provideDataSource(get()) }
    single<StationsRepository> { StationsRepositoryImpl(get(), get()) }
    single<UsersRepository> { UsersRepositoryImpl(get()) }
}

fun useCaseModule() = module {
    factory { UserUseCase(get()) }
    factory { StationsUseCase(get()) }
}

fun provideApi(): StationsApi {
    return StationsApi()
}

fun provideDataSource(driver: SqlDriver): StationsDatabase {
    return StationsDatabase(driver, StationEntity.Adapter(
        featuresAdapter = object : ColumnAdapter<List<Station>, String> {
            override fun decode(databaseValue: String): List<Station> {
                return if (databaseValue.isEmpty()){
                    listOf()
                } else {
                    return Json.decodeFromString<List<Station>>(databaseValue)
                }
            }

            override fun encode(value: List<Station>): String {
                return Json.encodeToString(
                    value
                )
            }
        }
    ), UserInfoEntity.Adapter(favoriteStationsAdapter = object : ColumnAdapter<List<Station>, String> {
        override fun decode(databaseValue: String): List<Station> {
            return if (databaseValue.isEmpty()){
                listOf()
            } else {
                return Json.decodeFromString<List<Station>>(databaseValue)
            }
        }
        override fun encode(value: List<Station>): String {
            return Json.encodeToString(
                value
            )
        }
    }))
}