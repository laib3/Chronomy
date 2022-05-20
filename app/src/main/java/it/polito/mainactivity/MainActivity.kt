package it.polito.mainactivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.internal.NavigationMenuItemView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import it.polito.mainactivity.databinding.ActivityMainBinding
import it.polito.mainactivity.ui.userprofile.UserProfileViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val vm: MainViewModel by viewModels()
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
        userProfileViewModel.balance.observe(this) { navHeaderBalance.text = String.format(getString(R.string.user_profile_balance_placeholder), it) }
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

        navView.menu.findItem(R.id.bGotoTimeslotList).setOnMenuItemClickListener {
            drawerLayout.closeDrawers()
            navController.popBackStack()
            navController.navigate(R.id.nav_list)
            true
        }

        navView.menu.findItem(R.id.bGotoShowProfile).setOnMenuItemClickListener {
            drawerLayout.closeDrawers()
            navController.popBackStack()
            navController.navigate(R.id.nav_show_profile)
            true
        }

        // when logout is clicked
        navView.menu.findItem(R.id.bLogout).setOnMenuItemClickListener {
            // when logout is clicked -> go to login screen
            AuthUI.getInstance().signOut(this).addOnCompleteListener {
                drawerLayout.closeDrawers()
                navController.popBackStack()
                navController.navigate(R.id.nav_login_fragment)
            }
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}