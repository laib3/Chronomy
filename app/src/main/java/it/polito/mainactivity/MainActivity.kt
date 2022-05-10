package it.polito.mainactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import it.polito.mainactivity.databinding.ActivityMainBinding
import it.polito.mainactivity.ui.userprofile.UserProfileViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    var snackBarMessage : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)


        LayoutInflater.from(this).inflate(R.layout.nav_header_main, navView)
        val navHeaderName : TextView = navView.findViewById(R.id.navHeaderName)
        val navHeaderSurname: TextView = navView.findViewById(R.id.navHeaderSurname)
        val navHeaderBalance : TextView = navView.findViewById(R.id.navHeaderBalance)
        val navProfilePicture: ImageView = navView.findViewById(R.id.navHeaderProfilePicture)

        navProfilePicture.clipToOutline = true

        //val userProfileViewModel = UserProfileViewModel(application)
        val userProfileViewModel=
            ViewModelProvider(this).get(UserProfileViewModel::class.java)

        // observe viewModel changes
        userProfileViewModel.name.observe(this) { navHeaderName.text = it }
        userProfileViewModel.surname.observe(this) { navHeaderSurname.text = it }
        userProfileViewModel.balance.observe(this) {navHeaderBalance.text = String.format(getString(R.string.user_profile_balance_placeholder), it) }
        userProfileViewModel.picture.observe(this) {
            if(it != null) navProfilePicture.setImageDrawable(it)
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_list, R.id.nav_show_profile
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }*/


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}