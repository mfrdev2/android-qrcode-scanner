package com.ba.qrc_scanner

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ba.qrc_scanner.base.BaseActivity
import com.ba.qrc_scanner.databinding.ActivityMainBinding
import com.ba.qrc_scanner.utils.dialog.ResultDialog
import com.ba.qrc_scanner.utils.remote.observeResource
import com.ba.qrc_scanner.viewmodel.MainViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {


    override fun onViewBind(savedInstanceState: Bundle?): ActivityMainBinding {
        return DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    override fun initViewModel(): MainViewModel {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        return ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreatedView(savedInstanceState: Bundle?) {
        binding?.viewModel = viewModel;
        binding?.lifecycleOwner = this
        binding?.scanQrBtn?.setOnClickListener {
            requestMultiplePermissionsSafely(
                arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }

        binding?.reScanQrBtn?.setOnClickListener {
            requestMultiplePermissionsSafely(
                arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }

        binding?.approvedBtn?.setOnClickListener {
            binding?.approvedBtn?.isEnabled = false
            viewModel?.changeTokenState()
        }

        binding?.homeBtn?.setOnClickListener {
            initHomeView()
        }

        observeData()
        initHomeView()
    }

    private fun initHomeView() {
        binding?.scanResultLayout?.visibility = View.GONE
        binding?.scanQrBtn?.visibility = View.VISIBLE
        binding?.versionTv?.visibility = View.VISIBLE
        viewModel?.initScanResult(null)
    }

    private fun initScanDetailsView() {
        binding?.scanQrBtn?.visibility = View.GONE
        binding?.versionTv?.visibility = View.GONE
        binding?.scanResultLayout?.visibility = View.VISIBLE
    }

    private fun observeData() {
        val resultDialog = ResultDialog(this)
        viewModel?.tokenStateResult?.observeResource(
            owner = this,
            onLoading = {
                showLoading()
            },
            onSuccess = { tokenState ->
                binding?.approvedBtn?.isEnabled = true
                hideLoading()
                resultDialog.showSuccess(
                    title = "Token Approved!",
                    message = "Your token has been successfully processed and approved.",
                    buttonText = "Continue"
                ) {
                    initHomeView();
                }

            },
            onError = { error ->
                binding?.approvedBtn?.isEnabled = true
                hideLoading()
                resultDialog.showError(
                    title = "Approval Failed",
                    message = error ?: "An unexpected error occurred. Please try again.",
                    buttonText = "Retry"
                ) {
                    // Optional: Add retry logic or other actions
                    // retryTokenApproval()
                }
            }
        )
    }



    override fun onPermissionResult(permissions: Map<String, Boolean>) {
        super.onPermissionResult(permissions)
        val isAllGranted = permissions.values.all { it }
        if (isAllGranted) {
            val qrCode = ScanOptions().setPrompt("Scan QR code")
                .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            scannerLauncher.launch(qrCode)
        }
    }

    private val scannerLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ) { result ->

        if (result.contents == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        } else {
            var content: String = result.contents;
            if (content.isEmpty()) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
                return@registerForActivityResult
            }
            initScanDetailsView()
            viewModel?.initScanResult(content)
        }

    }
}