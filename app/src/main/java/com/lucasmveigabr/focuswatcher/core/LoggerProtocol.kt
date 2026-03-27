package com.lucasmveigabr.focuswatcher.core

interface LoggerProtocol {

    fun d(tag: String, message: String)
    fun e(tag: String, message: String, throwable: Throwable)

}