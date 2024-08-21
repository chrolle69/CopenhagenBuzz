package dk.itu.moapd.copenhagenbuzz.lgul.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import dk.itu.moapd.copenhagenbuzz.lgul.databinding.FavoriteRowItemBinding
import dk.itu.moapd.copenhagenbuzz.lgul.models.Event
import io.github.cdimascio.dotenv.dotenv

class FavoriteAdapter(private var listener: OnItemClickListener,
                        options: FirebaseRecyclerOptions<Event>
    ) : FirebaseRecyclerAdapter<Event, FavoriteAdapter.ViewHolder>(options) {
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env" // instead of '.env', use 'env'
    }
    private val DATABASE_URL = dotenv["DATABASE_URL"]
    class ViewHolder(private val binding: FavoriteRowItemBinding): RecyclerView.ViewHolder(binding.root) {
            private val eventNameView: TextView = binding.textViewTitleFavorite
            private val eventTypeView: TextView = binding.textViewTypeFavorite
            private val eventImageView: ImageView = binding.favoriteItemImage

            fun bind(event: Event){
                with(binding) {
                    Log.d("favorite event", event.eventName.toString())
                    eventNameView.text        = event.eventName
                    eventTypeView.text        = event.eventType
                    FirebaseStorage.getInstance().reference.child(event.id).downloadUrl.addOnCompleteListener{ result ->
                        Log.d("myUri", result.result.toString())
                        Picasso.get().load(result.result).into(eventImageView);
                    }
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = FavoriteRowItemBinding
        .inflate(LayoutInflater.from(parent.context), parent, false)
        .let(::ViewHolder)

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Event) {
        model.let(holder::bind)

        holder.itemView.setOnLongClickListener(View.OnLongClickListener() {
            Log.d("myEvent", model.toString())
            Firebase.database(DATABASE_URL).reference
                .child("events")
                .child(model.id)
                .removeValue()
            true
        })
    }

}