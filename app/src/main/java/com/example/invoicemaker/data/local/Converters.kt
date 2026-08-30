package com.example.invoicemaker.data.local

import androidx.room.TypeConverter
import java.math.BigDecimal
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String? = value?.toPlainString()

    @TypeConverter
    fun toBigDecimal(value: String?): BigDecimal? = value?.let { BigDecimal(it) }

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString() // ISO-8601: "2026-08-30"

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }
}