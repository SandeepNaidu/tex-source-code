package com.project.tex.recruiter.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.project.tex.R
import com.project.tex.base.fragment.BaseFragment
import com.project.tex.databinding.FragmentRecHomeBinding
import com.project.tex.databinding.ProfileFormItemLayoutBinding
import com.project.tex.main.ui.adapter.CategoryAdapter
import com.project.tex.main.ui.adapter.RecTrendAdapter
import com.project.tex.main.ui.adapter.TrendAdapter
import com.project.tex.main.ui.home.HomePostViewModel
import com.project.tex.recruiter.ui.adapter.PostAdapter
import com.project.tex.recruiter.ui.adapter.ProfileAdapter
import com.project.tex.recruiter.ui.adapter.ProfileFormAdapter
import com.project.tex.recruiter.ui.dialog.PostActionDialog

class RecHomeFragment : BaseFragment<FragmentRecHomeBinding>(), PostAdapter.PostActions {

    override fun getViewBinding() = FragmentRecHomeBinding.inflate(layoutInflater)
    private var profileQuestionCurrentPage: Int = 0
    private val postOptionDialog by lazy {
        PostActionDialog()
    }
    private val mainViewModel: RecHomeViewModel by viewModels({ requireActivity() })

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.categoryRv.layoutManager =
            CenterLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        _binding.categoryRv.adapter = CategoryAdapter(
            mutableListOf(
                "All",
                "Singing",
                "Acting",
                "Music",
                "Dancing",
                "Singing"
            ), object : CategoryAdapter.CategoryListener {
                override fun categorySelected(pos: Int) {
                    _binding.categoryRv.smoothScrollToPosition(pos)
                    _binding.categoryRv.adapter?.notifyDataSetChanged()
                }
            })

        bindUi(_binding.profileLayout)
        _binding.profileLayout.postFrame.adapter = ProfileFormAdapter(mutableListOf(0, 1, 2, 3, 4))
//        _binding.recruiterHistoryRv.adapter = RecruiterHistoryAdapter(null)
        _binding.profileLayout.postFrame.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                profileQuestionCurrentPage = position
                updatePageText(position)
                _binding.profileLayout.dotsIndicator.setDotProgressiveSelection(position)
                updateFormButton()
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })

        updateFormButton()
//        val kohii = Kohii[this]

        mainViewModel.getAvids().subscribe({
            it.body?.avids?.let { it1 -> _binding.trendingAvidRv.adapter = TrendAdapter(it1)
            }
        }, {
            msg.showShortMsg("Failed to get the avids at the moment.")
        })

        _binding.trendingAvidRv.setItemViewCacheSize(10)
        _binding.recommendedProfileRv.adapter = ProfileAdapter()
        _binding.categoryRv.adapter?.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        _binding.trendingAvidRv.adapter?.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        _binding.recommendedProfileRv.adapter?.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        _binding.fabCreate.setOnClickListener {
//            startActivity(Intent(requireContext(), AddMediaActivity::class.java))
        }
    }

    private fun updatePageText(position: Int) {
        _binding.profileLayout.stepperTv.text =
            "${position + 1}/${_binding.profileLayout.postFrame.adapter!!.count}"
    }

    override fun onOptionMenuClick(pos: Int, postInfo: Any?) {
        postOptionDialog.showDialog(childFragmentManager)
    }

    override fun onSaveClick(pos: Int, postInfo: Any?) {
    }

    override fun onShareClick(pos: Int, postInfo: Any?) {
    }

    override fun onLikeClick(pos: Int, postInfo: Any?) {
    }

    private fun bindUi(view: ProfileFormItemLayoutBinding) {
        _binding.profileLayout.stepperTv.text = "1/5"
        _binding.profileLayout.dotsIndicator.setDotProgressiveSelection(0)
        view.btnSkip.setOnClickListener {
            skip()
        }
        view.leftArrowBtn.setOnClickListener {
            prev()
        }
        view.rightArrowBtn.setOnClickListener {
            next()
        }
        view.btnNext.setOnClickListener {
            next()
        }
    }

    private fun updateFormButton() {
        if (profileQuestionCurrentPage == _binding.profileLayout.postFrame.adapter!!.count - 1) {
            _binding.profileLayout.btnNext.visibility = View.INVISIBLE
            _binding.profileLayout.rightArrowBtn.isEnabled = false
            _binding.profileLayout.rightArrowBtn.imageAlpha = 120
            _binding.profileLayout.leftArrowBtn.imageAlpha = 255
        } else {
            _binding.profileLayout.rightArrowBtn.imageAlpha = 255
            _binding.profileLayout.btnNext.visibility = View.VISIBLE
            _binding.profileLayout.rightArrowBtn.isEnabled = true
            _binding.profileLayout.leftArrowBtn.imageAlpha =
                if (profileQuestionCurrentPage == 0) 120 else 255
        }
    }

    fun next() {
        if (profileQuestionCurrentPage == _binding.profileLayout.postFrame.adapter!!.count - 1) return
        _binding.profileLayout.postFrame.setCurrentItem(profileQuestionCurrentPage + 1, true)
    }

    fun prev() {
        if (profileQuestionCurrentPage == 0) return
        _binding.profileLayout.postFrame.setCurrentItem(profileQuestionCurrentPage - 1, true)
    }

    fun skip() {
        if (profileQuestionCurrentPage < 0) {
            return
        }
        // remove the question page from viewpager
        (_binding.profileLayout.postFrame.adapter as ProfileFormAdapter).remove(
            profileQuestionCurrentPage
        )
        val pages = _binding.profileLayout.postFrame.adapter!!.count
        if (profileQuestionCurrentPage >= pages - 1) {
            profileQuestionCurrentPage = pages - 1
        }
        // re initialize the dots count
        if (profileQuestionCurrentPage < pages) {
            _binding.profileLayout.dotsIndicator.initDots(pages)
        }
        // hide profile form section if all question skipped or answered
        if (pages == 0) {
            _binding.profileLayout.root.isVisible = false
        }
        // update the profile form section text
        updateFormButton()
        updatePageText(profileQuestionCurrentPage)
    }
}