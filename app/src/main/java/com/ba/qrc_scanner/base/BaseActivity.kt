package com.ba.qrc_scanner.base

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import com.ba.qrc_scanner.R
import com.ba.qrc_scanner.utils.CameraPermissionTextProvider
import com.ba.qrc_scanner.utils.DefaultPermissionTextProvider
import com.ba.qrc_scanner.utils.MediaWritePermissionTextProvider
import com.ba.qrc_scanner.utils.NotificationPermissionTextProvider
import com.ba.qrc_scanner.utils.PermissionTextProvider
import com.ba.qrc_scanner.utils.PhoneCallPermissionTextProvider
import com.ba.qrc_scanner.utils.RecordAudioPermissionTextProvider
import com.ba.qrc_scanner.utils.getVersionName
import com.ba.qrc_scanner.utils.isNetworkConnected
import com.ba.qrc_scanner.utils.showPermissionDialog


abstract class BaseActivity<V : ViewDataBinding?, VM : BaseViewModel?> :
    AppCompatActivity(), BaseFragment.Callback {
    protected var binding: V? = null
    protected var viewModel: VM? = null
    private var mProgressDialog: Dialog? = null


    override fun onDestroy() {
        super.onDestroy()
        binding = null
        viewModel = null
        mProgressDialog = null
        visiblePermissionDialogQueue = MutableLiveData<String>();
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = onViewBind(savedInstanceState)
        viewModel = initViewModel()
        onCreatedView(savedInstanceState)
        permissionDialogsShow()
    }

    protected abstract fun onViewBind(savedInstanceState: Bundle?): V

    protected abstract fun initViewModel(): VM

    protected abstract fun onCreatedView(savedInstanceState: Bundle?)


    override fun onFragmentAttached() {
    }

    override fun onFragmentDetached(tag: String?) {
    }

    protected fun showLoading() {
        hideLoading()
        mProgressDialog = Dialog(this).apply {
            setContentView(R.layout.progress_dialog) // your custom loading layout
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            show()
        }
    }

    protected fun hideLoading() {
        mProgressDialog?.dismiss()
        mProgressDialog = null
    }

    protected open val isNetworkConnected: Boolean
        get() {
            return isNetworkConnected(applicationContext)
        }

    protected open val versionName: String?
        get() {
            return getVersionName(this)
        }


    fun showToast(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun turn2ActivityForResult(intent: Intent?) {
        resultLauncher.launch(intent)
    }

    protected open fun initNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissions = arrayOf(
                Manifest.permission.POST_NOTIFICATIONS
            )
            multiplePermissionLauncher.launch(permissions)
        }
    }

    protected open fun requestMultiplePermissionsSafely(permissions: Array<String>) {
        multiplePermissionLauncher.launch(permissions)
    }

    protected open fun requestSinglePermissionsSafely(permissions: String) {
        singlePermissionLauncher.launch(permissions)
    }


    private val multiplePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
        ::onPermissionResult
    )

    private var singlePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(), ::onPermissionResult
    )


    private val resultLauncher = registerForActivityResult<Intent?, ActivityResult?>(
        StartActivityForResult(),
        ActivityResultCallback { result: ActivityResult? -> this.onActivityResultListener(result) })

    protected open fun onActivityResultListener(result: ActivityResult?) {

    }


    protected open fun onPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            // Permission was granted
            Log.d("Permissions", "granted.")
        } else {
            // Permission was denied
            Log.d("Permissions", "denied.")
        }

    }

    protected open fun onPermissionResult(permissions: Map<String, Boolean>) {
        permissions.forEach { (permission, isGranted) ->
            addPermissionOnQueue(permission, isGranted);
            if (isGranted) {
                // Permission was granted
                Log.d("Permissions", "$permission granted.")
            } else {
                // Permission was denied
                Log.d("Permissions", "$permission denied.")
            }
        }
    }


    // this lets keyboard close when clicked in background
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    getSystemService<InputMethodManager>()?.hideSoftInputFromWindow(
                        v.windowToken,
                        0
                    )
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }


    protected fun permissionDialogsShow() {
        visiblePermissionDialogQueue.observe(this) { permission ->
            if (permission.isEmpty()) {
                return@observe
            }
            showPermissionDialog(
                context = this,
                permissionTextProvider = getPermissionTextProvider(permission),
                isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                    permission
                ),
                onDismiss = ::dismissDialog,
                onOkClick = {
                    dismissDialog()
                    requestMultiplePermissionsSafely(arrayOf(permission))
                },
                onGoToAppSettingsClick = ::openAppSettings
            )
        }
    }


// ---


    private fun getPermissionTextProvider(permission: String): PermissionTextProvider {
        return when (permission) {
            Manifest.permission.CAMERA -> CameraPermissionTextProvider()
            Manifest.permission.RECORD_AUDIO -> RecordAudioPermissionTextProvider()
            Manifest.permission.CALL_PHONE -> PhoneCallPermissionTextProvider()
            Manifest.permission.POST_NOTIFICATIONS -> NotificationPermissionTextProvider()
            Manifest.permission.READ_MEDIA_IMAGES -> MediaWritePermissionTextProvider()
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> MediaWritePermissionTextProvider()
            Manifest.permission.ACCESS_FINE_LOCATION -> PhoneCallPermissionTextProvider()
            else -> DefaultPermissionTextProvider()
        }
    }


    var visiblePermissionDialogQueue = MutableLiveData<String>()


    fun dismissDialog() {
        visiblePermissionDialogQueue.value = ""
    }

    fun addPermissionOnQueue(
        permission: String,
        isGranted: Boolean
    ) {
        if (!isGranted) {
            visiblePermissionDialogQueue.value = permission
        }
    }

    fun openAppSettings() {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        ).also(::startActivity)
    }

}

