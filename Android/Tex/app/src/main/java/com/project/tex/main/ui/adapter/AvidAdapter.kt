package com.project.tex.main.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.gms.common.util.CollectionUtils
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.project.tex.GlobalApplication
import com.project.tex.databinding.AvidListBinding
import com.project.tex.main.model.AvidData
import com.project.tex.main.model.AvidDetailForArtist
import com.project.tex.main.ui.adapter.AvidAdapter.AdapterAvidHolder
import com.project.tex.main.ui.search.AvidViewModel
import com.tylersuehr.socialtextview.SocialTextView
import de.hdodenhof.circleimageview.CircleImageView
import org.apache.commons.lang3.StringUtils
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

class AvidAdapter(
    private val modelAvids: List<AvidData>,
    val mainViewModel: AvidViewModel,
    val viewLifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<AdapterAvidHolder>() {
    private var callback: AvidCallbacks? = null

    private val avidArtistMap = ConcurrentHashMap<Int, AvidDetailForArtist>()
    private val avidPositionMap = HashMap<Int, Int>()
    private val avidsNotify = HashMap<Int, Boolean>()
    private val avidsUrlCache = HashMap<Int, String>()

    private var isMute = false
    private var currentPosition = 0
    val storageRef = FirebaseStorage.getInstance()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterAvidHolder {
        return AdapterAvidHolder(
            AvidListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: AdapterAvidHolder, position: Int) {
        if (!avidsNotify.containsKey(position))
            avidsNotify.put(position, true)
        modelAvids[position].avidId?.let { avidPositionMap.put(it, position) }
        holder.setVideoData(modelAvids[position])
        currentPosition = position
    }

    fun setCallBack(callbacks: AvidCallbacks) {
        this.callback = callbacks
    }

    fun setMute(mute: Boolean) {
        this.isMute = mute
        notifyDataSetChanged()
    }

    fun setSave(avidId: Int) {
        avidPositionMap.get(avidId)?.let {
            avidPositionMap.get(avidId)?.let {
                avidsNotify.put(it, true)
                notifyItemChanged(it)
            }
        }
    }

    fun setLike(avidId: Int) {
        avidPositionMap.get(avidId)?.let {
            avidPositionMap.get(avidId)?.let {
                avidsNotify.put(it, true)
                notifyItemChanged(it)
            }
        }
    }

    fun setShare(avidId: Int) {
        avidPositionMap.get(avidId)?.let {
            avidPositionMap.get(avidId)?.let {
                avidsNotify.put(it, true)
                notifyItemChanged(it)
            }
        }
    }

    override fun getItemCount(): Int {
        return modelAvids.size
    }

    inner class AdapterAvidHolder(binding: AvidListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var simplePlayer: ExoPlayer? = null
        private var cacheDataSourceFactory: CacheDataSource.Factory? = null
        val imageThumb: ImageView = binding.thumbnail
//        val videoView: VideoView = binding.videoView
        val like: CheckedTextView = binding.likeImg
        val share: CheckedTextView = binding.shareImg
        val save: CheckedTextView = binding.saveImg
        val avatar: CircleImageView = binding.userImg
        val name: TextView = binding.usernameTv
        val description: SocialTextView = binding.descriptionTv
        fun setVideoData(modelAvid: AvidData) {
            //TextView
            if (name.text.isEmpty()) {
                var artistName = ""
                if (StringUtils.isNotEmpty(modelAvid.artistFirstName))
                    artistName = modelAvid.artistFirstName!!.trim()
                if (StringUtils.isNotEmpty(modelAvid.artistLastName))
                    artistName = "$artistName ${modelAvid.artistLastName}".trim()
//                if (StringUtils.isEmpty(artistName.replace(" ","")))
//                    artistName = list.random()
                name.text = artistName
            }
            if (description.text.isEmpty())
                description.setLinkText(modelAvid.caption)
            if (modelAvid.avidId != null && avidsNotify[avidPositionMap.get(modelAvid.avidId)!!] == true) {
                mainViewModel.getAvidDetail(modelAvid.avidId).observe(viewLifecycleOwner, Observer {
                    avidsNotify[avidPositionMap.get(modelAvid.avidId)!!] = false
                    if (it == null) return@Observer
                    if (it.responseCode == 400) return@Observer
                    it.body?.avid?.let { listData ->
                        if (!CollectionUtils.isEmpty(listData)) {
                            avidArtistMap.put(modelAvid.avidId, listData[0])
                            updateLikeSaveShareUi(listData[0])
                        } else {
                            avidArtistMap.put(modelAvid.avidId, AvidDetailForArtist())
                        }
                    }
                    setListeners(like, share, save, modelAvid)
                })
            } else if (modelAvid.avidId != null && avidArtistMap.containsKey(modelAvid.avidId)) {
                updateLikeSaveShareUi(avidArtistMap[modelAvid.avidId])
                setListeners(like, share, save, modelAvid)
            }

            Glide.with(itemView.context).load(modelAvid.coverContent).into(imageThumb)

//            avatar.setOnClickListener { v: View? -> }
//            name.setOnClickListener { v: View? -> }
            //Video
            if (StringUtils.isEmpty(avidsUrlCache.get(bindingAdapterPosition))) {
                // Create a reference to a file from a Google Cloud Storage URI
                val gsReference: StorageReference? = modelAvid.content?.let {
                    if (WEB_URL.matcher(it).matches()) {
                        storageRef.getReferenceFromUrl(it)
                    } else {
//                        MsgUtils(
//                            itemView.context,
//                            msgType = MSG_TYPE.TOAST
//                        ).showLongMsg("Invalid Content!")
                        null
                    }
                }

                if (modelAvid.content?.startsWith("http") == true) {
                    val proxyUrl = GlobalApplication.instance.getProxy(itemView.context)
                        ?.getProxyUrl(modelAvid.content)
                    if (proxyUrl != null) {
//                        videoView.setVideoPath(proxyUrl)
                        avidsUrlCache[bindingAdapterPosition] = proxyUrl
                    }
                } else if (modelAvid.content?.startsWith("gs") == true) {
                    gsReference?.downloadUrl?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            val proxyUrl = GlobalApplication.instance.getProxy(itemView.context)
                                ?.getProxyUrl(it.result.toString())
                            if (proxyUrl != null) {
//                                videoView.setVideoPath(proxyUrl)
                                avidsUrlCache[bindingAdapterPosition] = proxyUrl
                            }
                        }
                    }
                }

            } else {
            }
        }

        private fun updateLikeSaveShareUi(avidDetailForArtist: AvidDetailForArtist?) {
            avidDetailForArtist?.likeCount?.let { it1 -> like.text = "$it1 likes" }
            avidDetailForArtist?.shareCount?.let { it1 -> share.text = "$it1 Shares" }
            avidDetailForArtist?.saveCount?.let { it1 -> save.text = "$it1 saves" }
            like.isChecked = avidDetailForArtist?.isLiked == 1
            save.isChecked = avidDetailForArtist?.isSaved == 1
            share.isChecked = avidDetailForArtist?.isShared == 1
        }
    }

    private fun setListeners(
        like: CheckedTextView,
        share: CheckedTextView,
        save: CheckedTextView,
        modelAvid: AvidData
    ) {
        like.setOnClickListener { v: View? ->
            val isLiked = avidArtistMap.get(modelAvid.avidId)?.isLiked
            callback?.likeAvid(modelAvid, if (isLiked == 1) 0 else 1)
        }
        share.setOnClickListener { v: View? ->
            val isShared = avidArtistMap.get(modelAvid.avidId)?.isShared
            callback?.shareAvid(modelAvid, if (isShared == 1) 0 else 1)
        }
        save.setOnClickListener { v: View? ->
            val isSaved = avidArtistMap.get(modelAvid.avidId)?.isSaved
            callback?.saveAvid(modelAvid, if (isSaved == 1) 0 else 1)
        }
    }

    public interface AvidCallbacks {
        fun shareAvid(modelAvid: AvidData, toShare: Int)
        fun saveAvid(modelAvid: AvidData, toSave: Int)
        fun likeAvid(modelAvid: AvidData, toLike: Int)
    }

    companion object {
        private const val PROTOCOL = "(?i:http|https|rtsp|ftp|gs)://"
        private const val USER_INFO = ("(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)"
                + "\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_"
                + "\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@")

        /**
         * Valid UCS characters defined in RFC 3987. Excludes space characters.
         */
        private const val UCS_CHAR = "[" +
                "\u00A0-\uD7FF" +
                "\uF900-\uFDCF" +
                "\uFDF0-\uFFEF" +
                "\uD800\uDC00-\uD83F\uDFFD" +
                "\uD840\uDC00-\uD87F\uDFFD" +
                "\uD880\uDC00-\uD8BF\uDFFD" +
                "\uD8C0\uDC00-\uD8FF\uDFFD" +
                "\uD900\uDC00-\uD93F\uDFFD" +
                "\uD940\uDC00-\uD97F\uDFFD" +
                "\uD980\uDC00-\uD9BF\uDFFD" +
                "\uD9C0\uDC00-\uD9FF\uDFFD" +
                "\uDA00\uDC00-\uDA3F\uDFFD" +
                "\uDA40\uDC00-\uDA7F\uDFFD" +
                "\uDA80\uDC00-\uDABF\uDFFD" +
                "\uDAC0\uDC00-\uDAFF\uDFFD" +
                "\uDB00\uDC00-\uDB3F\uDFFD" +
                "\uDB44\uDC00-\uDB7F\uDFFD" +
                "&&[^\u00A0[\u2000-\u200A]\u2028\u2029\u202F\u3000]]"


        /**
         * Valid characters for IRI TLD defined in RFC 3987.
         */

        private val LABEL_CHAR = "a-zA-Z0-9" + UCS_CHAR

        /**
         * RFC 1035 Section 2.3.4 limits the labels to a maximum 63 octets.
         */
        private val IRI_LABEL =
            "[" + LABEL_CHAR + "](?:[" + LABEL_CHAR + "_\\-]{0,61}[" + LABEL_CHAR + "]){0,1}"
        private const val IP_ADDRESS_STRING =
            ("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9]))")
        private val TLD_CHAR = "a-zA-Z" + UCS_CHAR
        private const val PUNYCODE_TLD = "xn\\-\\-[\\w\\-]{0,58}\\w"
        private val TLD =
            "(" + PUNYCODE_TLD + "|" + "[" + TLD_CHAR + "]{2,63}" + ")"

        private val HOST_NAME = "(" + IRI_LABEL + "\\.)+" + TLD
        private val DOMAIN_NAME_STR =
            "(" + HOST_NAME + "|" + IP_ADDRESS_STRING + ")"
        private const val PORT_NUMBER = "\\:\\d{1,5}"
        private const val WORD_BOUNDARY = "(?:\\b|$|^)"
        private val PATH_AND_QUERY = ("[/\\?](?:(?:[" + LABEL_CHAR
                + ";/\\?:@&=#~" // plus optional query params
                + "\\-\\.\\+!\\*'\\(\\),_\\$])|(?:%[a-fA-F0-9]{2}))*")

        val WEB_URL = Pattern.compile(
            "("
                    + "("
                    + "(?:" + PROTOCOL + "(?:" + USER_INFO + ")?" + ")?"
                    + "(?:" + DOMAIN_NAME_STR + ")"
                    + "(?:" + PORT_NUMBER + ")?"
                    + ")"
                    + "(" + PATH_AND_QUERY + ")?"
                    + WORD_BOUNDARY
                    + ")"
        )
    }
}