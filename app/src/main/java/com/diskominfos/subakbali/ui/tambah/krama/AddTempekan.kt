package com.diskominfos.subakbali.ui.tambah.krama

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.api.ApiConfig
import com.diskominfos.subakbali.api.DataSubakResponse
import com.diskominfos.subakbali.api.TempekanResponse
import com.diskominfos.subakbali.databinding.ActivityAddDataUmumBinding
import com.diskominfos.subakbali.databinding.ActivityAddTempekanBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import com.diskominfos.subakbali.ui.tambah.datawilayah.AddDataWilayah
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class AddTempekan : AppCompatActivity() {
    private lateinit var tempekanViewModel: TempekanViewModel
    private lateinit var binding: ActivityAddTempekanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTempekanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSimpan.setOnClickListener{
            addDataTempekan()
            startActivity(Intent(this, AddKrama::class.java))
            finish()
        }
    }

    private fun addDataTempekan() {
        tempekanViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[TempekanViewModel::class.java]

        tempekanViewModel.getUser().observe(this) { it ->
            Log.e("token", it)
            val getName = binding.namaTempekan.text.toString()
            val nama = getName.toRequestBody("text/plain".toMediaType())

            val service = ApiConfig.getApiService().addTempekan(
                "Bearer $it",
                nama,
                1
            )

            service.enqueue(object : Callback<TempekanResponse> {
                override fun onResponse(
                    call: Call<TempekanResponse>,
                    response: Response<TempekanResponse>
                ) {
                    Log.e("token", it)
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddTempekan,
                            "Berhasil tambah tempekan",
                            Toast.LENGTH_SHORT
                        ).show()
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            Toast.makeText(
                                this@AddTempekan,
                                responseBody.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@AddTempekan,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<TempekanResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddTempekan,
                        "Cannot instance Retrofit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        }
    }
}