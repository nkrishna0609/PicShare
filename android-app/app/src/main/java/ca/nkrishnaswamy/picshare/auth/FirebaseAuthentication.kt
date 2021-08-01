package ca.nkrishnaswamy.picshare.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class FirebaseAuthentication {

    companion object {
        val instance = FirebaseAuthentication()
        val auth = FirebaseAuth.getInstance()
    }

    suspend fun registerUserWithEmailAndPassword(email: String, password: String) : String?{
        auth.createUserWithEmailAndPassword(email, password).await()
        return getUserIdToken()
    }

    suspend fun checkIfEmailExistsAlready(email: String): Boolean{
        val task = auth.fetchSignInMethodsForEmail(email).await()
        return task.signInMethods?.isEmpty() as Boolean
    }

    suspend fun sendPasswordResetEmail(email: String){
        auth.sendPasswordResetEmail(email).await()
    }

    suspend fun loginWithEmailAndPassword(email: String, password: String): Int {
        var check: Int = 0
        try {
            auth.signInWithEmailAndPassword(email, password).await()
        }
        catch (e: FirebaseAuthInvalidCredentialsException){
            check=1
        }
        catch (e: FirebaseAuthInvalidUserException) {
            check=2
        }
        return check
    }

    suspend fun getUserIdToken(): String? {
        val currentUser = auth.currentUser
        val task = currentUser?.getIdToken(true)?.await()
        return task?.token
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentSignedOnUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun deleteAccountFromFirebase(user : FirebaseUser) : Boolean {
        var check : Boolean = true
        user.delete().addOnCompleteListener {
            if (!it.isSuccessful) {
                check = false
            }
        }
        return check
    }

}