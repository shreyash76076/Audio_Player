package com.example.audioplayer

import android.text.TextUtils
import androidx.databinding.ObservableField

open class ObservableString : ObservableField<String> {
    constructor(s: String?) {
        set(s)
    }

    constructor() {}

    override fun get(): String {
        return if (super.get() == null) "" else super.get()!!
    }

    /**
     * Method for get trimmed data
     *
     * @return trimmed data
     */
    val trimmed: String
        get() {
            val stringData = get()
            return getTrimmedData(stringData)
        }

    /**
     * Get String length
     *
     * @return length
     */
    val trimmedLength: Int
        get() {
            val trimmedData = trimmed
            return if (!TextUtils.isEmpty(trimmedData)) trimmedData.length else 0
        }

    /**
     * Check is Empty String
     *
     * @return is Empty
     */
    val isEmptyData: Boolean
        get() = TextUtils.isEmpty(trimmed)

    companion object {
        fun getTrimmedData(data: String): String {
            return if (!TextUtils.isEmpty(data)) data.trim { it <= ' ' } else ""
        }
    }
}