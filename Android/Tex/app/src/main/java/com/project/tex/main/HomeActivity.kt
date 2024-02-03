package com.project.tex.main

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.facebook.login.LoginManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.makeramen.roundedimageview.RoundedImageView
import com.project.tex.R
import com.project.tex.aritist_profile.ui.ProfileActivity
import com.project.tex.base.activity.BaseOnboarding
import com.project.tex.databinding.NavigationDrawerBinding
import com.project.tex.main.ui.avid.avidCamera.ui.AvidCaptureActivity
import com.project.tex.onboarding.Constants.INTENT_URI
import com.project.tex.onboarding.login.QuickSignInActivity
import com.project.tex.settings.ui.SettingsActivity
import com.project.tex.utils.ViewUtils.dpToPx


class HomeActivity : BaseOnboarding<NavigationDrawerBinding, HomeViewModel>() {

    private var navViewLastScreenSelectedId: Int? = R.id.navigation_home
    private var isUserTypeAritst: Boolean = false
    private var deeplink: String = "DEFAULT"
    private val TAG: String = HomeActivity::class.java.simpleName

    override fun getViewBinding() = NavigationDrawerBinding.inflate(layoutInflater)

    override fun getViewModelInstance() = ViewModelProvider(this).get(HomeViewModel::class.java)

    private val activityResultSettings =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            navViewLastScreenSelectedId?.let { _binding.content.navView.selectedItemId = it }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navView: BottomNavigationView = _binding.content.navView
        if (intent != null && intent.hasExtra(INTENT_URI)) {
            (deeplink) = intent.getStringExtra(INTENT_URI) ?: "DEFAULT"
        }
        isUserTypeAritst = dataKeyValue.getValueString("userType")?.equals("artistadmin") ?: false

        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            if (isUserTypeAritst) {
                setOf(
                    R.id.navigation_home,
                    R.id.navigation_job,
                    R.id.navigation_setting,
                    R.id.navigation_avid,
                    R.id.navigation_search
                )
            } else {
                setOf(
                    R.id.navigation_home_rec,
                    R.id.navigation_search_rec,
                    R.id.navigation_jobs_rec,
                    R.id.navigation_dashboard_rec,
                    R.id.navigation_setting_rec
                )
            }
        )
        navController.setGraph(if (isUserTypeAritst) R.navigation.mobile_navigation else R.navigation.mobile_navigation_rec)
        // remove default item indicator
        navView.isItemActiveIndicatorEnabled = false
        // remove default item color tinting
        navView.itemIconTintList = null

        setSupportActionBar(_binding.mainToolbar) //Set toolbar

        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayShowHomeEnabled(true)
            setIcon(R.drawable.ic_logo)
            setLogo(R.drawable.ic_logo)
        }

        _binding.mainToolbar.inflateMenu(R.menu.home_menu)
        _binding.content.navView.inflateMenu(if (isUserTypeAritst) R.menu.bottom_nav_menu else R.menu.bottom_nav_recr_menu)

        val mActionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            _binding.myDrawerLayout,
            _binding.mainToolbar,
            R.string.drawer_opened,
            R.string.drawer_closed
        )

        _binding.myDrawerLayout.addDrawerListener(mActionBarDrawerToggle)
        _binding.drawerView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_account -> {
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_logout -> {
                    mGoogleSignInClient.signOut().addOnCompleteListener { sign ->
                        if (sign.isSuccessful) {
                            startActivity(Intent(this, QuickSignInActivity::class.java))
                            finish()
                        }
                    }
                    LoginManager.getInstance().logOut()
                    FirebaseAuth.getInstance().signOut()
                    dataKeyValue.clearSharedPreference()

                    return@setNavigationItemSelectedListener true
                }
                else -> {
                    return@setNavigationItemSelectedListener false
                }
            }
        }

        setupActionBarWithNavController(
            navController, appBarConfiguration
        ) //Setup toolbar with back button and drawer icon according to appBarConfiguration

        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.navigation_avid) {
                _binding.mainAppbar.isVisible = false
                _binding.content.root.setPadding(0, 0, 0, 0)
            } else {
                updateProfileIcon()
                _binding.mainAppbar.isVisible = true
                _binding.content.root.setPadding(0, dpToPx(56), 0, 0)
            }
        }

        navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    navController.popBackStack(R.id.navigation_home, false)
                }
                R.id.navigation_setting, R.id.navigation_setting_rec -> {
                    navViewLastScreenSelectedId = navView.selectedItemId
                    activityResultSettings.launch(Intent(this, SettingsActivity::class.java))
                }
                R.id.navigation_avid -> {
                    navController.navigate(R.id.navigation_avid)
                }
            }
            return@setOnItemSelectedListener true
        }
        getDeeplinkIntent()

    }

    override fun onResume() {
        super.onResume()
        updateProfileIcon()
    }

    private fun updateProfileIcon() {
        Glide.with(this).asDrawable().load(viewModel.getProfileImage())
            .into(object : CustomTarget<Drawable?>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    _binding.mainToolbar.menu?.get(1)?.actionView?.findViewById<RoundedImageView>(R.id.profile)
                        ?.setImageDrawable(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
        _binding.mainToolbar.menu?.get(1)?.actionView?.findViewById<RoundedImageView>(R.id.profile)
            ?.setOnClickListener {
                if (_binding.mainToolbar.menu?.get(1) != null) {
                    onOptionsItemSelected(_binding.mainToolbar.menu?.get(1)!!)
                }
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                _binding.myDrawerLayout.openDrawer(GravityCompat.END)
                true
            }
            R.id.action_notification -> {
//                startActivity(Intent(this, AvidCaptureActivity::class.java))
                true
            }
            R.id.action_profile_pic -> {
                startActivity(Intent(this, ProfileActivity::class.java).putExtra("id", viewModel.getUserId()))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getDeeplinkIntent() {
        if (this@HomeActivity.isFinishing) return
        if (intent != null && intent.hasExtra(INTENT_URI)) {
            gotoDeepLink(deeplink)
        }
    }

    override fun onNewIntent(intentNew: Intent?) {
        super.onNewIntent(intentNew)
        if (intentNew != null && intentNew.hasExtra(INTENT_URI)) {
            (deeplink) = intentNew.getStringExtra(INTENT_URI) ?: "DEFAULT"
        }
        gotoDeepLink(deeplink)
    }

    private fun gotoDeepLink(mDeepLink: String) {
        if (!TextUtils.isEmpty(mDeepLink)) {
            val arr = mDeepLink.split("/").toMutableList()
            arr.removeIf { (it.isEmpty()) }
            if (arr.isNotEmpty()) {
                when (arr[0]) {
                    "avid" -> {
                        if (isUserTypeAritst) {
                            findNavController(R.id.nav_host_fragment_activity_home).navigate(R.id.navigation_avid,
                                Bundle().apply {
                                    if (arr.size > 0) {
                                        putString("avidId", arr[1])
                                    }
                                })
                        } else {
                            //to be covered later
                        }
                    }
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home_menu, menu)
        updateProfileIcon()
        return true
    }

    override fun onBackPressed() {
        when { //If drawer layout is open close that on back pressed
            _binding.myDrawerLayout.isDrawerOpen(GravityCompat.START) -> {
                _binding.myDrawerLayout.closeDrawer(GravityCompat.START)
            }
            else -> {
                super.onBackPressed() //If drawer is already in closed condition then go back
            }
        }
    }

}