package com.project.tex.aritist_profile.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.google.gson.Gson
import com.project.tex.R
import com.project.tex.aritist_profile.adapter.CitiesAdapter
import com.project.tex.aritist_profile.model.CitiesList
import com.project.tex.base.fragment.BaseDialog
import com.project.tex.databinding.DialogCitiesBinding
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.*


@Suppress("DEPRECATION", "RedundantOverride")
class CityDialog : BaseDialog<DialogCitiesBinding>(), CitiesAdapter.ClickAction {
    override fun getViewBinding() = DialogCitiesBinding.inflate(layoutInflater)

    private val cities: MutableList<CitiesList.CitiesListItem> = mutableListOf()
    var mListener: CitiesAdapter.ClickAction? = null

    override fun onStart() {
        super.onStart()
        dialog?.window?.setWindowAnimations(R.style.DialogAnimation)
        dialog?.setCancelable(true)
        dialog?.setCanceledOnTouchOutside(true)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    fun setListener(listener: CitiesAdapter.ClickAction) {
        this.mListener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { args ->
        }

        _binding.title.addTextChangedListener {
            val filterList = cities.filter { city ->
                city.name?.contains(it.toString(), true) == true
            }
            _binding.rvCities.adapter = CitiesAdapter(filterList, this)
        }
        readCity()
    }

    fun readCity() {
        Observable.create<String> {
            val `is`: InputStream = resources.openRawResource(R.raw.cities)
            val writer: Writer = StringWriter()
            val buffer = CharArray(1024)
            try {
                val reader: Reader = BufferedReader(InputStreamReader(`is`, "UTF-8"))
                var n: Int
                while (reader.read(buffer).also { n = it } != -1) {
                    writer.write(buffer, 0, n)
                }
            } finally {
                `is`.close()
            }

            val jsonString: String = writer.toString()
            it.onNext(jsonString)
        }.subscribeOn(Schedulers.io())
            .flatMap {
                val cities = Gson().fromJson(it, CitiesList::class.java)
                Observable.just(cities)
            }.observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                cities.addAll(it)
                _binding.rvCities.adapter = CitiesAdapter(it, this)
            }, {
                msgUtils.showShortMsg("Failed to load cities")
            }, {

            })
    }

    override fun getTheme(): Int {
        return R.style.DialogAnimation
    }

    override fun itemClick(item: CitiesList.CitiesListItem) {
        mListener?.itemClick(item)
        dismissAllowingStateLoss()
    }

}