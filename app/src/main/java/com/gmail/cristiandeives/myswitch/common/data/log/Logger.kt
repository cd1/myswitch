package com.gmail.cristiandeives.myswitch.common.data.log

interface Logger {
    fun v(tag: String, msg: String, ex: Throwable? = null) =
        log(Priority.Verbose, tag, msg, ex)

    fun d(tag: String, msg: String, ex: Throwable? = null) =
        log(Priority.Debug, tag, msg, ex)

    fun i(tag: String, msg: String, ex: Throwable? = null) =
        log(Priority.Info, tag, msg, ex)

    fun w(tag: String, msg: String, ex: Throwable? = null) =
        log(Priority.Warning, tag, msg, ex)

    fun e(tag: String, msg: String, ex: Throwable? = null) =
        log(Priority.Error, tag, msg, ex)

    fun log(priority: Priority, tag: String, msg: String, ex: Throwable?)

    enum class Priority {
        Verbose,
        Debug,
        Info,
        Warning,
        Error,
    }
}