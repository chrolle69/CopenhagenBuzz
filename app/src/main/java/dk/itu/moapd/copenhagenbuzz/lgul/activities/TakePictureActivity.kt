package dk.itu.moapd.copenhagenbuzz.lgul.activities

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import dk.itu.moapd.copenhagenbuzz.lgul.R
import dk.itu.moapd.copenhagenbuzz.lgul.databinding.ActivityTakePictureBinding

class TakePictureActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityTakePictureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTakePictureBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

}