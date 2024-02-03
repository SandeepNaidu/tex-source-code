Setup configuration

IDE Used - Android Studio Electric Eel | 2022.1.1

Step 1 Create a Firebase project

1.  In the [Firebase console](https://console.firebase.google.com/) click Add project.
To add Firebase resources to an existing Google Cloud project, enter its project name or select it from the dropdown menu.

To create a new project, enter the desired project name. You can also optionally edit the project ID displayed below the project name.

2. If prompted, review and accept the Firebase terms.

3. Click Continue.

4. Click Create project

Step 2: Register your app with Firebase
To use Firebase in your Android app, you need to register your app with your Firebase project.
Registering your app is often called "adding" your app to your project.

1. Go to the Firebase console.

2. In the center of the project overview page, click the Android icon (plat_android) or Add app to launch the setup workflow.

3. Enter your app's package name in the Android package name field.

4. Enter other app information: App nickname and Debug signing certificate SHA-1.

5. Click Register app.

Step 3: Add a Firebase configuration file

1. Download and then add the Firebase Android configuration file (google-services.json) to your app:

   Click Download google-services.json to obtain your Firebase Android config file.

   Move your config file into the module (app-level) root directory of your app and replace the existing with the new one.

Enable Authentication

Goto above created Firebase project
Open Build section and then Click on Authentication
Then goto Sign in methods
Enable all the methods


Enable realtime database

Goto above created Firebase project
Open Build section and then Click on Realtime database
click on enable button to enable the database
Copy the Bucket Url and paste/replace it in FirebaseDBManager class against BASE_URL variable.

Enable Crashlytics to record any crash logs

Goto above created Firebase project
Open Release and Monitor section and then Click on Crashlytics
Click on Enable to enable it.

Enable Storage

Goto above created Firebase project
Open Build section and then Click on Storage
Click on Enable to enable it.
Create Folders with following folder names:
1. post_photo
2. avid_cover
3. avid_video
4. post_audio
5. post_event
6. post_document
7. portfolio
8. post_thumb
9. user_profile
