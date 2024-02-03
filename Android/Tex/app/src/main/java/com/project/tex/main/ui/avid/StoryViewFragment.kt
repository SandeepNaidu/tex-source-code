package com.project.tex.main.ui.avid

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.danikula.videocache.CacheListener
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.gms.common.util.CollectionUtils
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import com.project.tex.GlobalApplication
import com.project.tex.base.fragment.BaseFragment
import com.project.tex.databinding.AvidListBinding
import com.project.tex.main.model.AvidData
import com.project.tex.main.model.AvidDetailForArtist
import com.project.tex.main.model.LikeShareSaveResponse
import com.project.tex.main.ui.search.AvidViewModel
import com.project.tex.utils.IntentUtils
import org.apache.commons.lang3.StringUtils
import java.io.File

class StoryViewFragment : BaseFragment<AvidListBinding>(), CacheListener {

    override fun getViewBinding() = AvidListBinding.inflate(LayoutInflater.from(context))

    private var storyUrl: String? = null
    private var storiesDataModel: AvidData? = null

    private var simplePlayer: ExoPlayer? = null
    private var toPlayVideoPosition: Int = -1

    private var avidDetailForArtist: AvidDetailForArtist? = null
    private val shareResponse: MutableLiveData<LikeShareSaveResponse> =
        MutableLiveData<LikeShareSaveResponse>()
    private val saveResponse: MutableLiveData<LikeShareSaveResponse> =
        MutableLiveData<LikeShareSaveResponse>()
    private val likeResponse: MutableLiveData<LikeShareSaveResponse> =
        MutableLiveData<LikeShareSaveResponse>()

    companion object {
        fun newInstance(storiesDataModel: AvidData) = StoryViewFragment()
            .apply {
                arguments = Bundle().apply {
                    putSerializable("dataAvid", storiesDataModel)
                }
            }
    }

    private val mainViewModel: AvidViewModel by viewModels({ requireActivity() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            storiesDataModel = arguments?.getSerializable("dataAvid", AvidData::class.java)
        } else {
            storiesDataModel = arguments?.getSerializable("dataAvid") as AvidData?
        }
        _binding.thumbnail.isVisible = true
        setData()
    }

    private fun setData() {
        if (_binding.usernameTv.text.isEmpty()) {
            var artistName = ""
            if (StringUtils.isNotEmpty(storiesDataModel?.artistFirstName))
                artistName = storiesDataModel?.artistFirstName!!.trim()
            if (StringUtils.isNotEmpty(storiesDataModel?.artistLastName))
                artistName = "$artistName ${storiesDataModel?.artistLastName}".trim()
//            if (StringUtils.isEmpty(artistName.replace(" ","")))
//                artistName = list.random()
            _binding.usernameTv.text = artistName
        }
        if (_binding.descriptionTv.text.isEmpty())
            _binding.descriptionTv.setLinkText(storiesDataModel?.caption)

        getAvidDetailsForArtist()

        Glide.with(requireContext()).load(storiesDataModel?.coverContent).into(_binding.thumbnail)

        val simplePlayer = getPlayer()
        _binding.playerViewStory.player = simplePlayer
        simplePlayer?.addListener(playerCallback)

        storyUrl = storiesDataModel?.content
        storyUrl?.let { prepareMedia(it) }

        mainViewModel.getIsMute().observe(viewLifecycleOwner, Observer {
            if (it) {
                simplePlayer?.volume = 0f
            } else {
                simplePlayer?.volume = 1f
            }
        })

        _binding.playerViewStory.setOnClickListener {
            simplePlayer?.playWhenReady = !(simplePlayer?.isPlaying == true)
        }
    }

    private fun getAvidDetailsForArtist() {
        storiesDataModel?.avidId?.let {
            mainViewModel.getAvidDetail(storiesDataModel?.avidId!!)
                .observe(viewLifecycleOwner, Observer {
                    if (it == null) return@Observer
                    if (it.responseCode == 400) return@Observer
                    it.body?.avid?.let { listData ->
                        if (!CollectionUtils.isEmpty(listData)) {
                            avidDetailForArtist = listData[0]
                            updateLikeSaveShareUi()
                        }
                    }
                    setListeners()
                })
        }
    }

    private fun setListeners() {
        _binding.likeImg.setOnClickListener { v: View? ->
            val isLiked = avidDetailForArtist?.isLiked
            v?.isEnabled = false
            storiesDataModel?.avidId?.let {
                mainViewModel.likeAvid(
                    it,
                    if (isLiked == 1) 0 else 1,
                    likeResponse
                ).observe(viewLifecycleOwner, Observer {
                    v?.isEnabled = true
                    if (it == null) return@Observer
                    if (it.responseCode == 400) return@Observer
                    getAvidDetailsForArtist()
                })
            }
        }
        _binding.shareImg.setOnClickListener { v: View? ->
            val isShared = avidDetailForArtist?.isShared
            if (isShared == 0) {
                storiesDataModel?.avidId?.let {
                    v?.isEnabled = false
                    mainViewModel.shareAvid(
                        it,
                        if (isShared == 1) 0 else 1,
                        shareResponse
                    ).observe(viewLifecycleOwner, Observer {
                        v?.isEnabled = true
                        if (it == null) return@Observer
                        if (it.responseCode == 400) return@Observer
                        getAvidDetailsForArtist()
                    })
                }
            }
            val avidId: String = storiesDataModel?.avidId.toString()
            val longUri =
                Uri.parse("https://texapp.page.link/?link=https://www.texapp.com/avid/$avidId/&apn=${requireContext().packageName}")
            val shortLinkTask = Firebase.dynamicLinks.shortLinkAsync {
                longLink = longUri
            }.addOnSuccessListener {
                it.shortLink?.let {
                    IntentUtils.shareTextUrl(
                        requireContext(),
                        "Hey there, here is the shared avid link : $it"
                    )
                }
            }.addOnFailureListener {
                // Error
                // ...
                IntentUtils.shareTextUrl(
                    requireContext(),
                    "Hey there, here is the shared avid link : $longUri"
                )
            }
        }
        _binding.saveImg.setOnClickListener { v: View? ->
            val isSaved = avidDetailForArtist?.isSaved
            storiesDataModel?.avidId?.let {
                v?.isEnabled = false
                mainViewModel.saveAvid(
                    it,
                    if (isSaved == 1) 0 else 1,
                    saveResponse
                ).observe(viewLifecycleOwner, Observer {
                    v?.isEnabled = true
                    if (it == null) return@Observer
                    if (it.responseCode == 400) return@Observer
                    getAvidDetailsForArtist()
                })
            }
        }
    }

    private fun updateLikeSaveShareUi() {
        avidDetailForArtist?.likeCount?.let { it1 -> _binding.likeImg.text = "$it1 likes" }
        avidDetailForArtist?.shareCount?.let { it1 -> _binding.shareImg.text = "$it1 Shares" }
        avidDetailForArtist?.saveCount?.let { it1 -> _binding.saveImg.text = "$it1 saves" }
        _binding.likeImg.isChecked = avidDetailForArtist?.isLiked == 1
        _binding.saveImg.isChecked = avidDetailForArtist?.isSaved == 1
        _binding.shareImg.isChecked = avidDetailForArtist?.isShared == 1
    }

    override fun onPause() {
        pauseVideo()
        super.onPause()
    }

    override fun onResume() {
        restartVideo()
        super.onResume()
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }

    private val playerCallback: Player.Listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> {
                    _binding.thumbnail.isVisible = false
                    _binding.pb.isVisible = false
                    "ExoPlayer.STATE_READY     -"
                }
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Log.d("STATE", "changed state to $stateString")
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
        }

        override fun onVolumeChanged(volume: Float) {
            super.onVolumeChanged(volume)
        }

        override fun onRenderedFirstFrame() {
            super.onRenderedFirstFrame()
        }
    }

    private fun prepareVideoPlayer() {
        simplePlayer = ExoPlayer.Builder(requireContext()).build()
    }

    private fun getPlayer(): ExoPlayer? {
        if (simplePlayer == null) {
            prepareVideoPlayer()
        }
        return simplePlayer
    }

    private fun prepareMedia(linkUrl: String) {
        Log.d("prepareMedia", "prepareMedia linkUrl: $linkUrl")

        val proxyUrl = GlobalApplication.instance.getProxy(requireContext())!!.getProxyUrl(linkUrl)
        val uri = Uri.parse(proxyUrl)

        val mediaItem = MediaItem.fromUri(uri)
        val dataSourceFactory = DefaultDataSourceFactory(requireContext(), "sample")
        val mediaSource =
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)

        simplePlayer?.prepare(mediaSource)
        simplePlayer?.repeatMode = Player.REPEAT_MODE_ONE
        simplePlayer?.playWhenReady = true
        simplePlayer?.addListener(playerCallback)
        toPlayVideoPosition = -1

    }

    private fun setArtwork(drawable: Drawable, playerView: PlayerView) {
        playerView.useArtwork = true
        playerView.defaultArtwork = drawable
    }

    private fun playVideo() {
        simplePlayer?.playWhenReady = true
    }

    private fun restartVideo() {
        if (simplePlayer == null) {
            storyUrl?.let { prepareMedia(it) }
        } else {
            simplePlayer?.seekToDefaultPosition()
            simplePlayer?.playWhenReady = true
        }
    }

    private fun pauseVideo() {
        simplePlayer?.playWhenReady = false
    }

    private fun releasePlayer() {
        simplePlayer?.stop(true)
        simplePlayer?.release()
    }

    override fun onCacheAvailable(cacheFile: File?, url: String?, percentsAvailable: Int) {
        _binding.thumbnail.isVisible = false
        playVideo()
    }

}