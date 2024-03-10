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

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import dk.itu.moapd.copenhagenbuzz.lgul.databinding.ActivityAddEventBinding
import dk.itu.moapd.copenhagenbuzz.lgul.models.Event

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
class AddEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEventBinding


    companion object {
        private val TAG = MainActivity::class.qualifiedName
    }

    private lateinit var eventName: TextInputLayout
    private lateinit var eventLocation: TextInputLayout
    private lateinit var eventDate: TextInputLayout
    private lateinit var eventType: TextInputLayout
    private lateinit var eventDescription: TextInputLayout
    private lateinit var fabAddEvent: FloatingActionButton
    private val event: Event = Event("","","","","")

    /**
     * this method inflates the activities and creates listeners
     *
     * @param savedInstanceState a lazy container
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityAddEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventName = binding.textFieldEventName
        eventLocation = binding.textFieldEventLocation
        eventDate = binding.textFieldEventDate
        eventType = binding.dropdownMenuEventType
        eventDescription = binding.textFieldEventDescription
        fabAddEvent = binding.fabAddEvent


        fabAddEvent.setOnClickListener {
            if (eventName.editText?.text.toString().isNotEmpty() &&
                eventLocation.editText?.text.toString().isNotEmpty() &&
                eventDate.editText?.text.toString().isNotEmpty() &&
                eventType.editText?.text.toString().isNotEmpty() &&
                eventDescription.editText?.text.toString().isNotEmpty()) {

                event.eventName = eventName.editText?.text.toString().trim()
                event.eventLocation = eventLocation.editText?.text.toString().trim()
                event.eventDate = eventDate.editText?.text.toString().trim()
                event.eventType = eventType.editText?.text.toString().trim()
                event.eventDescription = eventDescription.editText?.text.toString().trim()

                showMessage()
            }
        }

    }

    /**
     * this method shows the currently made event
     */
    private fun showMessage() {
        Snackbar.make(binding.root, event.toString(), Snackbar.LENGTH_SHORT).show()
    }
}