package com.ar.digi_bell

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ar.digi_bell.R
import kotlinx.android.synthetic.main.activity_scan_receive.*

class ScanReceive : AppCompatActivity() {

    private val sharedPrefFile = "SharedPref"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_receive)

        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE)
        val editor:SharedPreferences.Editor =  sharedPreferences.edit()

            generate.setOnClickListener {
                val snd = "Sender"
                editor.putString("Snd", snd)
                editor.apply()
                editor.commit()
                val intent = Intent (this, Generator1::class.java )
                startActivity(intent)
                overridePendingTransition(0, 0)
            }

           scanner.setOnClickListener {
               val rc = "Receiver"
               editor.putString("Snd", rc)
               editor.apply()
               editor.commit()
               val intentSc = Intent(this, Scanner::class.java)
               startActivity(intentSc)
               overridePendingTransition(0, 0)
           }


        }
    }
