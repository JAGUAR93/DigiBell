package com.ar.digi_bell

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.ar.digi_bell.R
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val sharedPrefFile = "SharedPref"

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance())
        auth = Firebase.auth
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recSen = sharedPreferences.getString("Snd", " ")
        val TIME_OUT = 400
        Handler().postDelayed({
            if(auth.currentUser == null){
            val inte = Intent(this, SignUp::class.java)
            startActivity(inte)
            finish()}
            else if (auth.currentUser != null && recSen == "Sender"){
                val intent = Intent(this, HomeScreen1::class.java)
                startActivity(intent)
                finish()
            }
            else if (auth.currentUser != null && recSen == "Receiver"){
                val intent = Intent(this, Scanner::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, ScanReceive::class.java)
                startActivity(intent)
                finish()
            }
        }, TIME_OUT.toLong())
    }
}