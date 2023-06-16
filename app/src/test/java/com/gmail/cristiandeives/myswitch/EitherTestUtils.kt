package com.gmail.cristiandeives.myswitch

import arrow.core.Either
import com.google.common.truth.Subject

fun Subject.isRight() {
    this.isInstanceOf(Either.Right::class.java)
}

fun Subject.isLeft() {
    this.isInstanceOf(Either.Left::class.java)
}