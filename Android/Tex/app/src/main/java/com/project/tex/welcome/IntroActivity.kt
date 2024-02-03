package com.project.tex.welcome

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.project.tex.R
import com.project.tex.base.activity.BaseActivity
import com.project.tex.databinding.ActivityIntroBinding
import com.project.tex.onboarding.login.QuickSignInActivity

class IntroActivity : BaseActivity<ActivityIntroBinding, IntroViewModel>() {
    private var screenPager: ViewPager? = null
    var introViewPagerAdapter: IntroViewPagerAdapter? = null
    var tabIndicator: TabLayout? = null
    var next: Button? = null
    var position = 0

    override fun getViewBinding(): ActivityIntroBinding = ActivityIntroBinding.inflate(layoutInflater)

    override fun getViewModelInstance(): IntroViewModel = ViewModelProvider(this).get(IntroViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //ini view
        next = findViewById(R.id.next)
        tabIndicator = findViewById(R.id.indicator)

        //Fill list screen
        val mList: MutableList<ScreenItem> = ArrayList()
        mList.add(
            ScreenItem(
                """
    Post video, image & text
    With hashtag and mention
    your friends
    """.trimIndent(), R.drawable.ic_one
            )
        )
        mList.add(
            ScreenItem(
                """
    Watch or create reels with
    face filters camera
    """.trimIndent(), R.drawable.ic_two
            )
        )
        mList.add(
            ScreenItem(
                """
    Private chat with video &
    voice calls
    """.trimIndent(), R.drawable.ic_three
            )
        )
        mList.add(
            ScreenItem(
                """
    Create or join groups & 
    group chat with group
    Video & voice call
    """.trimIndent(), R.drawable.ic_four
            )
        )
        mList.add(
            ScreenItem(
                """
    Create or join Live stream
    & chat during stream 
    """.trimIndent(), R.drawable.ic_five
            )
        )
        mList.add(
            ScreenItem(
                """
    Create or join Live stream
    & chat during stream 
    """.trimIndent(), R.drawable.ic_five
            )
        )

        //Setup viewpager
        screenPager = findViewById(R.id.screen_viewpager)
        introViewPagerAdapter = IntroViewPagerAdapter(this, mList)
        screenPager!!.setAdapter(introViewPagerAdapter)

        //setup tabLayout with pagerView
        tabIndicator!!.setupWithViewPager(screenPager)

        //Next btn click
        next!!.setOnClickListener(View.OnClickListener { view: View? ->
            position = screenPager!!.getCurrentItem()
            if (position < mList.size) {
                position++
                screenPager!!.setCurrentItem(position)
            }
            //When reached last
            if (position == mList.size - 1) {
                loadLastScreen()
            }
        })

        //tabLayout last
        tabIndicator!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == mList.size - 1) {
                    loadLastScreen()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun loadLastScreen() {
        val intent = Intent(applicationContext, QuickSignInActivity::class.java)
        startActivity(intent)
        finish()
    }
}