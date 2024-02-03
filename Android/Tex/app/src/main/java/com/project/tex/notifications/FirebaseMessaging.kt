package com.project.tex.notifications

//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage

//class FirebaseMessaging : FirebaseMessagingService() {
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        super.onMessageReceived(remoteMessage)
//        if (isAppIsInBackground(getApplicationContext())) {
//            // Show the notification
//            val sp: SharedPreferences = getSharedPreferences("SP_USER", Context.MODE_PRIVATE)
//            val savedCurrentUser: String = sp.getString("Current_USERID", "None")!!
//            val sent: String? = remoteMessage.getData().get("sent")
//            val user: String? = remoteMessage.getData().get("user")
//            val fUser: FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()
//            if (fUser != null) {
//                assert(sent != null)
//                if (sent == fUser.getUid()) {
//                    if (savedCurrentUser != user) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                            sendOAndAboveNotification(remoteMessage)
//                        } else {
//                            sendNormalNotification(remoteMessage)
//                        }
//                    }
//                }
//            }
//        } else {
//        }
//    }
//
//    private fun sendNormalNotification(remoteMessage: RemoteMessage) {
//        val user = "" + remoteMessage.getData().get("sent")
//        val icon = "" + remoteMessage.getData().get("icon")
//        val title = "" + remoteMessage.getData().get("title")
//        val body = "" + remoteMessage.getData().get("body")
//        val notification: RemoteMessage.Notification = remoteMessage.getNotification()!!
//        val i = user.replace("[\\D]".toRegex(), "").toInt()
//        val intent = Intent(this, SplashScreenActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val pIntent: PendingIntent =
//            PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT)
//        val defSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this)
//            .setSmallIcon(icon.toInt())
//            .setContentText(body)
//            .setContentTitle(title)
//            .setAutoCancel(true)
//            .setSound(defSoundUri)
//            .setContentIntent(pIntent)
//        val notificationManager: NotificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        var j = 0
//        if (i > 0) {
//            j = i
//        }
//        notificationManager.notify(j, builder.build())
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private fun sendOAndAboveNotification(remoteMessage: RemoteMessage) {
//        val user = "" + remoteMessage.getData().get("user")
//        val icon = "" + remoteMessage.getData().get("icon")
//        val title = "" + remoteMessage.getData().get("title")
//        val body = "" + remoteMessage.getData().get("body")
//        val notification: RemoteMessage.Notification = remoteMessage.getNotification()!!
//        val i = user.replace("[\\D]".toRegex(), "").toInt()
//        val intent = Intent(this, SplashScreenActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val pIntent: PendingIntent =
//            PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT)
//        val defSoundUri = Uri.parse("android.resource://" + getPackageName() + "/raw/notification")
//        val notification1 = OreoAndAboveNotification(this)
//        val builder: Notification.Builder =
//            notification1.getONotifications(title, body, pIntent, defSoundUri, icon)
//        var j = 0
//        if (i > 0) {
//            j = i
//        }
//        notification1.manager!!.notify(j, builder.build())
//    }
//
//    override fun onNewToken(s: String) {
//        super.onNewToken(s)
//        val user: FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()
//        if (user != null) {
//            updateToken(s)
//        }
//    }
//
//    private fun updateToken(tokenRefresh: String) {
//        val user: FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()
//        val ref: DatabaseReference = FirebaseDatabase.getInstance("https://friend-social-66b71-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Tokens")
//        val token = Token(tokenRefresh)
//        ref.child(user!!.getUid()).setValue(token)
//    }
//
//    @SuppressLint("ObsoleteSdkInt")
//    private fun isAppIsInBackground(context: Context): Boolean {
//        var isInBackground = true
//        val am: ActivityManager =
//            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
//            val runningProcesses: List<ActivityManager.RunningAppProcessInfo> = am.getRunningAppProcesses()
//            for (processInfo in runningProcesses) {
//                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                    for (activeProcess in processInfo.pkgList) {
//                        if (activeProcess == context.packageName) {
//                            isInBackground = false
//                        }
//                    }
//                }
//            }
//        } else {
//            val taskInfo: List<ActivityManager.RunningTaskInfo> = am.getRunningTasks(1)
//            val componentInfo: ComponentName = taskInfo[0].topActivity!!
//            if (componentInfo.getPackageName() == context.packageName) {
//                isInBackground = false
//            }
//        }
//        return isInBackground
//    }
//}