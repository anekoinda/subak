package com.diskominfos.subakbali.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.api.*

class AwigAdapter(
    private var results: MutableList<GetAllAwig>,
    val listener: OnAdapterAwigListener,
    val token: String
) :
    RecyclerView.Adapter<AwigAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val inflatedView = layoutInflater.inflate(R.layout.item_awig, parent, false)
        return ViewHolder(inflatedView)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var judul: TextView = view.findViewById(R.id.judulAwig)
        private var jenis: TextView = view.findViewById(R.id.jenisAwig)

        fun bindItem(data: GetAllAwig) {
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

    interface OnAdapterAwigListener {
        fun onClick(result: GetAllAwig)
    }
}

