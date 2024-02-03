package com.project.tex.firebase

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.tex.firebase.model.TPost
import com.project.tex.firebase.model.TUser
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class FirebaseDBManager {

    private val TAG: String = "FirebaseManager"

    companion object {
        //TODO : change below url with your firebase realtime database url
        private val BASE_URL =
            "https://friend-social-66b71-default-rtdb.asia-southeast1.firebasedatabase.app"
        val instance = FirebaseDBManager()
        val dbInstance = FirebaseDatabase.getInstance(BASE_URL)
    }

    private constructor() {

    }

    fun authroize(emitter: SingleEmitter<FirebaseUser>) {
        FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener { auth ->
            if (auth.isSuccessful) {
                emitter.onSuccess(auth.result.user!!)
            } else {
                emitter.onError(auth.exception!!)
            }
        }
    }

    fun authorizeAndRegisterUser(
        mName: String,
        mUsername: String,
        usernameType: Boolean = false,
        userType: String
    ): Single<Task<Void>> {
        return Single.create<FirebaseUser?> { emitter ->
            authroize(emitter)
        }.map { fuser ->
            val email = if (usernameType) mUsername else ""
            val phone = if (usernameType) "" else mUsername
            return@map register(fuser, mName, email, phone, userType)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun register(
        currentUser: FirebaseUser,
        mName: String,
        email: String,
        contact: String,
        userType: String
    ): Task<Void> {
        val userId = Objects.requireNonNull(currentUser).uid
        val user = TUser(
            id = userId,
            name = mName,
            username = email.ifEmpty { contact },
            email = email,
            phone = contact,
            userType = userType
        )
        return dbInstance.getReference("Users").child(userId).setValue(user)
    }

    fun checkUserExist(): Single<Boolean> {
        return Single.create<FirebaseUser?> { emitter ->
            authroize(emitter)
        }.flatMap { fUser ->
            return@flatMap Single.create { emitter ->
                dbInstance.getReference("Users").child(fUser.uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            emitter.onSuccess(snapshot.exists())
                            if (!snapshot.exists()) {
                                Log.d(TAG, "onDataChange: ")
                                FirebaseAuth.getInstance().currentUser!!.delete()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            emitter.onError(error.toException())
                            Log.e(TAG, "onCancelled: ", error.toException())
                        }
                    })
            }
        }
    }

    fun createPost(post: TPost): Task<Void> {
        return dbInstance.getReference("Posts").child(post.pTime).setValue(post)
    }

}