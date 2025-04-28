package com.ba.qrc_scanner.ui

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ba.qrc_scanner.R
import java.io.IOException

class NidProcessingActivity : AppCompatActivity() {
    private lateinit var nidImageView: ImageView
    private lateinit var statusTextView: TextView
    private lateinit var extractButton: Button
    private lateinit var doneButton: Button


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nid_processing)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.nid_process_root)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Initialize views
        nidImageView = findViewById(R.id.nidImageView)
        statusTextView = findViewById(R.id.statusTextView)
        extractButton = findViewById(R.id.extractButton)
        doneButton = findViewById(R.id.doneButton)

        // Get the NID image URI from the intent
        val nidUriString = intent.getStringExtra("nid_uri")
        if (nidUriString != null) {
            val nidUri = Uri.parse(nidUriString)

            // Display the NID image
            try {
                val inputStream = contentResolver.openInputStream(nidUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                nidImageView.setImageBitmap(bitmap)
                statusTextView.text = "NID Image Loaded Successfully"
            } catch (e: IOException) {
                statusTextView.text = "Error loading NID image: ${e.message}"
            }

            // Set up the extract button
            extractButton.setOnClickListener {
                extractNidInfo(nidUri)
            }

            // Set up the done button
            doneButton.setOnClickListener {
                finish()
            }
        } else {
            statusTextView.text = "No NID image URI provided"
            extractButton.isEnabled = false
        }


    }



    private fun extractNidInfo(nidUri: Uri) {
        // Here you would implement OCR or other image processing to extract NID information
        // For demonstration purposes, we'll just show a success message

        statusTextView.text = "Processing NID image..."

        // Simulate processing delay
        statusTextView.postDelayed({
            statusTextView.text = "NID Information Extracted:\n" +
                    "Name: John Doe\n" +
                    "ID Number: 1234567890\n" +
                    "Date of Birth: 01/01/1990\n" +
                    "Issue Date: 01/01/2020\n" +
                    "Expiry Date: 01/01/2030"

            // In a real application, you would extract this information from the image
            // using OCR technologies like Google ML Kit, Tesseract, etc.

            // Enable the done button after extraction
            doneButton.isEnabled = true
        }, 2000)
    }

}