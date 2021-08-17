
package com.ar.digi_bell

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ar.digi_bell.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_gesture_main.*
import java.util.*
import kotlin.math.sqrt


class GestureMain : AppCompatActivity() {

    var count = -1
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f
    private var counter = 0
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private var firebaseUserID: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gesture_main)

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensorManager1 = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        auth = Firebase.auth
        firebaseUserID = auth.currentUser!!.uid
        val sharedPrefs = getSharedPreferences("SharedPref", MODE_PRIVATE)
        gesOn.isChecked = sharedPrefs.getBoolean("Checked", false)
        ges2On.isChecked = sharedPrefs.getBoolean("Checked2", false)

     gesOn.setOnCheckedChangeListener { _, isChecked ->
         if(isChecked){
             val editor = getSharedPreferences("SharedPref", MODE_PRIVATE).edit()
             editor.putBoolean("Checked", true)
             editor.apply()
             Objects.requireNonNull(sensorManager).registerListener(sensorListener, sensorManager
                 .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
             acceleration = 10f
             currentAcceleration = SensorManager.GRAVITY_EARTH
             lastAcceleration = SensorManager.GRAVITY_EARTH
         } else{
             val editor = getSharedPreferences("SharedPref", MODE_PRIVATE).edit()
             editor.putBoolean("Checked", false)
             editor.apply()
            // sensorManager.unregisterListener(sensorListener)
             unregisterListener()
             Log.e("UR", "Unregistered ACC")
             dbRef = FirebaseDatabase.getInstance("https://digibell-90668-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("Users").child(firebaseUserID)
             dbRef.child("Help").removeValue()

         }
     }



        ges2On.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){

                val editor = getSharedPreferences("SharedPref", MODE_PRIVATE).edit()
                editor.putBoolean("Checked2", true)
                editor.apply()
                Objects.requireNonNull(sensorManager1).registerListener(sensorListener1, sensorManager1
                    .getDefaultSensor(Sensor.TYPE_PROXIMITY), 1000 * 1000)

            }else{
                val editor = getSharedPreferences("SharedPref", MODE_PRIVATE).edit()
                editor.putBoolean("Checked2", false)
                editor.apply()
               // sensorManager1.unregisterListener(sensorListener1)
                unregisterListener()
                Log.e("UR", "Unregistered PRO")
                dbRef = FirebaseDatabase.getInstance("https://digibell-90668-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("Users").child(firebaseUserID)
                dbRef.child("Help").removeValue()

            }
        }

        backGes.setOnClickListener {
            val inte = Intent(this, HomeScreen1::class.java)
            startActivity(inte)
            overridePendingTransition(0, 0)
        }

    }

    private fun unregisterListener(){
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensorManager1 = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.unregisterListener(sensorListener)
        sensorManager1.unregisterListener(sensorListener1)
    }


    private var sensorListener: SensorEventListener = object : SensorEventListener {

        override fun onSensorChanged(event: SensorEvent) {


            val x = event.values[0]
            val y = event.values[1]
            //   val z = event.values[2]
            lastAcceleration = currentAcceleration
            currentAcceleration = sqrt((x * x + y * y).toDouble()).toFloat()

            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta

            var curTime = System.currentTimeMillis()
            // only allow one update every 100ms.
            var lastUpdate = 0
            if ((curTime - lastUpdate) > 100) {
                var diffTime = (curTime - lastUpdate)
                lastUpdate = curTime.toInt();
                if (acceleration > 20) {
                    counter++
                    Log.e("Counter", "$counter")

                }

                if (counter == 3) {
                    val help: String = "1234"
                    Log.d("help", " help")
                    sendHelp(help)
                    val i = Intent(this@GestureMain, HomeScreen2::class.java)
                    startActivity(i)
                    Toast.makeText(applicationContext, "Shake phone", Toast.LENGTH_SHORT).show()
                    counter = 0
                }
            }
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }

    }
    private var sensorListener1: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event!!.values[0] > 0)
            {
                Log.e("Count", "$count")
                count++

            }

            if (count == 3) {
               Log.e("Complete", "Three Counts")
                val help: String = "1234"
                Log.d("help", " help")
                sendHelp(help)
                val i = Intent(this@GestureMain, HomeScreen2::class.java)
                startActivity(i)
                Toast.makeText(applicationContext, "Touch Proxy Sensor", Toast.LENGTH_SHORT).show()
                count = 0
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }

    }


    override fun onResume() {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensorManager1 = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
        )
        super.onResume()
        sensorManager1.registerListener(sensorListener1, sensorManager1.getDefaultSensor(
            Sensor.TYPE_PROXIMITY), 1000 * 1000
        )
    }
    override fun onPause() {
//        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        val sensorManager1 = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        sensorManager.unregisterListener(sensorListener)
//         sensorManager1.unregisterListener(sensorListener1)
        super.onPause()
    }

    private fun sendHelp(sendHelp: String){
        firebaseUserID = auth.currentUser!!.uid
        dbRef = FirebaseDatabase.getInstance("https://digibell-90668-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("Users").child(firebaseUserID)
        dbRef.child("Help").setValue(sendHelp)
        Toast.makeText(this, "Asking Help", Toast.LENGTH_SHORT).show()
    }


}




