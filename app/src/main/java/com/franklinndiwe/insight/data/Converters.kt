package com.franklinndiwe.insight.data

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun listOfIntToString(value: List<Int>) = Json.encodeToString(value)

    @TypeConverter
    fun stringToListOfInt(value: String) = Json.decodeFromString<List<Int>>(value)

}