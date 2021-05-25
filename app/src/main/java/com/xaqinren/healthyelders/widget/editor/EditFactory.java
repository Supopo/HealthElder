package com.xaqinren.healthyelders.widget.editor;

import android.text.Editable;

import androidx.annotation.GuardedBy;
import androidx.annotation.Nullable;

public class EditFactory extends Editable.Factory {
    private static final Object sInstanceLock = new Object();
    @GuardedBy("sInstanceLock")
    private static volatile Editable.Factory sInstance;

    @Nullable
    private static Class<?> sWatcherClass;

    public EditFactory() {
        try {
            String className = "android.text.DynamicLayout$ChangeWatcher";
            sWatcherClass = this.getClass().getClassLoader().loadClass(className);
        } catch (Throwable var2) {
        }
    }

    @Override
    public Editable newEditable(CharSequence source) {
        return (Editable) (sWatcherClass != null ? EmojiSpannableStringBuilder.create(sWatcherClass, source)
                : super.newEditable(source));
    }


}
