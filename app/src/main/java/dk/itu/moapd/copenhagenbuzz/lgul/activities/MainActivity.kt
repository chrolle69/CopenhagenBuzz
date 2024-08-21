/*
 * MIT License
 *
 * Copyright (c) 2024 Lucas Roy Guldbrandsen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dk.itu.moapd.copenhagenbuzz.lgul.activities

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark
import com.google.firebase.storage.FirebaseStorage
import dk.itu.moapd.copenhagenbuzz.lgul.R
import dk.itu.moapd.copenhagenbuzz.lgul.SharedPreferenceUtil
import dk.itu.moapd.copenhagenbuzz.lgul.databinding.ActivityMainBinding
import dk.itu.moapd.copenhagenbuzz.lgul.fragments.FaceAuthDialogFragment
import dk.itu.moapd.copenhagenbuzz.lgul.fragments.UserInfoDialogFragment
import java.io.IOException


/**
 * general ui of CopenhagenBuzz.
 *
 * this class is used for UI components such as navigation and
 * other components that should be usable within other activities.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var topAppBar: MaterialToolbar
    private var newFace: Bitmap? = null
    private lateinit var sharedPreferences: SharedPreferences

    //
    companion object {
        private val TAG = MainActivity::class.qualifiedName
    }

    /**
     * this method inflates the activities and creates listeners
     *
     * @param savedInstanceState a lazy container used when the activity is destroyed but
     * some data should be saved for the new creation
     */
    override fun onStart() {
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        topAppBar = binding.topAppBar
        auth = FirebaseAuth.getInstance()

        sharedPreferences = this
            .getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        setContentView(binding.root)
        topAppBar.bringToFront()
        //logged in //logged in
        if (auth.currentUser != null) {
            //auth face
            sharedPreferences.getBoolean(SharedPreferenceUtil.KEY_AUTH_ENABLED, false)
                .let { enabled ->
                    if (enabled) {
                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        resultLauncher.launch(cameraIntent)
                    }
                }
            //info button
            topAppBar.menu.getItem(0).setVisible(true)
            //logout button
            Log.d("logged in", auth.currentUser.toString())
            topAppBar.menu.getItem(1).setVisible(true)
            //login button
            topAppBar.menu.getItem(2).setVisible(false)
            //new event button
            topAppBar.menu.getItem(3).setVisible(true)
            //face auth
            topAppBar.menu.getItem(4).setVisible(true)
            //bottom nav favorites
            binding.bottomNavigation.menu.getItem(1).setVisible(true)

        } else { //logged out
            //Info button
            Log.d("logged out", auth.currentUser.toString())
            topAppBar.menu.getItem(0).setVisible(false)
            //logout button
            topAppBar.menu.getItem(1).setVisible(false)
            //Login button
            topAppBar.menu.getItem(2).setVisible(true)
            //new event button
            topAppBar.menu.getItem(3).setVisible(false)
            //face auth
            topAppBar.menu.getItem(4).setVisible(false)
            //bottom nav favorites
            binding.bottomNavigation.menu.getItem(1).setVisible(false)

        }



        topAppBar.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.login -> {
                    startLoginActivity()
                    true
                }
                R.id.logout -> {
                    auth.signOut()
                    Intent(this, MainActivity::class.java).apply {
                        // An alternative to instead of calling finish() method.
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }.let(::startActivity)
                    true
                }
                R.id.action_user_info -> {
                    UserInfoDialogFragment().apply {
                        isCancelable = false
                    }.also { dialogFragment ->
                        dialogFragment.show(supportFragmentManager,
                            "UserInfoDialogFragment")
                    }
                    true
                }
                R.id.new_event -> {
                    val intent = Intent(this,
                        EditEventActivity::class.java).apply {
                        putExtra("eventId", "0")
                        putExtra("isEditing", false)
                    }
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.face_auth -> {
                    FaceAuthDialogFragment().apply {
                        isCancelable = false
                    }.also { dialogFragment ->
                        dialogFragment.show(supportFragmentManager,
                            "FaceAuthDialogFragment")
                    }
                    true
                }
                else -> false
            }
        }


        val navHostFragment = supportFragmentManager
            .findFragmentById(
                R.id.fragment_container_view
            ) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
    }

    // Get your image
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d("Final img", "result")

            if (result.resultCode == Activity.RESULT_OK) {
                if (result?.data != null) {
                    val imgUri = result.data!!.data
                    val img: Any? = result.data!!.extras!!.get("data")
                    val bitmap = img as Bitmap
                    newFace = bitmap

                    //compare
                    if (newFace != null) {
                        val newFaceImage: FirebaseVisionImage
                        val authFaceImage: FirebaseVisionImage
                        try {
                            newFaceImage = FirebaseVisionImage.fromBitmap(newFace!!)

                            val highAccuracyOpts = FirebaseVisionFaceDetectorOptions.Builder()
                                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                                .build()

                            val detector = FirebaseVision.getInstance()
                                .getVisionFaceDetector(highAccuracyOpts)

                            detector.detectInImage(newFaceImage).addOnSuccessListener { new ->
                                if (new.size == 1) {
                                    val newFace = new[0]
                                    if (validateFace(newFace)) {
                                        Log.d("compare", "DONE")
                                    } else {
                                        alertFailTryAgain("Login Failed","Incorrect face")
                                    }
                                } else {
                                    alertFailTryAgain("Login Failed","Face authentication failed. Image is unclear please try again")
                                }

                            }.addOnFailureListener { e ->
                                Log.d("Face recognition", "detector failed on - newFace")
                                alertFailTryAgain("Login Failed","Face authentication failed. Face detector may not be able to detect the inputted face")
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                            alertFailTryAgain("Login Failed","Face authentication failed")
                        }

                    }
                }
            }
        }


    private fun validateFace(face: FirebaseVisionFace): Boolean {
        val eyeLopenFactor = face.leftEyeOpenProbability
        val eyeRopenFactor = face.rightEyeOpenProbability
        val smileFactor = face.smilingProbability

        Log.d("Factor", eyeLopenFactor.toString())
        Log.d("Factor", eyeRopenFactor.toString())
        Log.d("Factor", smileFactor.toString())

        val eyeLShouldBeOpen = sharedPreferences.getBoolean(SharedPreferenceUtil.KEY_LEFT_EYE_AUTH, false)
        val eyeRShouldBeOpen = sharedPreferences.getBoolean(SharedPreferenceUtil.KEY_RIGHT_EYE_AUTH, false)
        val mouthShouldBeSmiling = sharedPreferences.getBoolean(SharedPreferenceUtil.KEY_MOUTH_AUTH, false)

        Log.d("Factor", eyeLShouldBeOpen.toString())
        Log.d("Factor", eyeRShouldBeOpen.toString())
        Log.d("Factor", mouthShouldBeSmiling.toString())

        //left eye should be open
        if (eyeLShouldBeOpen &&
            eyeLopenFactor < 0.5) {
            return false
        }
        //left eye should not be open
        if (!eyeLShouldBeOpen &&
            eyeLopenFactor > 0.5) {
            return false
        }

        //right eye should be open
        if (eyeRShouldBeOpen &&
            eyeRopenFactor < 0.5) {
            return false
        }
        //right eye should not be open
        if (!eyeRShouldBeOpen &&
            eyeRopenFactor > 0.5) {
            return false
        }

        //mouth should be smiling
        if (mouthShouldBeSmiling &&
            smileFactor < 0.5) {
            return false
        }
        //mouth should not be smiling
        if (!mouthShouldBeSmiling &&
            smileFactor > 0.5) {
            return false
        }
        return true
    }


    private fun startLoginActivity() {
        Intent(this, FirebaseLoginActivity::class.java).apply {
            // An alternative to instead of calling finish() method.
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.let(::startActivity)
    }

    private fun alertFailTryAgain(title: String, message: String) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(title)
            .setMessage(message)
            .setNegativeButton("Enter without signing in",
                DialogInterface.OnClickListener { dialoginterface, i ->
                    logout()
                })
            .setPositiveButton("Try again",
                DialogInterface.OnClickListener { dialoginterface, i ->
                    Intent(this, MainActivity::class.java).apply {
                        // An alternative to instead of calling finish() method.
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }.let(::startActivity)
                })
            .setCancelable(false)
            .show()
    }
    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        Intent(this, MainActivity::class.java).apply {
            // An alternative to instead of calling finish() method.
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.let(::startActivity)
    }
}