package com.project.tex.aritist_profile.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.project.tex.R
import com.project.tex.aritist_profile.ui.ProfileActivity
import java.util.*

class ImageSliderAdapter(val context: Context, private val imageList: MutableList<ProfileActivity.CoverImage>) : PagerAdapter() {
    // on below line we are creating a method
    // as get count to return the size of the list.
    override fun getCount(): Int {
        return imageList.size
    }

    // on below line we are returning the object
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as FrameLayout
    }

    // on below line we are initializing
    // our item and inflating our layout file
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        // on below line we are initializing
        // our layout inflater.
        val mLayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // on below line we are inflating our custom
        // layout file which we have created.
        val itemView: View =
            mLayoutInflater.inflate(R.layout.image_slide_item, container, false)

        // on below line we are initializing
        // our image view with the id.
        val imageView: ImageView = itemView.findViewById<View>(R.id.idIVImage) as ImageView

        // on below line we are setting
        // image resource for image view.
        val thumbnail: RequestBuilder<Bitmap> = Glide.with(itemView)
            .asBitmap()
            .load(imageList.get(position).url)
            .override(300)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(context).asBitmap().load(imageList.get(position).url)
            .thumbnail(thumbnail)
            .error(R.drawable.ic_no_preview).into(imageView)

        // on the below line we are adding this
        // item view to the container.
        Objects.requireNonNull(container).addView(itemView)

        // on below line we are simply
        // returning our item view.
        return itemView
    }

    // on below line we are creating a destroy item method.
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        // on below line we are removing view
        container.removeView(`object` as FrameLayout)
    }
}