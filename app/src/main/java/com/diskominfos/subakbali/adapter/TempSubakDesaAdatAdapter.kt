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

class TempSubakDesaAdatAdapter(
    private var results: MutableList<GetAllDataTempSubakDesaAdat>,
    val listener: OnAdapterAllTempSubakDesaAdatListener,
    val token: String
) :
    RecyclerView.Adapter<TempSubakDesaAdatAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val inflatedView = layoutInflater.inflate(R.layout.item_temp_subak_desa_adat, parent, false)
        return ViewHolder(inflatedView)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var nama: TextView = view.findViewById(R.id.nama)
        var deleteButton: ImageView = itemView.findViewById(R.id.btnHapusDesaAdat)
        fun bindItem(data: GetAllDataTempSubakDesaAdat) {
            nama.text = data.desa_adat?.nama
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

    interface OnAdapterAllTempSubakDesaAdatListener {
        fun onClick(result: GetAllDataTempSubakDesaAdat)
    }

    override fun getItemCount(): Int = results.size
    fun setOnClickCallBack(any: Any) {

    }

    fun onDelete(data: GetAllDataTempSubakDesaAdat, id: String, token: String) {
        // Make API call to delete the item
        val client =
            ApiConfig.getApiService().deleteDataTempSubakDesaAdat("Bearer $token", "$id")
        client.enqueue(object : Callback<DeleteTempSubakDesaAdatResponse> {
            override fun onResponse(
                call: Call<DeleteTempSubakDesaAdatResponse>,
                response: Response<DeleteTempSubakDesaAdatResponse>
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

            override fun onFailure(call: Call<DeleteTempSubakDesaAdatResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }
}

