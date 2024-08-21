package dk.itu.moapd.copenhagenbuzz.lgul.models

import android.net.Uri
import java.util.UUID

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
data class Event(
    var id: String = UUID.randomUUID().toString(),
    var userId: String ?= "N/A",
    var eventName: String ?= "N/A",
    var eventLocation: EventLocation ?= EventLocation("N/A", 0.0, 0.0),
    var eventDate: Long ?= 0,
    var eventType: String ?= "N/A",
    var eventDescription: String ?= "N/A",
    var favorites: Map<String, String> ?= emptyMap()
)

