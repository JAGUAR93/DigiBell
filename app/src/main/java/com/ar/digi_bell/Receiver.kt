package com.ar.digi_bell


import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.SparseBooleanArray
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.budiyev.android.codescanner.*
import com.ar.digi_bell.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home_screen1.*
import kotlinx.android.synthetic.main.activity_personal_info.*
import kotlinx.android.synthetic.main.activity_receiver.*
import kotlinx.android.synthetic.main.activity_receiver.show
import kotlinx.android.synthetic.main.toolbar_main.*


class Receiver : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef1: DatabaseReference
    private lateinit var dbRef2: DatabaseReference
    private lateinit var dbRef3: DatabaseReference
    private lateinit var dbRef4: DatabaseReference
    private var firebaseUserID: String = ""
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var runnable:Runnable
    private var handler: Handler = Handler()
    private var pause:Boolean = false
    private var name: String? = ""
    private var name2: String? = ""
    private var abdt: ActionBarDrawerToggle? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receiver)
        auth = Firebase.auth
        firebaseUserID = auth.currentUser!!.uid
        dbRef4 = FirebaseDatabase.getInstance("https://digibell-90668-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("Users").child(firebaseUserID)

        val list = arrayListOf<String>()
        val list2 = arrayListOf<String>()
       // val list3 = arrayListOf<String>()
      //  val list4 = arrayListOf<String>()

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, list)
        val adapter2 = ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, list2)
      //  val adapter3 = ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, list3)
     //   val adapter4 = ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, list4)



//        bcmSender.setOnClickListener {
//            val bcmS = Intent(this, ScanReceive::class.java)
//            startActivity(bcmS)
//        }
        var ino = 1
        dbRef4.child("INo").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val inoD = snapshot.getValue(Long::class.java)
                ino = inoD!!.toInt()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        show.setOnClickListener {
            dbRef4.child("ListId").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds: DataSnapshot in snapshot.children){
                        val data = ds.getValue(String::class.java)
                        if (data != null) {
                            list.add(data)
                            lvSenderId.adapter = adapter
                            adapter.notifyDataSetChanged()
                            dataRef(list)
                        }

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

            dbRef4.child("ListName").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds: DataSnapshot in snapshot.children){
                        val data = ds.getValue(String::class.java)
                        if (data != null) {
                            list2.add(data)
                            lvSenderName.adapter = adapter2
                            adapter2.notifyDataSetChanged()
                        }

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            senderIDx.text.clear()

        }

        lvSenderName.setOnItemClickListener { _, _, i, _ ->
            Toast.makeText(this, "You Selected the name --> "+ list2[i], Toast.LENGTH_SHORT).show()
           // Toast.makeText(this, "You Selected the id --> "+ list[i], Toast.LENGTH_SHORT).show()
        }
        deleteId.setOnClickListener {
            val position: SparseBooleanArray = lvSenderName.checkedItemPositions
            val count = lvSenderName.count
            var item = count - 1
            val position2: SparseBooleanArray = lvSenderName.checkedItemPositions
            val count2 = lvSenderId.count
            var item2 = count2 - 1
//            val position3: SparseBooleanArray = lvSenderName.checkedItemPositions
//            val count3 = lvnum.count
//            var item3 = count3 - 1
            while (item >= 0 && item2 >= 0) {
                if (position.get(item) && position2.get(item2))
                {

                    var ix:Int = item
                    ix++
                    Log.e("ITEM", "n$ix")
                    dbRef4.child("ListName").child("n$ix").removeValue()
                    dbRef4.child("ListId").child("n$ix").removeValue()
                    ino--
                    dbRef4.child("INo").setValue(ino)
                    adapter.clear()
                    adapter2.clear()
                }
                item--
                item2--
            }
            position.clear()
            adapter2.notifyDataSetChanged()
            position2.clear()
            adapter.notifyDataSetChanged()
            dataRef(list)
        }

       audioON.setOnCheckedChangeListener{ _, isChecked ->
           if (isChecked){
               if(pause){
                   mediaPlayer.seekTo(mediaPlayer.currentPosition)
                   mediaPlayer.start()
              pause = false

               }else{

                   mediaPlayer = MediaPlayer.create(applicationContext, R.raw.test)
                   mediaPlayer.start()


               }
               initializeSeekBar()

               mediaPlayer.setOnCompletionListener {

                   Toast.makeText(this,"end",Toast.LENGTH_SHORT).show()
               }
           }
           else{
               if(mediaPlayer.isPlaying || pause){
                   pause = false
                   seek_bar.progress = 0
                   mediaPlayer.stop()
                   mediaPlayer.reset()
                   mediaPlayer.release()
                   handler.removeCallbacks(runnable)


                   tv_pass.text = ""
                   tv_due.text = ""
                   Toast.makeText(this,"Call was dismissed",Toast.LENGTH_SHORT).show()
               }
           }
       }



//        bakSR.setOnClickListener {
//            val bakScRcv = Intent(this, ScanReceive::class.java)
//            startActivity(bakScRcv)
//        }
//
//        up3.setOnClickListener {
//            val usrPro = Intent(this, UserProfile::class.java)
//            startActivity(usrPro)
//        }



        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
                    mediaPlayer.seekTo(i * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        abdt = ActionBarDrawerToggle(
            this,
            drawer_layout1,toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        abdt!!.isDrawerIndicatorEnabled = true
        drawer_layout1!!.addDrawerListener(abdt!!)
        abdt!!.syncState()



        navigation1!!.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.profile1 -> {
                    val gotoAcc = Intent(this, Personal_info::class.java)
                    startActivity(gotoAcc)
                    overridePendingTransition(0, 0)
                    return@setNavigationItemSelectedListener true
                }

                R.id.bcmSender -> {
                    val gotoScRcv = Intent(this, ScanReceive::class.java)
                    startActivity(gotoScRcv)
                    overridePendingTransition(0, 0)
                    return@setNavigationItemSelectedListener true
                }

                R.id.scanner -> {
                    val gotoScRcv = Intent(this, Scanner::class.java)
                    startActivity(gotoScRcv)
                    overridePendingTransition(0, 0)
                    return@setNavigationItemSelectedListener true
                }

                R.id.logout1 -> {
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (abdt!!.onOptionsItemSelected(item)) true else super.onOptionsItemSelected(item!!)
    }


    private fun dataRef(list: ArrayList<String>) {

      list.forEachIndexed { index, _ ->
          dbRef2 = FirebaseDatabase.getInstance("https://digibell-90668-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("Users").child(list[index])
          dbRef3 = FirebaseDatabase.getInstance("https://digibell-90668-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("Users")
          dbRef2.child("Help").addValueEventListener(object : ValueEventListener{
              override fun onDataChange(snapshot: DataSnapshot) {
                  val help = snapshot.getValue(String::class.java)
                  helpReceived.setText(help)
                  val h = "1234"
                  if(helpReceived.text.toString() == h){
                      Log.e("ON", "${helpReceived.text} + $h")
                      try{
                          val inx = list[index]
                          dbRef3.child(inx).child("Name").addValueEventListener(object : ValueEventListener{
                          override fun onDataChange(snapshot: DataSnapshot) {
                              name2 = snapshot.getValue(String::class.java)
                              Toast.makeText(this@Receiver," Call from $name2 incoming ",Toast.LENGTH_SHORT).show()
                              audioON.isVisible= true
                              audioON.isChecked = true
                          }

                          override fun onCancelled(error: DatabaseError) {
                              TODO("Not yet implemented")
                          }

                      })}catch (e: IndexOutOfBoundsException){
                         Log.e("Del", "Deleted user calling")
                          audioON.isVisible = false
                          audioON.isChecked = false

                      }

                  }
                  else{
                      audioON.isVisible = false
                      audioON.isChecked = false
                  }
              }
              override fun onCancelled(error: DatabaseError) {
                  TODO("Not yet implemented")
              }

          })
      }

    }


    private fun initializeSeekBar() {
        seek_bar.max = mediaPlayer.seconds

        runnable = Runnable {
            seek_bar.progress = mediaPlayer.currentSeconds

            tv_pass.text = "${mediaPlayer.currentSeconds} sec"
            val diff = mediaPlayer.seconds - mediaPlayer.currentSeconds
            tv_due.text = "$diff sec"

            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
    }

}
val MediaPlayer.seconds:Int
    get() {
        return this.duration / 1000
    }
val MediaPlayer.currentSeconds:Int
    get() {
        return this.currentPosition/1000
    }