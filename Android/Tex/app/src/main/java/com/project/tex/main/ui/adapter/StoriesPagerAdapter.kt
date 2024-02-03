package com.project.tex.main.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.project.tex.main.model.AvidData
import com.project.tex.main.ui.avid.StoryViewFragment

class StoriesPagerAdapter(fragment: Fragment, val dataList: MutableList<AvidData> = mutableListOf()) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun createFragment(position: Int): Fragment {
        return StoryViewFragment.newInstance(dataList[position])
    }
}