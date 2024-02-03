package com.project.tex.aritist_profile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.project.tex.aritist_profile.adapter.PostGridAdapter
import com.project.tex.aritist_profile.viewmodel.ProfileViewModel
import com.project.tex.base.fragment.BaseFragment
import com.project.tex.databinding.FragmentPortfolioBinding


/**
 * A simple [Fragment] subclass.
 * Use the [PortfolioFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PortfolioFragment : BaseFragment<FragmentPortfolioBinding>() {

    override fun getViewBinding() = FragmentPortfolioBinding.inflate(LayoutInflater.from(context))
    private val mainViewModel: ProfileViewModel by viewModels({ requireActivity() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.fabAddMedia.setOnClickListener {
            (context as? ProfileActivity)?.onClick(it)
        }
        _binding.btnAddMedia.setOnClickListener {
            (context as? ProfileActivity)?.onClick(it)
        }

        if (!mainViewModel.getIsSelfView()) {
            _binding.btnAddMedia.isVisible = false
            _binding.fabAddMedia.isVisible = false
        }

        _binding.postList.itemAnimator = null
        (_binding.postList.layoutManager as StaggeredGridLayoutManager).gapStrategy =
            StaggeredGridLayoutManager.GAP_HANDLING_NONE
        mainViewModel.portfolioLd.observe(viewLifecycleOwner, Observer {
            val mutableList = mutableListOf<Any>()
            it.body?.portfolios?.data?.let { j ->
                j.forEach { p ->
                    if (p != null) {
                        mutableList.add(p)
                    }
                }
            }
            if (mutableList.isEmpty()) {
                _binding.contentGroup.isVisible = false
                _binding.fabAddMedia.isVisible = false
                _binding.llPlaceholder.isVisible = true
            } else {
                _binding.contentGroup.isVisible = true
                _binding.fabAddMedia.isVisible = mainViewModel.getIsSelfView()
                _binding.llPlaceholder.isVisible = false
                _binding.postList.adapter = PostGridAdapter(mutableList)
            }
        })

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment PortfolioFragment.
         */
        @JvmStatic
        fun newInstance() =
            PortfolioFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

}