package com.dev_hss.pusherchat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dev_hss.pusherchat.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnLogin.setOnClickListener {
            if (mBinding.username.text.isNotEmpty()) {
                val user = mBinding.username.text.toString()

                App.user = user
                startActivity(Intent(this@MainActivity, ChatActivity::class.java))
            } else {
                Toast.makeText(
                    applicationContext, "Username should not be empty", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}