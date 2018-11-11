package com.gmail.xbikan.pxcrus;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import android.os.Bundle;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;


public class SettingsActivity extends PreferenceActivity implements  DatePickerDialog.OnDateSetListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        EditText editText1 = ((EditTextPreference) findPreference("currency_rate_number"))
                .getEditText();
        editText1.setFilters(new InputFilter[]{new InputFilterMinMax("1", ConstantsPxC.EURO_UP_LIMIT)});

        //очистка истории поиска
        Preference clearHistoryPref = findPreference("pref_delete_history");


        clearHistoryPref
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        SearchRecentSuggestions suggestions = new
                                SearchRecentSuggestions(getBaseContext(), MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
                        suggestions.clearHistory();
                        return true;
                    }
                });

        Preference accountChange = findPreference("user_name");


        accountChange
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                     //   Intent intent = new Intent(getBaseContext(), EmailPasswordActivity.class);
                     //   startActivity(intent);
                        return true;
                    }
                });
        //проверка авторизации
        if (user != null) {
               mAuth.getCurrentUser().reload();
         }

        Preference userName = findPreference("user_name");
        if (user != null) {
            userName.setSummary(user.getEmail());
        } else {
            PreferenceScreen pPreferenceScreen = (PreferenceScreen) findPreference("preferenceScreen");
            PreferenceCategory mCategory = (PreferenceCategory) findPreference("pref_key_cust");
            pPreferenceScreen.removePreference(mCategory);
            userName.setSummary("нет аккаунта");
        }


    }

    private void fillUserViews() {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.getCurrentUser()
                .reload()
                .addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(Exception e) {

                        PreferenceScreen pPreferenceScreen = (PreferenceScreen) findPreference("preferenceScreen");
                        PreferenceCategory mCategory = (PreferenceCategory) findPreference("pref_key_cust");
                        pPreferenceScreen.removePreference(mCategory);
                    }
                });
       // auth.getCurrentUser()
       //         .reload().addOnSuccessListener(new OnSuccessListener<Void>() {

         //   @Override
     //       public void onSuccess(Void aVoid) {
      //          // addPreferencesFromResource(R.xml.preferences);
     //       }
   //     });

    }

    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Log.i("dasd","year "+i+" month "+i1+" day "+i2);
    }
    private void showDateDialog(){
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this,this, year, month, day).show();

    }
}

