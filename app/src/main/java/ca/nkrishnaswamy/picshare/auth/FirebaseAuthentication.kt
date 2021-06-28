package ca.nkrishnaswamy.picshare.auth

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
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

    suspend fun loginWithEmailAndPassword(email: String, password: String): Boolean {
        var check: Boolean = true
        try {
            auth.signInWithEmailAndPassword(email, password).await()
        }
        catch (e: FirebaseAuthInvalidCredentialsException){
            check=false
        }
        return check
    }

    fun getCurrentSignedOnUser(): FirebaseUser? {
        return auth.currentUser
    }

}