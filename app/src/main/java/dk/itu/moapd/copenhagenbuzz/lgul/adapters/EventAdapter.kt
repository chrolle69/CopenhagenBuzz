package dk.itu.moapd.copenhagenbuzz.lgul.adapters

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import dk.itu.moapd.copenhagenbuzz.lgul.models.DummyModel
import dk.itu.moapd.copenhagenbuzz.lgul.R




class EventAdapter(
    private val events: List<DummyModel>?,
    private val favorites: List<DummyModel>?
): BaseAdapter() {
    override fun getCount(): Int {
        if (events == null) return 0
        return events.size
    }
    override fun getItem(position: Int): DummyModel? {
        if (events == null) return null
        return events[position]
    }
    override fun getItemId(position: Int): Long {
        TODO("Not yet implemented")
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        //populate old view
        val view = convertView ?: LayoutInflater.from(parent?.context).inflate(R.layout.event_row_item, parent, false)
        val viewHolder = (view.tag as? ViewHolder) ?: ViewHolder(view)

        getItem(position)?.let { dummy ->
            populateViewHolder(viewHolder, dummy)
        }
        view.tag = viewHolder
        return view
    }
    private fun populateViewHolder(viewHolder: ViewHolder, dummy: DummyModel){
        with(viewHolder) {
            eventNameView.text        = dummy.eventName
            eventLocationView.text    = dummy.eventLocation
            eventDateView.text        = dummy.eventDate
            eventTypeView.text        = dummy.eventType
            eventDescriptionView.text = dummy.eventDescription
            eventImageView.setImageResource(dummy.eventImage)
            Log.d("favorites", favorites?.size.toString())
            if (favorites != null) {
                if (favorites.contains(dummy)) {
                    //favorites contains this event
                    favIconOn.visibility = View.VISIBLE
                    favIconOff.visibility = View.INVISIBLE
                //if favorites exits but is not containing this event
                } else {
                    favIconOn.visibility = View.INVISIBLE
                    favIconOff.visibility = View.VISIBLE
                }
            //if no favorites is made
            } else {
                favIconOn.visibility = View.INVISIBLE
                favIconOff.visibility = View.VISIBLE
            }

        }
    }
    class ViewHolder(view: View){
        val eventNameView: TextView        = view.findViewById(R.id.event_item_name)
        val eventLocationView: TextView    = view.findViewById(R.id.event_item_location)
        val eventDateView: TextView        = view.findViewById(R.id.event_item_date)
        val eventTypeView: TextView        = view.findViewById(R.id.event_item_type)
        val eventDescriptionView: TextView = view.findViewById(R.id.event_item_description)
        val eventImageView: ImageView      = view.findViewById(R.id.event_item_image)
        val favIconOn: Button                = view.findViewById(R.id.icon_button_fav_events_on)
        val favIconOff: Button                = view.findViewById(R.id.icon_button_fav_events_off)

    }
}