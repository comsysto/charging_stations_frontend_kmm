package com.example.emobilitychargingstations.models

enum class ChargerTypesEnum(val displayName: String) {
    DC_EU("Standard DC"),
    AC_TYPE_2("Standard AC"),
    AC_TYPE_1("AC Type 1"),
    DC_CHADEMO("DC CHAdeMO"),
    TESLA("Tesla"),
    ALL("All charger types")
}