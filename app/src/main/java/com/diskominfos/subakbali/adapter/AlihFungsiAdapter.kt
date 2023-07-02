package com.diskominfos.subakbali.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.api.*

class AlihFungsiAdapter(
    private var results: MutableList<GetAllAlihFungsi>,
    val listener: OnAdapterAlihFungsiListener,
    val token: String
) :
    RecyclerView.Adapter<AlihFungsiAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val inflatedView = layoutInflater.inflate(R.layout.item_alih_fungsi, parent, false)
        return ViewHolder(inflatedView)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var nama: TextView = view.findViewById(R.id.namaAlihFungsi)

        fun bindItem(data: GetAllAlihFungsi) {
            nama.text = data.nama_alih_lahan
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = results[position]
        holder.bindItem(result)
        holder.itemView.setOnClickListener {
            listener.onClick(result)
        }
    }

    override fun getItemCount(): Int = results.size
    fun setOnClickCallBack(any: Any) {

    }

    interface OnAdapterAlihFungsiListener {
        fun onClick(result: GetAllAlihFungsi)
    }
}

