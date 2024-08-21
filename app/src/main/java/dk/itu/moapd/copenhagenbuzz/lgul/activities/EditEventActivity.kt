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

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dk.itu.moapd.copenhagenbuzz.lgul.R
import dk.itu.moapd.copenhagenbuzz.lgul.databinding.ActivityEditEventBinding
import dk.itu.moapd.copenhagenbuzz.lgul.models.Event
import dk.itu.moapd.copenhagenbuzz.lgul.models.EventLocation
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.UUID


/**
 * general ui of CopenhagenBuzz.
 *
 * this class is an activity containing a form for creating an event.
 *
 * @property eventName the xml component from content_main.xml that takes a event name
 * @property eventLocation the xml component from content_main.xml that takes a event location
 * @property eventDate the xml component from content_main.xml that takes a date for when the event is happening
 * @property eventType the xml component from content_main.xml that takes a event type
 * @property eventDescription the xml component from content_main.xml that takes a event description
 */
class EditEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditEventBinding
    companion object {
        private val TAG = MainActivity::class.qualifiedName
    }
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env" // instead of '.env', use 'env'
    }
    private val DATABASE_URL = dotenv["DATABASE_URL"]

    private var chosenDate: Date = java.sql.Date.valueOf(LocalDate.now().toString())
    private var dateChanged: Boolean = false
    private lateinit var eventName: TextInputLayout
    private lateinit var eventLocation: TextInputLayout
    private lateinit var eventDate: TextInputLayout
    private lateinit var eventType: TextInputLayout
    private lateinit var eventDescription: TextInputLayout
    private lateinit var takePictureButton: Button
    private lateinit var choosePictureButton: Button
    private var isEditing: Boolean = false
    private lateinit var queue: RequestQueue
    private var imageUri: Uri? = null

    private val event: Event = Event("","","", EventLocation("N/A", 0.0, 0.0),0,"", "", emptyMap())


    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        queue = Volley.newRequestQueue(this).apply {
            start()
        }

        isEditing = intent.getBooleanExtra("isEditing", false)

        binding = ActivityEditEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventName = binding.textFieldEventName
        val eventLocation = binding.textFieldEventLocation
        val eventDate = binding.textFieldEventDate
        val eventType = binding.dropdownMenuEventType
        val eventDescription = binding.textFieldEventDescription
        val calendarContainer = binding.calendarContainerNewEvent
        val chooseDateButton = binding.buttonChooseDate
        val confirmButton = binding.confirmButton
        val deleteButton = binding.deleteButton
        val cancelButton = binding.cancelButton
        takePictureButton = binding.takePictureButton
        choosePictureButton = binding.choosePictureButton

        //get current event if editing
        if (isEditing) {
            intent.getStringExtra("eventId")?.let {
                val formatter = SimpleDateFormat("dd/MM-yyyy")

                    Firebase.database(DATABASE_URL).reference.child("events").child(it).get().addOnSuccessListener() { res ->
                    eventName.editText?.setText(res.child("eventName").value.toString())
                    eventLocation.editText?.setText(res.child("eventLocation").child("address").value.toString())
                    eventDate.editText?.setText(
                        formatter.format(
                            Date(
                                res.child("eventDate").value.toString().toLong()
                            )
                        )
                    )
                    chosenDate = Date(res.child("eventDate").value.toString().toLong())
                    dateChanged = true
                    eventType.editText?.setText(res.child("eventType").value.toString())
                    eventDescription.editText?.setText(res.child("eventDescription").value.toString())
                    eventType.editText?.setText(res.child("eventType").value.toString())
                    Log.d("refactor", "2")

                    FirebaseStorage.getInstance().reference.child(it).downloadUrl.addOnSuccessListener { uri ->
                        imageUri = uri
                        Log.d("refactor", "3")
                    }
                }
            }
            confirmButton.visibility = View.VISIBLE
            deleteButton.visibility = View.VISIBLE

            binding.fabAddEvent.visibility = View.INVISIBLE
        } else {
            confirmButton.visibility = View.INVISIBLE
            deleteButton.visibility = View.INVISIBLE

            binding.fabAddEvent.visibility = View.VISIBLE
        }

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                imageUri = uri
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
        choosePictureButton.setOnClickListener{
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        takePictureButton.setOnClickListener{
            goToTakePictureActivity()
        }
        eventDate.editText?.setOnClickListener {
            binding.calendarViewNewEvent.date = java.util.Date().time
            calendarContainer.visibility = View.VISIBLE
        }
        binding.calendarViewNewEvent.setOnDateChangeListener{ view, year, month, dayOfMonth ->
            val localDate: LocalDate = LocalDate.of(year, month+1, dayOfMonth)
            chosenDate = java.sql.Date.valueOf(localDate.toString())
            dateChanged = true
        }
        chooseDateButton.setOnClickListener {
            eventDate.editText?.setText(chosenDate.toString())
            calendarContainer.visibility = View.GONE
        }
        cancelButton.setOnClickListener {
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }.let(::startActivity)
        }
        deleteButton.setOnClickListener {
            intent.getStringExtra("eventId")?.let {
                Firebase.database(DATABASE_URL).reference
                    .child("events")
                    .child(it)
                    .removeValue()
                Firebase.database(DATABASE_URL).reference
                    .child("favorites")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child(it)
                    .removeValue()
                FirebaseStorage.getInstance().reference.child(it).delete()

                Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }.let(::startActivity)
            }
        }
        binding.fabAddEvent.setOnClickListener {
            if (eventName.editText?.text.toString().isNotEmpty() &&
                eventLocation.editText?.text.toString().isNotEmpty() &&
                eventDate.editText?.text.toString().isNotEmpty() &&
                eventType.editText?.text.toString().isNotEmpty() &&
                eventDescription.editText?.text.toString().isNotEmpty() &&
                imageUri != null) {
                    event.id = UUID.randomUUID().toString()
                    event.userId = FirebaseAuth.getInstance().currentUser!!.uid
                    event.eventName = eventName.editText?.text.toString().trim()
                    event.eventDate = chosenDate.time
                    event.eventType = eventType.editText?.text.toString().trim()
                    event.eventDescription = eventDescription.editText?.text.toString().trim()
                    if (imageUri != null && !isEditing) {
                        FirebaseStorage.getInstance().reference.child(event.id).putFile(imageUri!!);
                        Thread.sleep(500)
                    }
                    getLocation(eventLocation.editText?.text.toString(), { eventLocation ->
                        event.eventLocation = eventLocation
                        Log.d("final", event.eventLocation!!.address!!)
                        addEvent()
                        returnToMainActivity()
                    },{ errorString ->
                        alertView(errorString, "Event could not be created")
                        Log.d("Geo api error", errorString)
                    })
                } else {
                    alertView("must fill all fields and choose image", "Event could not be created")
                }
            }
        confirmButton.setOnClickListener {
            val name = eventName.editText?.text.toString().trim()
            val type = eventType.editText?.text.toString().trim()
            val description = eventDescription.editText?.text.toString().trim()
            val location = eventLocation.editText?.text.toString()
            if (isEditing) {
                intent.getStringExtra("eventId")?.let {
                    getLocation(location, { eventLocation ->
                        val userUid = FirebaseAuth.getInstance().currentUser!!.uid
                        //change values in Firebase realtime db
                        val eventsRef = Firebase.database(DATABASE_URL).reference
                            .child("events")
                            .child(it)
                        val favoritesRef = Firebase.database(DATABASE_URL).reference
                            .child("favorites")
                            .child(userUid)
                            .child(it)
                        val map = HashMap<String, Any>()
                        map["eventName"] = name
                        map["eventDate"] = chosenDate.time
                        map["eventType"] = type
                        map["eventDescription"] = description
                        map["eventLocation"] = eventLocation
                        eventsRef.updateChildren(map)
                        favoritesRef.get().addOnCompleteListener { response ->
                            if (response.result.hasChildren()) {
                                favoritesRef.updateChildren(map)
                            }
                            //change photo in firebase storage
                            FirebaseStorage.getInstance().reference.child(it).putFile(imageUri!!)
                            //return
                            returnToMainActivity()
                        }

                    }, { errorString ->
                        alertView(errorString, "Event could not be created")
                    })
                }
            }
        }
    }
    private fun addEvent() {
        Snackbar.make(binding.root, event.toString(), Snackbar.LENGTH_SHORT).show()
        Firebase.database(DATABASE_URL).reference
            .child("events")
            .child(event.id)
            .setValue(event)
    }
    private fun getLocation(search: String, successCallback: (EventLocation) -> Unit, errorCallback: (String) -> Unit) {
        Thread.sleep(1300)
        var newSearch = search.trim()
        newSearch = newSearch.replace(Regex(" +"), "+")
        val url = "https://geocode.maps.co/search?q=${newSearch}&api_key=${getString(R.string.geocode_api_key)}"
        Log.d("URL", url)
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                if (response.isNull(0)) {
                    errorCallback("no search results")
                } else {
                    Log.d("Location Success", response.toString())
                    val chosenAddress = response.getJSONObject(0)
                    Log.d("JSON OBJECT", chosenAddress.get("place_id").toString())
                    val addressString = displayNameToString(chosenAddress.getString("display_name"))
                    val eventLocation = EventLocation(
                        addressString,
                        chosenAddress.getString("lon").toDouble(),
                        chosenAddress.getString("lat").toDouble()
                    )
                    successCallback(eventLocation)

                }
            },
            { error ->
                Log.d("Location Failure", error.message.toString())
                    errorCallback(error.message.toString())
            })
        queue.add(request)
    }
    private fun displayNameToString(displayName: String): String {
        val lst = displayName.split(",")
        return "${lst[1]}  ${lst[0]}\n${lst[lst.size - 2]} ${lst[lst.size - 1]}"
    }
    private fun alertView(message: String, title: String) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok",
                DialogInterface.OnClickListener { dialoginterface, i -> }).show()
    }
    private fun returnToMainActivity() {
        Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.let(::startActivity)
    }
    private fun goToTakePictureActivity() {
/*
        Intent(this, TakePictureActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.let(::startActivity)
*/

        val intent = Intent(this,
            TakePictureActivity::class.java).apply {
            putExtra("eventId", intent.getStringExtra("eventId"))
            putExtra("isEditing", intent.getBooleanExtra("isEditing", false))
        }
        startActivity(intent)
        finish()
    }
}