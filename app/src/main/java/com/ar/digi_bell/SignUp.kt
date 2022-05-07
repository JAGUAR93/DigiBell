package com.ar.digi_bell


import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ar.digi_bell.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap


class SignUp : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private var firebaseUserID: String = ""
    private var codeSent: String = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        showPassInNum.text = null
        showPassInNum.textOn = null
        showPassInNum.textOff = null




        showPassInNum.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showPassInNum.setBackgroundResource(R.drawable.ic_openeye)
                passInNum.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                showPassInNum.text = null
                showPassInNum.textOn = null
                showPassInNum.textOff = null
            } else {
                showPassInNum.setBackgroundResource(R.drawable.ic_closeeye)
                showPassInNum.text = null
                showPassInNum.textOn = null
                showPassInNum.textOff = null
                passInNum.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passInNum.setSelection(passInNum.text.length)
            }
        }


        loginSx.setOnClickListener {

            val gotoLogin = Intent(this, Login::class.java)
            startActivity(gotoLogin)
            overridePendingTransition(0, 0)

        }


        getCode.setOnClickListener {
            sendVerificationCode()
        }

        signupcnfrmNum.setOnClickListener {

//            val email = number.text.toString()
//            val pwd = passInNum.text.toString()
//            if (email.isEmpty()) {
//                number.error = "Please enter email id"
//                number.requestFocus()
//            } else if (pwd.isEmpty()) {
//                passInNum.error = "Please enter your password"
//                passInNum.requestFocus()
//            } else if (email.isEmpty() && pwd.isEmpty()) {
//                Toast.makeText(this, "Fields are empty", Toast.LENGTH_SHORT).show()
//            } else if (pwd != passCnfrmInNum.text.toString()) {
//                Toast.makeText(
//                    this,
//                    "Please confirm your password and enter it again",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else if (!(email.isEmpty() && pwd.isEmpty())) {
//
//                verifySignInCode()
//                val vCode: String = signUpCode.text.toString().trim { it <= ' ' }
//                if (vCode.isEmpty() || vCode.length < 6) {
//                    number.error = "Enter valid code"
//                    number.requestFocus()
//                    return@setOnClickListener
//                }
//                verifyVerificationCode(vCode)
//            } else {
//                Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT).show()
//            }
        }


    }

    private fun verifySignInCode() {
        val vCode: String = number.text.toString()
        val credential = PhoneAuthProvider.getCredential(codeSent, vCode)
        signInWithPhoneAuthCredential(credential)
    }

    private fun sendVerificationCode() {
        val phone: String = number.text.toString()
        if (phone.isEmpty()) {
            number.error = "Phone Number is required"
            number.requestFocus()
            return
        }
        if (phone.length != 10) {
            number.error = "Please enter a valid phone"
            number.requestFocus()
            return
        }
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+91$phone", 60, TimeUnit.SECONDS, this, mCallbacks
        )
    }

    private var mCallbacks: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val code = phoneAuthCredential.smsCode
                if (code != null) {
                    signUpCode.setText(code)
                    verifyVerificationCode(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {

                Toast.makeText(this@SignUp, e.message, Toast.LENGTH_LONG).show()
                Log.i("Fail", "onVerificationFailed: " + e.localizedMessage)


            }

            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
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
                    Toast.makeText(this, "test1234", Toast.LENGTH_SHORT).show()
                    task.result.user?.let { Log.d("rishabh", it.uid) }
                    val message = "Success"
                    firebaseUserID = auth.currentUser!!.uid
                    dbRef =
                        FirebaseDatabase.getInstance().reference.child(
                            "Users"
                        ).child(firebaseUserID)
                    val userHashMap = HashMap<String, Any>()
                    val ino = 1
                    userHashMap["uid"] = firebaseUserID
                    userHashMap["Name"] = nameInNum.text.toString()
                    userHashMap["Number"] = number.text.toString()
                    userHashMap["Email Id"]
                    userHashMap["Password"] = passInNum.text.toString()
                    userHashMap["ListName"]
                    userHashMap["ListNum"]
                    userHashMap["ListId"]
                    userHashMap["Help"]
                    userHashMap["INo"] = ino
//                    dbRef.child("uid").setValue(firebaseUserID)
//                    dbRef.child("Name").setValue(nameInNum.text.toString())
//                    dbRef.child("Number").setValue(number.text.toString())
//                    dbRef.child("Password").setValue(passInNum.text.toString())
//                    dbRef.child("INo").setValue(ino)
//                    val intent = Intent(this, ScanReceive::class.java)
//                    intent.flags =
//                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    startActivity(intent)
//                    finish()
//                    Toast.makeText(this, "SignUp Successful", Toast.LENGTH_SHORT).show()


                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    dbRef.setValue(userHashMap)
                    dbRef.setValue(userHashMap).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("testing", " datahasbeensaved")
                            val intent = Intent(this, ScanReceive::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                            Toast.makeText(this, "SignUp Successful", Toast.LENGTH_LONG).show()

                        } else {
                            Log.d("testing", " datahasnotbeensaved")
                        }
                    }
//                    dbRef.updateChildren(userHashMap).addOnCompleteListener(){taskk  ->
//                        if (taskk.isSuccessful){
//                           val intent = Intent(this, ScanReceive::class.java)
//                            intent.flags =
//                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                            startActivity(intent)
//                            finish()
//                            Toast.makeText(this, "SignUp Successful", Toast.LENGTH_LONG).show()
//                        }
//                  }
                } else {
                    val message = "Loading..."
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
    }


    }