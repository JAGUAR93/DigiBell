package com.ar.digi_bell

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ar.digi_bell.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home_screen2.*
import kotlinx.android.synthetic.main.help.*


class HomeScreen2 : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private var firebaseUserID: String = ""
    private var handlerAnimation = Handler()
    private var statusAnimation = false

    override fun onCreate(savedInstanceState: Bundle?) {

        auth = Firebase.auth
        firebaseUserID = auth.currentUser!!.uid

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen2)

        if (statusAnimation) {
            stopPulse()
        } else {
            startPulse()
        }

        statusAnimation = statusAnimation

        val TIME_OUT = 10000
        Handler().postDelayed({
            dbRef = FirebaseDatabase.getInstance("https://digibell-90668-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("Users").child(firebaseUserID)
            dbRef.child("Help").removeValue()
            val intent = Intent(this, HomeScreen1::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }, TIME_OUT.toLong())

        hmbtn2.setOnClickListener {
            dbRef = FirebaseDatabase.getInstance("https://digibell-90668-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("Users").child(firebaseUserID)
            dbRef.child("Help").removeValue()
            val i = Intent(this, HomeScreen1::class.java)
            startActivity(i)
            overridePendingTransition(0, 0)
        }


    }
        private fun startPulse()
        {
            runnable.run()
        }
        private fun stopPulse()
        {
            handlerAnimation.removeCallbacks(runnable)

        }
        private var runnable = object : Runnable
        {
            override fun run()
            {
                disco.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(1000).withEndAction {
                    disco.scaleX = 1f
                    disco.scaleY = 1f
                    disco.alpha = 1f
                }
                disco1.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(700).withEndAction {
                    disco1.scaleX = 1f
                    disco1.scaleY = 1f
                    disco1.alpha = 1f
                }



                handlerAnimation.postDelayed(this, 1500)
            }

        }



          }
//          val colorFrom = ContextCompat.getColor(this, R.color.red)
//          val colorTo = ContextCompat.getColor(this, R.color.green)
//          val circle = ContextCompat.getDrawable(this, R.drawable.circle)
//          val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
//          colorAnimation.duration = 100 // milliseconds
//          colorAnimation.repeatCount = 100
//          colorAnimation.repeatMode = ValueAnimator.REVERSE
//          colorAnimation.addUpdateListener { animator -> disco.setBackgroundColor(animator.animatedValue as Int) }
//          colorAnimation.start()
//
//    }
