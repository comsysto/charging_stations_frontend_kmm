package com.example.emobilitychargingstations.android.ui.auto.extensions

import android.graphics.Bitmap
import android.text.SpannableString
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.CarLocation
import androidx.car.app.model.Metadata
import androidx.car.app.model.Place
import androidx.car.app.model.PlaceMarker
import androidx.car.app.model.Row
import androidx.core.graphics.drawable.IconCompat

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


fun buildRow(title: SpannableString, place: Place, onClickFunction: () -> Unit): Row =
    Row.Builder().setBrowsable(false).setTitle(title).setMetadata(
        buildMetadata(place)
    ).setOnClickListener {
        onClickFunction()
    }.build()

fun buildMetadata(place: Place): Metadata =
    Metadata.Builder().setPlace(
        place
    ).build()
