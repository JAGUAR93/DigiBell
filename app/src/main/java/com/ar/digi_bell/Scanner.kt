package com.ar.digi_bell

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.ar.digi_bell.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_receiver.*
import kotlinx.android.synthetic.main.activity_scanner.*

class Scanner : AppCompatActivity() {
    lateinit var codescanner: CodeScanner
    private lateinit var dbRef1: DatabaseReference
    private lateinit var dbRef4: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var firebaseUserID: String = ""
    private var name: String? = ""
    private var ids: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        auth = Firebase.auth
        firebaseUserID = auth.currentUser!!.uid
        dbRef4 = FirebaseDatabase.getInstance("https://digibell-90668-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("Users").child(firebaseUserID)

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                123
            )
        } else {
            startScanning()
        }

        var ino = 1
        dbRef4.child("INo").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val inoD = snapshot.getValue(Long::class.java)
                if (inoD != null) {
                    ino = inoD.toInt()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        backScan.setOnClickListener {
            val bkscan = Intent(this, ScanReceive::class.java)
            startActivity(bkscan)
            overridePendingTransition(0, 0)
        }

        addScanner.setOnClickListener {
            dbRef4.child("ListName").child("n$ino").setValue(name)
            dbRef4.child("ListId").child("n$ino").setValue(ids)
            ino++
            dbRef4.child("INo").setValue(ino)
        }

        done.setOnClickListener {
            val ma= Intent(this, Receiver::class.java)
            startActivity(ma)
            overridePendingTransition(0, 0)
        }

    }

    private fun startScanning() {

        val scannerView: CodeScannerView = findViewById(R.id.scanner)
        codescanner = CodeScanner(this, scannerView)
        codescanner.camera = CodeScanner.CAMERA_BACK
        codescanner.formats = CodeScanner.ALL_FORMATS
        codescanner.autoFocusMode = AutoFocusMode.SAFE
        codescanner.scanMode = ScanMode.SINGLE
        codescanner.isAutoFocusEnabled = true
        codescanner.isFlashEnabled = false
        codescanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                Log.e("rslt", "$it")
                if(it.toString() != " "){
                    Toast.makeText(this, "Scan Completed click on Add to add in the list", Toast.LENGTH_SHORT).show()
                    dbRef1 = FirebaseDatabase.getInstance("https://digibell-90668-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("Users").child(it.text)
                    dbRef1.child("Name").addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            name = snapshot.getValue(String::class.java)
                        }
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                    dbRef1.child("uid").addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            ids = snapshot.getValue(String::class.java)
                        }
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                }else{
                    Toast.makeText(this, "Not Scanned properly please tap on scanner and try again", Toast.LENGTH_SHORT).show()
                }

            }
        }
        codescanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(
                    this,
                    "Camera Initialization Error: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
        scannerView.setOnClickListener {
            codescanner.startPreview()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
                startScanning()
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::codescanner.isInitialized)
        {
            codescanner?.startPreview()
        }
    }

    override fun onPanelClosed(featureId: Int, menu: Menu) {
        if (::codescanner.isInitialized)
        {
            codescanner?.releaseResources()
        }

        super.onPause()
    }


}