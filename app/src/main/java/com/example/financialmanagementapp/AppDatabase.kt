package com.example.financialmanagementapp

//import androidx.room.Database
//import androidx.room.RoomDatabase

//@Database(entities = arrayOf(Transaction::class), version = 1)
//abstract class AppDatabase : RoomDatabase() {
//    abstract fun transactionDao() : TransactionDao
//}

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Transaction::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "transactions"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}