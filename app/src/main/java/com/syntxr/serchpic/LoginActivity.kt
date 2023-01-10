package com.syntxr.serchpic

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.EncryptUtils
import com.syntxr.serchpic.`object`.Api
import com.syntxr.serchpic.`object`.SharedPref
import com.syntxr.serchpic.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch
import kotlin.math.log

class LoginActivity : AppCompatActivity(R.layout.activity_login) {

    private val binding: ActivityLoginBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = getSharedPreferences(SharedPref.pref, Context.MODE_PRIVATE)



        binding.btnLogin.setOnClickListener {
            val email = binding.inputLoginEmail.text.toString()
            val password = binding.inputLoginPassword.text.toString()

            if (email.isEmpty()) {
                binding.inputLoginEmailLayout.error = "anda belum mengisi email"
                return@setOnClickListener
            }

            binding.inputLoginEmailLayout.error = null

            if (password.isEmpty()) {
                binding.inputLoginPasswordLayout.error = "anda belum mengisi password"
                return@setOnClickListener
            }

            binding.inputLoginPasswordLayout.error = null

            val encrypt = EncryptUtils.encryptMD5ToString(password)

            val api = Api.create()

            if (CheckInternet().checkInternet(this)) {

                lifecycleScope.launch {

                    try {
                        binding.loadLogin.visibility = View.VISIBLE
                        val rensponse = api.login("*", "eq.$email", "eq.$encrypt")
                        if (rensponse.isEmpty()) {
                            binding.inputLoginPasswordLayout.error =
                                "Email atau Password anda salah"
                            return@launch
                        }

                        binding.inputLoginPasswordLayout.error = null

                        val login = rensponse.first()

                        preferences.edit().putInt(SharedPref.userId, login.id).apply()
                        preferences.edit().putString(SharedPref.userName, login.username).apply()
                        preferences.edit().putString(SharedPref.userEmail, login.email).apply()
                        preferences.edit().putString(SharedPref.userImg, login.image).apply()

                        binding.loadLogin.visibility = View.GONE

                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@LoginActivity,
                            "kesalahan ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(this, "Periksa Jaringan Internet Anda", Toast.LENGTH_SHORT).show()
            }
        }






        binding.loginToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()

        val preferences = getSharedPreferences(SharedPref.pref, Context.MODE_PRIVATE)
        val id = preferences.getInt(SharedPref.userId, 0)

        if (id != 0) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}