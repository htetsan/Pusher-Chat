package com.dev_hss.pusherchat

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev_hss.pusherchat.adapters.MessageAdapter
import com.dev_hss.pusherchat.data.Message
import com.dev_hss.pusherchat.databinding.ActivityChatBinding
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

private const val TAG = "ChatActivity"

class ChatActivity : AppCompatActivity() {

    private lateinit var adapter: MessageAdapter
    private lateinit var mBinding: ActivityChatBinding
    private val CHANNEL_NAME = "private-notifications-user_one"
    private val CLUSTER = "ap1"
    private val API_KEY = "7874809b61a13e74bda5"
    private val EVENT_NAME = "new-message"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mBinding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.messageList.layoutManager = LinearLayoutManager(this)
        adapter = MessageAdapter(this)
        mBinding.messageList.adapter = adapter

        setUpClickListener()
        setupPusher()
    }

    private fun setUpClickListener() {
        mBinding.btnSend.setOnClickListener {
            if (mBinding.txtMessage.text.isNotEmpty()) {
                val message = Message(
                    App.user,
                    mBinding.txtMessage.text.toString(),
                    Calendar.getInstance().timeInMillis
                )

                val call = ChatService.create().postMessage(message)

                call.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        resetInput()
                        if (!response.isSuccessful) {
                            Log.e(TAG, response.code().toString());
                            Toast.makeText(
                                applicationContext,
                                "Response was not successful",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        resetInput()
                        Log.e(TAG, t.toString());
                        Toast.makeText(
                            applicationContext, "Error when calling the service", Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            } else {
                Toast.makeText(
                    applicationContext, "Message should not be empty", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun resetInput() {
        // Clean text box
        mBinding.txtMessage.text.clear()

        // Hide keyboard
        val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    private fun setupPusher() {
        // it is ok just with channel
        val options = PusherOptions()
        options.setCluster(CLUSTER)

        val pusher = Pusher(API_KEY, options)
        val channel = pusher.subscribe(CHANNEL_NAME)

        channel.bind(EVENT_NAME) { channelName, eventName, data ->
            val jsonObject = JSONObject(data)

            val message = Message(
                jsonObject["user"].toString(),
                jsonObject["message"].toString(),
                jsonObject["time"].toString().toLong()
            )


            runOnUiThread {
                adapter.addMessage(message)
                // scroll the RecyclerView to the last added element
                mBinding.messageList.scrollToPosition(adapter.itemCount - 1);
            }

        }

        pusher.connect()
    }
}
