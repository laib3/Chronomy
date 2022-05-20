package it.polito.mainactivity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.polito.mainactivity.data.User
import it.polito.mainactivity.databinding.ActivityMainBinding
import it.polito.mainactivity.ui.userprofile.UserProfileViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: User
    private var userState: FirebaseUser? = null
    private var RC_SIGN_IN: Int = 5
    // private val vm: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    var snackBarMessage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        LayoutInflater.from(this).inflate(R.layout.nav_header_main, navView)
        val navHeaderName: TextView = navView.findViewById(R.id.navHeaderName)
        val navHeaderSurname: TextView = navView.findViewById(R.id.navHeaderSurname)
        val navHeaderBalance: TextView = navView.findViewById(R.id.navHeaderBalance)
        val navProfilePicture: ImageView = navView.findViewById(R.id.navHeaderProfilePicture)

        navProfilePicture.clipToOutline = true

        //val userProfileViewModel = UserProfileViewModel(application)
        val userProfileViewModel =
            ViewModelProvider(this).get(UserProfileViewModel::class.java)

        // observe viewModel changes
        userProfileViewModel.user.observe(this) {
            navHeaderName.text = it.name
            navHeaderSurname.text = it.surname
            navHeaderBalance.text = String.format(getString(R.string.user_profile_balance_placeholder), it.balance)
            // TODO: profile picture
            //if (it.profilePicture != null) navProfilePicture.setImageDrawable(it)
        }


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_list, R.id.nav_show_profile, R.id.nav_home
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        auth = Firebase.auth
        auth.addAuthStateListener { state ->
            userState = state.currentUser
            if (userState == null) {
                Log.d("Auth error", "user is null")
            } else {
                TODO("retrieve data")

            }
        }

    }

    private fun populateHeader(navigationView: NavigationView) {
        val headerView: View = navigationView.getHeaderView(0)
    }



    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}