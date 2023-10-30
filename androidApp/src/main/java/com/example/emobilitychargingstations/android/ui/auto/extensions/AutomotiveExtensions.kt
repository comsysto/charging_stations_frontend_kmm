package com.example.emobilitychargingstations.android.ui.auto.extensions

import android.graphics.Bitmap
import android.text.SpannableString
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.CarLocation
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Metadata
import androidx.car.app.model.Place
import androidx.car.app.model.PlaceMarker
import androidx.car.app.model.Row
import androidx.core.graphics.drawable.IconCompat
import com.comsystoreply.emobilitychargingstations.android.R

fun getPlaceWithMarker(lat: Double, lng: Double, carColor: CarColor, bitmap: Bitmap? = null): Place = Place.Builder(
    CarLocation.create(
        lat, lng
    )
).setMarker(
    if (bitmap == null) markerWithoutIcon(carColor) else markerWithIcon(carColor, bitmap)
).build()

private fun markerWithIcon(carColor: CarColor, bitmap: Bitmap) = PlaceMarker.Builder().setColor(carColor)
    .setIcon(CarIcon.Builder(IconCompat.createWithBitmap(bitmap)).build(), PlaceMarker.TYPE_ICON)
    .build()

private fun markerWithoutIcon(carColor: CarColor) = PlaceMarker.Builder().setColor(carColor)
    .build()


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

fun BuildRowWithTextAndIcon(title: SpannableString, text: String, carIcon: Bitmap, onClickListener: () -> Unit): Row = Row.Builder().apply {
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


fun Screen.getMessageTemplateBuilderWithTitle(title: String, Message: String): MessageTemplate.Builder {
    val messageTemplateBuilder = MessageTemplate.Builder(Message)
    messageTemplateBuilder.apply {
        setTitle(title)
        setHeaderAction(Action.BACK)
    }
    return messageTemplateBuilder
}
fun Screen.getString(stringId: Int): String = this.carContext.getString(stringId)
fun Screen.getString(stringId: Int, stringArgument: String): String = this.carContext.getString(stringId, stringArgument)
