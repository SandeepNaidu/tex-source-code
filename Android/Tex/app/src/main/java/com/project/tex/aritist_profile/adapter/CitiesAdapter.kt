package com.project.tex.aritist_profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.project.tex.aritist_profile.dialog.CityDialog
import com.project.tex.aritist_profile.model.CitiesList
import com.project.tex.aritist_profile.model.ExperienceListResponse
import com.project.tex.databinding.ExperienceItemLayoutBinding
import com.project.tex.databinding.SimpleItemCityBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class CitiesAdapter(
    val list: List<CitiesList.CitiesListItem?>,
    val listener: ClickAction
) :
    RecyclerView.Adapter<CitiesAdapter.CityHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityHolder {
        return CityHolder(
            SimpleItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CityHolder, position: Int) {
        holder.itemView.tag = position
        val d = list[position]
        if (d != null) {
            holder.init(d)
            holder.itemView.setOnClickListener {
                listener.itemClick(d)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    inner class CityHolder(val view: SimpleItemCityBinding) :
        RecyclerView.ViewHolder(view.root) {

        fun init(item: CitiesList.CitiesListItem) {
            view.text1.text = item.name
        }
    }

    interface ClickAction {
        fun itemClick(item: CitiesList.CitiesListItem)
    }
}