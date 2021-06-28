package ca.nkrishnaswamy.picshare.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ca.nkrishnaswamy.picshare.repositories.AuthRepository
import com.google.firebase.auth.FirebaseUser

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository()

    fun registerUserByEmailAndPassword(email: String, password: String){
        repository.registerUserByEmail(email, password)
    }

    suspend fun checkIfEmailExistsAlready(email: String): Boolean{
        return repository.checkIfEmailExistsAlready(email)
    }

    suspend fun sendPasswordResetEmail(email: String){
        repository.sendPasswordResetEmail(email)
    }

    suspend fun loginWithEmailAndPassword(email: String, password: String): Boolean{
        return repository.loginWithEmailAndPassword(email, password)
    }

    fun getCurrentSignedOnUser(): FirebaseUser? {
        return repository.getCurrentUser()
    }
}