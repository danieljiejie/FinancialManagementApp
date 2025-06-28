package com.example.financialmanagementapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

//class TransactionAdapter(private var transactions: List<Transaction>) :
//    RecyclerView.Adapter<TransactionAdapter.TransactionHolder>() {
//
//    class TransactionHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val label : TextView = view.findViewById(R.id.label)
//        val amount : TextView = view.findViewById(R.id.amount)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_layout, parent, false)
//        return TransactionHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
//        val transaction = transactions[position]
//        val context = holder.amount.context
//
//        if(transaction.amount >= 0){
//            holder.amount.text = "+ $%.2f".format(transaction.amount)
//            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.green))
//        }else {
//            holder.amount.text = "- $%.2f".format(Math.abs(transaction.amount))
//            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.red))
//        }
//
//        holder.label.text = transaction.label
//
//        holder.itemView.setOnClickListener {
//            val intent = Intent(context, DetailedActivity::class.java)
//            intent.putExtra("transaction", transaction)
//            context.startActivity(intent)
//        }
//
//    }
//
//    override fun getItemCount(): Int {
//        return transactions.size
//    }
//
//    fun setData(transactions: List<Transaction>){
//        this.transactions = transactions
//        notifyDataSetChanged()
//    }
//}



class TransactionAdapter(private var transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionHolder>() {

    class TransactionHolder(view: View) : RecyclerView.ViewHolder(view) {
        //val label: TextView = view.findViewById(R.id.label)
        val amount: TextView = view.findViewById(R.id.amount)
        val type: TextView = view.findViewById(R.id.type)
        val category: TextView = view.findViewById(R.id.category)
        val date: TextView = view.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_layout, parent, false)
        return TransactionHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        val transaction = transactions[position]
        val context = holder.amount.context

        // holder.label.text = transaction.label
        holder.type.text = transaction.type
        holder.category.text = transaction.category
        //val dateFormatted = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            //.format(Date(transaction.date))
        val malaysiaTimeZone = TimeZone.getTimeZone("Asia/Kuala_Lumpur")
        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        formatter.timeZone = malaysiaTimeZone

        if (transaction.type == "Budget") {
            holder.amount.text = "+ $%.2f".format(transaction.amount)
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.green))
        } else {
            holder.amount.text = "- $%.2f".format(transaction.amount)
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.red))
        }
        holder.date.text = formatter.format(Date(transaction.date))

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailedActivity::class.java)
            intent.putExtra("transaction", transaction)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = transactions.size

    fun setData(transactions: List<Transaction>) {
        this.transactions = transactions
        notifyDataSetChanged()
    }
}