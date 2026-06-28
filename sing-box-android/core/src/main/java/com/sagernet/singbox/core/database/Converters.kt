package com.sagernet.singbox.core.database

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromProfileType(value: ProfileType): String {
        return value.name
    }

    @TypeConverter
    fun toProfileType(value: String): ProfileType {
        return ProfileType.valueOf(value)
    }
}
