package com.example.financialmanagementapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.Text

//class MainActivity : AppCompatActivity() {
//
//    private lateinit var deletedTransaction: Transaction
//    private lateinit var oldTransactions : List<Transaction>
//    private lateinit var transactions : List<Transaction>
//    private lateinit var transactionAdapter: TransactionAdapter
//    private lateinit var linearLayoutManager: LinearLayoutManager
//    private lateinit var db : AppDatabase
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
//        val addBtn = findViewById<FloatingActionButton>(R.id.addBtn)
//
//        transactions = arrayListOf()
//
//        transactionAdapter = TransactionAdapter(transactions)
//        linearLayoutManager = LinearLayoutManager(this)
//
//        db = Room.databaseBuilder(this,
//            AppDatabase::class.java,
//            "transactions").build()
//
//        recyclerview.apply {
//            adapter = transactionAdapter
//            layoutManager = linearLayoutManager
//        }
//
//        // swipe to remove
//        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
//            override fun onMove(
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder,
//                target: RecyclerView.ViewHolder
//            ): Boolean {
//                return false
//            }
//
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                deleteTransaction(transactions[viewHolder.adapterPosition])
//            }
//
//        }
//
//        val swipeHelper = ItemTouchHelper(itemTouchHelper)
//        swipeHelper.attachToRecyclerView(recyclerview)
//
//        addBtn.setOnClickListener {
//            val intent = Intent(this, AddTransactionActivity::class.java)
//            startActivity(intent)
//        }
//
//    }
//
//    private fun fetchAll(){
//        GlobalScope.launch {
//            transactions = db.transactionDao().getAll()
//
//            runOnUiThread {
//                updateDashboard()
//                transactionAdapter.setData(transactions)
//            }
//        }
//    }
//
//    private fun updateDashboard(){
//        val totalAmount = transactions.map { it.amount }.sum()
//        val budgetAmount = transactions.filter { it.amount>0 }.map{it.amount}.sum()
//        val expenseAmount = totalAmount - budgetAmount
//        val balance = findViewById<TextView>(R.id.balance)
//        val budget = findViewById<TextView>(R.id.budget)
//        val expense = findViewById<TextView>(R.id.expense)
//        balance.text = "$ %.2f".format(totalAmount)
//        budget.text = "$ %.2f".format(budgetAmount)
//        expense.text = "$ %.2f".format(expenseAmount)
//    }
//
//    private fun undoDelete(){
//        GlobalScope.launch {
//            db.transactionDao().insertAll(deletedTransaction)
//
//            transactions = oldTransactions
//
//            runOnUiThread {
//                transactionAdapter.setData(transactions)
//                updateDashboard()
//            }
//        }
//    }
//
//    private fun showSnackbar(){
//        val view = findViewById<View>(R.id.coordinator)
//        val snackbar = Snackbar.make(view, "Transaction deleted!",Snackbar.LENGTH_LONG)
//        snackbar.setAction("Undo"){
//            undoDelete()
//        }
//            .setActionTextColor(ContextCompat.getColor(this, R.color.red))
//            .setTextColor(ContextCompat.getColor(this, R.color.white))
//            .show()
//    }
//
//    private fun deleteTransaction(transaction: Transaction){
//        deletedTransaction = transaction
//        oldTransactions = transactions
//
//        GlobalScope.launch {
//            db.transactionDao().delete(transaction)
//
//            transactions = transactions.filter { it.id != transaction.id }
//            runOnUiThread {
//                updateDashboard()
//                transactionAdapter.setData(transactions)
//                showSnackbar()
//            }
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        fetchAll()
//    }
//}

//import android.content.Intent
//import android.os.Bundle
//import android.view.View
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
//import androidx.recyclerview.widget.ItemTouchHelper
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var deletedTransaction: Transaction
    private lateinit var transactions: List<Transaction>
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Stay in MainActivity
                    true
                }

                R.id.nav_statistics -> {
                    // Launch StatisticActivity
                    val intent = Intent(this, StatisticActivity::class.java)
                    startActivity(intent)
                    true
                }

//                R.id.navigation_pr -> {
//                    val intent = Intent(this, ProfileActivity::class.java)
//                    startActivity(intent)
//                    true
//                }

                else -> false
            }
        }





    val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val addBtn = findViewById<FloatingActionButton>(R.id.addBtn)

        transactions = emptyList()
        transactionAdapter = TransactionAdapter(transactions)
        linearLayoutManager = LinearLayoutManager(this)
        db = AppDatabase.getDatabase(this)

        recyclerView.apply {
            adapter = transactionAdapter
            layoutManager = linearLayoutManager
        }

        // Swipe to remove
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteTransaction(transactions[viewHolder.adapterPosition])
            }
        }

        val swipeHelper = ItemTouchHelper(itemTouchHelper)
        swipeHelper.attachToRecyclerView(recyclerView)

        addBtn.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

        // Observe transactions
        lifecycleScope.launch {
            db.transactionDao().getAll().collectLatest { newTransactions ->
                transactions = newTransactions
                updateDashboard()
                transactionAdapter.setData(transactions)
            }
        }
    }

    private fun updateDashboard() {

        val budgetAmount = transactions.filter { it.type == "Budget" }.sumOf { it.amount }
        val expenseAmount = transactions.filter { it.type == "Expense" }.sumOf { it.amount }
        val totalAmount = budgetAmount - expenseAmount
        findViewById<TextView>(R.id.balance).text = "$ %.2f".format(totalAmount)
        findViewById<TextView>(R.id.budget).text = "$ %.2f".format(budgetAmount)
        findViewById<TextView>(R.id.expense).text = "$ %.2f".format(expenseAmount)
    }

    private fun deleteTransaction(transaction: Transaction) {
        deletedTransaction = transaction
        val oldTransactions = transactions.toList()

        lifecycleScope.launch {
            db.transactionDao().delete(transaction)
            runOnUiThread {
                showSnackbar(oldTransactions)
            }
        }
    }

    private fun undoDelete(oldTransactions: List<Transaction>) {
        lifecycleScope.launch {
            db.transactionDao().insert(deletedTransaction)
            runOnUiThread {
                transactionAdapter.setData(transactions)
                updateDashboard()
            }
        }
    }

    private fun showSnackbar(oldTransactions: List<Transaction>) {
        //val view = findViewById<View>(R.id.coordinator)

        val snackbar = Snackbar.make(findViewById(R.id.coordinator), "Transaction deleted!", Snackbar.LENGTH_LONG)
        snackbar.anchorView = findViewById(R.id.bottom_navigation) // ✅ Anchor to BottomNavigation
        snackbar.setAction("Undo") { undoDelete(oldTransactions) }
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.red))
        snackbar.setTextColor(ContextCompat.getColor(this, R.color.white))
        snackbar.show()
    }
}
