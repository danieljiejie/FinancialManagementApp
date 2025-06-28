package com.example.financialmanagementapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.example.financialmanagementapp.databinding.ActivityDetailedBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


//class DetailedActivity : AppCompatActivity() {
//    private lateinit var transaction : Transaction
//    private lateinit var binding: ActivityDetailedBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_detailed)
//
//        binding = ActivityDetailedBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        transaction = intent.getSerializableExtra("transaction") as Transaction
//
//
//        binding.labelInput.setText(transaction.label)
//        binding.amountInput.setText(transaction.amount.toString())
//        binding.descriptionInput.setText(transaction.description)
//
//
//        binding.rootView.setOnClickListener {
//            this.window.decorView.clearFocus()
//
//            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(it.windowToken, 0)
//        }
//
//        binding.labelInput.addTextChangedListener {
//            binding.updateBtn.visibility = View.VISIBLE
//            if(it!!.count() > 0)
//                binding.labelLayout.error = null
//        }
//
//        binding.amountInput.addTextChangedListener {
//            binding.updateBtn.visibility = View.VISIBLE
//            if(it!!.count() > 0)
//                binding.amountLayout.error = null
//        }
//
//        binding.descriptionInput.addTextChangedListener {
//            binding.updateBtn.visibility = View.VISIBLE
//        }
//
//        binding.updateBtn.setOnClickListener {
//            val label = binding.labelInput.text.toString()
//            val description = binding.descriptionInput.text.toString()
//            val amount = binding.amountInput.text.toString().toDoubleOrNull()
//
//            if(label.isEmpty())
//                binding.labelLayout.error = "Please neter a valid label"
//
//            else if(amount == null)
//                binding.amountLayout.error = "Please enter a valid amount"
//            else {
//                val transaction  = Transaction(transaction.id, label, amount, description)
//                update(transaction)
//            }
//        }
//
//        binding.closeBtn.setOnClickListener {
//            finish()
//        }
//    }
//
//    private fun update(transaction: Transaction){
//        val db = Room.databaseBuilder(this,
//            AppDatabase::class.java,
//            "transactions").build()
//
//        GlobalScope.launch {
//            db.transactionDao().update(transaction)
//            finish()
//        }
//    }
//
//}

//import android.content.Context
//import android.os.Bundle
//import android.view.View
//import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
//import android.widget.AutoCompleteTextView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

//import com.example.financialmanagementapp.databinding.ActivityDetailedBinding
//import kotlinx.coroutines.launch
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale

class DetailedActivity : AppCompatActivity() {
    private lateinit var transaction: Transaction
    private lateinit var binding: ActivityDetailedBinding
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)
        transaction = intent.getSerializableExtra("transaction") as Transaction

        // Setup dropdowns
        val typeAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.transaction_types,
            android.R.layout.simple_dropdown_item_1line
        )
        binding.typeInput.setAdapter(typeAdapter)
        binding.typeInput.setText(transaction.type, false)

        val categoryAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line
        )
        binding.categoryInput.setAdapter(categoryAdapter)
        updateCategoryDropdown(transaction.type)
        binding.categoryInput.setText(transaction.category, false)

        binding.typeInput.setOnItemClickListener { _, _, position, _ ->
            val selectedType = typeAdapter.getItem(position)
            updateCategoryDropdown(selectedType.toString())
            binding.categoryInput.setText("")
            binding.updateBtn.visibility = View.VISIBLE
        }

        // Set initial values
        //binding.labelInput.setText(transaction.label)
        val malaysiaTimeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        formatter.timeZone = malaysiaTimeZone
        binding.amountInput.setText(transaction.amount.toString())
        binding.dateInput.setText(formatter.format(Date(transaction.date)))
        binding.descriptionInput.setText(transaction.description)

        // Click listeners
        binding.rootView.setOnClickListener {
            window.decorView.clearFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        binding.closeBtn.setOnClickListener {
            finish()
        }

        // Real-time validation
//        binding.labelInput.addTextChangedListener {
//            binding.updateBtn.visibility = View.VISIBLE
//            if (it!!.isNotEmpty()) binding.labelLayout.error = null
//        }
        binding.amountInput.addTextChangedListener {
            binding.updateBtn.visibility = View.VISIBLE
            if (it!!.isNotEmpty() && it.toString().toDoubleOrNull() != null) {
                binding.amountLayout.error = null
            }
        }
        binding.descriptionInput.addTextChangedListener {
            binding.updateBtn.visibility = View.VISIBLE
        }
        binding.typeInput.addTextChangedListener {
            binding.updateBtn.visibility = View.VISIBLE
            if (it!!.isNotEmpty()) binding.typeLayout.error = null
        }
        binding.categoryInput.addTextChangedListener {
            binding.updateBtn.visibility = View.VISIBLE
            if (it!!.isNotEmpty()) binding.categoryLayout.error = null
        }

        binding.updateBtn.setOnClickListener {
            //val label = binding.labelInput.text.toString()
            val amount = binding.amountInput.text.toString().toDoubleOrNull()
            val description = binding.descriptionInput.text.toString()
            val type = binding.typeInput.text.toString()
            val category = binding.categoryInput.text.toString()

//            if (label.isEmpty()) {
//                binding.labelLayout.error = "Please enter a valid label"
//            } else
            if (amount == null) {
                binding.amountLayout.error = "Please enter a valid amount"
            } else if (type.isEmpty()) {
                binding.typeLayout.error = "Please select a transaction type"
            } else if (category.isEmpty()) {
                binding.categoryLayout.error = "Please select a category"
            } else {
                val updatedTransaction = Transaction(
                    id = transaction.id,
                    //label = label,
                    amount = amount,
                    description = description,
                    date = transaction.date,
                    type = type,
                    category = category
                )
                update(updatedTransaction)
            }
        }
    }

    private fun updateCategoryDropdown(selectedType: String?) {
        val categoryAdapter = binding.categoryInput.adapter as ArrayAdapter<String>
        categoryAdapter.clear()
        val categories = when (selectedType) {
            "Budget" -> resources.getStringArray(R.array.budget_categories)
            "Expense" -> resources.getStringArray(R.array.expense_categories)
            else -> emptyArray()
        }
        categoryAdapter.addAll(*categories)
        categoryAdapter.notifyDataSetChanged()
    }

    private fun update(transaction: Transaction) {
        lifecycleScope.launch {
            database.transactionDao().update(transaction)
            finish()
        }
    }
}