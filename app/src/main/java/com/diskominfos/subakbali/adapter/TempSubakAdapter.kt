package com.diskominfos.subakbali.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.api.*
import com.diskominfos.subakbali.databinding.ItemSubakBinding

class TempSubakAdapter(
    private var results: MutableList<DataTempSubak>,
    val listener: OnAdapterAllTempSubakListener
) :
    RecyclerView.Adapter<TempSubakAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val inflatedView = layoutInflater.inflate(R.layout.item_subak, parent, false)
        return ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = results[position]
        holder.bindItem(result)
        holder.itemView.setOnClickListener{
            listener.onClick(result)
        }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private var nama : TextView = view.findViewById(R.id.nama)
        private var jenis : TextView = view.findViewById(R.id.jenis)
        fun bindItem(data: DataTempSubak) {
            nama.text = data.nama
            jenis.text = data.jenis_subak
        }
    }

    interface OnAdapterAllTempSubakListener {
        fun onClick(result: DataTempSubak)
    }

    override fun getItemCount(): Int = results.size
    fun setOnClickCallBack(any: Any) {

    }
}

