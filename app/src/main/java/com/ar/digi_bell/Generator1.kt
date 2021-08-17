package com.ar.digi_bell

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.ar.digi_bell.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.activity_generator1.*

class Generator1 : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
  //  private lateinit var dbRef: DatabaseReference
    private var firebaseUserID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generator1)

        auth = Firebase.auth
        firebaseUserID = auth.currentUser!!.uid
         etData.setText(firebaseUserID).toString()
     //   dbRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserID)

        backSR.setOnClickListener {
            val sndScnRcv = Intent(this, ScanReceive::class.java)
            startActivity(sndScnRcv)
            overridePendingTransition(0, 0)
        }

        val writer = QRCodeWriter()
            try {

                val bitMatrix = writer.encode(firebaseUserID, BarcodeFormat.QR_CODE, 512, 512)
                val width = bitMatrix.width
                val height = bitMatrix.height
                val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)

                    }
                }

                ivQRCode.setImageBitmap(bmp)

            } catch (e: WriterException) {
                e.printStackTrace()
            }

        sendToHm1.setOnClickListener {
            val sndHm = Intent(this, HomeScreen1::class.java)
            startActivity(sndHm)
            overridePendingTransition(0, 0)
        }
        }

    }


