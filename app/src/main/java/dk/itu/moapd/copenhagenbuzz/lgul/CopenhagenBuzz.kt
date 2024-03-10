package dk.itu.moapd.copenhagenbuzz.lgul

import android.app.Application
import com.google.android.material.color.DynamicColors

class CopenhagenBuzz: Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}