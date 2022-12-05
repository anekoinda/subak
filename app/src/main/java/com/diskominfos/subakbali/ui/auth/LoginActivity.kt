package com.diskominfos.subakbali.ui.auth

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.diskominfos.subakbali.HomeActivity
import com.diskominfos.subakbali.api.ApiConfig
import com.diskominfos.subakbali.api.UserRequest
import com.diskominfos.subakbali.api.UserResponse
import com.diskominfos.subakbali.databinding.ActivityLoginBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import retrofit2.Call
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]
    }

    private fun setupAction() {
        binding.buttonLogin.setOnClickListener {
            showLoading(true)
            login()
        }
    }

    private fun login() {
        loginViewModel.login()
        val request = UserRequest()
        request.username = binding.inputUsername.text.toString().trim()
        request.password = binding.inputPassword.text.toString().trim()
        val client = ApiConfig.getApiService()
        client.login(request).enqueue(object : retrofit2.Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        Log.e("name", user.data?.name.toString())
                        Log.e("token", user.data?.token.toString())
                    }

                    Toast.makeText(this@LoginActivity, "Login Success", Toast.LENGTH_SHORT).show()
                    if (user != null) {
                        loginViewModel.saveUser(user.data?.token!!)
                    }
                    success()

                } else {
                    val user = response.body()
                    if (user != null) {
                        Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                    }
                    Toast.makeText(
                        this@LoginActivity,
                        "Email or password incorrect",
                        Toast.LENGTH_SHORT
                    ).show()
                    showLoading(false)
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun success() {
        Intent(this@LoginActivity, HomeActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}

