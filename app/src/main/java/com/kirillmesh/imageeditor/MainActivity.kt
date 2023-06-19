package com.kirillmesh.imageeditor

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.kirillmesh.imageeditor.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val activityResultLauncher = registerForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
    )
    { permissions ->
        // Handle Permission granted/rejected
        var permissionGranted = true
        permissions.entries.forEach {
            if (it.key in REQUIRED_PERMISSIONS && !it.value)
                permissionGranted = false
        }
        if (!permissionGranted) {
            Toast.makeText(
                this,
                "Permission request denied",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (!allPermissionsGranted()) {
                requestPermissionsFromFragment()
            }
        }

        if(intent != null){


            val bitmapUri = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
            } else {
                intent.getParcelableExtra(Intent.EXTRA_STREAM)
            }

            bitmapUri?.let{
                val bitmap = if (Build.VERSION.SDK_INT > 27) {
                    // on newer versions of Android, use the new decodeBitmap method
                    val source: ImageDecoder.Source =
                        ImageDecoder.createSource(
                            contentResolver,
                            bitmapUri
                        )
                    ImageDecoder.decodeBitmap(source) {
                            decoder,
                            _,
                            _ ->
                        decoder.isMutableRequired = true
                    }
                } else {
                    // support older versions of Android by using getBitmap
                    MediaStore.Images.Media.getBitmap(
                        contentResolver,
                        it
                    )
                }
                if(bitmap != null){
                findNavController(R.id.nav_host_fragment_content_main)
                    .navigate(
                        ChooseImageFragmentDirections
                            .actionChooseImageFragmentToEditImageFragment(
                                Utils.resizePhoto(bitmap, 4)
                            )
                    )
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //FIX: TransactionTooLargeException when sharing image via intent. Clear the Activity's bundle of the subsidiary fragments' bundles.
        outState.clear()
    }

    private fun requestPermissionsFromFragment() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS =
            mutableListOf<String>().apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}