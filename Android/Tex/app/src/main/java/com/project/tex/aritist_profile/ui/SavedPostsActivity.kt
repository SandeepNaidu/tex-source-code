package com.project.tex.aritist_profile.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import com.project.tex.R
import com.project.tex.aritist_profile.model.UserProfileData
import com.project.tex.base.activity.BaseActivity
import com.project.tex.databinding.ActivitySavedPostBinding
import com.project.tex.main.ui.adapter.PostAdapter
import com.project.tex.main.ui.dialog.PostActionDialog
import com.project.tex.main.ui.home.HomePostViewModel
import com.project.tex.post.model.AllPostData
import com.project.tex.settings.adapter.SettingAdapter
import com.project.tex.utils.IntentUtils
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

class SavedPostsActivity : BaseActivity<ActivitySavedPostBinding, HomePostViewModel>(),
    SettingAdapter.OnClickListener, PostAdapter.PostActions {

    override fun getViewBinding() = ActivitySavedPostBinding.inflate(LayoutInflater.from(this))

    override fun getViewModelInstance() = ViewModelProvider(this).get(HomePostViewModel::class.java)
    private var adapter: PostAdapter? = null
    private var postOptionDialog: PostActionDialog? = null

    private var userData: UserProfileData.Body.Users.Body? = null
    private val activityResultUpdate =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (it.resultCode == RESULT_OK) {
                userData = it.data?.getSerializableExtra("data") as UserProfileData.Body.Users.Body?
                setResult(RESULT_OK, Intent().putExtra("data", userData))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(_binding.mainToolbar) //Set toolbar

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            userData =
                intent?.getSerializableExtra("data", UserProfileData.Body.Users.Body::class.java)
        } else {
            userData = intent?.getSerializableExtra("data") as? UserProfileData.Body.Users.Body?
        }

        _binding.mainToolbar.setNavigationOnClickListener {
            finish()
        }

        callPostListApi()
    }

    private fun callPostListApi() {
        viewModel.add(viewModel.getAllSavedPostListing().subscribe({
            if (it.isNotEmpty()) {
                adapter = PostAdapter(this, this, it, viewModel.isScreenOffObserver)
                _binding.postRv.adapter = adapter
            } else {
                msg.showShortMsg("Failed to get the posts at the moment.")
            }
        }, {
//            _binding.swiperefresh.isRefreshing = false
            msg.showShortMsg(getString(R.string.something_went_wrong))
        }))
    }

    override fun itemClicked(pos: Int, id: Int) {
        when (id) {
            HELP -> {
                activityResultUpdate.launch(
                    Intent(this, StatusSettingsActivity::class.java)
                        .putExtra("data", userData)
                )
            }
            ABOUT -> {
                activityResultUpdate.launch(
                    Intent(this, ContactDetailsctivity::class.java)
                        .putExtra("data", userData)
                )
            }
        }
    }

    companion object {
        const val HELP: Int = 0
        const val ABOUT: Int = 1
    }

    override fun onProfileIconClicked(postOwnerId: Any) {

    }

    override fun onPause() {
        super.onPause()
        viewModel.setIsScreenOff(true)
    }

    override fun onOptionMenuClick(pos: Int, postInfo: Any?) {
        if (postInfo != null) {
            postOptionDialog = PostActionDialog()
            postOptionDialog?.showDialog(supportFragmentManager, postInfo, pos)
        }
    }

    override fun onSaveClick(pos: Int, postInfo: Any) {
        viewModel.savePost(postInfo).subscribe(object : SingleObserver<Any> {
            override fun onSubscribe(d: Disposable) {
                viewModel.compositeDisposable.add(d)
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
        viewModel.sharePost(postInfo).subscribe(object : SingleObserver<Any> {
            override fun onSubscribe(d: Disposable) {
                viewModel.compositeDisposable.add(d)
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
                        Uri.parse("https://texapp.page.link/?link=https://www.texapp.com/post/$postId/&apn=${packageName}")
                    val shortLinkTask = Firebase.dynamicLinks.shortLinkAsync {
                        longLink = longUri
                    }.addOnSuccessListener {
                        it.shortLink?.let {
                            IntentUtils.shareTextUrl(
                                this@SavedPostsActivity,
                                "Hey there, here is the shared Post link : $it"
                            )
                        }
                    }.addOnFailureListener {
                        IntentUtils.shareTextUrl(
                            this@SavedPostsActivity,
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
        viewModel.reportPost(postId).subscribe(object : SingleObserver<Any> {
            override fun onSubscribe(d: Disposable) {
                viewModel.compositeDisposable.add(d)
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
        viewModel.likePost(postInfo).subscribe(object : SingleObserver<Any> {
            override fun onSubscribe(d: Disposable) {
                viewModel.compositeDisposable.add(d)
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
        viewModel.votePoll(postInfo, clickedOptionId).subscribe(object : SingleObserver<Any> {
            override fun onSubscribe(d: Disposable) {
                viewModel.compositeDisposable.add(d)
            }

            override fun onSuccess(t: Any) {
                adapter?.notifyItemChanged(pos, t)
            }

            override fun onError(e: Throwable) {
                msg.showLongMsg(getString(R.string.something_wrong_try_again))
            }
        })
    }
}