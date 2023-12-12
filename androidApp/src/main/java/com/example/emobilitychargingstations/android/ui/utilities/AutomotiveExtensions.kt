package com.example.emobilitychargingstations.android.ui.utilities

import android.graphics.Bitmap
import android.location.Location
import android.text.SpannableString
import android.text.Spanned
import androidx.appcompat.content.res.AppCompatResources
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.CarLocation
import androidx.car.app.model.Distance
import androidx.car.app.model.DistanceSpan
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Metadata
import androidx.car.app.model.Place
import androidx.car.app.model.PlaceMarker
import androidx.car.app.model.Row
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import com.comsystoreply.emobilitychargingstations.android.R
import com.example.emobilitychargingstations.data.extensions.getLatitude
import com.example.emobilitychargingstations.data.extensions.getLongitude
import com.example.emobilitychargingstations.models.Station
import com.example.emobilitychargingstations.models.UserInfo
import com.example.emobilitychargingstations.models.UserLocation
import kotlinx.coroutines.launch

fun getPlaceWithMarker(
    latitude: Double,
    longitude: Double,
    carColor: CarColor,
    markerIcon: Bitmap? = null): Place = Place.Builder (
        CarLocation.create(
            latitude, longitude
        )
    ).setMarker(
        if (markerIcon == null) markerWithoutIcon(carColor) else markerWithIcon(carColor, markerIcon)
    ).build()

private fun markerWithIcon(carColor: CarColor, bitmap: Bitmap) = PlaceMarker.Builder()
    .setIcon(createCarIconFromBitmap(bitmap = bitmap), PlaceMarker.TYPE_ICON)
    .setColor(carColor)
    .build()

private fun markerWithoutIcon(carColor: CarColor) = PlaceMarker.Builder().setColor(carColor)
    .build()

fun createCarIconFromBitmap(bitmap: Bitmap): CarIcon {
    return CarIcon.Builder(IconCompat.createWithBitmap(bitmap)).build()
}

fun buildRowWithPlace(title: SpannableString, place: Place, onClickFunction: () -> Unit): Row =
    Row.Builder().apply {
        setBrowsable(true)
        setTitle(title)
        setMetadata(buildMetadata(place))
        setOnClickListener {
            onClickFunction()
        }
    }.build()

fun buildRowWithText(title: SpannableString, text: String): Row = Row.Builder().apply {
    setBrowsable(false)
    setTitle(title)
    addText(text)
}.build()

fun buildClickableRowWithTextAndIcon(title: SpannableString, text: String, carIcon: Bitmap, onClickListener: () -> Unit): Row = Row.Builder().apply {
    setBrowsable(false)
    setImage(CarIcon.Builder(IconCompat.createWithBitmap(carIcon)).build())
    setTitle(title)
    addText(text)
    setOnClickListener (onClickListener)
}.build()


fun buildMetadata(place: Place): Metadata =
    Metadata.Builder().setPlace(
        place
    ).build()


fun Screen.getMessageTemplateBuilderWithTitle(title: String, message: String): MessageTemplate.Builder {
    val messageTemplateBuilder = MessageTemplate.Builder(message)
    messageTemplateBuilder.apply {
        setTitle(title)
        setHeaderAction(Action.BACK)
    }
    return messageTemplateBuilder
}

fun Screen.getFavoritesAction(station: Station, userInfo: UserInfo?, onFavoriteChange: (userInfo: UserInfo) -> Unit): Action {
    val isAlreadyInFavorites = userInfo?.favoriteStations?.firstOrNull { it.id == station.id }?.let { true } ?: false
    val actionText = if (isAlreadyInFavorites) getString(R.string.auto_navigation_complete_remove_action) else getString(R.string.auto_navigation_complete_add_action)
    return Action.Builder().apply {
        setTitle(actionText)
        setOnClickListener {
            lifecycleScope.launch {
                if (isAlreadyInFavorites) {
                    userInfo?.favoriteStations?.remove(station)
                    onFavoriteChange(userInfo!!)
                } else {
                    if (userInfo?.favoriteStations.isNullOrEmpty()) onFavoriteChange(UserInfo(filterProperties = userInfo?.filterProperties, favoriteStations = mutableListOf(station)))
                    else {
                        userInfo?.favoriteStations?.add(station)
                        onFavoriteChange(userInfo!!)
                    }
                }
            }
        }
    }.build()
}

fun Screen.getTransparentCarColor() = CarColor.createCustom(
    Color.Transparent.hashCode(),
    Color.Transparent.hashCode()
)

fun Screen.getDrawableAsBitmap(resourceId: Int) = AppCompatResources.getDrawable(carContext, resourceId)?.toBitmap()

fun Screen.getString(stringId: Int): String = this.carContext.getString(stringId)
fun Screen.getString(stringId: Int, stringArgument: String): String = this.carContext.getString(stringId, stringArgument)

fun Station.getTitleAsSpannableStringAndAddDistance(userLocation: UserLocation): SpannableString {
    val title = SpannableString("${properties.street} - ")
    val distanceResult: FloatArray = floatArrayOf(0.0f)
    this.let {
        Location.distanceBetween(
            userLocation.latitude,
            userLocation.longitude,
            it.geometry.getLatitude(),
            it.geometry.getLongitude(),
            distanceResult
        )
        title.setSpan(
            DistanceSpan.create(
                Distance.create(
                    distanceResult[0] / 1000.toDouble(),
                    Distance.UNIT_KILOMETERS_P1
                )
            ),
            title.length,
            title.length,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
    }
    return title
}