package ca.nkrishnaswamy.picshare.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseAuthentication {

    companion object {
        val instance = FirebaseAuthentication()
        val auth = FirebaseAuth.getInstance()
    }

    fun registerUserWithEmailAndPassword(email: String, password: String){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
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

}