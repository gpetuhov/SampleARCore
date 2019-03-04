package com.gpetuhov.android.samplearcore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // In this sample we do not check if ARCore is supported,
        // because ARCore is declared as required in the manifest,
        // so the app will not be visible for devices, that do not support ARCore.

        // We do not have to check Camera permission,
        // because ArFragment automatically requests the camera permission
        // before creating the AR session.

    }

    override fun onResume() {
        super.onResume()

        // We do not have to check whether ARCore is installed,
        // because ArFragment automatically checks
        // that ARCore is installed and up to date before creating the AR session.
    }
}
