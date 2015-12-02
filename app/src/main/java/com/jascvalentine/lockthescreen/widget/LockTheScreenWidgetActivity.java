package com.jascvalentine.lockthescreen.widget;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.widget.Toast;

import com.jascvalentine.lockthescreen.R;

public class LockTheScreenWidgetActivity extends Activity {

    private ComponentName mDeviceAdminSample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mDeviceAdminSample == null) {
            mDeviceAdminSample = new ComponentName(this, LockTheScreenWidgetConfigureActivity.DeviceAdminSampleReceiver.class);
        }

        try {
            DevicePolicyManager mDPM = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
            // check if device admin is enabled
            if (mDPM.isAdminActive(mDeviceAdminSample)) {
                mDPM.lockNow();
            } else {
                Toast.makeText(this, R.string.reenable_device_admin, Toast.LENGTH_LONG).show();
            }
        } finally {
            finish();
        }
    }
}
