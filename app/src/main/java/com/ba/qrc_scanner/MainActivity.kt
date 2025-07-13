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
import com.ba.qrc_scanner.model.TokenState
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
            viewModel?.changeTokenState(TokenState("158","0"))
        }

        observeData()
    }

    private fun observeData() {
        viewModel?.tokenStateResult?.observeResource(
            owner = this,
            onLoading = {
                showLoading()
            },
            onSuccess = { tokenState ->
                hideLoading()

            },
            onError = { error ->
                hideLoading()
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
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
            binding?.scanQrBtn?.visibility = View.GONE
            binding?.scanResultLayout?.visibility = View.VISIBLE
            binding?.scannedValueTv?.text = content
        }

    }
}