package com.example.financialmanagementapp

import androidx.room.*

//@Dao
//interface TransactionDao {
//    @Query("SELECT * from transactions")
//    fun getAll(): List<Transaction>
//
//    @Insert
//    fun insertAll(vararg transaction: Transaction)
//
//    @Delete
//    fun delete(transaction: Transaction)
//
//    @Update
//    fun update(vararg transaction: Transaction)
//}

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAll(): Flow<List<Transaction>>

    @Insert
    suspend fun insert(transaction: Transaction)

    @Insert
    suspend fun insertAll(vararg transactions: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Update
    suspend fun update(vararg transactions: Transaction)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Int): Transaction?

    @Query("SELECT * FROM transactions WHERE type = 'Expense' AND date BETWEEN :start AND :end")
    fun getExpensesBetween(start: Long, end: Long): List<Transaction>
}