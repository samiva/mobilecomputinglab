package com.example.mobilecomputing

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
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
        mylist.onItemClickListener = AdapterView.OnItemClickListener{_,_, position, _->
            val selected =mylist.adapter.getItem(position) as Reminder

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete reminder?")
                .setMessage(selected.message)
                .setPositiveButton("Delete" ){_,_->

                    if(selected.time != null) {
                        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        val intent = Intent(this, ReminderReceiver::class.java)
                        val pending = PendingIntent.getBroadcast(this, selected.uid!!, intent, PendingIntent.FLAG_ONE_SHOT)
                        manager.cancel(pending)
                    }
                    doAsync {
                        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "reminders").build()
                       // db.reminderDao().delete(selected.uid!!)
                        db.close()
                        refreshList()
                    }
                }
                .setNegativeButton("Cancel"){dialog,_->
                    dialog.dismiss()
                }
                .show()
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
