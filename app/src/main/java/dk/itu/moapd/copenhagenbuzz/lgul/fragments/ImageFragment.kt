
package dk.itu.moapd.copenhagenbuzz.lgul.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import dk.itu.moapd.copenhagenbuzz.lgul.databinding.FragmentImageBinding
import dk.itu.moapd.copenhagenbuzz.lgul.R


class ImageFragment : Fragment() {


    private var _binding: FragmentImageBinding? = null

    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentImageBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the listener for back button.
        binding.buttonBack.setOnClickListener {
            requireActivity().findNavController(R.id.fragment_container_view).popBackStack()
        }

        // Showing the last taken image.
        arguments?.getString("ARG_IMAGE")?.let { uri ->
            binding.imageView.setImageURI(Uri.parse(uri))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}