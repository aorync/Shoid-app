package com.syntxr.serchpic.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.blankj.utilcode.util.EncryptUtils
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.syntxr.serchpic.CheckInternet
import com.syntxr.serchpic.MainActivity
import com.syntxr.serchpic.R
import com.syntxr.serchpic.`object`.Api
import com.syntxr.serchpic.`object`.SharedPref
import com.syntxr.serchpic.databinding.FragmentAddPostBinding
import com.syntxr.serchpic.model.post.AddPost
import kotlinx.coroutines.launch

class AddPostFragment : Fragment(R.layout.fragment_add_post) {

    private val PickFromGallery = 101
    private val PICK_FROM_CAMERA = 100
    private var imageUrl : String? = null
    private var description : String? = null

    private val binding: FragmentAddPostBinding by viewBinding()
    private val storage = Firebase.storage

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val preferences = requireContext().getSharedPreferences(SharedPref.pref,Context.MODE_PRIVATE)
        val id = preferences.getInt(SharedPref.userId,0)



        binding.cardAddPost.setOnClickListener {
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

        binding.imgAddPost.load(imageUrl)

        if (CheckInternet().checkInternet(requireContext())) {

            binding.btnAddPost.setOnClickListener {
                val title = binding.addPostTitle.text.toString()
                val desc = binding.addPostDesc.text.toString()

                if (imageUrl.isNullOrEmpty()){
                    Toast.makeText(requireContext(), "anda belum memilih gambar", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (title.isEmpty()){
                    binding.addPostTitle.error = "Judul anda kosong"
                    return@setOnClickListener
                }

                if (desc.isEmpty()){
                    val addPost = AddPost(
                        userId = id,
                        title = title,
                        desc = null.toString(),
                        imageUrl = imageUrl.toString(),
                        date = "now()"
                    )

                    val api = Api.create()
                    lifecycleScope.launch {
                        val rensponse = api.uploadPost(addPost)
                        if (rensponse.isEmpty()){
                            Toast.makeText(requireContext(), "Gagal mengupload Unggahan", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        binding.addPostTitle.text = null
                        binding.addPostDesc.text = null
                        findNavController().navigate(R.id.action_add_to_home)
                    }

                    return@setOnClickListener
                }



                val addPost = AddPost(
                    userId = id,
                    title = title,
                    desc = desc,
                    imageUrl = imageUrl.toString(),
                    date = "now()"
                )

                val api = Api.create()
                lifecycleScope.launch {
                    try {
                        binding.loadAddPost.visibility = View.VISIBLE
                        val rensponse = api.uploadPost(addPost)
                        if (rensponse.isEmpty()){
                            Toast.makeText(requireContext(), "Gagal mengupload Unggahan", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        binding.loadAddPost.visibility = View.GONE

                        binding.addPostTitle.text = null
                        binding.addPostDesc.text = null
                        findNavController().navigate(R.id.action_add_to_home)
                    }catch (e : Exception){
                        Toast.makeText(requireContext(), "kesalahan ${e.message}", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        } else {
            Toast.makeText(requireContext(), "Periksa Jaringan Internet Anda", Toast.LENGTH_SHORT).show()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val pref = requireContext().getSharedPreferences(SharedPref.pref,Context.MODE_PRIVATE)
        val id = pref.getInt(SharedPref.userId,0)

        when(resultCode){
            Activity.RESULT_OK ->  {
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
                                    Toast.makeText(requireContext(), "Gagal mengupload photo", Toast.LENGTH_SHORT).show()
                                    return@addOnCompleteListener
                                }
                                it.result.storage.downloadUrl.addOnSuccessListener {
                                    val image = it.toString()
                                    imageUrl = image
                                    binding.tvTextAdd.visibility = View.GONE
                                    binding.imgAddPost.load(image)
                                }
                            }
                    }
                }
            }

            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }

            else ->{
                Toast.makeText(requireContext(), "tugas dibatalkan", Toast.LENGTH_SHORT).show()
            }
        }

    }

}