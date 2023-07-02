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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TempSubakDesaDinasAdapter(
    private var results: MutableList<GetAllDataTempSubakDesaDinas>,
    val listener: OnAdapterAllTempSubakDesaDinasListener,
    val token: String
) :
    RecyclerView.Adapter<TempSubakDesaDinasAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val inflatedView = layoutInflater.inflate(R.layout.item_temp_subak_desa_dinas, parent, false)
        return ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = results[position]
        holder.bindItem(result)
        holder.itemView.setOnClickListener{
            listener.onClick(result)
        }
        holder.deleteButton.setOnClickListener {
//            getUser().observe(context.applicationContext){
            onDelete(result, result.id.toString(), token)

        }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private var nama : TextView = view.findViewById(R.id.nama)
        var deleteButton: ImageView = itemView.findViewById(R.id.btnHapusDesaDinas)
        fun bindItem(data: GetAllDataTempSubakDesaDinas) {
            nama.text = data.desa_dinas?.name
        }
    }

    interface OnAdapterAllTempSubakDesaDinasListener {
        fun onClick(result: GetAllDataTempSubakDesaDinas)
    }

    override fun getItemCount(): Int = results.size
    fun setOnClickCallBack(any: Any) {

    }

    fun onDelete(data: GetAllDataTempSubakDesaDinas, id: String, token: String) {
        // Make API call to delete the item
        val client =
            ApiConfig.getApiService().deleteDataTempSubakDesaDinas("Bearer $token", "$id")
        client.enqueue(object : Callback<DeleteTempSubakDesaDinasResponse> {
            override fun onResponse(
                call: Call<DeleteTempSubakDesaDinasResponse>,
                response: Response<DeleteTempSubakDesaDinasResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {

                    }

                    results.remove(data)
                    notifyDataSetChanged()
                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<DeleteTempSubakDesaDinasResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }
}

