package com.diskominfos.subakbali.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.api.*

class SuratKeputusanAdapter(
    private var results: MutableList<GetAllSuratKeputusan>,
    val listener: OnAdapterSuratKeputusanListener,
    val token: String
) :
    RecyclerView.Adapter<SuratKeputusanAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val inflatedView = layoutInflater.inflate(R.layout.item_surat_keputusan, parent, false)
        return ViewHolder(inflatedView)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var nama: TextView = view.findViewById(R.id.namaSuratKeputusan)
        private var nomor: TextView = view.findViewById(R.id.nomorSuratKeputusan)

        fun bindItem(data: GetAllSuratKeputusan) {
            nama.text = data.nama
            nomor.text = data.no_sk
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

    interface OnAdapterSuratKeputusanListener {
        fun onClick(result: GetAllSuratKeputusan)
    }
}

