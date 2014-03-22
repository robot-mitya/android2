package ru.robotmitya.roboboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by dmitrydzz on 1/28/14.
 * Application options activity.
 * @author Dmitry Dzakhov.
 *
 */
public final class SettingsActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    private static String mMasterUri;

    private EditTextPreference mEditTextPreferenceMasterUri;

    public static String getMasterUri() {
        return mMasterUri;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.settings_activity);

        String key;
        String title;

        key = getString(R.string.option_master_uri_key);
        mEditTextPreferenceMasterUri = (EditTextPreference) this.findPreference(key);
        title = getString(R.string.option_master_uri_title) + ": " + mMasterUri;
        mEditTextPreferenceMasterUri.setTitle(title);
        mEditTextPreferenceMasterUri.setOnPreferenceChangeListener(this);
    }

    /**
     * Инициализация некоторых установок.
     * @param context контекст приложения.
     */
    public static void initialize(final Context context) {
        if (context == null) {
            return;
        }

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        String key;
        String defaultValue;

        key = context.getString(R.string.option_master_uri_key);
        defaultValue = context.getString(R.string.option_master_uri_default_value);
        mMasterUri = settings.getString(key, defaultValue);
    }

    /**
     * Обработчик листнера изенений настроек.
     * @param preference изменившаяся опция.
     * @param newValue новое значение.
     * @return принять ли изменения.
     */
    public boolean onPreferenceChange(final Preference preference, final Object newValue) {
        if (preference == null) {
            return false;
        }

        if (preference == mEditTextPreferenceMasterUri) {
            mMasterUri = (String) newValue;
            mEditTextPreferenceMasterUri.setTitle(R.string.option_master_uri_title);
            mEditTextPreferenceMasterUri.setTitle(mEditTextPreferenceMasterUri.getTitle() + ": " + newValue);
            return true;
        }

        return false;
    }
}