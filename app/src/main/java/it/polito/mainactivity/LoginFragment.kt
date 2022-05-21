package it.polito.mainactivity

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import it.polito.mainactivity.databinding.FragmentLoginBinding
import it.polito.mainactivity.ui.userprofile.UserProfileViewModel

class LoginFragment: Fragment() {

    private val vm: UserProfileViewModel by activityViewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var authStateListener: FirebaseAuth.AuthStateListener =
        FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if(user != null){ // logged in
                Log.d("LoginFragment", "logged in as " + user.uid)
            } else { // user = null, not logged in
                Log.d("LoginFragment", "not logged in")
            }
        }
    private val signInIntent = AuthUI.getInstance().createSignInIntentBuilder()
        .setAvailableProviders(listOf(AuthUI.IdpConfig.GoogleBuilder().build()))
        .build()
    private val signInLauncher =
        registerForActivityResult(FirebaseAuthUIActivityResultContract()) {
        res -> onSignInResult(res)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult){
        val response = result.idpResponse
        if(result.resultCode == RESULT_OK) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if(!vm.userIsRegistered()){
                findNavController().navigate(R.id.action_nav_login_fragment_to_nav_edit_profile)
            }
            // TODO if userId not yet present, add user to the db
            // check if user is present
            // if not present add to Firebase
            // redirect to timeslot list
            findNavController().navigate(R.id.nav_list)
            findNavController().clearBackStack(R.id.nav_login_fragment)
        } else {
            Log.d("LoginFragment", "sign in failed")
            if(response == null)
                Log.d("LoginFragment", "canceled by user")
            else
                Log.d("LoginFragment", "error:" + response.error?.errorCode)
        }
    }

    init {
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val currentUser = FirebaseAuth.getInstance().currentUser
        // if logged in
        if(currentUser != null){
            // TODO check if authorized
            findNavController().navigate(R.id.nav_list)
            findNavController().clearBackStack(R.id.nav_login_fragment)
        }

        val bSignIn = binding.bSignIn
        bSignIn.setOnClickListener {
            login()
        }

        return root
    }

    private fun login(){
        signInLauncher.launch(signInIntent)
    }

}