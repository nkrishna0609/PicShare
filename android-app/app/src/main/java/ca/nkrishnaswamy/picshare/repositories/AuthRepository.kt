package ca.nkrishnaswamy.picshare.repositories

import ca.nkrishnaswamy.picshare.auth.FirebaseAuthentication
import com.google.firebase.auth.FirebaseUser

class AuthRepository {
    private val firebaseauthclass = FirebaseAuthentication.instance

    suspend fun registerUserByEmail(email: String, password: String): String? {
        return firebaseauthclass.registerUserWithEmailAndPassword(email, password)
    }

    suspend fun checkIfEmailExistsAlready(email: String): Boolean{
        return firebaseauthclass.checkIfEmailExistsAlready(email)
    }

    suspend fun sendPasswordResetEmail(email: String){
        firebaseauthclass.sendPasswordResetEmail(email)
    }

    suspend fun loginWithEmailAndPassword(email: String, password: String): Int{
        return firebaseauthclass.loginWithEmailAndPassword(email, password)
    }

    suspend fun getUserIdToken(): String? {
        return firebaseauthclass.getUserIdToken();
    }

    fun getCurrentSignedInFirebaseUser(): FirebaseUser? {
        return firebaseauthclass.getCurrentSignedOnUser()
    }

    fun signOutOfFirebase(){
        firebaseauthclass.signOut()
    }

    fun deleteAccountFromFirebase(user : FirebaseUser) : Boolean {
        return firebaseauthclass.deleteAccountFromFirebase(user)
    }
}