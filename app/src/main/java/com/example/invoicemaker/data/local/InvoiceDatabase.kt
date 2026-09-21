package com.example.invoicemaker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.invoicemaker.data.local.dao.ClientDao
import com.example.invoicemaker.data.local.dao.EstimateDao
import com.example.invoicemaker.data.local.dao.EstimateItemDao
import com.example.invoicemaker.data.local.dao.InvoiceDao
import com.example.invoicemaker.data.local.dao.InvoiceItemDao
import com.example.invoicemaker.data.local.dao.ItemDao
import com.example.invoicemaker.data.local.dao.PaymentDao
import com.example.invoicemaker.data.local.entity.Client
import com.example.invoicemaker.data.local.entity.EstimateEntity
import com.example.invoicemaker.data.local.entity.EstimateItemEntity
import com.example.invoicemaker.data.local.entity.InvoiceEntity
import com.example.invoicemaker.data.local.entity.InvoiceItemEntity
import com.example.invoicemaker.data.local.entity.Item
import com.example.invoicemaker.data.local.entity.PaymentEntity

@Database(
    entities = [
        Client::class,
        InvoiceEntity::class,
        InvoiceItemEntity::class,
        PaymentEntity::class,
        EstimateEntity::class,
        EstimateItemEntity::class,
        Item::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class InvoiceDatabase : RoomDatabase() {

    abstract fun invoiceDao(): InvoiceDao
    abstract fun clientDao(): ClientDao
    abstract fun invoiceItemDao(): InvoiceItemDao
    abstract fun paymentDao(): PaymentDao
    abstract fun estimateDao(): EstimateDao
    abstract fun estimateItemDao(): EstimateItemDao
    abstract fun itemDao(): ItemDao


    companion object {
        @Volatile private var INSTANCE: InvoiceDatabase? = null

        fun getInstance(context: Context): InvoiceDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    InvoiceDatabase::class.java,
                    "invoice_maker.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}