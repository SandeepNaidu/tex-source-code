package com.project.tex.main.ui.avid.avidCamera.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.project.tex.R
import com.project.tex.base.activity.BaseActivity
import com.project.tex.databinding.ActivityTakesBinding
import com.project.tex.db.table.AvidTakes
import com.project.tex.main.ui.avid.avidCamera.adapter.TakesGridAdapter
import com.project.tex.main.ui.search.AvidViewModel

class TakesActivity : BaseActivity<ActivityTakesBinding, AvidViewModel>(), View.OnClickListener {
    override fun getViewBinding() = ActivityTakesBinding.inflate(layoutInflater)

    override fun getViewModelInstance() =
        ViewModelProvider(this).get(AvidViewModel::class.java)

    private lateinit var adapter: TakesGridAdapter
    private val takesList: List<AvidTakes> = mutableListOf()
    private lateinit var avidId: String

    // Receiver
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val value = it.data?.getBooleanExtra("isDeleted", false)
                if (value == true) {
                    adapter.resetSelection()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        avidId = intent.getStringExtra("avidId")!!
        adapter = TakesGridAdapter(this, takesList)
        _binding.recyclerView2.adapter = adapter
        _binding.backBtn.setOnClickListener(this)

        adapter.setOnItemClickListener(object : TakesGridAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, model: AvidTakes?) {
                if (model != null) {
                    val intent = Intent(this@TakesActivity, TrimVideoActivity::class.java)
                    intent.putExtra("videoPath", Uri.parse(model.videoUrl))
                    intent.putExtra("avidTakeId", model.avidTakeId)
                    intent.putExtra("avidId", avidId)
                    intent.putExtra("isFromGallery", false)
                    getResult.launch(intent)
                }
            }
        })
        observer()
    }

    override fun onResume() {
        super.onResume()
        viewModel.deleteAllOld()
    }

    private fun observer() {
        viewModel.getAllTakes().observe(this, Observer {
            adapter.setData(it)
            _binding.noTakes.isVisible = !it.isNotEmpty()
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back_btn -> {
                finish()
            }
        }
    }
}