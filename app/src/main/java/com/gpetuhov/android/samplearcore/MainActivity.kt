package com.gpetuhov.android.samplearcore

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.pawegio.kandroid.toast

// 3D model used: Aiming Sentinel by Jeremy Eyring:
// https://poly.google.com/view/61og3j-bM-G

class MainActivity : AppCompatActivity() {

    companion object {
        const val MIN_OPENGL_VERSION = 3.0
    }

    private var arFragment: ArFragment? = null
    private var modelRenderable: ModelRenderable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // In this sample we do not check if ARCore is supported,
        // because ARCore is declared as required in the manifest,
        // so the app will not be visible for devices, that do not support ARCore.

        // We do not have to check Camera permission,
        // because ArFragment automatically requests the camera permission
        // before creating the AR session.

        // But we DO check, if the device has the required OpenGL version
        if (!checkIsSupportedDeviceOrFinish(this)) {
            return
        }

        setContentView(R.layout.activity_main)

        arFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment

        // When you build a Renderable, Sceneform loads its resources in the background while returning
        // a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
        ModelRenderable.builder()
            // R.raw.model_name is created by Sceneform plugin (see build.gradle for details)
            .setSource(this, R.raw.sentinel_aiming)
            .build()
            .thenAccept { renderable -> modelRenderable = renderable }
            .exceptionally { throwable ->
                toast("Unable to load renderable")
                null
            }

        // This is needed to place our model on the detected plane,
        // at the place of the user's tap.
        arFragment?.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane, motionEvent: MotionEvent ->
            if (modelRenderable == null) {
                return@setOnTapArPlaneListener
            }

            // Create the Anchor at the place of the tap.
            val anchor = hitResult.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.setParent(arFragment?.arSceneView?.scene)

            // Create the transformable model and add it to the anchor.
            val model = TransformableNode(arFragment?.transformationSystem)
            model.setParent(anchorNode)
            model.renderable = modelRenderable
            model.select()
        }
    }

    override fun onResume() {
        super.onResume()

        // We do not have to check whether ARCore is installed,
        // because ArFragment automatically checks
        // that ARCore is installed and up to date before creating the AR session.
    }

    /**
     * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
     * on this device.
     *
     * Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
     *
     * Finishes the activity if Sceneform can not run
     */
    private fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            toast("Sceneform requires Android N or later")
            activity.finish()
            return false
        }

        val openGlVersionString = (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .deviceConfigurationInfo
            .glEsVersion

        if (openGlVersionString.toDouble() < MIN_OPENGL_VERSION) {
            toast("Sceneform requires OpenGL ES 3.0 or later")
            activity.finish()
            return false
        }

        return true
    }
}
