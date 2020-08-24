package com.example.messengerapp.Model

class Chat {

    private var sender: String = ""
    private var message: String = ""
    private var receiver: String = ""
    private var isseen: Boolean = false
    private var messageid: String = ""
    private var url: String = ""

    constructor()

    constructor(
        sender: String,
        message: String,
        receiver: String,
        isseen: Boolean,
        messageid: String,
        url: String
    ) {
        this.sender = sender
        this.message = message
        this.receiver = receiver
        this.isseen = isseen
        this.messageid = messageid
        this.url = url
    }

    fun getSender(): String {
        return sender
    }

    fun setSender(sender: String) {
        this.sender = sender!!
    }

    fun getMessage(): String {
        return message
    }

    fun setMessage(message: String) {
        this.message = message!!
    }

    fun getReceiver(): String {
        return receiver
    }

    fun setReceiver(receiver: String) {
        this.receiver = receiver!!
    }

    fun getIsSeen(): Boolean {
        return isseen
    }

    fun setIsSeen(isseen: Boolean) {
        this.isseen = isseen!!
    }

    fun getMessageId(): String {
        return messageid
    }

    fun setMessageId(messageid: String) {
        this.sender = sender!!
    }

    fun getUrl(): String {
        return url
    }

    fun setUrl(url: String) {
        this.url = url!!
    }


}