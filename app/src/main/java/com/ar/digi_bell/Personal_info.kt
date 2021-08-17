package com.ar.digi_bell

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.ar.digi_bell.R
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_personal_info.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.name_update.*
import kotlinx.android.synthetic.main.number_update.*


class Personal_info : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private var currentPassword: String = ""
    private var firebaseUserID: String = ""
    private val sharedPrefFile = "SharedPref"
    private var sendid: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_info)
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE)
        auth = Firebase.auth
        firebaseUserID = auth.currentUser!!.uid

        dbRef = FirebaseDatabase.getInstance("https://digibell-90668-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("Users").child(firebaseUserID)

        backPI.setOnClickListener {
            sendid = sharedPreferences.getString("Snd"," ")
            if (sendid == "Sender"){
                val bak = Intent(this, HomeScreen1::class.java)
                startActivity(bak)
                overridePendingTransition(0, 0)
            } else if(sendid == "Receiver"){
                val bakRec = Intent(this, Receiver::class.java)
                startActivity(bakRec)
                overridePendingTransition(0, 0)
            }
        }

        dbRef.child("Name").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val nameD = snapshot.getValue(String::class.java)
                namePI.setText(nameD) 
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        dbRef.child("Number").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val numberD = snapshot.getValue(String::class.java)
                numberPI.setText(numberD)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        dbRef.child("Email Id").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val emailD = snapshot.getValue(String::class.java)
                emailPI.setText(emailD)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        dbRef.child("Password").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentPasswrd = snapshot.getValue(String::class.java)
                if (currentPasswrd != null) {
                    currentPassword = currentPasswrd
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        nameEdit.setOnClickListener {
            showUpdateName(namePI)
        }
        numberEdit.setOnClickListener {
            val goToUpdateNum = Intent(this, NumberUpdate::class.java)
            startActivity(goToUpdateNum)
        }
        emailEdit.setOnClickListener {
            showUpdateEmail(emailPI)
        }
        chngPass.setOnClickListener {
            showUpdatePassword()
        }

    }

    private fun showUpdatePassword(){
    firebaseUserID = auth.currentUser!!.uid
    dbRef = FirebaseDatabase.getInstance("https://digibell-90668-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("Users").child(firebaseUserID)
    val user = FirebaseAuth.getInstance().currentUser
    val builder = AlertDialog.Builder(this)
    builder.setTitle("Update Password")
    val inflater = LayoutInflater.from(this)
    val view = inflater.inflate(R.layout.update_pass, null)
    val currPass = view.findViewById<EditText>(R.id.currPass)
    val newPass = view.findViewById<EditText>(R.id.newPass)
    val newPassCnfrm = view.findViewById<EditText>(R.id.newPassCnfrm)
    builder.setView(view)
    builder.setPositiveButton("Update"){_,_ ->
        Log.e("CurrPass","$currentPassword")
      if (currPass.text.toString() == currentPassword){
           if(newPass.text.toString() == newPassCnfrm.text.toString()){
               val new = newPass.text.toString()
               dbRef.child("Password").setValue(new)
//              user!!.updatePassword(new)
//                  .addOnCompleteListener { task ->
//                      if (task.isSuccessful) {
//                          Log.e("Password_Update", "User password updated.")
//                      }
//                      Toast.makeText(this, "User password updated.", Toast.LENGTH_LONG).show()
//                  }
          }else{
              Log.e("Match","It doesn't match your  new password")
              newPassCnfrm.error = "It doesn't match your  new password"
               Toast.makeText(this, "New password confirmation error", Toast.LENGTH_LONG).show()
               newPassCnfrm.requestFocus()
          }
        }else{
            Log.e("Wrong","Your current password is wrong" )
          currPass.error = "Your current password is wrong"
          Toast.makeText(this, "Your current password is wrong", Toast.LENGTH_LONG).show()
          currPass.requestFocus()
        }
    }
    builder.setNegativeButton("No"){ _, _ ->

    }
    val alert = builder.create()
    alert.show()
}


    private fun showUpdateEmail(emailPI: EditText) {
        firebaseUserID = auth.currentUser!!.uid
        dbRef = FirebaseDatabase.getInstance("https://digibell-90668-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("Users").child(firebaseUserID)
        val user = Firebase.auth.currentUser
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Update Email")
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.email_update, null)
        val editText = view.findViewById<EditText>(R.id.emailEN)
        val editText2 = view.findViewById<EditText>(R.id.passEN)

        editText.text = emailPI.editableText
        builder.setView(view)
        builder.setPositiveButton("Update") { _, _ ->
            val em = emailPI.text.toString()
            val emailID = editText.text.toString()
            val pass = editText2.text.toString()
            if (emailID == null) {
                editText.error = "Please enter a Email Id"
                editText.requestFocus()
                return@setPositiveButton
            } else if (editText2.text.toString() == currentPassword && em.isEmpty()){
                    val credential = EmailAuthProvider.getCredential(emailID, pass)
                Toast.makeText(this, "Please login again to link your email to your account", Toast.LENGTH_SHORT).show()
                user?.linkWithCredential(credential)?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.e("Success", "linkWithCredential:success")
                        dbRef.child("Email Id").setValue(emailID)
                        fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
                        emailPI.text = emailID.toEditable()
                        Toast.makeText(this, "Email Updated", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("Fail", "linkWithCredential:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }else if (editText2.text.toString() == currentPassword && em.isNotEmpty()){
                user!!.updateEmail(emailID)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            dbRef.child("Email Id").setValue(emailID)
                            fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
                            emailPI.text = emailID.toEditable()
                            Toast.makeText(this, "Email Updated", Toast.LENGTH_SHORT).show()
                            Log.d("TAG", "User email address updated.")
                        }
                    }
            }
        }
        builder.setNegativeButton("Cancel"){ _, _ ->

        }
        val alert = builder.create()
        alert.show()
    }

//    private fun showUpdateNumber(numberPI: EditText) {
//        firebaseUserID = auth.currentUser!!.uid
//        dbRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserID)
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Update Number")
//        val inflater = LayoutInflater.from(this)
//        val view = inflater.inflate(R.layout.number_update, null)
//        val editText = view.findViewById<EditText>(R.id.numberEN)
//        editText.text = numberPI.editableText
//        builder.setView(view)
//        builder.setPositiveButton("Update"){ _, _ ->
//            val name = editText.text.toString()
//            if(name == null){
//                editText.error = "Please enter a number"
//                editText.requestFocus()
//                return@setPositiveButton
//            }
//            dbRef.child("Number").setValue(name)
//            fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
//            numberPI.text = name.toEditable()
//            Toast.makeText(this, "Number Updated", Toast.LENGTH_SHORT).show()
//        }
//        builder.setNegativeButton("No"){ _, _ ->
//
//        }
//        val alert = builder.create()
//        alert.show()
//    }


    private fun showUpdateName(namePI: EditText) {
        firebaseUserID = auth.currentUser!!.uid
        dbRef = FirebaseDatabase.getInstance("https://digibell-90668-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("Users").child(firebaseUserID)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Update Name")
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.name_update, null)
        val editText = view.findViewById<EditText>(R.id.nameEN)
        editText.text = namePI.editableText
        builder.setView(view)
        builder.setPositiveButton("Change"){ _, _ ->
            val name = editText.text.toString()
            if(name == null){
                editText.error = "Please enter a name"
                editText.requestFocus()
                return@setPositiveButton
            }
            dbRef.child("Name").setValue(name)
            fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
            namePI.text = name.toEditable()
            Toast.makeText(this, "Name Updated", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("Cancel"){ _, _ ->

        }
        val alert = builder.create()
            alert.show()
    }

}