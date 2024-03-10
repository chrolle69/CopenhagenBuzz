package dk.itu.moapd.copenhagenbuzz.lgul.models

import android.media.Image

/**
 * an event made by a user
 *
 * this data class contains nothing more than the attributes of an event
 *
 * @property eventName the name of the event
 * @property eventLocation the location of the event
 * @property eventDate the date that the event is happening
 * @property eventType the type of event
 * @property eventDescription the description of the event
 */
data class Event (
    var eventName: String,
    var eventLocation: String,
    var eventDate: String,
    var eventType: String,
    var eventDescription: String,
)

