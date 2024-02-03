package com.project.tex.recruiter.ui.adapter

import android.graphics.Color
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.CheckedTextView
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.project.tex.R
import com.project.tex.databinding.*
import com.project.tex.main.ui.player.MediaObserver
import com.project.tex.recruiter.ui.home.RecHomeFragment
import com.project.tex.utils.ViewUtils.dpToPx
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.random.Random

class PostAdapter(val fragment: RecHomeFragment, val list: List<Int>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_VIDEO -> {
                VideoHolder(
                    VideoPostItemLayoutBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ), fragment as PostActions
                )
            }
            TYPE_TRENDS -> {
                TrendsHolder(
                    TrendingAvidLayoutBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            TYPE_IMAGE -> {
                ImageHolder(
                    VideoPostItemLayoutBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ), fragment as PostActions
                )
            }
            TYPE_SURVEY -> {
                SurveyHolder(
                    VideoPostItemLayoutBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ), fragment as PostActions
                )
            }
            TYPE_EVENT -> {
                EventHolder(
                    VideoPostItemLayoutBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ), fragment as PostActions
                )
            }
            TYPE_DOCUMENT -> {
                DocumentHolder(
                    VideoPostItemLayoutBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ), fragment as PostActions
                )
            }
            TYPE_AUDIO -> {
                AudioHolder(
                    VideoPostItemLayoutBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ), fragment as PostActions
                )
            }
            else -> {
                throw java.lang.RuntimeException("No such view type!")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DocumentHolder) {
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position]
    }

    override fun getItemCount(): Int = list.size
    public var positionToPlayVideo = 0

    fun updateVisibleItem(visibleItemPosition: Int) {
        if (positionToPlayVideo == visibleItemPosition) return
        notifyItemChanged(positionToPlayVideo)
        positionToPlayVideo = visibleItemPosition
        notifyDataSetChanged()
    }

    private inner class VideoHolder(view: ViewBinding, action: PostActions) :
        BasePostHolder(view.root, action) {
        init {
            val prm =
                (view as VideoPostItemLayoutBinding).postFrame.layoutParams as ViewGroup.LayoutParams
            prm.height = dpToPx(view.root.context, 192)
            view.postFrame.layoutParams = prm
        }
    }

    private inner class DocumentHolder(val view: ViewBinding, val action: PostActions) :
        BasePostHolder(view.root, action) {

        init {
        }

    }

    private inner class AudioHolder(view: ViewBinding, action: PostActions) :
        BasePostHolder(view.root, action) {
        init {
        }
    }

    private inner class ImageHolder(view: ViewBinding, action: PostActions) :
        BasePostHolder(view.root, action) {
        init {
            val prm =
                (view as VideoPostItemLayoutBinding).postFrame.layoutParams as ViewGroup.LayoutParams
            prm.height = dpToPx(view.root.context, 192)
            view.postFrame.layoutParams = prm
        }
    }

    private inner class SurveyHolder(view: ViewBinding, action: PostActions) :
        BasePostHolder(view.root, action) {
        init {
            val surveyContent =
                SurveyContentLayoutBinding.inflate(LayoutInflater.from(view.root.context))
            val s = Random.nextInt(6)
            for (i in 0..s) {
                val questionItem =
                    QuestOptionsItemLayoutBinding.inflate(LayoutInflater.from(view.root.context))

                surveyContent.optionsLl.addView(questionItem.root)
                val params = questionItem.root.layoutParams as MarginLayoutParams
                params.height = dpToPx(view.root.context, 32)
                params.setMargins(0, dpToPx(view.root.context, 16), 0, 0)
                questionItem.root.layoutParams = params
            }

            (view as VideoPostItemLayoutBinding).postFrame.addView(surveyContent.root)
            val prm = view.postFrame.layoutParams as ViewGroup.LayoutParams
            prm.height = ViewGroup.LayoutParams.WRAP_CONTENT
            view.postFrame.layoutParams = prm
            view.postFrame.setBackgroundColor(Color.WHITE)
        }
    }

    private inner class EventHolder(view: ViewBinding, action: PostActions) :
        BasePostHolder(view.root, action) {
        init {
            val eventUi =
                EventContentItemLayoutBinding.inflate(LayoutInflater.from(view.root.context))
            (view as VideoPostItemLayoutBinding).postFrame.addView(eventUi.root)
        }
    }

    private inner class TrendsHolder(view: ViewBinding) : RecyclerView.ViewHolder(view.root) {
        init {
        }
    }

    private open inner class BasePostHolder(view: View, action: PostActions) :
        RecyclerView.ViewHolder(view) {
        val userPicImage = view.findViewById<CircleImageView>(R.id.profile_img)
        val usernameText = view.findViewById<AppCompatTextView>(R.id.username_tv)
        val postLocationText = view.findViewById<AppCompatTextView>(R.id.post_location_tv)
        val postTimeText = view.findViewById<AppCompatTextView>(R.id.post_time_tv)
        val likes = view.findViewById<CheckedTextView>(R.id.like_ctv)
        val shares = view.findViewById<CheckedTextView>(R.id.share_ctv)
        val saves = view.findViewById<CheckedTextView>(R.id.save_ctv)
        val postFrame = view.findViewById<FrameLayout>(R.id.post_frame)
        val optionsMenu = view.findViewById<ImageView>(R.id.options_iv)
        val description = view.findViewById<AppCompatTextView>(R.id.description_tv)

        init {
            optionsMenu.setOnClickListener {
                action.onOptionMenuClick(bindingAdapterPosition)
            }
        }
    }

    interface PostActions {
        fun onOptionMenuClick(pos: Int, postInfo: Any? = null)
        fun onSaveClick(pos: Int, postInfo: Any? = null)
        fun onShareClick(pos: Int, postInfo: Any? = null)
        fun onLikeClick(pos: Int, postInfo: Any? = null)
    }

    companion object {
        const val TYPE_VIDEO = 1
        const val TYPE_TRENDS = 2
        const val TYPE_IMAGE = 3
        const val TYPE_EVENT = 4
        const val TYPE_SURVEY = 5
        const val TYPE_DOCUMENT = 6
        const val TYPE_AUDIO = 7
    }
}