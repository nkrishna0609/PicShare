package ca.nkrishnaswamy.picshare.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ca.nkrishnaswamy.picshare.repositories.AuthRepository

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository()

    fun registerUserByEmailAndPassword(email: String, password: String){
        repository.registerUserByEmail(email, password)
    }
}