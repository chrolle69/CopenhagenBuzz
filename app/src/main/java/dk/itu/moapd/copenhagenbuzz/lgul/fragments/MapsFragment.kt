
package dk.itu.moapd.copenhagenbuzz.lgul.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import dk.itu.moapd.copenhagenbuzz.lgul.R
import dk.itu.moapd.copenhagenbuzz.lgul.SharedPreferenceUtil
import dk.itu.moapd.copenhagenbuzz.lgul.databinding.FragmentMapsBinding
import dk.itu.moapd.copenhagenbuzz.lgul.models.Event
import dk.itu.moapd.copenhagenbuzz.lgul.myServices.LocationService
import io.github.cdimascio.dotenv.dotenv


class MapsFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {


    private inner class LocationBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            Log.d("Receiving", "1")
            val location = if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                intent.getParcelableExtra(LocationService.EXTRA_LOCATION, Location::class.java)
            else
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(LocationService.EXTRA_LOCATION)
            location?.let {
                Log.d("lat", it.latitude.toString())
                Log.d("lon", it.longitude.toString())
                updateLocationDetails(it)
            }
            if (!initPos) {
                Log.d("moving", currentLocation.toString())
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
                initPos = true
                locationService?.unsubscribeToLocationUpdates()
            }
        }


    }

    private var _binding: FragmentMapsBinding? = null


    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }


    companion object {
        private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
    }

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var locationBroadcastReceiver: LocationBroadcastReceiver
    private var locationService: LocationService? = null
    private var currentLocation: LatLng = LatLng(0.00,0.00)
    private var locationServiceBound = false
    private lateinit var googleMap: GoogleMap
    private var initPos = false
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env" // instead of '.env', use 'env'
    }
    private val DATABASE_URL = dotenv["DATABASE_URL"]


    private val serviceConnection = object : ServiceConnection {


        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationService.LocalBinder
            Log.d("binder-", binder.service.toString())
            locationService = binder.service
            locationServiceBound = true

            sharedPreferences.getBoolean(SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false)
                .let { enabled ->
                    if (enabled) {
                        resetLocationDetails()
                        locationService?.unsubscribeToLocationUpdates()
                    } else {
                        if (checkPermission()) {
                            locationService?.subscribeToLocationUpdates()
                        } else {
                            requestUserPermissions()
                        }
                    }
                }
        }


        override fun onServiceDisconnected(name: ComponentName) {
            Log.d("Disconnect-", name.toString())
            locationService = null
            locationServiceBound = false
        }
    }

    private val callback = OnMapReadyCallback { map ->
        googleMap = map

        // Add a marker in IT University of Copenhagen and move the camera.
        val itu = LatLng(55.6596, 12.5910)
        val userLocation = currentLocation
        Firebase.database(DATABASE_URL).reference
            .child("events").get().addOnCompleteListener {
                Log.d("event to map", it.result.value.toString())
                val gson = Gson()
                val events = it.result.children
                events.forEach {
                    val title = it.child("eventName").value
                    val lat = it.child("eventLocation")
                        .child("lat").value
                    val lon = it.child("eventLocation")
                        .child("lng").value
                    val evenLocation = LatLng(lat.toString().toDouble(), lon.toString().toDouble())
                    Log.d("event location", evenLocation.toString())
                    googleMap.addMarker(MarkerOptions().position(evenLocation).title(title.toString()))

                }
            }



        googleMap.addMarker(MarkerOptions().position(itu).title("IT University of Copenhagen"))
        // Move the Google Maps UI buttons under the OS top bar.
        googleMap.setPadding(0, 100, 0, 0)

        if (checkPermission()) {
            googleMap.isBuildingsEnabled = true
        } else {
            requestUserPermissions()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentMapsBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the SharedPreferences instance.
        sharedPreferences = requireActivity()
            .getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)


        // Initialize the broadcast receiver.
        locationBroadcastReceiver = LocationBroadcastReceiver()

        // Define the UI behavior using lambda expressions.
        Log.d("START VIEW", "1")

        val mapFragment = childFragmentManager
            .findFragmentById(binding.map.id) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }


    override fun onStart() {
        super.onStart()
        Log.d("Starting", "start")
        // Update the UI to reflect the state of the service.
        updateButtonState(
            sharedPreferences.getBoolean(SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false)
        )

        // Register the shared preference change listener.
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        Log.d("starting service" ,"1.1")
        // Bind to the service.
        Intent(requireContext(), LocationService::class.java).let { serviceIntent ->
            requireActivity().bindService(
                serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
        Log.d("service started" ,"1.2")

    }

    override fun onResume() {
        super.onResume()

        // Register the broadcast receiver.
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            locationBroadcastReceiver,
            IntentFilter(LocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
        )
    }


    override fun onPause() {
        // Unregister the broadcast receiver.
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(
            locationBroadcastReceiver
        )
        super.onPause()
    }


    override fun onStop() {
        // Unbind from the service.
        locationService?.unsubscribeToLocationUpdates()
        if (locationServiceBound) {
            requireActivity().unbindService(serviceConnection)
            locationServiceBound = false
        }
        // Unregister the shared preference change listener.
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onStop()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED


    private fun requestUserPermissions() {
        if (!checkPermission())
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        if (key == SharedPreferenceUtil.KEY_FOREGROUND_ENABLED)
            sharedPreferences.getBoolean(SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false)
                .let(::updateButtonState)
    }

    private fun updateButtonState(trackingLocation: Boolean) {
/*        val buttonText = if (trackingLocation) R.string.button_stop else R.string.button_start
        binding.buttonState.text = getString(buttonText)*/
    }

    @SuppressLint("StringFormatInvalid")
    private fun updateLocationDetails(location: Location) {
        with(binding) {
            // Fill the event details into the UI components.
            editTextLatitude.setText(location.latitude.toString())
            editTextLongitude.setText(location.longitude.toString())

        }
            currentLocation = LatLng(location.latitude, location.longitude)
    }

    private fun resetLocationDetails() {
        with(binding) {
            // Fill the event details into the UI components.
            editTextLatitude.setText(getString(R.string.text_not_available))
            editTextLongitude.setText(getString(R.string.text_not_available))


        }
    }

}