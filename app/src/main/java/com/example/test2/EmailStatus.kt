package com.example.test2

import android.graphics.Color

enum class EmailStatus private constructor(internal var resId: Int, color: Int) {

    AVAILABLE(R.string.email_status_available, Color.parseColor("#149c0b")),
    TAKEN(R.string.email_status_taken, Color.RED);

    var color: Int = 0
        internal set

    init {
        this.color = color
    }

    fun getText(ctx: android.content.Context): CharSequence {
        return ctx.getText(resId)
    }

    companion object {

        fun cekEmailStatus(arrUser: ArrayList<UserCls>, email: String): EmailStatus {

            arrUser.forEach { it -> if (it.Email == email) return TAKEN }

            return AVAILABLE
        }

    }
}