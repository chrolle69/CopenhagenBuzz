package dk.itu.moapd.copenhagenbuzz.lgul.fragments

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import dk.itu.moapd.copenhagenbuzz.lgul.databinding.FragmentUserInfoDialogBinding
import dk.itu.moapd.copenhagenbuzz.lgul.R
import dk.itu.moapd.copenhagenbuzz.lgul.SharedPreferenceUtil
import dk.itu.moapd.copenhagenbuzz.lgul.databinding.FragmentFaceAuthDialogBinding



class FaceAuthDialogFragment : DialogFragment() {

    private var _binding: FragmentFaceAuthDialogBinding? = null
    private var imageUri: Uri? = null
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var sharedPreferences: SharedPreferences



    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        // Inflate the view using view binding.
        _binding = FragmentFaceAuthDialogBinding.inflate(layoutInflater)
        // Get the SharedPreferences instance.
        sharedPreferences = requireActivity()
            .getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)


        with(binding){
            leftEyeAuth.isChecked = sharedPreferences.getBoolean(SharedPreferenceUtil.KEY_LEFT_EYE_AUTH, false)
            rightEyeAuth.isChecked = sharedPreferences.getBoolean(SharedPreferenceUtil.KEY_RIGHT_EYE_AUTH, false)
            mouthAuth.isChecked = sharedPreferences.getBoolean(SharedPreferenceUtil.KEY_MOUTH_AUTH, false)

            sharedPreferences.getBoolean(SharedPreferenceUtil.KEY_AUTH_ENABLED, false)
                .let { enabled ->
                    if (enabled) {
                        authSwitch.isChecked = true
                        happy()
                    } else {
                        authSwitch.isChecked = false
                        sad()
                    }
                }
            authSwitch.setOnClickListener{
                Log.d("onCLick", authSwitch.isChecked.toString())
                if (authSwitch.isChecked) {
                    SharedPreferenceUtil.saveFaceAuth(requireActivity(), true)
                    happy()
                } else {
                    SharedPreferenceUtil.saveFaceAuth(requireActivity(), false)
                    sad()
                }
            }
            leftEyeAuth.setOnClickListener{
                if (leftEyeAuth.isChecked){
                    SharedPreferenceUtil.saveLeftEyeAuth(requireActivity(), true)
                } else {
                    SharedPreferenceUtil.saveLeftEyeAuth(requireActivity(), false)
                }
                Log.d("prefff", sharedPreferences.getBoolean(SharedPreferenceUtil.KEY_LEFT_EYE_AUTH, false).toString())
            }
            rightEyeAuth.setOnClickListener{
                if (rightEyeAuth.isChecked){
                    SharedPreferenceUtil.saveRightEyeAuth(requireActivity(), true)
                } else {
                    SharedPreferenceUtil.saveRightEyeAuth(requireActivity(), false)
                }
                Log.d("prefff", sharedPreferences.getBoolean(SharedPreferenceUtil.KEY_RIGHT_EYE_AUTH, false).toString())
            }
            mouthAuth.setOnClickListener{
                if (mouthAuth.isChecked){
                    SharedPreferenceUtil.saveMouthAuth(requireActivity(), true)
                } else {
                    SharedPreferenceUtil.saveMouthAuth(requireActivity(), false)
                }
                Log.d("prefff", sharedPreferences.getBoolean(SharedPreferenceUtil.KEY_MOUTH_AUTH, false).toString())

            }
        }

        // Create and return a new instance of MaterialAlertDialogBuilder.
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.face_authentication)
            .setView(binding.root)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }
    private fun happy() {
        binding.happyContainer.visibility = View.VISIBLE
        binding.sadContainer.visibility = View.INVISIBLE

        binding.leftEyeAuth.isEnabled = true
        binding.rightEyeAuth.isEnabled = true
        binding.mouthAuth.isEnabled = true
    }
    private fun sad() {
        binding.happyContainer.visibility = View.INVISIBLE
        binding.sadContainer.visibility = View.VISIBLE

        binding.leftEyeAuth.isEnabled = false
        binding.rightEyeAuth.isEnabled = false
        binding.mouthAuth.isEnabled = false
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}