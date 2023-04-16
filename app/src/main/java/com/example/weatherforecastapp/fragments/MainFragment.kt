package com.example.weatherforecastapp.fragments

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.adapters.VpAdapter
import com.example.weatherforecastapp.databinding.FragmentMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainFragment : Fragment() {
    private lateinit var pLauncher : ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
    private val fList = listOf (
        Hours.newInstance(),
        Days.newInstance(),
    )
    private val tList = listOf("hours", "days")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
    }

    private fun init() = with(binding) {
        val adapter = VpAdapter(activity as AppCompatActivity, fList as List<Fragment>)
        vp.adapter = adapter
        TabLayoutMediator(tlMenu, vp) {
            tab, pos -> tab.text = tList[pos].toString()
        }.attach()
    }
    private fun permissionListener() {
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermission() {
        if(!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}