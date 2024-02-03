package com.project.tex.notifications

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.tex.NightMode
import com.project.tex.R
import com.project.tex.firebase.FirebaseDBManager
import com.project.tex.model.ModelNotification
import java.util.*

class NotificationScreen : AppCompatActivity() {
    private var notifications: ArrayList<ModelNotification?>? = null
//    private var adapterNotification: AdapterNotification? = null
    var recyclerView: RecyclerView? = null
    var sharedPref: NightMode? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        sharedPref = NightMode(this)
        if (sharedPref!!.loadNightModeState().equals("night")) {
            setTheme(R.style.DarkTheme)
        } else if (sharedPref!!.loadNightModeState().equals("dim")) {
            setTheme(R.style.DimTheme)
        } else setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_screen)

        //Notification
        FirebaseDBManager.dbInstance.getReference("Users").child(
            Objects.requireNonNull<FirebaseUser>(FirebaseAuth.getInstance().getCurrentUser())
                .getUid()
        ).child("Count").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    FirebaseDBManager.dbInstance.getReference("Users").child(
                        Objects.requireNonNull<FirebaseUser>(
                            FirebaseAuth.getInstance().getCurrentUser()
                        ).getUid()
                    ).child("Count").getRef().removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        //User
        recyclerView = findViewById<RecyclerView>(R.id.notify)
        recyclerView!!.layoutManager = LinearLayoutManager(this@NotificationScreen)
        notifications = ArrayList<ModelNotification?>()
        findViewById<View>(R.id.back).setOnClickListener(View.OnClickListener { v: View? ->
            FirebaseDBManager.dbInstance.getReference("Users").child(
                Objects.requireNonNull<FirebaseUser>(
                    FirebaseAuth.getInstance().getCurrentUser()
                ).getUid()
            ).child("Count").getRef().removeValue()
            onBackPressed()
        })
        allNotifications
    }

    private val allNotifications: Unit
        private get() {
            FirebaseDBManager.dbInstance.getReference("Users").child(
                Objects.requireNonNull<FirebaseUser>(
                    FirebaseAuth.getInstance().getCurrentUser()
                ).getUid()
            ).child("Notifications")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        notifications!!.clear()
                        for (ds in snapshot.getChildren()) {
                            val modelNotification: ModelNotification =
                                ds.getValue(ModelNotification::class.java)!!
                            notifications!!.add(modelNotification)
                        }
                        Collections.reverse(notifications)
//                        adapterNotification =
//                            AdapterNotification(this@NotificationScreen, notifications)
//                        recyclerView!!.adapter = adapterNotification
//                        if (adapterNotification.getItemCount() === 0) {
//                            findViewById<View>(R.id.progressBar).setVisibility(View.GONE)
//                            findViewById<View>(R.id.notify).setVisibility(View.GONE)
//                            findViewById<View>(R.id.nothing).setVisibility(View.VISIBLE)
//                        } else {
//                            findViewById<View>(R.id.progressBar).setVisibility(View.GONE)
//                            findViewById<View>(R.id.notify).setVisibility(View.VISIBLE)
//                            findViewById<View>(R.id.nothing).setVisibility(View.GONE)
//                        }
//                        if (!snapshot.exists()) {
//                            findViewById<View>(R.id.progressBar).setVisibility(View.GONE)
//                            findViewById<View>(R.id.notify).setVisibility(View.GONE)
//                            findViewById<View>(R.id.nothing).setVisibility(View.VISIBLE)
//                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
}