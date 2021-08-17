package com.ar.digi_bell

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ar.digi_bell.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_personal_info.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.number_update.*
import java.util.concurrent.TimeUnit

class NumberUpdate: AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private var firebaseUserID: String = ""
    private var codeSent: String = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.number_update)

        auth = Firebase.auth
        firebaseUserID = auth.currentUser!!.uid

        dbRef = FirebaseDatabase.getInstance("https://digibell-90668-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("Users").child(firebaseUserID)

        inUpdateVcode.setOnClickListener {
            sendVerificationCode()
        }

        done.setOnClickListener {
            verifySignInCode()
            val vCode: String = codeNumUp.text.toString().trim { it <= ' ' }
            if (vCode.isEmpty() || vCode.length < 6) {
                number.error = "Enter valid code"
                number.requestFocus()
                return@setOnClickListener
            }

            verifyVerificationCode(vCode)
        }

        backUpNum.setOnClickListener {
            val goBack = Intent(this, Personal_info::class.java)
            startActivity(goBack)
        }

    }
    private fun verifySignInCode() {
        val vCode: String = numberEN.text.toString()
        val credential = PhoneAuthProvider.getCredential(codeSent, vCode)
        signInWithPhoneAuthCredential(credential)
    }

    private fun sendVerificationCode() {
        val phone: String = numberEN.text.toString()
        if (phone.isEmpty()) {
            numberEN.error = "Phone Number is required"
            numberEN.requestFocus()
            return
        }
        if (phone.length != 10) {
            numberEN.error = "Please enter a valid phone"
            numberEN.requestFocus()
            return
        }
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+91$phone", 60, TimeUnit.SECONDS, this, mCallbacks
        )
    }

    private var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

                val code = phoneAuthCredential.smsCode
                if(code != null){
                    numberEN.setText(code)
                    verifyVerificationCode(code)
                }
            }
            override fun onVerificationFailed(e: FirebaseException) {

                Toast.makeText(this@NumberUpdate, e.message, Toast.LENGTH_LONG).show();
                Log.i("Fail", "onVerificationFailed: " + e.localizedMessage);

            }
            override fun onCodeSent(s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                codeSent = s
            }
        }

    private fun verifyVerificationCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(codeSent, code)
        signInWithPhoneAuthCredential(credential)
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val name = numberEN.text.toString()
                    if(name == null){
                        numberEN.error = "Please enter a number"
                        numberEN.requestFocus()
                        return@addOnCompleteListener
                    }
                    dbRef.child("Number").setValue(name)
                    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
                    numberPI.text = name.toEditable()
                    val intent = Intent(this, Personal_info::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    Toast.makeText(this, "Number Updated", Toast.LENGTH_SHORT).show()

                } else {

                    var message = "Something is wrong, we will fix it soon..."
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        message = "Invalid code entered..."
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
    }

}