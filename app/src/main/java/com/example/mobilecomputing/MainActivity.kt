package com.example.mobilecomputing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var fabopened = false
        fab_time.translationY = 0F
        fab_map.translationY = 0F
        fab.setOnClickListener{
            if(!fabopened) {
                fabopened = true
                fab_map.animate().translationY(-resources.getDimension(R.dimen.standard_66))
                fab_time.animate().translationY(-resources.getDimension(R.dimen.standard_116))
            }
            else{
                fabopened = false
                fab_map.animate().translationY(0F)
                fab_time.animate().translationY(0F)
            }
        }


        fab_time.setOnClickListener {
            val intent = Intent(applicationContext, TimeActivity::class.java)
            startActivity(intent)
        }

        fab_map.setOnClickListener{
            val intent = Intent(applicationContext, MapActivity::class.java)
            startActivity(intent)
        }



    }

    override fun onResume() {
        super.onResume()

        refreshList()
    }
    private fun refreshList() {
        doAsync {
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "reminders").build()
            val reminders = db.reminderDao().getReminders()
            db.close()

            uiThread {
                if(reminders.isNotEmpty()) {
                    val adapter = ReminderAdapter(applicationContext, reminders)
                    mylist.adapter = adapter
                } else {
                    toast("No reminders")
                }
            }
        }
    }
}
