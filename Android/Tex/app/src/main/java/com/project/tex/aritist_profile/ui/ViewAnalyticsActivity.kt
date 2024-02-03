package com.project.tex.aritist_profile.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.project.tex.R
import com.project.tex.aritist_profile.viewmodel.ProfileViewModel
import com.project.tex.base.activity.BaseActivity
import com.project.tex.databinding.ActivityViewAnalyticsBinding

class ViewAnalyticsActivity : BaseActivity<ActivityViewAnalyticsBinding, ProfileViewModel>(),
    View.OnClickListener {

    private var viewCount: Int? = 0

    override fun getViewBinding() = ActivityViewAnalyticsBinding.inflate(LayoutInflater.from(this))

    override fun getViewModelInstance() = ViewModelProvider(this).get(ProfileViewModel::class.java)

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(_binding.mainToolbar) //Set toolbar

        _binding.mainToolbar.setNavigationOnClickListener {
            finish()
        }

        _binding.profileViewsTv.text = (viewCount ?: 0).toString() + " profile views"
        viewModel.add(
            viewModel.getProfileAnalytics().subscribe(
                {
                    if (it.responseCode == 200 && it.body?.post != null && it.body.post.isNotEmpty()) {
                        _binding.postReactionTv.text =
                            (it.body.post.get(0)?.reactionCount ?: 0).toString() + " post reactions"
                        _binding.searchAppearTv.text =
                            (it.body.post.get(0)?.searchCount ?: 0).toString() + " search appearances"
                    } else {
                        msg.showShortMsg(getString(R.string.failed_to_fetch_analytics_data))
                    }
                },
                {
                    msg.showShortMsg(getString(R.string.something_wrong_try_again))
                })
        )

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.privacy -> {

            }
            R.id.terms_of_use -> {

            }
            R.id.open_source -> {

            }
        }
    }

}