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

class SumberDanaAdapter(
    private var results: MutableList<GetAllSumberDana>,
    val listener: OnAdapterSumberDanaListener,
    val token: String
) :
    RecyclerView.Adapter<SumberDanaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val inflatedView = layoutInflater.inflate(R.layout.item_sumber_dana, parent, false)
        return ViewHolder(inflatedView)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var nominal: TextView = view.findViewById(R.id.nominal)
        private var jenis: TextView = view.findViewById(R.id.jenisSumberDana)
        private var tahun: TextView = view.findViewById(R.id.tahunPemberianDana)

        fun bindItem(data: GetAllSumberDana) {
            nominal.text = data.nominal
            jenis.text = data.jenis_sumber_dana?.nama
            tahun.text = data.tahun
            Log.e("tahun sumber dana", data.tahun)
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

    interface OnAdapterSumberDanaListener {
        fun onClick(result: GetAllSumberDana)
    }
}

