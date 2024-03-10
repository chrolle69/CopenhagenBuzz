package dk.itu.moapd.copenhagenbuzz.lgul.models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.github.javafaker.Faker
import kotlinx.coroutines.coroutineScope
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
    public val events: MutableLiveData<List<DummyModel>> by lazy {
        savedStateHandle.getLiveData(EVENT_KEY)
    }
    public val favorites: MutableLiveData<List<DummyModel>> by lazy {
        savedStateHandle.getLiveData(FAVORITE_KEY)
    }

      suspend fun fakeEventFetch() {
          coroutineScope {
              val faker = Faker(Random(42))
              //remember to change DummyModel to Event
              val data = ArrayList<DummyModel>()
              (1..50).forEach { it ->
                  launch {
                      val address = faker.address()
                      data.add(
                          //remember to change DummyModel to Event
                          DummyModel(
                              eventName = faker.funnyName().name(),
                              eventLocation = address.fullAddress(),
                              eventDate = faker.toString(),
                              eventType = faker.beer().name(),
                              eventDescription = faker.lordOfTheRings().toString(),
                              eventImage = R.drawable.img
                          )
                      )
                  }

              }
              events.value = data
          }
     }

    fun generateRandomFavorites (events: List<DummyModel>?) {
        if (events == null) return
        val shuffledIndices = (events.indices).shuffled().take(25).sorted()
        Log.d("shuffled", shuffledIndices.toString())
        Log.d("events", events.toString())
        favorites.value = shuffledIndices.mapNotNull { index -> events.getOrNull(index) }
    }

}

