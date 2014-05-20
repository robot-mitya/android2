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

import org.json.JSONException;

import java.util.ArrayList;

import ru.robotmitya.robocommonlib.AppConst;
import ru.robotmitya.robocommonlib.Log;

/**
 * Created by dmitrydzz on 1/28/14.
 * Application options activity.
 * @author Dmitry Dzakhov.
 *
 */
public final class SettingsFragment extends PreferenceFragment implements OnPreferenceChangeListener {

    private static String mMasterUri;

    private static CameraSizesSet mCameraSizesSet;

    private static int mCameraIndex;
    private static String mFirstCameraMode;
    private static String mSecondCameraMode;

    /**
     * Robot's Bluetooth adapter MAC-address.
     */
    private static String mRoboBodyMac; // "00:12:03:31:01:22"

    private EditTextPreference mEditTextPreferenceMasterUri;

    private ListPreference mListPreferenceCamera;
    private ListPreference mListPreferenceFirstCameraMode;
    private ListPreference mListPreferenceSecondCameraMode;

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

    public static String getFirstCameraMode() {
        return mFirstCameraMode;
    }

    public static String getSecondCameraMode() {
        return mSecondCameraMode;
    }

    public static void setCameraIndex(final Context context, final int cameraIndex) {
        mCameraIndex = cameraIndex;

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putString(context.getString(R.string.option_camera_index_key), String.valueOf(cameraIndex)).commit();
    }

/*
    public static void setFrontCameraIndex(final Context context, final int cameraIndex) {
        if ((cameraIndex < -1) || (cameraIndex >= mNumberOfCameras)) {
            return;
        }

        mFirstCameraMode = cameraIndex;

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putString(context.getString(R.string.option_first_camera_mode_key), String.valueOf(cameraIndex)).commit();
    }

    public static void setBackCameraIndex(final Context context, final int cameraIndex) {
        if ((cameraIndex < -1) || (cameraIndex >= mNumberOfCameras)) {
            return;
        }

        mSecondCameraMode = cameraIndex;

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putString(context.getString(R.string.option_second_camera_mode_key), String.valueOf(cameraIndex)).commit();
    }
*/

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
        PreferenceCategory preferenceCategoryRosCore = (PreferenceCategory) this.findPreference(key);
        if (preferenceCategoryRosCore != null) {
            title = getString(R.string.preference_ros_core);
            final String currentRosCore = getCurrentRosCore();
            if (!currentRosCore.equals("")) {
                title += " (" + getCurrentRosCore() + ")";
            }
            preferenceCategoryRosCore.setTitle(title);
        }

        key = getString(R.string.option_master_uri_key);
        mEditTextPreferenceMasterUri = (EditTextPreference) this.findPreference(key);
        title = getString(R.string.option_master_uri_title) + ": " + mMasterUri;
        mEditTextPreferenceMasterUri.setTitle(title);
        mEditTextPreferenceMasterUri.setOnPreferenceChangeListener(this);

        ArrayList<CharSequence> cameraEntries = getCameraEntries();
        ArrayList<CharSequence> cameraValues = getCameraValues();

        key = getString(R.string.option_camera_index_key);
        mListPreferenceCamera = (ListPreference) this.findPreference(key);
        mListPreferenceCamera.setEntries(cameraEntries.toArray(new CharSequence[cameraEntries.size()]));
        mListPreferenceCamera.setEntryValues(cameraValues.toArray(new CharSequence[cameraValues.size()]));
        mListPreferenceCamera.setValue(String.valueOf(mCameraIndex));
        title = getString(R.string.option_camera_index_title) + ": " + getCameraValueDescription(mCameraIndex);
        mListPreferenceCamera.setTitle(title);
        mListPreferenceCamera.setDialogTitle(R.string.option_camera_index_dialog_title);
        mListPreferenceCamera.setOnPreferenceChangeListener(this);

        ArrayList<CharSequence> cameraModeEntries = getCameraModeEntries(mCameraSizesSet);
        ArrayList<CharSequence> cameraModeValues = getCameraModeValues(mCameraSizesSet);

        key = getString(R.string.option_first_camera_mode_key);
        mListPreferenceFirstCameraMode = (ListPreference) this.findPreference(key);
        mListPreferenceFirstCameraMode.setEntries(cameraModeEntries.toArray(new CharSequence[cameraModeEntries.size()]));
        mListPreferenceFirstCameraMode.setEntryValues(cameraModeValues.toArray(new CharSequence[cameraModeValues.size()]));
        mListPreferenceFirstCameraMode.setValue(mFirstCameraMode);
        title = getString(R.string.option_first_camera_mode_title) + ": " + getCameraModeValueDescription(mFirstCameraMode, mCameraSizesSet);
        mListPreferenceFirstCameraMode.setTitle(title);
        mListPreferenceFirstCameraMode.setDialogTitle(R.string.option_first_camera_mode_dialog_title);
        mListPreferenceFirstCameraMode.setOnPreferenceChangeListener(this);

        key = getString(R.string.option_second_camera_mode_key);
        mListPreferenceSecondCameraMode = (ListPreference) this.findPreference(key);
        mListPreferenceSecondCameraMode.setEntries(cameraModeEntries.toArray(new CharSequence[cameraModeEntries.size()]));
        mListPreferenceSecondCameraMode.setEntryValues(cameraModeValues.toArray(new CharSequence[cameraModeValues.size()]));
        mListPreferenceSecondCameraMode.setValue(String.valueOf(mSecondCameraMode));
        title = getString(R.string.option_second_camera_mode_title) + ": " + getCameraModeValueDescription(mSecondCameraMode, mCameraSizesSet);
        mListPreferenceSecondCameraMode.setTitle(title);
        mListPreferenceSecondCameraMode.setDialogTitle(R.string.option_second_camera_mode_dialog_title);
        mListPreferenceSecondCameraMode.setOnPreferenceChangeListener(this);

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

        key = context.getString(R.string.option_master_uri_key);
        defaultValue = context.getString(R.string.option_master_uri_default_value);
        mMasterUri = settings.getString(key, defaultValue);

        // Loading static mCameraSizesSet:
        loadCameraSizesSet(context);

        final int numberOfCameras = Camera.getNumberOfCameras();

        key = context.getString(R.string.option_first_camera_mode_key);
        if (numberOfCameras == 0) {
            defaultValue = integerToHex(0xff, 0xff);
        } else {
            defaultValue = integerToHex(
                    mCameraSizesSet.get(mCameraSizesSet.length() - 1).getCameraIndex(),
                    0xff);
        }
        mFirstCameraMode = settings.getString(key, defaultValue);

        key = context.getString(R.string.option_second_camera_mode_key);
        if (numberOfCameras == 0) {
            defaultValue = integerToHex(0xff, 0xff);
        } else {
            defaultValue = integerToHex(
                    mCameraSizesSet.get(0).getCameraIndex(),
                    0xff);
        }
        mSecondCameraMode = settings.getString(key, defaultValue);

        key = context.getString(R.string.option_camera_index_key);
        if (numberOfCameras == 0) {
            defaultValue = String.valueOf(AppConst.Camera.DISABLED);
        } else {
            defaultValue = String.valueOf(AppConst.Camera.FIRST);
        }
        mCameraIndex = Integer.valueOf(settings.getString(key, defaultValue));

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
            String title = getString(R.string.option_camera_index_title) + ": " + getCameraValueDescription(mCameraIndex);
            mListPreferenceCamera.setTitle(title);
            sendCameraSettingsWereChangedBroadcast();
            return true;
        }

        if (preference == mListPreferenceFirstCameraMode) {
            mFirstCameraMode = (String) newValue;
            String title = getString(R.string.option_first_camera_mode_title) + ": " + getCameraModeValueDescription(mFirstCameraMode, mCameraSizesSet);
            mListPreferenceFirstCameraMode.setTitle(title);
            sendCameraSettingsWereChangedBroadcast();
            return true;
        }

        if (preference == mListPreferenceSecondCameraMode) {
            mSecondCameraMode = (String) newValue;
            String title = getString(R.string.option_second_camera_mode_title) + ": " + getCameraModeValueDescription(mSecondCameraMode, mCameraSizesSet);
            mListPreferenceSecondCameraMode.setTitle(title);
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
        if ((getActivity() != null) && (getActivity().getApplicationContext() != null)) {
            LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).sendBroadcast(intent);
        }
    }

    private String getCurrentRosCore() {
        final String ipAddressText = getIPAddress();
        if (ipAddressText.equals("")) {
            return "";
        } else {
            return mMasterUri
                    .toLowerCase()
                    .replaceFirst("localhost", ipAddressText);
        }
    }

    public String getIPAddress() {
        if ((getActivity() != null) && (getActivity().getApplicationContext() != null)) {
            final WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                final WifiInfo wifiInf = wifiManager.getConnectionInfo();
                if (wifiInf != null) {
                    final int ipAddress = wifiInf.getIpAddress();
                    if (ipAddress != 0) {
                        return String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
                    }
                }
            }
        }
        return "";
    }

    private static void loadCameraSizesSet(final Context context) {
        mCameraSizesSet = new CameraSizesSet();

        // Load preference jsonCameraSizesSet only once after app's first launch.
        // Next time we'll read it from preference value.
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonCameraSizesSet = settings.getString(context.getString(R.string.option_json_camera_sizes_set), "");
        if (jsonCameraSizesSet.equals("")) {
            Log.d(mCameraSizesSet, "First load");
            mCameraSizesSet.load();
            try {
                jsonCameraSizesSet = mCameraSizesSet.toJson();
            } catch (JSONException e) {
                Log.e(mCameraSizesSet, e.getMessage());
            }
            settings.edit().putString(context.getString(R.string.option_json_camera_sizes_set), jsonCameraSizesSet).commit();
        } else {
            try {
                mCameraSizesSet.fromJson(jsonCameraSizesSet);
            } catch (JSONException e) {
                Log.e(mCameraSizesSet, e.getMessage());
            }
        }

        Log.d(mCameraSizesSet, "jsonCameraSizesSet = " + jsonCameraSizesSet);
    }

    public static ArrayList<CharSequence> getCameraEntries() {
        ArrayList<CharSequence> entries = new ArrayList<CharSequence>();
        ArrayList<CharSequence> values = getCameraValues();
        for (CharSequence value : values) {
            entries.add(getCameraValueDescription(Integer.parseInt((String)value)));
        }
        return entries;
    }

    public static ArrayList<CharSequence> getCameraValues() {
        ArrayList<CharSequence> values = new ArrayList<CharSequence>();
        values.add(String.valueOf(AppConst.Camera.DISABLED));
        values.add(String.valueOf(AppConst.Camera.FIRST));
        values.add(String.valueOf(AppConst.Camera.SECOND));
        return values;
    }

    public static ArrayList<CharSequence> getCameraModeEntries(final CameraSizesSet cameraSizesSet) {
        ArrayList<CharSequence> entries = new ArrayList<CharSequence>();

        ArrayList<CharSequence> values = getCameraModeValues(cameraSizesSet);
        for (CharSequence value : values) {
            entries.add(getCameraModeValueDescription(value.toString(), cameraSizesSet));
        }

        return entries;
    }

    public static ArrayList<CharSequence> getCameraModeValues(final CameraSizesSet cameraSizesSet) {
        ArrayList<CharSequence> values = new ArrayList<CharSequence>();

        values.add("FFFF");
        for (int i = 0; i < cameraSizesSet.length(); i++) {
            final CameraSizesSet.CameraSizes cameraSizes = cameraSizesSet.get(i);
            final int cameraNum = cameraSizes.getCameraIndex();
            values.add(integerToHex(cameraNum, 0xff));
            for (int j = 0; j < cameraSizes.getSizesLength(); j++) {
                values.add(integerToHex(cameraNum, j));
            }
        }

        return values;
    }

    private static String getCameraValueDescription(final int cameraIndex) {
        switch (cameraIndex) {
            case AppConst.Camera.FIRST:
                return "First camera";
            case AppConst.Camera.SECOND:
                return "Second camera";
            default:
                return "Disabled";
        }
    }

    private static String getCameraModeValueDescription(int value, CameraSizesSet cameraSizesSet) {
        //todo: Change string constants to resources
        value &= 0xffff;
        if (value == 0xffff) {
            return "Disabled";
        }
        // hiByte is the camera index in CameraSizesSet
        int hiByte = value & 0xff00;
        hiByte >>= 8;
        // loByte is the size index in CameraSizesSet for some cameraIndex
        int loByte = value & 0x00ff;
        String result = "Camera " + (cameraSizesSet.get(hiByte).getCameraIndex() + 1);
        if (loByte == 0xff) {
            result += " [default]";
        } else {
            CameraSizesSet.Size size = cameraSizesSet.get(hiByte).getSize(loByte);
            result += " [" + size.width + "x" + size.height + "]";
        }
        return result;
    }

    private static String getCameraModeValueDescription(String textValue, CameraSizesSet cameraSizesSet) {
        int value = Integer.parseInt(textValue, 16);
        return getCameraModeValueDescription(value, cameraSizesSet);
    }

    private static String integerToHex(int hiByte, int loByte) {
        hiByte = hiByte & 0xff;
        hiByte = hiByte << 8;
        loByte = loByte & 0xff;
        int value = hiByte + loByte;
        return Integer.toHexString(0x10000 | value).substring(1).toUpperCase();
    }

    public static int cameraModeToCameraIndex(final int cameraMode) {
        final int hiByte = (cameraMode & 0xff00) >> 8;
        if (hiByte == 0xff) {
            return -1;
        }
        return mCameraSizesSet.get(hiByte).getCameraIndex();
    }
/*
    public static int cameraModeToWidth(final int cameraMode) {
        final int hiByte = (cameraMode & 0xff00) >> 8;
        final int loByte = cameraMode & 0xff;
        if ((hiByte == 0xff) || (loByte == 0xff)) {
            return 0;
        }
        return mCameraSizesSet.get(hiByte).getSize(loByte).width;
    }

    public static int cameraModeToHeight(final int cameraMode) {
        final int hiByte = (cameraMode & 0xff00) >> 8;
        final int loByte = cameraMode & 0xff;
        if ((hiByte == 0xff) || (loByte == 0xff)) {
            return 0;
        }
        return mCameraSizesSet.get(hiByte).getSize(loByte).height;
    }
*/
}