package dk.itu.moapd.copenhagenbuzz.lgul.fragments

import android.app.DownloadManager.Query
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.copenhagenbuzz.lgul.R
import dk.itu.moapd.copenhagenbuzz.lgul.adapters.FavoriteAdapter
import dk.itu.moapd.copenhagenbuzz.lgul.models.DataViewModel
import dk.itu.moapd.copenhagenbuzz.lgul.models.Event
import io.github.cdimascio.dotenv.dotenv


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private val dotenv = dotenv {
    directory = "/assets"
    filename = "env" // instead of '.env', use 'env'
}
private val DATABASE_URL = dotenv["DATABASE_URL"]
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavoritesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoritesFragment : Fragment(), OnItemClickListener {
    // TODO: Rename and change types of parameters
    private lateinit var auth: FirebaseAuth
    private lateinit var query: Any
    private lateinit var options: FirebaseRecyclerOptions<Event>
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: View

    private val dataViewModel: DataViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = inflater.inflate(R.layout.fragment_favorites, container, false)

        /*
        val data = dataViewModel.favorites.value

        data?.let {
            adapter = FavoriteAdapter(data)
        }
        dataViewModel.events.observe(requireActivity()) {
            adapter.submitList(it)
        }
        */

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser !== null) {
            Log.d("USER", auth.currentUser!!.uid)
            query = Firebase.database(DATABASE_URL).reference
                 .child("favorites")
                .child(auth.currentUser!!.uid)

            options = FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(query as com.google.firebase.database.Query, Event::class.java)
                .setLifecycleOwner(this)
                .build()
        } else {
            //no query <-> no favorites
            query = Firebase.database(DATABASE_URL).reference
                .child("events")
                .child("favorites")
                .child("")

            options = FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(query as DatabaseReference, Event::class.java)
                .setLifecycleOwner(this)
                .build()
        }

        val recyclerView: RecyclerView = binding.findViewById(R.id.recycler_view_favorites)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = FavoriteAdapter(this, options)
        recyclerView.adapter = adapter

        return binding
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavoritesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoritesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun onItemClick(event: Event, position: Int) {
        Log.d("position", position.toString())
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        TODO("Not yet implemented")
    }


}