package com.project.tex.main.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import com.project.tex.R
import com.project.tex.aritist_profile.ui.ProfileActivity
import com.project.tex.base.fragment.BaseFragment
import com.project.tex.databinding.FragmentHomeBinding
import com.project.tex.main.model.AvidListResponse
import com.project.tex.main.ui.adapter.CategoryAdapter
import com.project.tex.main.ui.adapter.PostAdapter
import com.project.tex.main.ui.dialog.CreatePostDialog
import com.project.tex.main.ui.dialog.PostActionDialog
import com.project.tex.post.model.AllPostData
import com.project.tex.post.ui.CreatePostActivity
import com.project.tex.recruiter.ui.home.CenterLayoutManager
import com.project.tex.utils.IntentUtils
import com.project.tex.utils.enforceSingleScrollDirection
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable


class HomeFragment : BaseFragment<FragmentHomeBinding>(), PostAdapter.PostActions,
    CreatePostDialog.CreatePostClicks {

    private val TAG: String = "HomeFragment"
    private var trendPosition: Int = -1
    var mBundleRecyclerViewState = Bundle()

    override fun getViewBinding() = FragmentHomeBinding.inflate(layoutInflater)
    private var postOptionDialog: PostActionDialog? = null

    private val createPostDialog by lazy {
        CreatePostDialog()
    }

    private val mainViewModel: HomePostViewModel by viewModels({ requireActivity() })

    var adapter: PostAdapter? = null
    var poss = -1

    @SuppressLint("UseRequireInsteadOfGet", "CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding.categoryRv.layoutManager =
            CenterLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        _binding.categoryRv.adapter =
            CategoryAdapter(mutableListOf(
                "All",
                "Singing",
                "Acting",
                "Music",
                "Dancing",
                "Singing",
                "Mimicry"
            ),
                object : CategoryAdapter.CategoryListener {
                    override fun categorySelected(pos: Int) {
                        _binding.categoryRv.smoothScrollToPosition(pos)
                        _binding.categoryRv.adapter?.notifyDataSetChanged()
                    }
                })
        _binding.postRv.enforceSingleScrollDirection()
        _binding.postRv.recycledViewPool.setMaxRecycledViews(PostAdapter.TYPE_TRENDS, 0)
        _binding.postRv.recycledViewPool.setMaxRecycledViews(PostAdapter.TYPE_DOCUMENT, 0)
        _binding.postRv.recycledViewPool.setMaxRecycledViews(PostAdapter.TYPE_VIDEO, 0)
        _binding.postRv.recycledViewPool.setMaxRecycledViews(PostAdapter.TYPE_AUDIO, 0)
        _binding.postRv.adapter?.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        _binding.categoryRv.adapter?.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        _binding.fabCreate.setOnClickListener {
            createPostDialog.showDialog(childFragmentManager)
        }

        _binding.postRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val pos = (recyclerView.layoutManager as LinearLayoutManager)
                    .findFirstCompletelyVisibleItemPosition()
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    adapter?.playTrendingAvid(pos == trendPosition)
                }
            }
        })
        _binding.swiperefresh.setOnRefreshListener {
            callPostListApi()
        }

        callPostListApi()
    }

    private fun callPostListApi() {
        mainViewModel.add(mainViewModel.getAllPostListing().subscribe({
            if (!isAdded) return@subscribe
            _binding.swiperefresh.isRefreshing = false
            if (it.isNotEmpty()) {
                adapter =
                    PostAdapter(this, viewLifecycleOwner, it, mainViewModel.isScreenOffObserver)
                it.mapIndexed { index, any ->
                    if (any is List<*>) {
                        trendPosition = index
                    }
                }
                _binding.postRv.adapter = adapter
                mainViewModel.getAvids().subscribe(object : SingleObserver<AvidListResponse> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(t: AvidListResponse) {
                        t.body?.avids?.let { it1 ->
//                            it.set(trendPosition, it1)
                            adapter?.notifyItemChanged(trendPosition, t.body.avids)
                        }
                    }

                    override fun onError(e: Throwable) {
                        msg.showShortMsg("Failed to get the AVIDs at the moment.")
                    }
                })
            } else {
                msg.showShortMsg("Failed to get the posts at the moment.")
            }
        }, {
            if (!isAdded) return@subscribe
            _binding.swiperefresh.isRefreshing = false
            msg.showShortMsg(getString(R.string.something_went_wrong))
            Log.e(TAG, "callPostListApi: ", it)
        }))
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.setIsScreenOff(false)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        mainViewModel.setIsScreenOff(hidden)
    }

    override fun onPause() {
        super.onPause()
        mainViewModel.setIsScreenOff(true)
    }

    override fun onProfileIconClicked(postInfo: Any) {
        val postId = when (postInfo) {
            is AllPostData.Body.Posts.Video -> {
                postInfo.artistId
            }
            is AllPostData.Body.Posts.Music -> {
                postInfo.artistId
            }
            is AllPostData.Body.Posts.Image -> {
                postInfo.artistId
            }
            is AllPostData.Body.Posts.Document -> {
                postInfo.artistId
            }
            is AllPostData.Body.Posts.Event -> {
                postInfo.artistId
            }
            is AllPostData.Body.Posts.Poll -> {
                postInfo.artistId
            }
            else -> {
                -1
            }
        }
        startActivity(
            Intent(requireContext(), ProfileActivity::class.java)
                .putExtra("id", postId)
        )
    }

    override fun onOptionMenuClick(pos: Int, postInfo: Any?) {
        if (postInfo != null) {
            postOptionDialog = PostActionDialog()
            postOptionDialog?.showDialog(childFragmentManager, postInfo, pos)
        }
    }

    override fun onSaveClick(pos: Int, postInfo: Any) {
        mainViewModel.savePost(postInfo).subscribe(object : SingleObserver<Any> {
            override fun onSubscribe(d: Disposable) {
                mainViewModel.compositeDisposable.add(d)
            }

            override fun onSuccess(t: Any) {
                adapter?.notifyItemChanged(pos, t)
            }

            override fun onError(e: Throwable) {
                msg.showLongMsg(getString(R.string.something_wrong_try_again))
            }
        })
    }

    override fun onShareClick(pos: Int, postInfo: Any) {
        mainViewModel.sharePost(postInfo).subscribe(object : SingleObserver<Any> {
            override fun onSubscribe(d: Disposable) {
                mainViewModel.compositeDisposable.add(d)
            }

            override fun onSuccess(t: Any) {
                val postId = when (postInfo) {
                    is AllPostData.Body.Posts.Video -> {
                        postInfo.id
                    }
                    is AllPostData.Body.Posts.Music -> {
                        postInfo.id
                    }
                    is AllPostData.Body.Posts.Image -> {
                        postInfo.id
                    }
                    is AllPostData.Body.Posts.Document -> {
                        postInfo.id
                    }
                    is AllPostData.Body.Posts.Event -> {
                        postInfo.id
                    }
                    is AllPostData.Body.Posts.Poll -> {
                        postInfo.id
                    }
                    else -> {
                        -1
                    }
                }
                if (postId != null && postId != -1) {
                    val longUri =
                        Uri.parse("https://texapp.page.link/?link=https://www.texapp.com/post/$postId/&apn=${requireContext().packageName}")
                    val shortLinkTask = Firebase.dynamicLinks.shortLinkAsync {
                        longLink = longUri
                    }.addOnSuccessListener {
                        it.shortLink?.let {
                            IntentUtils.shareTextUrl(
                                requireContext(),
                                "Hey there, here is the shared Post link : $it"
                            )
                        }
                    }.addOnFailureListener {
                        IntentUtils.shareTextUrl(
                            requireContext(),
                            "Hey there, here is the shared Post link : $longUri"
                        )
                    }
                }
                adapter?.notifyItemChanged(pos, t)
            }

            override fun onError(e: Throwable) {
                msg.showLongMsg(getString(R.string.something_wrong_try_again))
            }
        })
    }

    override fun onReportClick(pos: Int, postInfo: Any) {
        val postId = when (postInfo) {
            is AllPostData.Body.Posts.Video -> {
                postInfo.id
            }
            is AllPostData.Body.Posts.Music -> {
                postInfo.id
            }
            is AllPostData.Body.Posts.Image -> {
                postInfo.id
            }
            is AllPostData.Body.Posts.Document -> {
                postInfo.id
            }
            is AllPostData.Body.Posts.Event -> {
                postInfo.id
            }
            is AllPostData.Body.Posts.Poll -> {
                postInfo.id
            }
            else -> {
                -1
            }
        } ?: return
        mainViewModel.reportPost(postId).subscribe(object : SingleObserver<Any> {
            override fun onSubscribe(d: Disposable) {
                mainViewModel.compositeDisposable.add(d)
            }

            override fun onSuccess(t: Any) {
                msg.showLongMsg(getString(R.string.post_reported_msg))
            }

            override fun onError(e: Throwable) {
                msg.showLongMsg(getString(R.string.something_wrong_try_again))
            }
        })
    }

    override fun onLikeClick(pos: Int, postInfo: Any) {
        mainViewModel.likePost(postInfo).subscribe(object : SingleObserver<Any> {
            override fun onSubscribe(d: Disposable) {
                mainViewModel.compositeDisposable.add(d)
            }

            override fun onSuccess(t: Any) {
                adapter?.notifyItemChanged(pos, t)
            }

            override fun onError(e: Throwable) {
                msg.showLongMsg(getString(R.string.something_wrong_try_again))
            }
        })
    }

    override fun onVoted(pos: Int, postInfo: AllPostData.Body.Posts.Poll, clickedOptionId: Int) {
        mainViewModel.votePoll(postInfo, clickedOptionId).subscribe(object : SingleObserver<Any> {
            override fun onSubscribe(d: Disposable) {
                mainViewModel.compositeDisposable.add(d)
            }

            override fun onSuccess(t: Any) {
                adapter?.notifyItemChanged(pos, t)
            }

            override fun onError(e: Throwable) {
                msg.showLongMsg(getString(R.string.something_wrong_try_again))
            }
        })
    }

    override fun createVideoClick() {
        startActivity(
            Intent(requireContext(), CreatePostActivity::class.java).putExtra(
                "type",
                PostTypes.TYPE_VIDEO
            )
        )
    }

    override fun createImageClick() {
        startActivity(
            Intent(requireContext(), CreatePostActivity::class.java).putExtra(
                "type",
                PostTypes.TYPE_PHOTO
            )
        )
    }

    override fun createAudioClick() {
        startActivity(
            Intent(requireContext(), CreatePostActivity::class.java).putExtra(
                "type",
                PostTypes.TYPE_MUSIC
            )
        )
    }

    override fun createDocumentClick() {
        startActivity(
            Intent(requireContext(), CreatePostActivity::class.java).putExtra(
                "type",
                PostTypes.TYPE_DOCUMENT
            )
        )
    }

    override fun createEventClick() {
        startActivity(
            Intent(requireContext(), CreatePostActivity::class.java).putExtra(
                "type",
                PostTypes.TYPE_EVENT
            )
        )
    }

    override fun createPollClick() {
        startActivity(
            Intent(requireContext(), CreatePostActivity::class.java).putExtra(
                "type",
                PostTypes.TYPE_POLL
            )
        )
    }

}

public interface PostTypes {
    companion object {
        const val TYPE_POLL = "POLL"
        const val TYPE_EVENT = "EVENT"
        const val TYPE_DOCUMENT = "DOCUMENT"
        const val TYPE_MUSIC = "MUSIC"
        const val TYPE_PHOTO = "PHOTO"
        const val TYPE_VIDEO = "VIDEO"
    }
}