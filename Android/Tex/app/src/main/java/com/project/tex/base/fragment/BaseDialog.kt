package com.project.tex.base.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.project.tex.base.activity.BaseActivity
import com.project.tex.utils.MsgUtils

abstract class BaseDialog<VB : ViewBinding> : DialogFragment() {
    private var binding: VB? = null
    val _binding get() = binding!!

    val msgUtils: MsgUtils by lazy {
        (activity as? BaseActivity<*, *>)?.msg!!.apply {
            updateSnackbarView(requireView().rootView)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // the content
        val root: ViewGroup = getParentLayout()
        root.layoutParams = getLayoutParams()

        // creating the fullscreen dialog
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.window!!.setGravity(getWindowGravity())
        }
        dialog.setCanceledOnTouchOutside(getCancellableOnTouchOutside())
        return dialog
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    protected open fun getCancellableOnTouchOutside(): Boolean {
        return false
    }

    protected open fun getWindowGravity(): Int {
        return Gravity.BOTTOM
    }

    protected open fun getLayoutParams(): ViewGroup.LayoutParams? {
        return ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    open fun getParentLayout(): ViewGroup {
        return RelativeLayout(activity)
    }


    open fun onBackPressed(): Boolean {
        return false
    }

    override fun show(fragmentManager: FragmentManager, tag: String?) {
        val transaction = fragmentManager.beginTransaction()
        val prevFragment = fragmentManager.findFragmentByTag(tag)
        if (prevFragment != null) {
            transaction.remove(prevFragment)
        }
        transaction.add(this, tag).commitAllowingStateLoss()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getViewBinding()
        return _binding.root
    }

    abstract fun getViewBinding(): VB
}