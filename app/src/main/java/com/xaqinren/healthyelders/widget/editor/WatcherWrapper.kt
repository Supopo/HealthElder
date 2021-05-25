package com.xaqinren.healthyelders.widget.editor

import android.text.Editable
import android.text.SpanWatcher
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.ImageSpan
import java.util.concurrent.atomic.AtomicInteger

class WatcherWrapper internal constructor(val mObject: Any) : TextWatcher, SpanWatcher {
    private val mBlockCalls = AtomicInteger(0)

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        (mObject as TextWatcher).beforeTextChanged(s, start, count, after)
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        (mObject as TextWatcher).onTextChanged(s, start, before, count)
    }

    override fun afterTextChanged(s: Editable) {
        (mObject as TextWatcher).afterTextChanged(s)
    }

    /**
     * Prevent the onSpanAdded calls to DynamicLayout$ChangeWatcher if in a replace operation
     * (mBlockCalls is set) and the span that is added is an EmojiSpan.
     */
    override fun onSpanAdded(text: Spannable, what: Any, start: Int, end: Int) {
        if (mBlockCalls.get() > 0 && isImageSpan(what)) {
            return
        }
        (mObject as SpanWatcher).onSpanAdded(text, what, start, end)
    }

    /**
     * Prevent the onSpanRemoved calls to DynamicLayout$ChangeWatcher if in a replace operation
     * (mBlockCalls is set) and the span that is added is an EmojiSpan.
     */
    override fun onSpanRemoved(text: Spannable, what: Any, start: Int, end: Int) {
        if (mBlockCalls.get() > 0 && isImageSpan(what)) {
            return
        }
        (mObject as SpanWatcher).onSpanRemoved(text, what, start, end)
    }

    /**
     * Prevent the onSpanChanged calls to DynamicLayout$ChangeWatcher if in a replace operation
     * (mBlockCalls is set) and the span that is added is an EmojiSpan.
     */
    override fun onSpanChanged(text: Spannable, what: Any, ostart: Int, oend: Int, nstart: Int,
                               nend: Int) {
        if (mBlockCalls.get() > 0 && isImageSpan(what)) {
            return
        }
        (mObject as SpanWatcher).onSpanChanged(text, what, ostart, oend, nstart, nend)
    }

    internal fun blockCalls() {
        mBlockCalls.incrementAndGet()
    }

    internal fun unblockCalls() {
        mBlockCalls.decrementAndGet()
    }

    private fun isImageSpan(span: Any): Boolean {
        return span is ImageSpan
    }
}