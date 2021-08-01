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

    fun registerUserWithEmailAndPassword(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                //else if(it.isSuccessful){

                //}
            }
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

    suspend fun getUserIdToken(user: FirebaseUser): String? {
        val task = user.getIdToken(true).await()
        return task.token
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