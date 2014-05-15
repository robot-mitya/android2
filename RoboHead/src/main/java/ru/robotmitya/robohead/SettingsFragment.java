package ru.robotmitya.robohead;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dmitrydzz on 1/28/14.
 * Application options activity.
 * @author Dmitry Dzakhov.
 *
 */
public final class SettingsFragment extends PreferenceFragment implements OnPreferenceChangeListener {

    private static String mMasterUri;

    private static int mCameraIndex;
    private static int mFrontCameraIndex;
    private static int mBackCameraIndex;

    private static int mNumberOfCameras;

    /**
     * Robot's Bluetooth adapter MAC-address.
     */
    private static String mRoboBodyMac; // "00:12:03:31:01:22"

    private PreferenceCategory mPreferenceCategoryRosCore;

    private EditTextPreference mEditTextPreferenceMasterUri;

    private ListPreference mListPreferenceCamera;
    private ListPreference mListPreferenceFrontCamera;
    private ListPreference mListPreferenceBackCamera;

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

    public static int getFrontCameraIndex() {
        return mFrontCameraIndex;
    }

    public static int getBackCameraIndex() {
        return mBackCameraIndex;
    }

    public static void setCameraIndex(final Context context, final int cameraIndex) {
        if ((cameraIndex < -1) || (cameraIndex >= mNumberOfCameras)) {
            return;
        }

        mCameraIndex = cameraIndex;

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putString(context.getString(R.string.option_camera_key), String.valueOf(cameraIndex)).commit();
    }

    public static void setFrontCameraIndex(final Context context, final int cameraIndex) {
        if ((cameraIndex < -1) || (cameraIndex >= mNumberOfCameras)) {
            return;
        }

        mFrontCameraIndex = cameraIndex;

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putString(context.getString(R.string.option_front_camera_key), String.valueOf(cameraIndex)).commit();
    }

    public static void setBackCameraIndex(final Context context, final int cameraIndex) {
        if ((cameraIndex < -1) || (cameraIndex >= mNumberOfCameras)) {
            return;
        }

        mBackCameraIndex = cameraIndex;

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putString(context.getString(R.string.option_back_camera_key), String.valueOf(cameraIndex)).commit();
    }

    /**
     * Аксессор поля mRoboBodyMac.
     * @return MAC-адрес Bluetooth-адаптера контроллера робота.
     */
    public static String getRoboBodyMac() {
        return mRoboBodyMac;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.settings_fragment);

        String key;
        String title;

        key = getString(R.string.preference_ros_core_key);
        mPreferenceCategoryRosCore = (PreferenceCategory) this.findPreference(key);
        title = getString(R.string.preference_ros_core);
        final String currentRosCore = getCurrentRosCore();
        if (!currentRosCore.equals("")) {
           title += " (" + getCurrentRosCore() + ")";
        }
        mPreferenceCategoryRosCore.setTitle(title);

        key = getString(R.string.option_master_uri_key);
        mEditTextPreferenceMasterUri = (EditTextPreference) this.findPreference(key);
        title = getString(R.string.option_master_uri_title) + ": " + mMasterUri;
        mEditTextPreferenceMasterUri.setTitle(title);
        mEditTextPreferenceMasterUri.setOnPreferenceChangeListener(this);

        ArrayList<CharSequence> entries = new ArrayList<CharSequence>();
        ArrayList<CharSequence> values = new ArrayList<CharSequence>();
        entries.add(getString(R.string.option_camera_entry_none));
        values.add(String.valueOf(-1));
        for (int i = 0; i < mNumberOfCameras; i++) {
            entries.add(String.format(getString(R.string.option_camera_entry), i + 1));
            values.add(String.valueOf(i));
        }

        key = getString(R.string.option_camera_key);
        mListPreferenceCamera = (ListPreference) this.findPreference(key);
        mListPreferenceCamera.setEntries(entries.toArray(new CharSequence[entries.size()]));
        mListPreferenceCamera.setEntryValues(values.toArray(new CharSequence[values.size()]));
        mListPreferenceCamera.setValue(String.valueOf(mCameraIndex));
        title = getString(R.string.option_camera_title) + ": " + getCameraTextValue(mCameraIndex);
        mListPreferenceCamera.setTitle(title);
        mListPreferenceCamera.setDialogTitle(R.string.option_camera_dialog_title);
        mListPreferenceCamera.setOnPreferenceChangeListener(this);

        key = getString(R.string.option_front_camera_key);
        mListPreferenceFrontCamera = (ListPreference) this.findPreference(key);
        mListPreferenceFrontCamera.setEntries(entries.toArray(new CharSequence[entries.size()]));
        mListPreferenceFrontCamera.setEntryValues(values.toArray(new CharSequence[values.size()]));
        mListPreferenceFrontCamera.setValue(String.valueOf(mFrontCameraIndex));
        title = getString(R.string.option_front_camera_title) + ": " + getCameraTextValue(mFrontCameraIndex);
        mListPreferenceFrontCamera.setTitle(title);
        mListPreferenceFrontCamera.setDialogTitle(R.string.option_front_camera_dialog_title);
        mListPreferenceFrontCamera.setOnPreferenceChangeListener(this);

        key = getString(R.string.option_back_camera_key);
        mListPreferenceBackCamera = (ListPreference) this.findPreference(key);
        mListPreferenceBackCamera.setEntries(entries.toArray(new CharSequence[entries.size()]));
        mListPreferenceBackCamera.setEntryValues(values.toArray(new CharSequence[values.size()]));
        mListPreferenceBackCamera.setValue(String.valueOf(mBackCameraIndex));
        title = getString(R.string.option_back_camera_title) + ": " + getCameraTextValue(mBackCameraIndex);
        mListPreferenceBackCamera.setTitle(title);
        mListPreferenceBackCamera.setDialogTitle(R.string.option_back_camera_dialog_title);
        mListPreferenceBackCamera.setOnPreferenceChangeListener(this);

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


        mNumberOfCameras = Camera.getNumberOfCameras();

        key = context.getString(R.string.option_front_camera_key);
        if (mNumberOfCameras > 1) {
            defaultValue = String.valueOf(mNumberOfCameras - 1);
        } else if (mNumberOfCameras > 0) {
            defaultValue = String.valueOf(0);
        } else {
            defaultValue = String.valueOf(-1);
        }
        mFrontCameraIndex = Integer.valueOf(settings.getString(key, defaultValue));

        key = context.getString(R.string.option_back_camera_key);
        if (mNumberOfCameras > 0) {
            defaultValue = String.valueOf(0);
        } else {
            defaultValue = String.valueOf(-1);
        }
        mBackCameraIndex = Integer.valueOf(settings.getString(key, defaultValue));

        key = context.getString(R.string.option_camera_key);
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
            sendCameraSettingsWereChangedBroadcast();
            return true;
        }

        if (preference == mListPreferenceFrontCamera) {
            mFrontCameraIndex = Integer.valueOf((String) newValue);
            String title = getString(R.string.option_front_camera_title) + ": " + getCameraTextValue(mFrontCameraIndex);
            mListPreferenceFrontCamera.setTitle(title);
            sendCameraSettingsWereChangedBroadcast();
            return true;
        }

        if (preference == mListPreferenceBackCamera) {
            mBackCameraIndex = Integer.valueOf((String) newValue);
            String title = getString(R.string.option_back_camera_title) + ": " + getCameraTextValue(mBackCameraIndex);
            mListPreferenceBackCamera.setTitle(title);
            sendCameraSettingsWereChangedBroadcast();
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

    private void sendCameraSettingsWereChangedBroadcast() {
        Intent intent = new Intent(EyePreviewView.BROADCAST_CAMERA_SETTINGS_NAME);
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).sendBroadcast(intent);
    }

    private String getCurrentRosCore() {
        final String ipAddressText = getIPAddress();
        if (ipAddressText == "") {
            return "";
        } else {
            return mMasterUri.toLowerCase().replaceFirst("localhost", ipAddressText);
        }
    }

    public String getIPAddress() {
        final WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            final WifiInfo wifiInf = wifiManager.getConnectionInfo();
            if (wifiInf != null) {
                final int ipAddress = wifiInf.getIpAddress();
                return String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
            }
        }
        return "";
    }
}