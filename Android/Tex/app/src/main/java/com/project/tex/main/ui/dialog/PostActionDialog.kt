package com.project.tex.main.ui.dialog

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.project.tex.R
import com.project.tex.base.fragment.BaseDialog
import com.project.tex.databinding.DialogPostActionBinding
import com.project.tex.main.ui.adapter.PostAdapter
import com.project.tex.main.ui.home.HomeFragment
import com.project.tex.post.model.AllPostData
import org.apache.commons.lang3.StringUtils

@Suppress("DEPRECATION", "RedundantOverride")
class PostActionDialog : BaseDialog<DialogPostActionBinding>(), View.OnClickListener {
    override fun getViewBinding() = DialogPostActionBinding.inflate(layoutInflater)

    var mListener: PostAdapter.PostActions? = null
    fun showDialog(manager: FragmentManager, postData: Any, pos: Int) {
        arguments = Bundle().apply {
            when (postData) {
                is AllPostData.Body.Posts.Video -> {
                    putSerializable("data", postData)
                    putString("dataType", "Video")
                }
                is AllPostData.Body.Posts.Music -> {
                    putSerializable("data", postData)
                    putString("dataType", "Music")
                }
                is AllPostData.Body.Posts.Image -> {
                    putSerializable("data", postData)
                    putString("dataType", "Image")
                }
                is AllPostData.Body.Posts.Document -> {
                    putSerializable("data", postData)
                    putString("dataType", "Document")
                }
                is AllPostData.Body.Posts.Event -> {
                    putSerializable("data", postData)
                    putString("dataType", "Event")
                }
                is AllPostData.Body.Posts.Poll -> {
                    putSerializable("data", postData)
                    putString("dataType", "Poll")
                }
            }
            putInt("position", pos)
        }
        showNow(manager, "post action")
    }

    private var mPostData: Any? = null
    private var mPosition: Int = -1

    override fun onStart() {
        super.onStart()
        dialog?.window?.setWindowAnimations(R.style.DialogAnimation)
        dialog?.setCancelable(true)
        dialog?.setCanceledOnTouchOutside(true)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mListener = (parentFragment as? HomeFragment)

        arguments?.let { args ->
            val type = args.getString("dataType")
            mPosition = args.getInt("position")
            if (StringUtils.isNotEmpty(type)) {
                when (type) {
                    "Video" -> {
                        mPostData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            args.getSerializable(
                                "data",
                                AllPostData.Body.Posts.Video::class.java
                            )
                        } else {
                            args.getSerializable("data")
                        }
                    }
                    "Music" -> {
                        mPostData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            args.getSerializable(
                                "data",
                                AllPostData.Body.Posts.Music::class.java
                            )
                        } else {
                            args.getSerializable("data")
                        }
                    }
                    "Image" -> {
                        mPostData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            args.getSerializable(
                                "data",
                                AllPostData.Body.Posts.Image::class.java
                            )
                        } else {
                            args.getSerializable("data")
                        }
                    }
                    "Document" -> {
                        mPostData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            args.getSerializable(
                                "data",
                                AllPostData.Body.Posts.Document::class.java
                            )
                        } else {
                            args.getSerializable("data")
                        }
                    }
                    "Event" -> {
                        mPostData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            args.getSerializable(
                                "data",
                                AllPostData.Body.Posts.Event::class.java
                            )
                        } else {
                            args.getSerializable("data")
                        }
                    }
                    "Poll" -> {
                        mPostData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            args.getSerializable(
                                "data",
                                AllPostData.Body.Posts.Poll::class.java
                            )
                        } else {
                            args.getSerializable("data")
                        }
                    }
                }
            }
        }
        _binding.shareTv.setOnClickListener(this)
        _binding.linkTv.setOnClickListener(this)
        _binding.saveTv.setOnClickListener(this)
        _binding.infoTv.setOnClickListener(this)
        _binding.reportTv.setOnClickListener(this)
    }

    override fun getTheme(): Int {
        return R.style.DialogAnimation
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.share_tv -> {
                mPostData?.let { mListener?.onShareClick(mPosition, it) }
                dismissAllowingStateLoss()
            }
            R.id.save_tv -> {
                mPostData?.let { mListener?.onSaveClick(mPosition, it) }
                dismissAllowingStateLoss()
            }
            R.id.link_tv -> {
                mPostData?.let { mListener?.onShareClick(mPosition, it) }
                dismissAllowingStateLoss()
            }
            R.id.info_tv -> {
                dismissAllowingStateLoss()
            }
            R.id.report_tv -> {
                mPostData?.let { mListener?.onReportClick(mPosition, postInfo = it) }
                dismissAllowingStateLoss()
            }
        }
    }
}