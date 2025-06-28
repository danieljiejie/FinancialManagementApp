package com.example.financialmanagementapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
//import androidx.room.Room
//import kotlinx.android.synthetic.main.activity_add_transaction.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

//class AddTransactionActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_add_transaction)
//
//        val labelInput = findViewById<TextInputEditText>(R.id.labelInput)
//        val amountInput = findViewById<TextInputEditText>(R.id.amountInput)
//        val labelLayout = findViewById<TextInputLayout>(R.id.labelLayout)
//        val amountLayout = findViewById<TextInputLayout>(R.id.amountLayout)
//        val addTransactionBtn = findViewById<Button>(R.id.addTransactionBtn)
//        val descriptionInput = findViewById<TextInputEditText>(R.id.descriptionInput)
//        val closeBtn = findViewById<ImageButton>(R.id.closeBtn)
//
//        labelInput.addTextChangedListener {
//            if (it!!.count() > 0)
//                labelLayout.error = null
//        }
//
//        amountInput.addTextChangedListener {
//            if (it!!.count() > 0)
//                amountLayout.error = null
//        }
//
//        addTransactionBtn.setOnClickListener {
//            val label = labelInput.text.toString()
//            val description = descriptionInput.text.toString()
//            val amount = amountInput.text.toString().toDoubleOrNull()
//
//            if (label.isEmpty())
//                labelLayout.error = "Please enter a valid label"
//            else if (amount == null)
//                amountLayout.error = "Please enter a valid amount"
//
//            else {
//                val transaction  =Transaction(0, label, amount, description)
//                insert(transaction)
//            }
//
//        }
//
//        closeBtn.setOnClickListener {
//            finish()
//        }
//    }
//
//    private fun insert(transaction: Transaction){
//        val db = Room.databaseBuilder(this,
//            AppDatabase::class.java,
//            "transactions").build()
//
//        GlobalScope.launch {
//            db.transactionDao().insertAll(transaction)
//            finish()
//        }
//    }
//}

//import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
//import android.widget.Button
//import android.widget.ImageButton
//import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
//import com.google.android.material.textfield.TextInputEditText
//import com.google.android.material.textfield.TextInputLayout
//import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddTransactionActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase
    private lateinit var typeInput: AutoCompleteTextView
    private lateinit var categoryInput: AutoCompleteTextView
    //private lateinit var labelInput: TextInputEditText
    private lateinit var amountInput: TextInputEditText
    private lateinit var descriptionInput: TextInputEditText
    private lateinit var typeLayout: TextInputLayout
    private lateinit var categoryLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        // Initialize Room database
        database = AppDatabase.getDatabase(this)

        // Initialize views
        typeInput = findViewById(R.id.typeInput)
        categoryInput = findViewById(R.id.categoryInput)
        //labelInput = findViewById(R.id.labelInput)
        amountInput = findViewById(R.id.amountInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        typeLayout = findViewById(R.id.typeLayout)
        categoryLayout = findViewById(R.id.categoryLayout)
        val addTransactionBtn = findViewById<Button>(R.id.addTransactionBtn)
        val closeBtn = findViewById<ImageButton>(R.id.closeBtn)

        // Setup transaction type dropdown
        val typeAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.transaction_types,
            android.R.layout.simple_dropdown_item_1line
        )
        typeInput.setAdapter(typeAdapter)

        // Setup category dropdown (initially empty)
        val categoryAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line
        )
        categoryInput.setAdapter(categoryAdapter)

        // Update category dropdown based on type selection
        typeInput.setOnItemClickListener { _, _, position, _ ->
            val selectedType = typeAdapter.getItem(position)
            categoryAdapter.clear()
            val categories = when (selectedType) {
                "Budget" -> resources.getStringArray(R.array.budget_categories)
                "Expense" -> resources.getStringArray(R.array.expense_categories)
                else -> emptyArray()
            }
            categoryAdapter.addAll(*categories)
            categoryAdapter.notifyDataSetChanged()
            categoryInput.setText("") // Clear category selection
            categoryLayout.error = null // Clear any previous error
        }

        // Add transaction button click
        addTransactionBtn.setOnClickListener {
            if (validateInputs()) {
                saveTransaction()
            }
        }

        // Close button click
        closeBtn.setOnClickListener {
            finish()
        }

        // Real-time validation
//        labelInput.addTextChangedListener {
//            if (it!!.isNotEmpty()) findViewById<TextInputLayout>(R.id.labelLayout).error = null
//        }
        amountInput.addTextChangedListener {
            if (it!!.isNotEmpty() && it.toString().toDoubleOrNull() != null) {
                findViewById<TextInputLayout>(R.id.amountLayout).error = null
            }
        }
        typeInput.addTextChangedListener {
            if (it!!.isNotEmpty()) typeLayout.error = null
        }
        categoryInput.addTextChangedListener {
            if (it!!.isNotEmpty()) categoryLayout.error = null
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (typeInput.text.isNullOrEmpty()) {
            typeLayout.error = "Please select a transaction type"
            isValid = false
        } else {
            typeLayout.error = null
        }

        if (categoryInput.text.isNullOrEmpty()) {
            categoryLayout.error = "Please select a category"
            isValid = false
        } else {
            categoryLayout.error = null
        }

//        if (labelInput.text.isNullOrEmpty()) {
//            findViewById<TextInputLayout>(R.id.labelLayout).error = "Label is required"
//            isValid = false
//        } else {
//            findViewById<TextInputLayout>(R.id.labelLayout).error = null
//        }

        if (amountInput.text.isNullOrEmpty() || amountInput.text.toString().toDoubleOrNull() == null) {
            findViewById<TextInputLayout>(R.id.amountLayout).error = "Valid amount is required"
            isValid = false
        } else {
            findViewById<TextInputLayout>(R.id.amountLayout).error = null
        }

        return isValid
    }

    private fun saveTransaction() {
        val amount = amountInput.text.toString().toDouble()
        val description = descriptionInput.text.toString().ifEmpty { "No description" }
        val type = typeInput.text.toString()
        val category = categoryInput.text.toString()
        //val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val date = System.currentTimeMillis()
        //val label = labelInput.text.toString()

        val transaction = Transaction(

            //label = label,
            amount = amount,
            description = description,
            date = date,
            type = type,
            category = category
        )

        lifecycleScope.launch {
            database.transactionDao().insert(transaction)
            finish()
        }
    }
}