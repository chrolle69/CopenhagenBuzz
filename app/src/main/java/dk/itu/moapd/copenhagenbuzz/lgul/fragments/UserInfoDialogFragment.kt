package dk.itu.moapd.copenhagenbuzz.lgul.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import dk.itu.moapd.copenhagenbuzz.lgul.databinding.FragmentUserInfoDialogBinding
import dk.itu.moapd.copenhagenbuzz.lgul.R


class UserInfoDialogFragment : DialogFragment() {

    private var _binding: FragmentUserInfoDialogBinding? = null

    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        // Inflate the view using view binding.
        _binding = FragmentUserInfoDialogBinding.inflate(layoutInflater)

        // Get the current user from Firebase Authentication.
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Populate the dialog view with user information.
        currentUser?.let { user ->
            with(binding) {
                textViewName.text = user.displayName ?: getString(R.string.unknown_user)
                textViewEmail.text = user.email ?: user.phoneNumber
                user.photoUrl?.let { url ->
                    imageViewPhoto?.let {
                        it.imageTintMode = null
                        Picasso.get().load(url).into(it)
                    }
                }
            }
        }

        // Create and return a new instance of MaterialAlertDialogBuilder.
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.user_info_title)
            .setView(binding.root)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}