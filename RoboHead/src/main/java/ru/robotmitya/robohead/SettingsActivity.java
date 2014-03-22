package ru.robotmitya.robohead;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import java.util.ArrayList;

/**
 * Created by dmitrydzz on 1/28/14.
 * Application options activity.
 * @author Dmitry Dzakhov.
 *
 */
public final class SettingsActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    private static String mMasterUri;

    private static int mCameraIndex;

    private static int mNumberOfCameras;

    /**
     * Robot's Bluetooth adapter MAC-address.
     */
    private static String mRoboBodyMac; // "00:12:03:31:01:22"

    private EditTextPreference mEditTextPreferenceMasterUri;

    private ListPreference mListPreferenceCamera;

    /**
     * EditText for mRoboBodyMac option.
     */
    private EditTextPreference mEditTextPreferenceRoboBodyMac;

    public static String getMasterUri() {
        return mMasterUri;
    }

    public static int getCameraIndex() {
        return mCameraIndex;
    }

    public static void setCameraIndex(final Context context, final int cameraIndex) {
        if ((cameraIndex < -1) || (cameraIndex >= mNumberOfCameras)) {
            return;
        }

        mCameraIndex = cameraIndex;

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putString(context.getString(R.string.option_camera_key), String.valueOf(cameraIndex)).commit();
    }

    /**
     * Аксессор поля mRoboBodyMac.
     * @return MAC-адрес Bluetooth-адаптера контроллера робота.
     */
    public static String getRoboBodyMac() {
        return mRoboBodyMac;
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

        key = getString(R.string.option_camera_key);
        mListPreferenceCamera = (ListPreference) this.findPreference(key);
        ArrayList<CharSequence> entries = new ArrayList<CharSequence>();
        ArrayList<CharSequence> values = new ArrayList<CharSequence>();
        entries.add(getString(R.string.option_camera_entry_none));
        values.add(String.valueOf(-1));
        for (int i = 0; i < mNumberOfCameras; i++) {
            entries.add(String.format(getString(R.string.option_camera_entry), i + 1));
            values.add(String.valueOf(i));
        }
        mListPreferenceCamera.setEntries(entries.toArray(new CharSequence[entries.size()]));
        mListPreferenceCamera.setEntryValues(values.toArray(new CharSequence[values.size()]));
        mListPreferenceCamera.setValue(String.valueOf(mCameraIndex));
        title = getString(R.string.option_camera_title) + ": " + getCameraTextValue(mCameraIndex);
        mListPreferenceCamera.setTitle(title);
        mListPreferenceCamera.setDialogTitle(R.string.option_camera_dialog_title);
        mListPreferenceCamera.setOnPreferenceChangeListener(this);

        key = getString(R.string.option_robobody_mac_key);
        mEditTextPreferenceRoboBodyMac = (EditTextPreference) this.findPreference(key);
        title = getString(R.string.option_robobody_mac_title) + ": " + mRoboBodyMac;
        mEditTextPreferenceRoboBodyMac.setTitle(title);
        mEditTextPreferenceRoboBodyMac.setOnPreferenceChangeListener(this);
    }

    private String getCameraTextValue(final int value) {
        if (value < 0) {
            return getString(R.string.option_camera_entry_none);
        } else {
            return String.format(getString(R.string.option_camera_entry), value + 1);
        }
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

        key = context.getString(R.string.option_camera_key);
        mNumberOfCameras = Camera.getNumberOfCameras();
        defaultValue = String.valueOf(mNumberOfCameras - 1);
        mCameraIndex = Integer.valueOf(settings.getString(key, defaultValue));
        if (mCameraIndex >= mNumberOfCameras) {
            mCameraIndex = mNumberOfCameras - 1;
        }

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

        if (preference == mEditTextPreferenceMasterUri) {
            mMasterUri = (String) newValue;
            mEditTextPreferenceMasterUri.setTitle(R.string.option_master_uri_title);
            mEditTextPreferenceMasterUri.setTitle(mEditTextPreferenceMasterUri.getTitle() + ": " + newValue);
            return true;
        }

        if (preference == mListPreferenceCamera) {
            mCameraIndex = Integer.valueOf((String) newValue);
            String title = getString(R.string.option_camera_title) + ": " + getCameraTextValue(mCameraIndex);
            mListPreferenceCamera.setTitle(title);
            return true;
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