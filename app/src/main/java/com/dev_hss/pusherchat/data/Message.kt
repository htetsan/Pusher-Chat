package com.dev_hss.pusherchat.data

data class Message(
    var user: String,
    var message: String,
    var time: Long
) {
}