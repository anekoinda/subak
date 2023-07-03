package com.diskominfos.subakbali.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.api.*

class PeraremAdapter(
    private var results: MutableList<GetAllPerarem>,
    val listener: OnAdapterPeraremListener,
    val token: String
) :
    RecyclerView.Adapter<PeraremAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val inflatedView = layoutInflater.inflate(R.layout.item_perarem, parent, false)
        return ViewHolder(inflatedView)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var judul: TextView = view.findViewById(R.id.judulPerarem)
        private var jenis: TextView = view.findViewById(R.id.jenisPerarem)

        fun bindItem(data: GetAllPerarem) {
            judul.text = data.judul
            jenis.text = data.jenis
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

    interface OnAdapterPeraremListener {
        fun onClick(result: GetAllPerarem)
    }
}

