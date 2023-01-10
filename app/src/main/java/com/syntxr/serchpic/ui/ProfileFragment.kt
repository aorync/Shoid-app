package com.syntxr.serchpic.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.faltenreich.skeletonlayout.createSkeleton
import com.google.android.material.tabs.TabLayoutMediator
import com.syntxr.serchpic.*
import com.syntxr.serchpic.`object`.Api
import com.syntxr.serchpic.`object`.SharedPref
import com.syntxr.serchpic.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    val binding: FragmentProfileBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = requireContext().getSharedPreferences(SharedPref.pref, Context.MODE_PRIVATE)

        if (CheckInternet().checkInternet(requireContext())) {
            fetchData()
        } else {
            Toast.makeText(requireContext(), "Periksa Jaringan Internet Anda", Toast.LENGTH_SHORT).show()
        }

        binding.swipeProfile.setOnRefreshListener {
            if (CheckInternet().checkInternet(requireContext())) {
                fetchData()
            } else {
                Toast.makeText(requireContext(), "Periksa Jaringan Internet Anda", Toast.LENGTH_SHORT).show()
            }
        }

        val fragmentList = listOf(PostFragment(), SavedFragment(), OfflineSavedFragment())
        val adapter = ViewPagerFragmentAdapter(requireParentFragment(), fragmentList)

        val title = listOf("Unggahan", "Disimpan", "Offline")
        binding.viewPagerProfile.adapter = adapter
        TabLayoutMediator(binding.tabLayoutProfile, binding.viewPagerProfile) { tab, pos ->
            tab.text = title[pos]
        }.attach()

        binding.btnLogout.setOnClickListener {
            pref.edit().remove(SharedPref.userId).apply()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            activity?.finish()
        }




    }

    @SuppressLint("ResourceAsColor")
    private fun fetchData() {

        val pref = requireContext().getSharedPreferences(SharedPref.pref, Context.MODE_PRIVATE)
        val id = pref.getInt(SharedPref.userId, 0)

        val api = Api.create()
        lifecycleScope.launch {
            try {
                val shimmerName = binding.tvProfileName.createSkeleton()
                val shimmerEmail = binding.tvProfileEmail.createSkeleton()
                val shimmerImg = binding.imgProfile.createSkeleton()
                val shimmerPost = binding.tvCountPostUser.createSkeleton()
                val shimmerFollow = binding.tvCountFollowUser.createSkeleton()

                shimmerEmail.showSkeleton()
                shimmerImg.showSkeleton()
                shimmerName.showSkeleton()
                shimmerFollow.showSkeleton()
                shimmerPost.showSkeleton()

                val rensponse = api.getUserData("eq.$id", "*")
                val getPost = api.getPost("eq.$id", "*")
                val getFollower = api.getFollower("eq.$id")

                val user = rensponse.first()

                binding.tvProfileName.text = user.username
                binding.tvProfileEmail.text = user.email

                binding.tvCountPostUser.text = getPost.count().toString()
                binding.tvCountFollowUser.text = getFollower.count().toString()



                if (user.image.isNullOrEmpty()) {
                    binding.imgProfile.setBackgroundColor(R.color.purple_200)
                }else{
                    binding.imgProfile.load(user.image)

                }


                shimmerImg.showOriginal()
                shimmerEmail.showOriginal()
                shimmerName.showOriginal()
                shimmerFollow.showOriginal()
                shimmerPost.showOriginal()

                binding.swipeProfile.isRefreshing = false

                if (rensponse.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "tidak dapat mengambil data pengguna",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }



                binding.btnEditUser.setOnClickListener {
                    val intent = Intent(requireContext(), EditProfileActivity::class.java)
                    intent.putExtra("id", user.id)
                    intent.putExtra("username", user.username)
                    intent.putExtra("email", user.email)
                    intent.putExtra("image", user.image)
                    intent.putExtra("password", user.password)

                    startActivity(intent)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "kesalahan ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    @SuppressLint("ServiceCast")
    private fun checkForInternet(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}