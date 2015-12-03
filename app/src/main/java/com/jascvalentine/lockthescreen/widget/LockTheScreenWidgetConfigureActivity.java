package com.jascvalentine.lockthescreen.widget;

import android.app.Activity;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jascvalentine.lockthescreen.R;

/**
 * The configuration screen for the {@link LockTheScreenWidget LockTheScreenWidget} AppWidget.
 */
public class LockTheScreenWidgetConfigureActivity extends Activity {

    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;

    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdminSample;
    private final View.OnClickListener mEnableOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
            //intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "");
            startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
        }
    };
    private Button mEnableButton, mAddButton;
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = LockTheScreenWidgetConfigureActivity.this;

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            LockTheScreenWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public LockTheScreenWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        mDPM = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        mDeviceAdminSample = new ComponentName(this, DeviceAdminSampleReceiver.class);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.lock_the_screen_widget_configure);
        mEnableButton = (Button) findViewById(R.id.enable_button);
        mEnableButton.setOnClickListener(mEnableOnClickListener);
        mAddButton = (Button) findViewById(R.id.add_button);
        mAddButton.setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateButtonState();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check if user did not activate device administrator
        if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, R.string.need_to_enable_device_admin, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Check if Device Admin is enabled
     */
    private boolean isActiveAdmin() {
        return mDPM.isAdminActive(mDeviceAdminSample);
    }

    private void updateButtonState() {
        mEnableButton.setEnabled(!isActiveAdmin());
        mAddButton.setEnabled(isActiveAdmin());
    }

    public static class DeviceAdminSampleReceiver extends DeviceAdminReceiver {
    }
}
