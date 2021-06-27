package ca.nkrishnaswamy.picshare.repositories

import ca.nkrishnaswamy.picshare.auth.FirebaseAuthentication

class AuthRepository {
    private val firebaseauthclass = FirebaseAuthentication.instance

    fun registerUserByEmail(email: String, password: String){
        firebaseauthclass.registerUserWithEmailAndPassword(email, password)
    }
}