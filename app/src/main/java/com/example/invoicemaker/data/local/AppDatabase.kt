package com.example.invoicemaker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yourpackage.invoicemaker.data.local.dao.ClientDao
import com.yourpackage.invoicemaker.data.local.dao.InvoiceDao
import com.yourpackage.invoicemaker.data.local.entity.ClientEntity
import com.yourpackage.invoicemaker.data.local.entity.InvoiceEntity
import com.yourpackage.invoicemaker.data.local.entity.InvoiceLineEntity
import com.yourpackage.invoicemaker.data.local.entity.PaymentEntity

@Database(
    entities = [
        ClientEntity::class,
        InvoiceEntity::class,
        InvoiceLineEntity::class,
        PaymentEntity::class
        // add EstimateEntity, EstimateLineEntity, ItemEntity here as you build them
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