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

class ProdukAdapter(
    private var results: MutableList<GetAllProduk>,
    val listener: OnAdapterProdukListener,
    val token: String
) :
    RecyclerView.Adapter<ProdukAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val inflatedView = layoutInflater.inflate(R.layout.item_produk, parent, false)
        return ViewHolder(inflatedView)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var nama: TextView = view.findViewById(R.id.namaProduk)
        private var jenis: TextView = view.findViewById(R.id.jenisProduk)

        fun bindItem(data: GetAllProduk) {
            nama.text = data.nama
            jenis.text = data.jenis_produk_id?.nama
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

    interface OnAdapterProdukListener {
        fun onClick(result: GetAllProduk)
    }
}

