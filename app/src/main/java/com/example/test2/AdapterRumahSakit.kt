package com.example.test2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.test2.SplashScreenActivity.Companion.dataUser
import com.squareup.picasso.Picasso
import java.math.RoundingMode

class AdapterRumahSakit (
    private val listRumahSakit : ArrayList<HospitalCls>
        ) : RecyclerView.Adapter<AdapterRumahSakit.ListViewHolder>() {
            private lateinit var onItemClickCallback : OnItemClickCallback

    interface OnItemClickCallback {
        fun onFavoriteClicked(data : HospitalCls)

        fun onItemRSClicked(data: HospitalCls)
    }

    fun setOnItemClickCallback (onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {
        var _tvNamaRS : TextView = itemView.findViewById(R.id.tvNamaRS)
        var _tvAlamatRS : TextView = itemView.findViewById(R.id.tvAlamatRS)
        var _tvKategoriPCR : TextView = itemView.findViewById(R.id.tvKategoriPCR)
        var _tvKategoriAntigen : TextView = itemView.findViewById(R.id.tvKategoriAntigen)
        var _cbFavorite : CheckBox = itemView.findViewById(R.id.cbFavorite)
        var _ivRS : ImageView = itemView.findViewById(R.id.ivRS)
        var _tvRating : TextView = itemView.findViewById(R.id.tvRating)
        var _tvDistance : TextView = itemView.findViewById(R.id.tvDistance)
        var _itemRS : ConstraintLayout = itemView.findViewById(R.id.itemRumahSakit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rumah_sakit, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val rs = listRumahSakit[position]

        holder._tvNamaRS.text = rs.Nama
        holder._tvAlamatRS.text = rs.Alamat

        //image
        val context = holder.itemView.context
        val imageRes = context.resources.getIdentifier(
            rs.Gambar,
            "drawable",
            context.packageName
        )
        Picasso.get().load(imageRes).into(holder._ivRS)
        //holder._ivRS.setImageResource(imageRes)


        //0 = dua", 1 = PCR, 2 = Antigen
        when (rs.KategoriTest) {
            "0" -> {
                holder._tvKategoriPCR.visibility = View.VISIBLE
                holder._tvKategoriAntigen.visibility = View.VISIBLE
            }
            "1" -> {
                holder._tvKategoriPCR.visibility = View.VISIBLE
                holder._tvKategoriAntigen.visibility = View.GONE
            }
            "2" -> {
                holder._tvKategoriPCR.visibility = View.GONE
                holder._tvKategoriAntigen.visibility = View.VISIBLE
            }
        }

        //false = tidak fav, true = fav
        holder._cbFavorite.isChecked = dataUser.Favorite.contains(rs.Nama)

        holder._tvRating.text = rs.Rating

        holder._cbFavorite.setOnClickListener {
            holder._cbFavorite.animate().apply {
                duration=1000
                rotationYBy(360f)
            }.start()
            onItemClickCallback.onFavoriteClicked(rs)
        }

        holder._itemRS.setOnClickListener {
            onItemClickCallback.onItemRSClicked(rs)
        }

        val dis = LocationDistance.CalculateDistance(dataUser.Address, rs.Alamat, context)

        holder._tvDistance.text = dis.toBigDecimal().setScale(2, RoundingMode.CEILING).toString() + " Km"
    }

    override fun getItemCount(): Int {
        return listRumahSakit.size
    }
}