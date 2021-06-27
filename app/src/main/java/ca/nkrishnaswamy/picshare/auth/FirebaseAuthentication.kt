package ca.nkrishnaswamy.picshare.auth

import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthentication {

    companion object {
        val instance = FirebaseAuthentication()
    }

    fun registerUserWithEmailAndPassword(email: String, password: String){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                //else if(it.isSuccessful){

                //}
            }
    }
}