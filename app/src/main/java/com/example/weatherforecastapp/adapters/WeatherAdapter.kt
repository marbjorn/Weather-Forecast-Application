package com.example.weatherforecastapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.databinding.ListItemBinding
import com.squareup.picasso.Picasso

class WeatherAdapter(val listener : Listener?) : ListAdapter<WeatherModel, WeatherAdapter.Holder>(Comparator()){
    class Holder(view : View, val listener: Listener?) : RecyclerView.ViewHolder(view){
        val binding = ListItemBinding.bind(view)
        var itemTemp : WeatherModel? = null
        fun bind(item : WeatherModel) = with(binding){
            itemTemp = item
            tvDate.text = item.time
            tvCurrentCondition.text = item.condition
            tvTemp.text = if(item.currentTemp.isEmpty())
                 "${item.maxTemp}°C/${item.minTemp}°C" else "${item.currentTemp}°C"
            Picasso.get().load("https:" + item.imageUrl).into(im)
        }
        init{
            itemView.setOnClickListener {
                itemTemp?.let{ listener?.onClick(it)}
            }
        }

    }
    class Comparator : DiffUtil.ItemCallback<WeatherModel>() {
        override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean = oldItem == newItem

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return Holder(view, listener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    interface Listener{
        fun onClick(item : WeatherModel) {}
    }
}
