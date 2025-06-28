package com.example.financialmanagementapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    //val label: String,
    val amount: Double,
    val description: String,
    val date: Long ,
    val type: String = "Expense",
    val category: String = "Food/Drink",
): Serializable {
}