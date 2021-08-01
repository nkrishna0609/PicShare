package ca.nkrishnaswamy.picshare.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ca.nkrishnaswamy.picshare.repositories.AuthRepository
import com.google.firebase.auth.FirebaseUser

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository()

    suspend fun registerUserByEmailAndPassword(email: String, password: String) : String? {
        return repository.registerUserByEmail(email, password)
    }

    suspend fun checkIfEmailExistsAlready(email: String): Boolean{
        return repository.checkIfEmailExistsAlready(email)
    }

    suspend fun sendPasswordResetEmail(email: String){
        repository.sendPasswordResetEmail(email)
    }

    suspend fun loginWithEmailAndPassword(email: String, password: String): Int{
        return repository.loginWithEmailAndPassword(email, password)
    }

    suspend fun getUserIdToken(): String? {
        return repository.getUserIdToken()
    }

    fun getCurrentSignedInFirebaseUser(): FirebaseUser? {
        return repository.getCurrentSignedInFirebaseUser()
    }

    fun signOutUserFromFirebase() {
        repository.signOutOfFirebase()
    }

    fun deleteAccountFromFirebase(user : FirebaseUser) : Boolean {
        return repository.deleteAccountFromFirebase(user)
    }
}