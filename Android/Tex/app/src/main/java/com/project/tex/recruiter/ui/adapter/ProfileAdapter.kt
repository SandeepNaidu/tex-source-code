package com.project.tex.recruiter.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.tex.R
import com.project.tex.databinding.RecommenedProfileItemLayoutBinding

class ProfileAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listOfProf = listOf(
        ProfileMod(
            "Emma Watson",
            "Actor, Influencer",
            "London, United Kingdom",
            R.drawable.profile1
        ),
        ProfileMod(
            "Emma Stone",
            "Actor, Influencer",
            "Scottsdale, Arizona, United States",
            R.drawable.profile2
        ),
        ProfileMod(
            "Emma Watson",
            "Actor, Influencer",
            "London, United Kingdom",
            R.drawable.profile1
        ),
        ProfileMod(
            "Emma Watson",
            "Actor, Influencer",
            "London, United Kingdom",
            R.drawable.profile2
        )
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProfileViewHolder(
            RecommenedProfileItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ProfileViewHolder) {
            holder.bind(listOfProf.get(position))
        }
    }

    override fun getItemCount(): Int = 4

    inner class ProfileViewHolder(val view: RecommenedProfileItemLayoutBinding) :
        RecyclerView.ViewHolder(view.root) {
        fun bind(get: ProfileMod) {
            view.usernameTv.setText(get.name)
            view.locationTv.setText(get.location)
            view.profileTypeTv.setText(get.profession)
            Glide.with(view.profileImg).load(get.img).into(view.profileImg)
        }
    }

    data class ProfileMod(
        val name: String,
        val profession: String,
        val location: String,
        val img: Int
    )
}