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

        fab.setOnClickListener{
            if(!fabopened) {
                fabopened = true
                fab_map.animate().translationY(-resources.getDimension(R.dimen.standard_66))
                fab_time.animate().translationY(-resources.getDimension(R.dimen.standard_116))
            }
            else{
                fab_map.animate().translationY(0F)
                fab_time.animate().translationY(0F)
            }
        }
        test_btn.setOnClickListener{
            val intent = Intent(applicationContext, TimeActivity::class.java)
        }
    }
}
