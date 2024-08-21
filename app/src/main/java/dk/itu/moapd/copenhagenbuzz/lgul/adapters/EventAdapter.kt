package dk.itu.moapd.copenhagenbuzz.lgul.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.storage.FirebaseStorage
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import dk.itu.moapd.copenhagenbuzz.lgul.R
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import dk.itu.moapd.copenhagenbuzz.lgul.activities.EditEventActivity
import dk.itu.moapd.copenhagenbuzz.lgul.fragments.TimelineFragment
import dk.itu.moapd.copenhagenbuzz.lgul.models.Event
import java.text.SimpleDateFormat
import java.util.Date
import io.github.cdimascio.dotenv.dotenv



class EventAdapter(
    private val timelineFragment: TimelineFragment,
    options: FirebaseListOptions<Event>
): FirebaseListAdapter<Event>(options) {
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env" // instead of '.env', use 'env'
    }
    private val DATABASE_URL = dotenv["DATABASE_URL"]

    @SuppressLint("SimpleDateFormat")
    override fun populateView(v: View, model: Event, position: Int) {
        val formatter = SimpleDateFormat("dd/MM-yyyy HH:mm")
        Log.d("model", model.toString())
        val viewHolder = (v.tag as? ViewHolder) ?: ViewHolder(v)
        with(viewHolder) {
            eventNameView.text        = model.eventName
            eventLocationView.text    = model.eventLocation?.address
            eventDateView.text        = formatter.format(Date(model.eventDate!!))
            eventTypeView.text        = model.eventType
            eventDescriptionView.text = model.eventDescription
            FirebaseStorage.getInstance().reference.child(model.id).downloadUrl.addOnCompleteListener{ result ->
                Log.d("myUri", result.result.toString())
                Picasso.get().load(result.result).into(eventImageView);
            }

            val user = FirebaseAuth.getInstance().currentUser
            //check if the event item should have a favorite symbol
            Log.d("favorites", model.favorites.toString())
            if (user !== null) {

            Log.d("user", user.uid)
                if (model.favorites!!.containsKey(user.uid)) {
                    Log.d("contains", true.toString())
                    //favorites contains this event
                    favIconOn.visibility = View.VISIBLE
                    favIconOff.visibility = View.INVISIBLE
                    //if favorites exits but is not containing this event
                } else {
                    favIconOn.visibility = View.INVISIBLE
                    favIconOff.visibility = View.VISIBLE
                }
                //check if i own the event
                if (model.userId == user.uid) {
                    editButtonContainer.visibility = View.VISIBLE
                    editButtonContainer.focusable = View.NOT_FOCUSABLE
                    editButton.focusable = View.NOT_FOCUSABLE
                } else {
                    editButtonContainer.visibility = View.INVISIBLE
                    editButtonContainer.focusable = View.NOT_FOCUSABLE
                    editButton.focusable = View.NOT_FOCUSABLE
                }
                favIconOn.setOnClickListener {
                    favIconOn.visibility = View.INVISIBLE
                    favIconOff.visibility = View.VISIBLE

                    Firebase.database(DATABASE_URL).reference
                        .child("events")
                        .child(model.id)
                        .child("favorites")
                        .child(user.uid)
                        .removeValue()
                    Firebase.database(DATABASE_URL).reference
                        .child("favorites")
                        .child(user.uid)
                        .child(model.id)
                        .removeValue()
                }
                favIconOff.setOnClickListener {
                    favIconOn.visibility = View.VISIBLE
                    favIconOff.visibility = View.INVISIBLE

                    Firebase.database(DATABASE_URL).reference
                        .child("events")
                        .child(model.id)
                        .child("favorites")
                        .child(user.uid)
                        .setValue(user.uid)
                    Firebase.database(DATABASE_URL).reference
                        .child("favorites")
                        .child(user.uid)
                        .child(model.id)
                        .setValue(model)
                }
                //if user not logged in
            } else {
                favIconOn.visibility = View.INVISIBLE
                favIconOff.visibility = View.INVISIBLE
            }
             editButton.setOnClickListener( View.OnClickListener() {
                 val intent = Intent(timelineFragment.requireActivity(),
                     EditEventActivity::class.java).apply {
                         putExtra("eventId", model.id)
                         putExtra("isEditing", true)
                 }
                 timelineFragment.requireActivity().startActivity(intent)
                 timelineFragment.requireActivity().finish()
            })
        }

    }
     override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        //populate old view
        val view = convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.event_row_item, parent, false)
        val viewHolder = (view.tag as? ViewHolder) ?: ViewHolder(view)
        val event = getItem(position)
        populateView(view, event, position)

        view.tag = viewHolder
        return view
    }

    class ViewHolder(view: View){
        val eventNameView: TextView          = view.findViewById(R.id.event_item_name)
        val eventLocationView: TextView      = view.findViewById(R.id.event_item_location)
        val eventDateView: TextView          = view.findViewById(R.id.event_item_date)
        val eventTypeView: TextView          = view.findViewById(R.id.event_item_type)
        val eventDescriptionView: TextView   = view.findViewById(R.id.event_item_description)
        val eventImageView: ImageView        = view.findViewById(R.id.event_item_image)
        val favIconOn: Button                = view.findViewById(R.id.icon_button_fav_events_on)
        val favIconOff: Button               = view.findViewById(R.id.icon_button_fav_events_off)
        val editButton: Button               = view.findViewById(R.id.edit_event_button)
        val editButtonContainer: FrameLayout = view.findViewById(R.id.edit_button_container)


    }

}