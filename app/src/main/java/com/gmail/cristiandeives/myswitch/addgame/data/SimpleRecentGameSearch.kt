package com.gmail.cristiandeives.myswitch.addgame.data

import com.gmail.cristiandeives.myswitch.addgame.data.db.SimpleRecentGameSearch as SimpleRecentGameSearchDb

data class SimpleRecentGameSearch(
    val id: Long,
    val query: String,
)

fun SimpleRecentGameSearchDb.toData() = SimpleRecentGameSearch(
    id = this.id,
    query = this.query,
)