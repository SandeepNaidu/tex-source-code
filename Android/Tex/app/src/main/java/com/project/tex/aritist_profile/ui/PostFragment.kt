package com.project.tex.aritist_profile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.project.tex.R
import com.project.tex.aritist_profile.adapter.PostGridAdapter
import com.project.tex.aritist_profile.dialog.FilterPostDialog
import com.project.tex.aritist_profile.dialog.SortPostDialog
import com.project.tex.aritist_profile.viewmodel.ProfileViewModel
import com.project.tex.base.fragment.BaseFragment
import com.project.tex.databinding.FragmentPostBinding
import com.project.tex.post.model.AllPostData
import kotlin.streams.toList


/**
 * A simple [Fragment] subclass.
 * Use the [PostFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PostFragment : BaseFragment<FragmentPostBinding>(), SortPostDialog.ClickAction,
    View.OnClickListener, FilterPostDialog.ClickAction {
    override fun getViewBinding() = FragmentPostBinding.inflate(LayoutInflater.from(context))

    private lateinit var adapterPost: PostGridAdapter
    private val sortDialog by lazy {
        SortPostDialog()
    }
    private val filterDialog by lazy {
        FilterPostDialog()
    }

    private val mainViewModel: ProfileViewModel by viewModels({ requireActivity() })
    private val listOfAvid: MutableList<Any> = mutableListOf()
    private val listOfPost: MutableList<Any> = mutableListOf()
    private var currentList: MutableList<Any>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.sortPost.setOnClickListener(this)
        _binding.filterPost.setOnClickListener(this)
        _binding.btnAddPost.setOnClickListener {
            (context as? ProfileActivity)?.onClick(it)
        }
        _binding.fabAdd.setOnClickListener {
            (context as? ProfileActivity)?.onClick(it)
        }
        adapterPost = PostGridAdapter(listOfPost)
        _binding.postList.itemAnimator = null
        (_binding.postList.layoutManager as StaggeredGridLayoutManager).gapStrategy =
            StaggeredGridLayoutManager.GAP_HANDLING_NONE
        _binding.postList.adapter = adapterPost
        mainViewModel.compositeDisposable.add(mainViewModel.getAllPostListing().subscribe({ posts ->
            listOfPost.addAll(posts)
            if (posts.isNotEmpty()) {
                _binding.contentGroup.isVisible = true
                _binding.llPlaceholder.isVisible = false
                _binding.fabAdd.isVisible = mainViewModel.getIsSelfView()
                _binding.btnAddPost.isVisible = mainViewModel.getIsSelfView()
                mainViewModel.compositeDisposable.add(
                    mainViewModel.getAvids()
                        .subscribe({ avids ->
                            avids.body?.avid?.let { it1 ->
                                listOfAvid.addAll(it1)
                                listOfPost.addAll(it1)
                            }
                            adapterPost.updateList(listOfPost)
                            currentList = listOfPost
                        }, {
                            adapterPost.updateList(listOfPost)
                            currentList = listOfPost
                        })
                )
            } else {
                mainViewModel.compositeDisposable.add(
                    mainViewModel.getAvids()
                        .subscribe({ avids ->
                            avids.body?.avid?.let { it1 ->
                                listOfAvid.addAll(it1)
                                listOfPost.addAll(it1)
                                adapterPost.updateList(listOfPost)
                                currentList = listOfPost
                                _binding.contentGroup.isVisible = true
                                _binding.llPlaceholder.isVisible = false
                                _binding.btnAddPost.isVisible = mainViewModel.getIsSelfView()
                                _binding.fabAdd.isVisible = mainViewModel.getIsSelfView()
                            } ?: kotlin.run {
                                adapterPost.updateList(listOfPost)
                                currentList = listOfPost
                                _binding.contentGroup.isVisible = false
                                _binding.llPlaceholder.isVisible = true
                                _binding.fabAdd.isVisible = mainViewModel.getIsSelfView()
                                _binding.btnAddPost.isVisible = mainViewModel.getIsSelfView()
                            }
                        }, {
                            _binding.contentGroup.isVisible = false
                            _binding.llPlaceholder.isVisible = true
                        })
                )
            }
        }, {
            _binding.contentGroup.isVisible = false
        }))
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment PostFragment.
         */
        @JvmStatic
        fun newInstance() = PostFragment().apply {
            arguments = Bundle().apply {}
        }
    }

    override fun byDate() {
        val dataList = currentList?.stream()?.sorted { o1, o2 ->
            try {
                (o2 as? AllPostData.Body.Posts.DateComparable)?.date?.let { it1 ->
                    (o1 as? AllPostData.Body.Posts.DateComparable)?.date?.compareTo(
                        it1
                    )
                } ?: 0
            } catch (e: Exception) {
                0
            }
        }?.toList()
        if (dataList != null) {
            adapterPost.updateList(dataList.toMutableList())
        }
    }

    override fun byPopularity() {
        val dataList = currentList?.stream()?.sorted { o1, o2 ->
            (o2 as AllPostData.Body.Posts.DateComparable).likeCounts?.let {
                (o1 as? AllPostData.Body.Posts.DateComparable)?.likeCounts?.compareTo(
                    it
                )
            } ?: 0
        }?.toList()
        if (dataList != null) {
            adapterPost.updateList(dataList.toMutableList())
        }
    }

    override fun byMostViewed() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sort_post -> {
                sortDialog.show(childFragmentManager, "")
            }
            R.id.filter_post -> {
                filterDialog.show(childFragmentManager, "")
            }
        }
    }

    override fun filterByAvid() {
        adapterPost.updateList(listOfAvid)
        currentList = listOfAvid
    }

    override fun filterByPost() {
        adapterPost.updateList(listOfPost)
        currentList = listOfPost
    }

}