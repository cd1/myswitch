package com.gmail.cristiandeives.myswitch.common.data.log

class FakeLogger : Logger {
    override fun log(priority: Logger.Priority, tag: String, msg: String, ex: Throwable?) {
        val logLine = buildString {
            append("[$priority] $tag: $msg")
            if (ex != null) {
                append(" (ex=${ex.message})")
            }
        }

        println(logLine)
    }
}