package com.project.tex.aritist_profile.ui

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.project.tex.R
import com.project.tex.aritist_profile.adapter.ExperienceGridAdapter
import com.project.tex.aritist_profile.model.ExperienceListResponse
import com.project.tex.aritist_profile.viewmodel.ProfileViewModel
import com.project.tex.base.fragment.BaseFragment
import com.project.tex.databinding.FragmentExperienceBinding
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [ExperienceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExperienceFragment : BaseFragment<FragmentExperienceBinding>() {

    private val mainViewModel: ProfileViewModel by viewModels({ requireActivity() })

    private fun callGetApi() {
        mainViewModel.profileDataLd.observe(viewLifecycleOwner) {
            if ((it?.isFresher ?: 0) == 1) {
                //fresher
                _binding.fresherCheckview.isChecked = true
            } else {
                _binding.fresherCheckview.isChecked = false
                //not a fresher start fetching experience
                mainViewModel.compositeDisposable.add(
                    mainViewModel.getAllExperience().subscribe({ re ->
                        if (re.responseCode == 200) {
                            re.body?.experiences?.let {
                                mainViewModel.setExperienceData(it)
                            }
                        } else {
                            mainViewModel.setExperienceData(emptyList())
                        }
                    }, {
                        msg.showShortMsg("Failed to get experience data")
                    })
                )
            }
        }
    }

    private fun setMultipleFontSize(years: Int, month: Int) {
        val s = String.format("%d years %d months", years, month)
        val ss1 = SpannableString(s)
        ss1.setSpan(
            AbsoluteSizeSpan(resources.getDimensionPixelSize(R.dimen.sp_24)),
            0,
            s.indexOf(" years"),
            0
        ) // set size
        ss1.setSpan(
            AbsoluteSizeSpan(resources.getDimensionPixelSize(R.dimen.sp_24)),
            s.indexOf("years") + 6,
            s.indexOf(" months"),
            0
        ) // set size
        _binding.valExpTitle.text = ss1
    }

    private val activityResultExperience =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                callGetApi()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding.btnNext.setOnClickListener {
            addExperience()
        }

        _binding.fabAddMedia.setOnClickListener {
            addExperience()
        }

        mainViewModel.experienceLd.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                _binding.placeholderTitle.isVisible = false
                _binding.btnNext.isVisible = false
                _binding.orTv.isVisible = false
                _binding.checkIfFresherTv.isVisible = false
                _binding.fresherCheckview.isVisible = false
                _binding.fabAddMedia.isVisible = mainViewModel.getIsSelfView()
                _binding.llTotalExp.isVisible = true
                _binding.rvExperience.isVisible = true
                calculateExperience(it)
                _binding.rvExperience.adapter =
                    ExperienceGridAdapter(it, object : ExperienceGridAdapter.EditClickListener {
                        override fun editClick(item: ExperienceListResponse.Body.Experience) {
                            activityResultExperience.launch(
                                Intent(
                                    context,
                                    AddExperienceActivity::class.java
                                ).putExtra("data", item)
                            )
                        }
                    }, mainViewModel.getIsSelfView())
            } else {
                _binding.placeholderTitle.isVisible = true
                if (mainViewModel.getIsSelfView()) {
                    _binding.placeholderTitle.text = "No experience"
                }
                _binding.checkIfFresherTv.isVisible = mainViewModel.getIsSelfView()
                _binding.btnNext.isVisible = mainViewModel.getIsSelfView()
                _binding.fresherCheckview.isVisible = mainViewModel.getIsSelfView()
                _binding.fabAddMedia.isVisible = mainViewModel.getIsSelfView()
                _binding.orTv.isVisible = mainViewModel.getIsSelfView()
            }
        }
        callGetApi()

        if (mainViewModel.getIsSelfView()) {
            _binding.fresherCheckview.setOnClickListener {
                mainViewModel.markAsFresher(
                    if ((mainViewModel.profileDataLd.value?.isFresher ?: 0) == 1) 0 else 1
                ).subscribe(
                    {
                        mainViewModel.getUpdateProfileData(activity as ProfileActivity)
                        msg.showShortMsg("Profile successfully updated")
                    },
                    {
                        msg.showShortMsg("Failed to update Experience")
                    }
                )
            }
        }
    }

    private fun addExperience() {
        if (_binding.fresherCheckview.isChecked) {
            msg.showShortMsg("Please unmark your profile as Fresher to add experience")
            return
        }
        activityResultExperience.launch(Intent(context, AddExperienceActivity::class.java))
    }

    private fun calculateExperience(experiences: List<ExperienceListResponse.Body.Experience?>) {
        try {
            var totalMonth = 0L
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(
                "yyyy-MM-dd'T'HH:mm:ss.SSSX",
                Locale.ENGLISH
            )
            experiences.forEach {
                val date1: LocalDate =
                    it?.startDate.let { d -> LocalDate.parse(d, formatter) } ?: LocalDate.now()
                val date2: LocalDate =
                    it?.endDate.let { d -> LocalDate.parse(d, formatter) } ?: LocalDate.now()
                val diff = Period.between(date1, date2)
                totalMonth += diff.toTotalMonths()
            }
            val year = (totalMonth / 12).toInt()
            val month = (totalMonth % 12).toInt()
            setMultipleFontSize(year, month)
        } catch (E: Exception) {
            Log.e("CalculateExp", "calculateExperience: ", E)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment ExperienceFragment.
         */
        @JvmStatic
        fun newInstance() = ExperienceFragment().apply {
            arguments = Bundle().apply {}
        }
    }

    override fun getViewBinding() = FragmentExperienceBinding.inflate(LayoutInflater.from(context))
}