package com.akobusinska.letsplay.data.local

import androidx.room.TypeConverter
import com.akobusinska.letsplay.data.entities.Play
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class Converters {

    @TypeConverter
    fun fromListOfExpansions(value: List<Int>): String {
        val type = object : TypeToken<List<Int>>() {}.type
        return Gson().toJson(value, type)
    }

    @TypeConverter
    fun toListOfExpansions(value: String): List<Int> {
        val type = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun toDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromListOfPlays(value: List<Play>): String {
        val type = object : TypeToken<List<Play>>() {}.type
        return Gson().toJson(value, type)
    }

    @TypeConverter
    fun toListOfPlays(value: String): List<Play> {
        val type = object : TypeToken<String>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromPoint(value: Point?): String? {
        val type = object : TypeToken<Point>() {}.type
        return Gson().toJson(value, type)
    }

    @TypeConverter
    fun toPoint(value: String?): Point? {
        val type = object : TypeToken<String>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun fromListOfPoints(value: List<Point>?): String? {
        val type = object : TypeToken<List<Point>>() {}.type
        return Gson().toJson(value, type)
    }

    @TypeConverter
    fun toListOfPoints(value: String?): List<Point>? {
        val type = object : TypeToken<List<Point>>() {}.type
        return Gson().fromJson(value, type)
    }
}