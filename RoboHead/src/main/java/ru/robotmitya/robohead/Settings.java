package ru.robotmitya.robohead;

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
public final class Settings extends PreferenceActivity implements OnPreferenceChangeListener {

    /**
     * Robot's Bluetooth adapter MAC-address.
     */
    private static String mRoboBodyMac; // "00:12:03:31:01:22"

    /**
     * EditText for mRoboBodyMac option.
     */
    private EditTextPreference mEditTextPreferenceRoboBodyMac;

    /**
     * Аксессор поля mRoboBodyMac.
     * @return MAC-адрес Bluetooth-адаптера контроллера робота.
     */
    public static String getRoboBodyMac() {
        return mRoboBodyMac;
    }

    /**
     * Длина сообщений, между уровнями ПК, телефон, контроллер.
     */
    public static final int MESSAGE_LENGTH = 5;

    /**
     * Длина идентификатора в сообщении.
     */
    public static final int MESSAGE_IDENTIFIER_LENGTH = 1;

    /**
     * Длина значения в сообщении.
     */
    public static final int MESSAGE_VALUE_LENGTH = 4;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.settings);

        String key;
        String title;

        key = getString(R.string.option_robobody_mac_key);
        mEditTextPreferenceRoboBodyMac = (EditTextPreference) this.findPreference(key);
        title = getString(R.string.option_robobody_mac_title) + ": " + mRoboBodyMac;
        mEditTextPreferenceRoboBodyMac.setTitle(title);
        mEditTextPreferenceRoboBodyMac.setOnPreferenceChangeListener(this);
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

        key = context.getString(R.string.option_robobody_mac_key);
        defaultValue = context.getString(R.string.option_robobody_mac_default_value);
        mRoboBodyMac = settings.getString(key, defaultValue);
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

        if (preference == mEditTextPreferenceRoboBodyMac) {
            mRoboBodyMac = (String) newValue;
            mEditTextPreferenceRoboBodyMac.setTitle(R.string.option_robobody_mac_title);
            mEditTextPreferenceRoboBodyMac.setTitle(mEditTextPreferenceRoboBodyMac.getTitle() + ": " + newValue);
            return true;
        }

        return false;
    }
}