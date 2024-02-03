package com.project.tex.welcome

import android.annotation.SuppressLint
import android.content.Context
import androidx.viewpager.widget.PagerAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.project.tex.R
import java.util.*

class IntroViewPagerAdapter(val mContext: Context, val mListScreen: List<ScreenItem>) :
    PagerAdapter() {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        @SuppressLint("InflateParams") val layoutScreen =
            Objects.requireNonNull(inflater).inflate(R.layout.layout_intro_screen, null)
        val logo = layoutScreen.findViewById<ImageView>(R.id.logo)
        val title = layoutScreen.findViewById<TextView>(R.id.title)
        title.text = mListScreen[position].title
        logo.setImageResource(mListScreen[position].screenImg)
        container.addView(layoutScreen)
        return layoutScreen
    }

    override fun getCount(): Int {
        return mListScreen.size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view === o
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}