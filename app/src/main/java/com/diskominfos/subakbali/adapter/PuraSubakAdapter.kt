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

class PuraSubakAdapter(
    private var results: MutableList<GetAllDataPuraSubak>,
    val listener: OnAdapterDataSubakPuraListener,
    val token: String
) :
    RecyclerView.Adapter<PuraSubakAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val inflatedView = layoutInflater.inflate(R.layout.item_pura_subak, parent, false)
        return ViewHolder(inflatedView)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var nama: TextView = view.findViewById(R.id.nama)
        var deleteButton: ImageView = itemView.findViewById(R.id.btnHapusDesaAdat)
        fun bindItem(data: GetAllDataPuraSubak) {
            nama.text = data.pura!!.nama
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
        holder.deleteButton.setOnClickListener {
//            getUser().observe(context.applicationContext){
            onDelete(result, result.id.toString(), token)
        }
    }

    fun onDelete(data: GetAllDataPuraSubak, id: String, token: String) {
        // Make API call to delete the item
        val client =
            ApiConfig.getApiService().deleteDataSubakPura("Bearer $token", "$id")
        client.enqueue(object : Callback<DeleteSubakPuraResponse> {
            override fun onResponse(
                call: Call<DeleteSubakPuraResponse>,
                response: Response<DeleteSubakPuraResponse>
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

            override fun onFailure(call: Call<DeleteSubakPuraResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    override fun getItemCount(): Int = results.size
    fun setOnClickCallBack(any: Any) {

    }

    interface OnAdapterDataSubakPuraListener {
        fun onClick(result: GetAllDataPuraSubak)
    }
}

