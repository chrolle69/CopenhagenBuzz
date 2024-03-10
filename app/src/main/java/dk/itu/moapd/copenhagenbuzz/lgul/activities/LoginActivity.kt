package dk.itu.moapd.copenhagenbuzz.lgul.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dk.itu.moapd.copenhagenbuzz.lgul.R
import dk.itu.moapd.copenhagenbuzz.lgul.databinding.ActivityLoginBinding
import dk.itu.moapd.copenhagenbuzz.lgul.databinding.ActivityMainBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var fabLoginB: Button
    private lateinit var fabLoginGuestB: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fabLoginB = binding.fabLogin
        fabLoginGuestB = binding.fabLoginGuest

        fabLoginB.setOnClickListener{
            login(this, true)
        }
        fabLoginGuestB.setOnClickListener{
            login(this, false)
        }
    }
    private fun login(activity: LoginActivity, isLoggedIn: Boolean) {
        val intent = Intent(activity, MainActivity::class.java).apply{putExtra("isLoggedIn", isLoggedIn)}
        startActivity(intent)
        finish()
    }
}