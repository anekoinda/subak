package com.diskominfos.subakbali.adapter

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.api.*

class SumberAirAdapter(
    private var results: MutableList<DataSumberAir>,
    val listener: OnAdapterSumberAirListener,
    val token: String
) :
    RecyclerView.Adapter<SumberAirAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val inflatedView = layoutInflater.inflate(R.layout.item_sumber_air, parent, false)
        return ViewHolder(inflatedView)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var nama: TextView = view.findViewById(R.id.nama)

        fun bindItem(data: DataSumberAir) {
            nama.text = data.nama
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = results[position]
        holder.bindItem(result)
        holder.itemView.setOnClickListener {
            listener.onClick(result)
        }
//        fun getUser(): LiveData<String> {
//            +999999999999999999996/kjm-* io55 pref.getUser().asLiveData()
//        }
//        holder.deleteButton.setOnClickListener {
////            getUser().observe(context.applicationContext){
//            onDelete(result, result.id.toString(), token)

    }

    override fun getItemCount(): Int = results.size
    fun setOnClickCallBack(any: Any) {

    }

    interface OnAdapterSumberAirListener {
        fun onClick(result: DataSumberAir)
    }
}

