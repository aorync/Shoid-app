package com.syntxr.serchpic

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.EncryptUtils
import com.syntxr.serchpic.`object`.Api
import com.syntxr.serchpic.`object`.SharedPref
import com.syntxr.serchpic.databinding.ActivityRegisterBinding
import com.syntxr.serchpic.model.register.Register
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity(R.layout.activity_register) {

    private val binding: ActivityRegisterBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pref = getSharedPreferences(SharedPref.pref, Context.MODE_PRIVATE)


        binding.btnRegister.setOnClickListener {
            val name = binding.inputRegisterName.text.toString()
            val email = binding.inputRegisterEmail.text.toString()
            val password = binding.inputRegisterPassword.text.toString()
            val confirm = binding.inputConfirmPassword.text.toString()

            if (name.isEmpty()) {
                binding.inputRegisterNameLayout.error = "anda belum mengisi nama"
                return@setOnClickListener
            }

            binding.inputRegisterNameLayout.error = null

            if (email.isEmpty()) {
                binding.inputRegisterEmailLayout.error = "anda belum mengisi email"
                return@setOnClickListener
            }

            binding.inputRegisterEmailLayout.error = null

            if (password.isEmpty()) {
                binding.inputRegisterPasswordLayout.error = "anda belum mengisi password"
                return@setOnClickListener
            }

            binding.inputRegisterPasswordLayout.error = null

            if (confirm != password) {
                binding.inputConfirmPasswordLayout.error = "password anda tidak sama"
                return@setOnClickListener
            }

            binding.inputConfirmPasswordLayout.error = null

            val encrypt = EncryptUtils.encryptMD5ToString(password)

            val register = Register(
                username = name,
                email = email,
                password = encrypt,
                image = null
            )

            val api = Api.create()
            if (CheckInternet().checkInternet(this)) {
                lifecycleScope.launch {
                    try {
                        binding.loadRegister.visibility = View.VISIBLE
                        val isUsernameExist = api.isUsernameExist("*", "eq.$name")
                        if (isUsernameExist.isNotEmpty()) {
                            binding.inputRegisterNameLayout.error = "nama ini telah digunakan"
                            return@launch
                        }

                        binding.inputRegisterNameLayout.error = null


                        val rensponse = api.register(register)
                        if (rensponse.isEmpty()) {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Gagal Mendaftarkan Akun",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@launch
                        }

                        binding.loadRegister.visibility = View.GONE

                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "kesalahan ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            } else {
                Toast.makeText(this, "Periksa Jaringan Internet Anda", Toast.LENGTH_SHORT).show()
            }
        }





        binding.registerToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}