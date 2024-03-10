/*
 * MIT License
 *
 * Copyright (c) 2024 Lucas Roy Guldbrandsen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dk.itu.moapd.copenhagenbuzz.lgul.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import dk.itu.moapd.copenhagenbuzz.lgul.R
import dk.itu.moapd.copenhagenbuzz.lgul.databinding.ActivityMainBinding
import dk.itu.moapd.copenhagenbuzz.lgul.fragments.TimelineFragment
import dk.itu.moapd.copenhagenbuzz.lgul.models.Event

/**
 * general ui of CopenhagenBuzz.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //
    companion object {
        private val TAG = MainActivity::class.qualifiedName
    }

    /**
     * this method inflates the activities and creates listeners
     *
     * @param savedInstanceState a lazy container
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val topAppBar = binding.topAppBar
        topAppBar.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.logout_guest -> {
                    intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        if (intent.getBooleanExtra("isLoggedIn", false)) {
            binding.topAppBar.menu.getItem(0).setVisible(true)
            binding.topAppBar.menu.getItem(1).setVisible(false)
        } else {
            binding.topAppBar.menu.getItem(0).setVisible(false)
            binding.topAppBar.menu.getItem(1).setVisible(true)
        }



        val navHostFragment = supportFragmentManager
            .findFragmentById(
                R.id.fragment_container_view
            ) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)


    }
}