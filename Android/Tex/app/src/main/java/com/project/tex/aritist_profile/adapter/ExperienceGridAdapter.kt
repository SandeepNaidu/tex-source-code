package com.project.tex.aritist_profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.project.tex.aritist_profile.model.ExperienceListResponse
import com.project.tex.databinding.ExperienceItemLayoutBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class ExperienceGridAdapter(
    val list: List<ExperienceListResponse.Body.Experience?>,
    val listener: EditClickListener,
    val enabledEdit: Boolean
) :
    RecyclerView.Adapter<ExperienceGridAdapter.PostThumbHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostThumbHolder {
        return PostThumbHolder(
            ExperienceItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: PostThumbHolder, position: Int) {
        holder.itemView.tag = position
        val d = list[position]
        holder.init((position == list.size - 1), d)
        holder.view.imageView2.setOnClickListener {
            if (d != null) {
                listener.editClick(d)
            }
        }

        holder.view.imageView2.isVisible = enabledEdit
    }

    override fun getItemCount(): Int = list.size
    private val formatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern(
            "yyyy-MM-dd'T'HH:mm:ss.SSSX",
            Locale.ENGLISH
        )

    inner class PostThumbHolder(val view: ExperienceItemLayoutBinding) :
        RecyclerView.ViewHolder(view.root) {

        fun init(isLast: Boolean, item: ExperienceListResponse.Body.Experience?) {
            view.textView5.text = item?.companyName
            view.textView6.text = item?.jobTitle
            view.textView8.text = item?.description
            view.seperator.isVisible = !isLast

            try {
                val date1: LocalDate =
                    item?.startDate.let { d -> LocalDate.parse(d, formatter) } ?: LocalDate.now()
                val date2: LocalDate =
                    item?.endDate.let { d -> LocalDate.parse(d, formatter) } ?: LocalDate.now()
                view.textView7.text = "${date1.year} - ${date2?.year}"
            } catch (e: Exception) {

            }
        }
    }

    interface EditClickListener {
        fun editClick(item: ExperienceListResponse.Body.Experience)
    }
}