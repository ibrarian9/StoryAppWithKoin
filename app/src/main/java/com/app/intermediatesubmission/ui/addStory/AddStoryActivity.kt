package com.app.intermediatesubmission.ui.addStory

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.app.intermediatesubmission.R
import com.app.intermediatesubmission.databinding.ActivityAddStoryBinding
import com.app.intermediatesubmission.di.Injection.messageToast
import com.app.intermediatesubmission.ui.listStory.ListStoryActivity
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddStoryActivity : AppCompatActivity() {

    private lateinit var bind: ActivityAddStoryBinding
    private var imageFile: File? = null
    private var imageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
    private val addStoryViewModel: AddStoryViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(bind.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bind.apply {

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@AddStoryActivity)

            btnGallery.setOnClickListener {
                resultLauncherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            btnCamera.setOnClickListener {
                imageUri = getImageUri(this@AddStoryActivity)
                imageUri?.let {
                    resultLauncherCamera.launch(it)
                }
            }

            addStoryViewModel.loading.observe(this@AddStoryActivity) { isLoading ->
                loading.visibility = if (isLoading) View.VISIBLE else View.GONE
            }

            addStoryViewModel.resultStory.observe(this@AddStoryActivity) { result ->
                result.onSuccess {
                    alertSuccess()
                }.onFailure {
                    alertFailed()
                }
            }

            btnUpload.setOnClickListener {

                btnUpload.isEnabled = false

                val dataDescription = edDesc.text.toString().trim()
                when {
                    dataDescription.isEmpty() -> {
                        checkData("Deskripsi Belum Diisi")
                        return@setOnClickListener
                    }
                    imageFile == null -> {
                        checkData("Image Belum Diisi")
                        return@setOnClickListener
                    }
                    else -> {

                        if (swLocation.isChecked) {
                            requestLocation(dataDescription)
                        } else {
                            uploadStory(dataDescription = dataDescription, latitude = null, longitude = null)
                        }

                        btnUpload.isEnabled = true
                    }
                }
            }
        }
    }

    private fun requestLocation(dataDescription: String) {
        if (checkLocationPermission()){
            getMyLocation(dataDescription)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this@AddStoryActivity, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLocation(description: String) {
        if (ActivityCompat.checkSelfPermission(
                this@AddStoryActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@AddStoryActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                uploadStory(description, location.latitude, location.longitude)
            } else {
                messageToast(this@AddStoryActivity,"Failed to get location")
            }
        }.addOnFailureListener {
            messageToast(this@AddStoryActivity,"Failed to get location")
        }
    }

    private fun uploadStory(dataDescription: String, latitude: Double?, longitude: Double?) {
        lifecycleScope.launch {
            val compressedFile = withContext(Dispatchers.IO) {
                Compressor(this@AddStoryActivity).compressToFile(imageFile)
            }

            val descRequest = dataDescription.toRequestBody("text/plain".toMediaType())
            val imageRequest = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultiPart = MultipartBody.Part.createFormData(
                "photo", compressedFile.name, imageRequest
            )
            if (latitude != null && longitude != null) {
                val dataLat = latitude.toString().toRequestBody("text/plain".toMediaType())
                val dataLong = longitude.toString().toRequestBody("text/plain".toMediaType())
                addStoryViewModel.postStory(
                    desc = descRequest,
                    image = imageMultiPart,
                    lat = dataLat,
                    long = dataLong
                )
            } else {
                addStoryViewModel.postStory(
                    desc = descRequest,
                    image = imageMultiPart,
                    lat = null,
                    long = null
                )
            }

            bind.btnUpload.isEnabled = true

        }

    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getMyLocation(bind.edDesc.text.toString().trim())
            } else {
                messageToast(this@AddStoryActivity,"Location permission denied")
            }
        }

    private fun checkData(pesan: String) {
        bind.btnUpload.isEnabled = true
        messageToast(this@AddStoryActivity, pesan)
    }

    private val resultLauncherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            Glide.with(this@AddStoryActivity).load(uri).fitCenter().into(bind.imageView)

            getPath(this@AddStoryActivity, uri)?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    imageFile = file
                } else {
                    messageToast(this@AddStoryActivity, "File not found")
                }
            }
        } else {
            messageToast(this@AddStoryActivity, "No image selected")
        }
    }

    private val resultLauncherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ){ isSuccess ->
        if (isSuccess && imageUri != null) {
            Glide.with(this).load(imageUri).fitCenter().into(bind.imageView)

            getPath(this@AddStoryActivity, imageUri!!)?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    imageFile = file
                } else {
                    messageToast(this@AddStoryActivity, "File not found")
                }
            }
        } else {
            messageToast(this@AddStoryActivity, "Image capture failed")
        }
    }

    private fun alertFailed() {
        AlertDialog.Builder(this).apply {
            setTitle("Failed")
            setMessage("Gagal Memposting")
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    private fun alertSuccess() {
        AlertDialog.Builder(this).apply {
            setTitle("Yeah")
            setMessage("Berhasil Memposting")
            setPositiveButton("lanjut") { _, _ ->
                val i = Intent(context, ListStoryActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
                finish()
            }
            create()
            show()
        }
    }

    private fun getPath(context: Context, uri: Uri): String? {
        var realPath: String? = null
        val data = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, data, null, null, null)
        cursor?.use {
            val indexColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            realPath = it.getString(indexColumn)
        }
        return realPath
    }

    private fun getImageUri(context: Context): Uri? {
        var uri: Uri? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpeg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
            }
            uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
        }
        return uri
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    }
}
