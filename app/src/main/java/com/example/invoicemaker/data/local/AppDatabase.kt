package com.example.invoicemaker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.invoicemaker.data.local.dao.ClientDao
import com.example.invoicemaker.data.local.dao.InvoiceDao
import com.example.invoicemaker.data.local.entity.ClientEntity
import com.example.invoicemaker.data.local.entity.EstimateEntity
import com.example.invoicemaker.data.local.entity.EstimateItemEntity
import com.example.invoicemaker.data.local.entity.InvoiceEntity
import com.example.invoicemaker.data.local.entity.InvoiceLineEntity
import com.example.invoicemaker.data.local.entity.ItemEntity
import com.example.invoicemaker.data.local.entity.PaymentEntity

@Database(
    entities = [
        ClientEntity::class,
        InvoiceEntity::class,
        InvoiceLineEntity::class,
        PaymentEntity::class,
        EstimateEntity::class,
        EstimateItemEntity::class,
        ItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun invoiceDao(): InvoiceDao
    abstract fun clientDao(): ClientDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "invoice_maker.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}