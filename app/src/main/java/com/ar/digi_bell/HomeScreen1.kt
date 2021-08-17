package com.ar.digi_bell


import android.content.Intent
import android.os.*
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.ar.digi_bell.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home_screen1.*
import kotlinx.android.synthetic.main.toolbar_main.*


class HomeScreen1 : AppCompatActivity() {

    private var abdt: ActionBarDrawerToggle? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private var firebaseUserID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen1)
        auth = Firebase.auth
        firebaseUserID = auth.currentUser!!.uid


        hmbtn1.setOnClickListener {
           val help:String = "1234"
            Log.d("help", " help")
           sendHelp(help)
            val i = Intent(this, HomeScreen2::class.java)
            startActivity(i)
            overridePendingTransition(0, 0)
       }

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        abdt = ActionBarDrawerToggle(
            this,
            drawer_layout,toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        abdt!!.isDrawerIndicatorEnabled = true
        drawer_layout!!.addDrawerListener(abdt!!)
        abdt!!.syncState()



        navigation!!.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.profile -> {
                    val gotoAcc = Intent(this, Personal_info::class.java)
                    startActivity(gotoAcc)
                    overridePendingTransition(0, 0)
                    return@setNavigationItemSelectedListener true
                }
                R.id.qrCode -> {
                    val gotoGen = Intent(this, Generator1::class.java)
                    startActivity(gotoGen)
                    overridePendingTransition(0, 0)
                    return@setNavigationItemSelectedListener true
                }
                R.id.bcmReceiver -> {
                    val gotoScRcv = Intent(this, ScanReceive::class.java)
                    startActivity(gotoScRcv)
                    overridePendingTransition(0, 0)
                    return@setNavigationItemSelectedListener true
                }
                R.id.gesture -> {
                    val gotoGes = Intent(this, GestureMain::class.java)
                    startActivity(gotoGes)
                    overridePendingTransition(0, 0)
                    return@setNavigationItemSelectedListener true
                }
                R.id.logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val log = Intent(this, Login::class.java)
                    startActivity(log)
                    overridePendingTransition(0, 0)
                    return@setNavigationItemSelectedListener true
                }
            }
             return@setNavigationItemSelectedListener true
        }

    }


    private fun sendHelp(sendHelp: String){
        firebaseUserID = auth.currentUser!!.uid
        dbRef = FirebaseDatabase.getInstance("https://digibell-90668-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("Users").child(firebaseUserID)
        dbRef.child("Help").setValue(sendHelp)
        Toast.makeText(this, "Asking Help", Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (abdt!!.onOptionsItemSelected(item)) true else super.onOptionsItemSelected(item!!)
    }


}