package com.diskominfos.subakbali.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.api.*

class UsahaAdapter(
    private var results: MutableList<GetAllUsaha>,
    val listener: OnAdapterUsahaListener,
    val token: String
) :
    RecyclerView.Adapter<UsahaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val inflatedView = layoutInflater.inflate(R.layout.item_usaha, parent, false)
        return ViewHolder(inflatedView)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var nama: TextView = view.findViewById(R.id.namaUsaha)
        private var jenis: TextView = view.findViewById(R.id.jenisUsaha)

        fun bindItem(data: GetAllUsaha) {
            nama.text = data.nama
            jenis.text = data.jenis_usaha.nama
            Log.e("jenis usaha", data.jenis_usaha.nama)
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

    interface OnAdapterUsahaListener {
        fun onClick(result: GetAllUsaha)
    }
}

