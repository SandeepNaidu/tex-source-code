package com.project.tex.main.ui.avid.avidCamera.ui

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.gms.common.util.CollectionUtils
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import com.hendraanggrian.appcompat.widget.Hashtag
import com.hendraanggrian.appcompat.widget.HashtagArrayAdapter
import com.project.tex.GlobalApplication
import com.project.tex.R
import com.project.tex.addmedia.FileUtils
import com.project.tex.base.activity.BaseActivity
import com.project.tex.databinding.ActivityAvidPreviewBinding
import com.project.tex.main.HomeActivity
import com.project.tex.main.model.Tag
import com.project.tex.main.ui.avid.AvidMode
import com.project.tex.main.ui.avid.avidCamera.utils.ExtractVideoInfoUtil
import com.project.tex.main.ui.avid.avidCamera.utils.UIUtils
import com.project.tex.main.ui.avid.avidCamera.view.NormalProgressDialog
import com.project.tex.main.ui.home.PostTypes
import com.project.tex.main.ui.search.AvidViewModel
import com.project.tex.utils.IntentUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*


class AvidPreviewActivity : BaseActivity<ActivityAvidPreviewBinding, AvidViewModel>(),
    View.OnClickListener {
    private lateinit var videoInfo: ExtractVideoInfoUtil
    private val TAG: String = AvidPreviewActivity::class.java.simpleName
    private var currentAvidFor: Int = AVID_AUDITION_TYPE

    override fun getViewBinding() = ActivityAvidPreviewBinding.inflate(layoutInflater)
    private var hashtagAdapter: ArrayAdapter<Hashtag>? = null

    override fun getViewModelInstance() = ViewModelProvider(this)[AvidViewModel::class.java]

    private lateinit var coverImg: Bitmap
    private lateinit var avidVideo: Uri

    private val listOfTags = mutableListOf<String>()
    private val url: Array<String> = arrayOf("", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            avidVideo = intent.getParcelableExtra("videoPath", Uri::class.java)!!
        } else {
            avidVideo = intent.getParcelableExtra("videoPath")!!
        }
        _binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.audition_type -> {
                    updateUi(AVID_AUDITION_TYPE)
                }
                R.id.public_type -> {
                    updateUi(AVID_PUBLIC_TYPE)
                }
                R.id.private_type -> {
                    updateUi(AVID_PRIVATE_TYPE)
                }
            }
        }

        videoInfo = ExtractVideoInfoUtil(Uri.parse(avidVideo.toString()))
        coverImg = videoInfo.extractFrame()
        Glide.with(this).load(coverImg).transform(CenterCrop(), RoundedCorners(UIUtils.dp2Px(4)))
            .into(_binding.avidPreviewImg)
        updateUi(currentAvidFor)
        _binding.backBtn.setOnClickListener(this)
        _binding.edtTag.isMentionEnabled = false
        _binding.edtTag.hashtagColor = ContextCompat.getColor(this, R.color.colorPrimary)
        _binding.edtTag.addTextChangedListener {}
        _binding.edtTag.setHashtagTextChangedListener { view, text ->
            viewModel.setTag(text.toString())
            if (text.isNotEmpty()) {
                viewModel.emit()
            }
        }

        viewModel.subscribeSubject()
        viewModel.tagByNameDataLD.observe(this, Observer {
            if (it.responseCode != 200) return@Observer
            if (it.body == null) return@Observer
            if (!CollectionUtils.isEmpty(it.body.tags)) {
                setHashTagSuggestionAdapter(it.body.tags)
            }
        })

        _binding.tvSaveDraft.setOnClickListener(this)
        _binding.tvEditCover.setOnClickListener(this)
        _binding.btnDone.setOnClickListener(this)
    }

    private fun updateUi(avidAuditionType: Int) {
        currentAvidFor = avidAuditionType
        when (avidAuditionType) {
            AVID_AUDITION_TYPE -> {
                _binding.titleTil.isInvisible = false
                _binding.captionTil.isInvisible = true
                _binding.edtTagTil.isInvisible = true
                _binding.tvSaveDraft.isVisible = true
                _binding.btnDone.isVisible = true
                _binding.btnDone.text = getString(R.string.create_link)
                _binding.btnDone.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.create_link, 0, 0, 0
                )
            }
            AVID_PUBLIC_TYPE -> {
                _binding.titleTil.isInvisible = false
                _binding.captionTil.isInvisible = false
                _binding.edtTagTil.isInvisible = false
                _binding.tvSaveDraft.isVisible = true
                _binding.btnDone.isVisible = true
                _binding.btnDone.text = getString(R.string.post)
                _binding.btnDone.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.post, 0)
            }
            AVID_PRIVATE_TYPE -> {
                _binding.titleTil.isInvisible = false
                _binding.captionTil.isInvisible = false
                _binding.edtTagTil.isInvisible = false
                _binding.tvSaveDraft.isVisible = false
                _binding.btnDone.isVisible = true
                _binding.btnDone.text = getString(R.string.save_video)
                _binding.btnDone.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.save_down, 0, 0, 0
                )
            }
        }
    }

    private fun setHashTagSuggestionAdapter(tags: List<Tag>?) {
        hashtagAdapter = HashtagArrayAdapter(this)
        tags?.forEach {
            hashtagAdapter?.add(it.label?.let { it1 -> Hashtag(it1) })
        }
        _binding.edtTag.hashtagAdapter = hashtagAdapter
//        hashtagAdapter.notifyDataSetChanged()
    }

    private val activityResultCoverImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result?.data?.let {
                val frameTime = it.getLongExtra("frameTime", 0L)
                if (frameTime != 0L) {
                    videoInfo.videoLength
                    coverImg = videoInfo.extractFrame(frameTime)
                    Glide.with(this).load(coverImg)
                        .transform(CenterCrop(), RoundedCorners(UIUtils.dp2Px(4)))
                        .into(_binding.avidPreviewImg)
                }
            }
        }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back_btn -> {
                finish()
            }
            R.id.tv_edit_cover -> {
                activityResultCoverImage.launch(
                    Intent(
                        this@AvidPreviewActivity,
                        EditCoverActivity::class.java
                    ).putExtra("videoPath", avidVideo)
                )
            }
            R.id.tv_save_draft -> {
//                viewModel.saveDraft(
//                    avidId = avidId ?: UUID.randomUUID().toString(),
//                    avidLocalUri = avidVideo.toString(),
//                    title = _binding.edtTitle.text.toString(),
//                    caption = _binding.edtCaption.text.toString(),
//                    tags = listOfTags.stream().reduce { t, u -> return@reduce "$t,$u" }.get(),
//                    avidMode = getAvidMode(),
//                    coverFrame = 0
//                ).subscribe({
                startActivity(Intent(this@AvidPreviewActivity, HomeActivity::class.java))
//                }, {
//                    Log.e(TAG, "onClick: ", it)
//                })?.let {
//                    viewModel.compositeDisposable.add(
//                        it
//                    )
//                }
            }
            R.id.btn_done -> {
                val mode = getAvidMode()
                callPostCreation(mode)
            }
        }
    }

    private fun getAvidMode(): String {
        return when (currentAvidFor) {
            AVID_AUDITION_TYPE -> {
                AvidMode.Audition
            }
            AVID_PUBLIC_TYPE -> {
                AvidMode.Public
            }
            AVID_PRIVATE_TYPE -> {
                AvidMode.Private
            }
            else -> {
                AvidMode.Audition
            }
        }.name
    }

    private fun callPostCreation(mode: String) {
        if (url.elementAt(0).isNotEmpty() && url.elementAt(1).isNotEmpty()) {
            if (mode == AvidMode.Audition.name) {
                NormalProgressDialog.showLoading(this, "Creating AVID Link...", false)
                generateAndShareUrl(url[0])
            } else if (mode == AvidMode.Public.name || mode == AvidMode.Private.name) {
                NormalProgressDialog.showLoading(this, "Uploading AVID...", false)
                viewModel.createPost(
                    url[1],
                    url[0],
                    mode,
                    _binding.edtTitle.text.toString(),
                    _binding.edtCaption.text.toString(),
                    if (CollectionUtils.isEmpty(listOfTags)) "" else if (listOfTags.size == 1) listOfTags[0] else listOfTags.stream()
                        .reduce { t, u -> return@reduce "$t,$u" }.get()
                ).observe(this@AvidPreviewActivity, Observer { r ->
                    NormalProgressDialog.stopLoading()
                    if (r.responseCode == 200) {
                        msg.showShortMsg("Avid Posted!")
                        startActivity(
                            Intent(
                                this@AvidPreviewActivity, HomeActivity::class.java
                            )
                        )
                    } else {
                        Log.e(TAG, "callPostCreation: api fail ${r}")
                    }
                })
            }
        } else {
            var i = 0
            NormalProgressDialog.showLoading(this, "Uploading AVID...", false)
            viewModel.startUploadAvidData(avidVideo, coverImg)
                ?.observeOn(AndroidSchedulers.mainThread())?.subscribe({
                    url[i++] = it
                }, {
                    NormalProgressDialog.stopLoading()
                    Log.e(TAG, "callPostCreation: ", it)
                }, {
                    if (mode == AvidMode.Audition.name) {
                        NormalProgressDialog.stopLoading()
                        generateAndShareUrl(url[0])
                        return@subscribe
                    }

                    viewModel.createPost(
                        url[1],
                        url[0],
                        mode,
                        _binding.edtTitle.text.toString(),
                        _binding.edtCaption.text.toString(),
                        if (CollectionUtils.isEmpty(listOfTags)) "" else if (listOfTags.size == 1) listOfTags[0] else listOfTags.stream()
                            .reduce { t, u -> return@reduce "$t,$u" }.get()
                    ).observe(this@AvidPreviewActivity, Observer { r ->
                        NormalProgressDialog.stopLoading()
                        if (r.responseCode == 200) {
                            msg.showShortMsg("Avid Posted!")
                            startActivity(
                                Intent(
                                    this@AvidPreviewActivity, HomeActivity::class.java
                                )
                            )
                        } else {
                            Log.e(TAG, "callPostCreation: api fail ${r}")
                        }
                    })
                })?.let {
                    viewModel.compositeDisposable.add(it)
                }
        }
    }

    private fun generateAndShareUrl(s: String) {
        val longUri = Uri.parse(s)
        val shortLinkTask = Firebase.dynamicLinks.shortLinkAsync {
            longLink = longUri
        }.addOnSuccessListener {
            it.shortLink?.let {
                startActivity(
                    Intent(
                        this@AvidPreviewActivity, HomeActivity::class.java
                    )
                )
                IntentUtils.shareTextUrl(
                    this,
                    "Hey there, here is the shared AVID Audition link : $it"
                )
            }
        }.addOnFailureListener {
            // Error
            // ...
            startActivity(
                Intent(
                    this@AvidPreviewActivity, HomeActivity::class.java
                )
            )
            IntentUtils.shareTextUrl(
                this,
                "Hey there, here is the shared AVID Audition link : $longUri"
            )
        }
    }

    companion object {
        const val AVID_AUDITION_TYPE = 12
        const val AVID_PUBLIC_TYPE = 13
        const val AVID_PRIVATE_TYPE = 14
    }

}