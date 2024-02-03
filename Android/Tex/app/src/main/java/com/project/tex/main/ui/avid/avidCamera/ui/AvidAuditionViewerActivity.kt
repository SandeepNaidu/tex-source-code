package com.project.tex.main.ui.avid.avidCamera.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.util.CollectionUtils
import com.project.tex.R
import com.project.tex.base.activity.BaseActivity
import com.project.tex.databinding.ActivityAvidPreviewBinding
import com.project.tex.main.model.AvidDetailForArtist
import com.project.tex.main.ui.search.AvidViewModel


class AvidAuditionViewerActivity : BaseActivity<ActivityAvidPreviewBinding, AvidViewModel>(),
    View.OnClickListener {
    private var avidId: Int? = null
    private val TAG: String = AvidAuditionViewerActivity::class.java.simpleName
    private var avidDetailForArtist: AvidDetailForArtist? = null

    override fun getViewBinding() = ActivityAvidPreviewBinding.inflate(layoutInflater)

    override fun getViewModelInstance() = ViewModelProvider(this)[AvidViewModel::class.java]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding.backBtn.setOnClickListener(this)

        getAvidDetailsForArtist()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back_btn -> {
                finish()
            }
        }
    }

    private fun getAvidDetailsForArtist() {
        avidId?.let {
            viewModel.getAvidDetail(it)
                .observe(this, Observer {
                    if (it == null) return@Observer
                    if (it.responseCode == 400) return@Observer
                    it.body?.avid?.let { listData ->
                        if (!CollectionUtils.isEmpty(listData)) {
                            avidDetailForArtist = listData[0]
                        }
                    }
                })
        }
    }

}