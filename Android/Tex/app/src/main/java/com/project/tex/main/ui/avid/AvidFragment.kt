package com.project.tex.main.ui.avid

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.project.tex.R
import com.project.tex.base.activity.BaseActivity
import com.project.tex.base.fragment.BaseFragment
import com.project.tex.databinding.FragmentAvidBinding
import com.project.tex.databinding.NumberPickerDialogBinding
import com.project.tex.main.model.AvidData
import com.project.tex.main.ui.adapter.AvidAdapter
import com.project.tex.main.ui.adapter.StoriesPagerAdapter
import com.project.tex.main.ui.avid.avidCamera.ui.AvidCaptureActivity
import com.project.tex.main.ui.search.AvidViewModel
import org.apache.commons.lang3.BooleanUtils
import org.apache.commons.lang3.StringUtils


class AvidFragment() : BaseFragment<FragmentAvidBinding>(), View.OnClickListener,
    AvidAdapter.AvidCallbacks {
    private val PERMISSION_CODE: Int = 123

    //    private lateinit var adapter: AvidAdapter
    private lateinit var storiesPagerAdapter: StoriesPagerAdapter

    override fun getViewBinding() = FragmentAvidBinding.inflate(layoutInflater)
    private val mainViewModel: AvidViewModel by viewModels({ requireActivity() })
    private val avidList = mutableListOf<AvidData>()
    private var avidId = ""
    private var PERMISSION: Array<String>? = null
    var permissionsList: ArrayList<String> = arrayListOf()
    var permissionAskedTime = 0
    var permissionsCount = 0
    var permissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val list: ArrayList<Boolean> = ArrayList(it.values)
            permissionsList = ArrayList()
            permissionsCount = 0
            for (i in PERMISSION!!.indices) {
                if (shouldShowRequestPermissionRationale(PERMISSION!![i])) {
                    permissionsList.add(PERMISSION!!.get(i))
                } else if (!hasPermission(requireContext(), PERMISSION!!.get(i))) {
                    permissionsCount++
                }
            }
            if (permissionsList.size > 0) {
                if (permissionAskedTime >= 1) {
                    permissionAskedTime = 0
                    return@registerForActivityResult
                }
                //Some permissions are denied and can be asked again.
                askForPermissions(permissionsList)
                permissionAskedTime++
            } else if (permissionsCount > 0) {
                val d = it.filter { permission ->
                    !permission.value
                }
                permissionsList.addAll(d.keys)
                showMissingPermissionDialog()
            } else {
                //All permissions granted. Do your stuff 🤞
                startActivity(Intent(context, AvidCaptureActivity::class.java))
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            avidId = it.getString("avidId") ?: ""
        }
        storiesPagerAdapter = StoriesPagerAdapter(this, avidList)
        _binding.videoPager.adapter = storiesPagerAdapter

        mainViewModel.getAllAvidList().observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            if (it.responseCode != 200) return@Observer
            it.body?.avids?.let { it1 ->
                val a =
                    it1.find { StringUtils.isNotEmpty(avidId) && avidId.isDigitsOnly() && it.avidId == avidId.toInt() }
                avidList.addAll(it1)
                if (a != null) {
                    avidList.remove(a)
                    avidList.add(0, a)
                }
                storiesPagerAdapter.notifyDataSetChanged()
            } ?: kotlin.run {
                msg.showLongMsg("Failed to load AVID's!")
            }
        })
        _binding.createAvid.setOnClickListener(this)
        _binding.muteAudio.setOnClickListener(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PERMISSION = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
            )
        } else {
            PERMISSION = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.create_avid -> {
                permissionsLauncher.launch(PERMISSION)
            }
            R.id.muteAudio -> {
                mainViewModel.setAudioMute(!_binding.muteAudio.isChecked)
            }
        }
    }

    private fun hasPermission(context: Context, permissionStr: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permissionStr
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun askForPermissions(permissionsList: ArrayList<String>) {
        val newPermissionStr = Array<String>(permissionsList.size) {
            permissionsList[it]
        }

        if (newPermissionStr.isNotEmpty()) {
            permissionsLauncher.launch(newPermissionStr)
        } else {
            /* User has pressed 'Deny & Don't ask again' so we have to show the enable permissions dialog
            which will lead them to app details page to enable permissions from there. */
            showMissingPermissionDialog()
        }
    }

    private fun showMissingPermissionDialog() {
        val isCameraDenied = permissionsList.contains(Manifest.permission.CAMERA)
        val isStorageDenied = permissionsList.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val isRecordAudioDenied = permissionsList.contains(Manifest.permission.RECORD_AUDIO)

        val builder = AlertDialog.Builder(requireContext())
        var missingPermissions = if (isCameraDenied) "Camera, " else ""
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            missingPermissions += if (isStorageDenied) "Storage, " else ""
        }
        missingPermissions += if (isRecordAudioDenied) "Audio Record" else ""
        missingPermissions = missingPermissions.trim().trimEnd(',')
        builder.setTitle("Permission Required!")
        builder.setMessage("Please provide $missingPermissions permission")
        builder.setPositiveButton("Allow", DialogInterface.OnClickListener { dialog, which ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri =
                Uri.fromParts("package", requireContext().packageName, null)
            intent.data = uri
            startActivity(intent)
        })
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun shareAvid(modelAvid: AvidData, toShare: Int) {
    }

    override fun saveAvid(modelAvid: AvidData, toSave: Int) {
    }

    override fun likeAvid(modelAvid: AvidData, toLike: Int) {
    }
}