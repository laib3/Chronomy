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
        } else {
            Log.d("LoginFragment", "sign in failed")
            if(response == null)
                Log.d("LoginFragment", "canceled by user")
            else
                Log.d("LoginFragment", "error:" + response.error?.errorCode)
        }
    }

    init {
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        vm.newUser.observe(viewLifecycleOwner) {
            if(it == true) {
                Log.d("LoginFragment", "newUser == true")
                findNavController().popBackStack()
                findNavController().navigate(R.id.nav_edit_profile)
            }
            else if (it == false){
                Log.d("LoginFragment", "newUser == false")
                findNavController().popBackStack()
                findNavController().navigate(R.id.nav_home)
            }
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