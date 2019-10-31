package com.example.easypermissionsdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
                                                                EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_CAMERA = 100;
    private static final int REQUEST_CODE_CAMERA_AND_CONTACTS = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_camera).setOnClickListener(this);
        findViewById(R.id.button_contacts).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_camera:
                if (hasCameraPermission()) {
                    Log.e(TAG, "已开启权限");
                } else {
                    EasyPermissions.requestPermissions(
                            this,
                            "拍照需要相机权限",
                            REQUEST_CODE_CAMERA,
                            Manifest.permission.CAMERA);
                }
                break;
            case R.id.button_contacts:
                if (hasCameraAndContactsPermission()) {
                    Log.e(TAG, "已开启权限");
                } else {
                    EasyPermissions.requestPermissions(
                            this,
                            "该功能需要相机权限和定位权限",
                            REQUEST_CODE_CAMERA_AND_CONTACTS,
                            Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS);
                }
                break;
        }
    }

    /** 是否有相机权限 */
    private boolean hasCameraPermission() {
        return EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA);
    }

    /** 是否有相机和读取联系人权限 */
    private boolean hasCameraAndContactsPermission() {
        return EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 这一句需要加，不然后面的onPermissionsGranted onPermissionsDenied onRationaleAccepted onRationaleDenied不会调用
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 权限授予
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "已授予权限");
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "拒绝开通权限");
        // 如果用户拒绝开启权限，并勾选“不再询问”之后跳转app设置界面
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    /**
     * 用户接受权限解释
     * @param requestCode
     */
    @Override
    public void onRationaleAccepted(int requestCode) {
        Log.d(TAG, "onRationaleAccepted:" + requestCode);
    }

    /**
     * 用户拒绝权限解释
     * @param requestCode
     */
    @Override
    public void onRationaleDenied(int requestCode) {
        Log.d(TAG, "onRationaleAccepted:" + requestCode);
    }

    /**
     * 从APP设置页面返回之后
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {

            String yes = getString(R.string.yes);
            String no = getString(R.string.no);

            Toast.makeText(this, getString(R.string.returned_from_app_settings_to_activity,
                            hasCameraPermission() ? yes : no,
                            hasCameraAndContactsPermission() ? yes : no),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }
}
