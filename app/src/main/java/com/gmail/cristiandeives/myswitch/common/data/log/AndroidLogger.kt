package com.gmail.cristiandeives.myswitch.common.data.log

import android.util.Log

class AndroidLogger : Logger {
    override fun log(priority: Logger.Priority, tag: String, msg: String, ex: Throwable?) {
        when (priority) {
            Logger.Priority.Verbose -> Log.v(tag, msg, ex)
            Logger.Priority.Debug -> Log.d(tag, msg, ex)
            Logger.Priority.Info -> Log.i(tag, msg, ex)
            Logger.Priority.Warning -> Log.w(tag, msg, ex)
            Logger.Priority.Error -> Log.e(tag, msg, ex)
        }
    }
}