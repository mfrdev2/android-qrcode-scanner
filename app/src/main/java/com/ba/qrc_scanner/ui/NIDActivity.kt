package com.ba.qrc_scanner.ui

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ba.qrc_scanner.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class NIDActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView
    private lateinit var captureButton: Button
    private lateinit var nidImageView: ImageView
    private lateinit var proceedButton: Button

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var capturedImageUri: Uri? = null

    private var capturedImageBytes: ByteArray? = null
    private var compressedImageBytes: ByteArray? = null

    // Target file size (500KB)
    private val TARGET_SIZE_BYTES = 500 * 1024

    private val client = OkHttpClient()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nidactivity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.nid_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }




        // Initialize views
        previewView = findViewById(R.id.previewView)
        captureButton = findViewById(R.id.captureButton)
        nidImageView = findViewById(R.id.nidImageView)
        proceedButton = findViewById(R.id.proceedButton)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // Set up the capture button listener
        captureButton.setOnClickListener { takePhoto() }

        // Set up the proceed button listener
        proceedButton.setOnClickListener {
         /*   if (capturedImageUri != null) {
                // Process the captured NID image
               // processNidImage()
                uploadNidImage()
            } else {
                Toast.makeText(this, "Please capture NID first", Toast.LENGTH_SHORT).show()
            }*/
            uploadNidImage()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()


    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            // Image capture
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
/*        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/NID-Capture-App")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri ?: return
                    capturedImageUri = savedUri
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)

                    // Update the ImageView with the captured photo
                    nidImageView.setImageURI(savedUri)

                    // Show the captured image and hide the camera preview
                    previewView.visibility = android.view.View.GONE
                    nidImageView.visibility = android.view.View.VISIBLE
                    captureButton.text = "Retake"
                    proceedButton.visibility = android.view.View.VISIBLE

                    captureButton.setOnClickListener {
                        // Reset the view for retaking
                        previewView.visibility = android.view.View.VISIBLE
                        nidImageView.visibility = android.view.View.GONE
                        proceedButton.visibility = android.view.View.GONE
                        captureButton.text = "Capture"
                        captureButton.setOnClickListener { takePhoto() }
                    }
                }
            }
        )*/


        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create a new ImageCapture.OnImageCapturedCallback
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    // Convert the image to bytes
                    val buffer = image.planes[0].buffer
                    val bytes = ByteArray(buffer.capacity())
                    buffer.get(bytes)

                    // Save original bytes
                    capturedImageBytes = bytes

                    // Compress the image
                    compressImage(bytes)

                    // Close the image to release resources
                    image.close()
                }

                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    Toast.makeText(baseContext, "Failed to capture image: ${exc.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }
        )



    }


    private fun compressImage(imageBytes: ByteArray) {
        // Create a background thread to handle image processing
        Thread {
            try {
                // Convert byte array to bitmap
                val originalBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                // Calculate initial dimensions - reduce size if too large
                var width = originalBitmap.width
                var height = originalBitmap.height

                // Start with a reasonable resolution (max 1920px on longest side)
                val maxDimension = 1920
                if (width > maxDimension || height > maxDimension) {
                    val aspectRatio = width.toFloat() / height.toFloat()
                    if (width > height) {
                        width = maxDimension
                        height = (width / aspectRatio).toInt()
                    } else {
                        height = maxDimension
                        width = (height * aspectRatio).toInt()
                    }
                }

                // Resize bitmap to reduce memory usage
                val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, true)

                // Recycle original bitmap to free memory
                if (originalBitmap != resizedBitmap) {
                    originalBitmap.recycle()
                }

                // Start with quality 90% and adjust downward as needed
                var quality = 90
                var compressed: ByteArray

                do {
                    val stream = ByteArrayOutputStream()
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
                    compressed = stream.toByteArray()

                    // Reduce quality by 10% if still too large
                    if (compressed.size > TARGET_SIZE_BYTES) {
                        quality -= 10
                    }

                    Log.d(TAG, "Compression quality: $quality, size: ${compressed.size / 1024}KB")

                    // Break if quality gets too low
                    if (quality < 20) break

                } while (compressed.size > TARGET_SIZE_BYTES && quality > 0)

                // Save the compressed bytes
                compressedImageBytes = compressed

                // Convert compressed bytes to bitmap for preview
                val compressedBitmap = BitmapFactory.decodeByteArray(
                    compressedImageBytes, 0, compressedImageBytes!!.size
                )

                // Update UI on main thread
                runOnUiThread {
                    // Show file size
                    val fileSizeKB = compressedImageBytes!!.size / 1024

                    val msg = "Image compressed to $fileSizeKB KB with quality $quality%"

                    Log.d(TAG,msg)

                    Toast.makeText(
                        baseContext,
                        msg,
                        Toast.LENGTH_SHORT
                    ).show()

                    // Update the ImageView with the captured photo
                    nidImageView.setImageBitmap(compressedBitmap)

                    // Show the captured image and hide the camera preview
                    previewView.visibility = android.view.View.GONE
                    nidImageView.visibility = android.view.View.VISIBLE
                    captureButton.text = "Retake"
                    proceedButton.visibility = android.view.View.VISIBLE

                    captureButton.setOnClickListener {
                        // Reset the view for retaking
                        previewView.visibility = android.view.View.VISIBLE
                        nidImageView.visibility = android.view.View.GONE
                        proceedButton.visibility = android.view.View.GONE
                        captureButton.text = "Capture"
                        captureButton.setOnClickListener { takePhoto() }
                    }
                }

                // Recycle bitmap to free memory
                resizedBitmap.recycle()

            } catch (e: Exception) {
                Log.e(TAG, "Error compressing image: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(
                        baseContext,
                        "Error processing image: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.start()
    }



    private fun uploadNidImage() {
        val bytes = compressedImageBytes ?: return

        // Show uploading status
        Toast.makeText(this, "Uploading NID image...", Toast.LENGTH_SHORT).show()

        // Create a temporary file to hold the image (required for multipart)
        val tempFile = File(cacheDir, "nid_image.jpg")
        FileOutputStream(tempFile).use { outputStream ->
            outputStream.write(bytes)
        }

        // Create multipart request
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                "nid_image.jpg",
                tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            .build()

        // Create the request
        val request = Request.Builder()
            .url("http://192.168.30.123:8702/v1/public/upload") // Replace with your actual API endpoint
            .post(requestBody)
            .build()

        // Execute the request asynchronously
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
                runOnUiThread {
                    Toast.makeText(baseContext, "Upload failed: ${e.message}",
                        Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle success
                val responseBody = response.body?.string()

                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(baseContext, "Upload successful", Toast.LENGTH_SHORT).show()

                        // Navigate to processing screen or next step
                        val intent = Intent(baseContext, NidProcessingActivity::class.java).apply {
                            putExtra("response_data", responseBody)
                        }
                        startActivity(intent)
                    } else {
                        Toast.makeText(baseContext, "Upload failed: ${response.code}",
                            Toast.LENGTH_LONG).show()
                    }
                }

                // Delete temp file
                tempFile.delete()
            }
        })
    }





    private fun processNidImage() {
        // Start a new activity to process the NID image
        val intent = Intent(this, NidProcessingActivity::class.java).apply {
            putExtra("nid_uri", capturedImageUri.toString())
        }
        startActivity(intent)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "NidCaptureApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                arrayOf(android.Manifest.permission.CAMERA)
            } else {
                arrayOf(android.Manifest.permission.CAMERA)
            }
    }
}