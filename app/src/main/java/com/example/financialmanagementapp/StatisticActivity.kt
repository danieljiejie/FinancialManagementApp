package com.example.financialmanagementapp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import org.eazegraph.lib.charts.PieChart
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.eazegraph.lib.models.PieModel
import java.util.Calendar
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StatisticActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var pieChart: PieChart
    private lateinit var spinnerFilter: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistic)

        pieChart = findViewById(R.id.piechartExpense)
        spinnerFilter = findViewById(R.id.spinnerFilter)
        db = AppDatabase.getDatabase(this)

        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                val selectedFilter = parent.getItemAtPosition(position).toString()
                loadPieChartData(selectedFilter)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }



    private fun loadPieChartData(filter: String) {
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis
        when (filter) {
            "Daily" -> calendar.add(Calendar.DAY_OF_YEAR, -1)
            "Weekly" -> calendar.add(Calendar.DAY_OF_YEAR, -7)
            "Monthly" -> calendar.add(Calendar.MONTH, -1)
            "Yearly" -> calendar.add(Calendar.YEAR, -1)
        }
        val startDate = calendar.timeInMillis

        lifecycleScope.launch {
            // Run the DB query in the background
            val expenses = withContext(Dispatchers.IO) {
                db.transactionDao().getExpensesBetween(startDate, endDate)
            }

            // Aggregate by category
            val categoryMap = mutableMapOf(
                "Food/Drink" to 0f,
                "Entertainment" to 0f,
                "Transportation" to 0f,
                "Other" to 0f
            )

            for (expense in expenses) {
                when (expense.category) {
                    "Food/Drink" -> categoryMap["Food/Drink"] = categoryMap["Food/Drink"]!! + expense.amount.toFloat()
                    "Entertainment" -> categoryMap["Entertainment"] = categoryMap["Entertainment"]!! + expense.amount.toFloat()
                    "Transportation" -> categoryMap["Transportation"] = categoryMap["Transportation"]!! + expense.amount.toFloat()
                    else -> categoryMap["Other"] = categoryMap["Other"]!! + expense.amount.toFloat()
                }
            }

            // Update pie chart on main thread
            pieChart.clearChart()
            if (categoryMap["Food/Drink"]!! > 0) pieChart.addPieSlice(PieModel("Food/Drink", categoryMap["Food/Drink"]!!, "#902C51".toColorInt()))
            if (categoryMap["Entertainment"]!! > 0) pieChart.addPieSlice(PieModel("Entertainment", categoryMap["Entertainment"]!!, "#3F9FD9".toColorInt()))
            if (categoryMap["Transportation"]!! > 0) pieChart.addPieSlice(PieModel("Transportation", categoryMap["Transportation"]!!, "#96CA5C".toColorInt()))
            if (categoryMap["Other"]!! > 0) pieChart.addPieSlice(PieModel("Other", categoryMap["Other"]!!, "#866CC8".toColorInt()))

            pieChart.startAnimation()
        }
    }
}
