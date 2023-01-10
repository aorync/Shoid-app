package com.syntxr.serchpic

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.blankj.utilcode.util.EncryptUtils
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.syntxr.serchpic.`object`.Api
import com.syntxr.serchpic.databinding.ActivityEditProfileBinding
import com.syntxr.serchpic.model.register.UpdateDataUser
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity(R.layout.activity_edit_profile) {

    private val binding : ActivityEditProfileBinding by viewBinding()
    private var oldPassword : String? = null
    private var newImage : String? = null
    private val PickFromGallery = 101
    private val storage = Firebase.storage


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val api = Api.create()


        val id = intent.getIntExtra("id",0)
        val name = intent.getStringExtra("username")
        val email = intent.getStringExtra("email")
        val image = intent.getStringExtra("image")
        val password = intent.getStringExtra("password")

        newImage = image

        binding.editAppBar.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_new)
        binding.editAppBar.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        if (newImage.isNullOrEmpty()){
            binding.imgEditProfile.setBackgroundColor(R.color.btn_search)
        }else{
            binding.imgEditProfile.load(newImage)
        }

        binding.imgEditProfile.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpg",
                        "image/jpeg"
                    )
                )
                .start(PickFromGallery)
        }

        binding.editName.setText(name)
        binding.editEmail.setText(email)

        binding.btnSaveEdit.setOnClickListener {

            val editName = binding.editName.text.toString()
            val editEmail = binding.editEmail.text.toString()
            val newPassword = binding.editPassword.text.toString()




            if (newPassword.isNullOrEmpty()){
//                val encrpyt = EncryptUtils.encryptMD5ToString(password)
                val userUpdateDataUser = UpdateDataUser(
                    username = editName,
                    email = editEmail,
                    password = password!!,
                    image = newImage.toString()

                )

                lifecycleScope.launch {
                    try {
                        binding.loadEditProfile.visibility = View.VISIBLE
                        val rensponse = api.updateUserData("eq.$id",userUpdateDataUser)
                        binding.loadEditProfile.visibility = View.GONE
                        onBackPressed()
                    }catch (e : Exception){
                        Toast.makeText(this@EditProfileActivity, "kesalahan ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                val encrpyt = EncryptUtils.encryptMD5ToString(newPassword)
                val userUpdateDataUser = UpdateDataUser(
                    username = editName,
                    email = encrpyt,
                    password = newPassword,
                    image = newImage.toString()
                )

                lifecycleScope.launch {
                    try {
                        binding.loadEditProfile.visibility = View.VISIBLE
                        val rensponse = api.updateUserData("eq.$id",userUpdateDataUser)
                        binding.loadEditProfile.visibility = View.GONE
                        onBackPressed()
                    }catch (e : Exception){
                        Toast.makeText(this@EditProfileActivity, "kesalahan ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }


        

        

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val id = intent.getIntExtra("id",0)

        when(resultCode){
            Activity.RESULT_OK -> {
                val uri = data?.data!!
                when(requestCode){
                    PickFromGallery -> {
                        storage.getReference("imageUpload")
                            .child("$id")
                            .child("profile")
                            .child("$uri")
                            .putFile(uri)
                            .addOnCompleteListener{
                                if (!it.isSuccessful){
                                    Toast.makeText(this, "gagal mengupload photo", Toast.LENGTH_SHORT).show()
                                    return@addOnCompleteListener
                                }
                                it.result.storage.downloadUrl.addOnSuccessListener {
                                    val image = it.toString()
                                    newImage = image
                                    binding.imgEditProfile.load(image)
                                }
                            }
                    }
                }
            }

            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }

            else -> {
                Toast.makeText(this, "tugas dibatalkan", Toast.LENGTH_SHORT).show()
            }
        }
    }
}