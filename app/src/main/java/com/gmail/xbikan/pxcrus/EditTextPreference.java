package com.gmail.xbikan.pxcrus;

/**
 * Created by xbika on 26-Nov-17.
 */
import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.widget.Toast;

public class EditTextPreference extends android.preference.EditTextPreference implements Preference.OnPreferenceChangeListener{
    public EditTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextPreference(Context context) {
        super(context);
    }

    @Override
    public CharSequence getSummary() {
        String summary = super.getSummary().toString();
        return String.format(summary, getText());
    }
    protected void onDialogClosed(boolean positiveResult) {
        boolean valid = this.getEditText().getText().toString().length() > 0;
        super.onDialogClosed(valid);
    }
    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {

        return false;
    }
}
