package com.syntxr.serchpic

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.syntxr.serchpic.`object`.Api
import com.syntxr.serchpic.`object`.SharedPref
import com.syntxr.serchpic.databinding.ActivityEditPostBinding
import com.syntxr.serchpic.model.post.UpdatePost
import kotlinx.coroutines.launch

class EditPostActivity : AppCompatActivity() {

    private val binding : ActivityEditPostBinding by viewBinding()
    private val storage = Firebase.storage
    private var newImage : String? = null
    private val PickFromGallery = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_post)

        val api = Api.create()

        val postId = intent.getIntExtra("postId",0)
        val title = intent.getStringExtra("title")
        val desc = intent.getStringExtra("desc")
        val image = intent.getStringExtra("image")

        newImage = image

        binding.editPostAppBar.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_ios_new)
        binding.editPostAppBar.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.imgPatchPost.load(image)

        binding.imgPatchPost.setOnClickListener {
            ImagePicker.with(this)
                .start(PickFromGallery)
        }

        binding.addPatchTitle.setText(title)
        binding.addPatchDesc.setText(desc)

        binding.btnPatch.setOnClickListener {
            val newTitle = binding.addPatchTitle.text.toString()
            val newDesc = binding.addPatchDesc.text.toString()

            val updatePost = UpdatePost(
                title = newTitle,
                desc = newDesc,
                imageUrl = newImage.toString()
            )

            lifecycleScope.launch {
                try {
                    binding.loadEditPost.visibility = View.VISIBLE
                    val rensponse = api.updatePost("eq.$postId", updatePost)
                    binding.loadEditPost.visibility = View.GONE
                    onBackPressed()
                }catch (e : Exception){
                    Toast.makeText(this@EditPostActivity, "kesalahan ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val pref = getSharedPreferences(SharedPref.pref, Context.MODE_PRIVATE)
        val id = pref.getInt(SharedPref.userId,0)

        when(resultCode){
            Activity.RESULT_OK -> {
                val uri = data?.data!!
                when(requestCode){
                    PickFromGallery -> {
                        storage.getReference("imageUpload")
                            .child("$id")
                            .child("post")
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
                                    binding.imgPatchPost.load(image)
                                }
                            }
                    }
                }
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }

            else ->{
                Toast.makeText(this, "tugas dibatalkan", Toast.LENGTH_SHORT).show()
            }
        }


    }
}