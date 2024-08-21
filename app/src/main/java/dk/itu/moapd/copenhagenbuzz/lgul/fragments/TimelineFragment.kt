package dk.itu.moapd.copenhagenbuzz.lgul.fragments

import android.app.DownloadManager.Query
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import androidx.fragment.app.activityViewModels
import com.firebase.ui.database.FirebaseListOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.copenhagenbuzz.lgul.R
import dk.itu.moapd.copenhagenbuzz.lgul.adapters.EventAdapter
import dk.itu.moapd.copenhagenbuzz.lgul.models.DataViewModel
import dk.itu.moapd.copenhagenbuzz.lgul.models.Event
import io.github.cdimascio.dotenv.dotenv
import java.util.Objects


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TimelineFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TimelineFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var currentDataQuery: com.google.firebase.database.Query
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env" // instead of '.env', use 'env'
    }
    private val DATABASE_URL = dotenv["DATABASE_URL"]
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: View
    private lateinit var adapter: EventAdapter

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
        binding = inflater.inflate(R.layout.fragment_timeline, container, false)


        /*
            if (dataViewModel.events.value != null &&
                dataViewModel.favorites.value != null) {
                    adapter = EventAdapter(dataViewModel.events.value!!, dataViewModel.favorites.value!!)
            }
            dataViewModel.events.observe(requireActivity()) {
                adapter.submitList(it)
            }
        */

        Log.d("FireDebug", "BEFORE query")
        val query = Firebase.database(DATABASE_URL).reference
            .child("events")
            .orderByChild("eventDate")
        Log.d("FireDebug", "query")

        currentDataQuery = query


        val options = FirebaseListOptions.Builder<Event>()
            .setLayout(R.layout.event_row_item)
            .setQuery(query, Event::class.java)
            .setLifecycleOwner(this)
            .build()
        Log.d("FireDebug", "options")


        adapter = EventAdapter(this, options)
        Log.d("FireDebug", "adapter")


        val listView: ListView = binding.findViewById(R.id.list_view_timeline)
        listView.adapter = adapter

        return binding
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TimelineFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TimelineFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}