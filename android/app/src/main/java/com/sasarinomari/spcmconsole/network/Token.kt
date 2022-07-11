package com.sasarinomari.spcmconsole.network

import java.util.*

class Token(val token: String) {
    private val createdAt = Date()
    fun isValid() : Boolean {
        return Date().time - createdAt.time < 1000 * 60
    }
}