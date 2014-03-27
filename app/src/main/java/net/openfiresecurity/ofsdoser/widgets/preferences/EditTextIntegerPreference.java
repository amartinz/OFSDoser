/*
 * Copyright (c) 2013. Alexander Martinz
 */

package net.openfiresecurity.ofsdoser.widgets.preferences;

import android.content.Context;
import android.preference.EditTextPreference;
import android.text.InputType;
import android.util.AttributeSet;

/**
 * Custom EditTestPreference for Integers
 */
public class EditTextIntegerPreference extends EditTextPreference {

    private Integer mInteger;

    public EditTextIntegerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
    }

    public EditTextIntegerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        getEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
    }

    public EditTextIntegerPreference(Context context) {
        super(context);
        getEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
    }

    @Override
    public void setText(String text) {
        final boolean wasBlocking = shouldDisableDependents();
        mInteger = parseInteger(text);
        mInteger = (mInteger < 100 ? 100 : mInteger);
        persistString(mInteger != null ? mInteger.toString() : null);
        final boolean isBlocking = shouldDisableDependents();
        if (isBlocking != wasBlocking) notifyDependencyChange(isBlocking);
    }

    @Override
    public String getText() {
        return mInteger != null ? mInteger.toString() : null;
    }

    private static Integer parseInteger(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 1000;
        }
    }
}
