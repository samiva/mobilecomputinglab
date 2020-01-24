package com.example.mobilecomputing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

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
        test_btn.setOnClickListener{
            val intent = Intent(applicationContext, TimeActivity::class.java)
        }

        fab_time.setOnClickListener {
            val intent = Intent(applicationContext, TimeActivity::class.java)
            startActivity(intent)
        }

        fab_map.setOnClickListener{
            val intent = Intent(applicationContext, MapActivity::class.java)
        }
    }
}
