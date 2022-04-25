package com.example.test2

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class AdapterTransaction (

    private val listTransaction : ArrayList<TransactionCls>
        ) : RecyclerView.Adapter<AdapterTransaction.ListViewHolder>() {
            private lateinit var onItemClickCallback : OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemTransactionClicked(data : TransactionCls)
    }

    fun setOnItemClickCallback (onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var _ivRS : ImageView = itemView.findViewById(R.id.ivRSTransaction)
        var _tvNamaRS : TextView = itemView.findViewById(R.id.tvNamaRSTransaction)
        var _tvAlamatRS : TextView = itemView.findViewById(R.id.tvAlamatRSTransaction)
        var _tvKategoriPCR : TextView = itemView.findViewById(R.id.tvKategoriPCRTransaction)
        var _tvKategoriAntigen : TextView = itemView.findViewById(R.id.tvKategoriAntigenTransaction)
        var _tvStatus : TextView = itemView.findViewById(R.id.tvStatusTransaction)
        var _getConstraint:ConstraintLayout=itemView.findViewById(R.id.itemRumahSakitTransaction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {

        val transaction = listTransaction[position]

        holder._getConstraint.setOnClickListener {
            onItemClickCallback.onItemTransactionClicked(transaction)
        }

        val context = holder.itemView.context
        val imageRes = context.resources.getIdentifier(
            transaction.HospitalImage,
            "drawable",
            context.packageName
        )
        Picasso.get().load(imageRes).into(holder._ivRS)


        holder._tvNamaRS.text = transaction.HospitalName
        holder._tvAlamatRS.text = transaction.HospitalAlamat

        val day = transaction.Tanggal.substring(0,2).toInt()
        val month = transaction.Tanggal.substring(3,5).toInt()
        val year = transaction.Tanggal.substring(6,10).toInt()

        var dtNow = Date()
        var dtTransaction = Date()
        var dtTransactionPlus1 = Date()
        var dtTransactionPlus3 = Date()

        val cNow = Calendar.getInstance()
        val cTransaction = Calendar.getInstance()
        val cTransactionPlus1 = Calendar.getInstance()
        val cTransactionPlus3 = Calendar.getInstance()

        dtNow = cNow.time

        cTransaction.set(year, month, day)
        dtTransaction = cTransaction.time

        cTransactionPlus1.set(year, month, day)
        cTransactionPlus1.add(Calendar.DATE, 1)
        dtTransactionPlus1 = cTransactionPlus1.time

        cTransactionPlus3.set(year, month, day)
        cTransactionPlus3.add(Calendar.DATE, 3)
        dtTransactionPlus3 = cTransactionPlus1.time


        when (transaction.KategoriTest) {
            "PCR" -> {
                holder._tvKategoriPCR.visibility = View.VISIBLE
                holder._tvKategoriAntigen.visibility = View.GONE

                //booked
                if (dtNow < dtTransaction) {
                    holder._tvStatus.text = "Booked"
                    holder._tvStatus.setTextColor(Color.BLUE)
                }
                //active
                else if (dtNow >= dtTransaction && dtNow <= dtTransactionPlus3) {
                    holder._tvStatus.text = "Active"
                    holder._tvStatus.setTextColor(Color.GREEN)
                }
                //expired
                else {
                    holder._tvStatus.text = "Expired"
                    holder._tvStatus.setTextColor(Color.RED)
                }

            }
            "Antigen" -> {
                holder._tvKategoriPCR.visibility = View.GONE
                holder._tvKategoriAntigen.visibility = View.VISIBLE

                //booked
                if (dtNow < dtTransaction) {
                    holder._tvStatus.text = "Booked"
                    holder._tvStatus.setTextColor(Color.BLUE)
                }
                //active
                else if (dtNow >= dtTransaction && dtNow <= dtTransactionPlus1) {
                    holder._tvStatus.text = "Active"
                    holder._tvStatus.setTextColor(Color.GREEN)
                }
                //expired
                else {
                    holder._tvStatus.text = "Expired"
                    holder._tvStatus.setTextColor(Color.RED)
                }

            }
        }

    }

    override fun getItemCount(): Int {
        return listTransaction.size
    }


}