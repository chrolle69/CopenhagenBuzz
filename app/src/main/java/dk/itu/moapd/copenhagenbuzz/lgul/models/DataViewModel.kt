package dk.itu.moapd.copenhagenbuzz.lgul.models

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.javafaker.Faker
import kotlinx.coroutines.launch
import java.util.Random
import dk.itu.moapd.copenhagenbuzz.lgul.R


class DataViewModel (private val savedStateHandle: SavedStateHandle) : ViewModel() {

    companion object {
        private const val EVENT_KEY = "CONT_KEY"
        private const val FAVORITE_KEY = "FAVORITE_KEY"
        private const val STATUS_KEY = "STATUS_KEY"
    }
    //remember to change DummyModel to Event
    public val events: MutableLiveData<List<Event>> by lazy {
        savedStateHandle.getLiveData(EVENT_KEY)
    }
    public val favorites: MutableLiveData<List<Event>> by lazy {
        savedStateHandle.getLiveData(FAVORITE_KEY)
    }
    init {
        fakeEventFetch()

        generateRandomFavorites(events.value)
    }



        fun fakeEventFetch() = viewModelScope.launch {
              val faker = Faker(Random(42))
              //remember to change DummyModel to Event
              val data = ArrayList<Event>()
              (1..50).forEach { it ->
                  val address = faker.address()
                  data.add(
                      //remember to change DummyModel to Event
                      Event(
                          userId = faker.idNumber().toString(),
                          eventName = faker.funnyName().name(),
                          eventLocation = EventLocation("N/A", 0.0, 0.0),
                          eventDate = 12345612,
                          eventType = faker.beer().name(),
                          eventDescription = faker.lordOfTheRings().toString(),
                          favorites = emptyMap()
                      )
                  )
              }
              events.value = data
          }


    private fun generateRandomFavorites (events: List<Event>?) {
        if (events == null) return
        val shuffledIndices = (events.indices).shuffled().take(25).sorted()
        Log.d("shuffled", shuffledIndices.toString())
        Log.d("events", events.toString())
        favorites.value = shuffledIndices.mapNotNull { index -> events.getOrNull(index) }
    }

}

