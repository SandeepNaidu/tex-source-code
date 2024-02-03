package com.project.tex.aritist_profile.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.project.tex.aritist_profile.ui.ExperienceFragment
import com.project.tex.aritist_profile.ui.PortfolioFragment
import com.project.tex.aritist_profile.ui.PostFragment

class ViewPagerAdapter(activity: FragmentActivity,val list: List<Fragment>) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }

}