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
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.ui.tambah.datawilayah.DesaAdatViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.AccessController.getContext

class TradisiSubakAdapter(
    private var results: MutableList<GetAllDataTradisi>,
    val listener: OnAdapterDataTradisiListener,
    val token: String
) :
    RecyclerView.Adapter<TradisiSubakAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val inflatedView = layoutInflater.inflate(R.layout.item_tradisi, parent, false)
        return ViewHolder(inflatedView)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var nama: TextView = view.findViewById(R.id.nama)

        //        var deleteButton: ImageView = itemView.findViewById(R.id.btnHapusDesaAdat)
        fun bindItem(data: GetAllDataTradisi) {
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
//            return pref.getUser().asLiveData()
//        }
//        holder.deleteButton.setOnClickListener {
////            getUser().observe(context.applicationContext){
//            onDelete(result, result.id.toString(), token)

    }

    override fun getItemCount(): Int = results.size
    fun setOnClickCallBack(any: Any) {

    }

    interface OnAdapterDataTradisiListener {
        fun onClick(result: GetAllDataTradisi)
    }
}

