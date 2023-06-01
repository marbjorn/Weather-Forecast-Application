package com.example.weatherforecastapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherforecastapp.MainViewModel
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.adapters.WeatherAdapter
import com.example.weatherforecastapp.adapters.WeatherModel
import com.example.weatherforecastapp.databinding.FragmentDaysBinding


class Days : Fragment(), WeatherAdapter.Listener {

    private val model : MainViewModel by activityViewModels()
    private lateinit var adapter : WeatherAdapter
    private lateinit var binding : FragmentDaysBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun init() =with(binding){
        rcView.layoutManager = LinearLayoutManager(activity)
        adapter = WeatherAdapter(this@Days)
        rcView.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        model.lifeDataList.observe(viewLifecycleOwner){
            adapter.submitList(it.subList(1, it.size))
        }
    }

    companion object {
       @JvmStatic
        fun newInstance() = Days()
    }

    override fun onClick(item : WeatherModel) {
        model.lifeDataCurrent.value = item
    }
}