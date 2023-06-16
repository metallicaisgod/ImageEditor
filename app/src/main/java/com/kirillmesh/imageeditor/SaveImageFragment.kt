package com.kirillmesh.imageeditor

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kirillmesh.imageeditor.databinding.FragmentSaveImageBinding
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class SaveImageFragment : Fragment() {

    private val args by navArgs<SaveImageFragmentArgs>()

    private var _binding: FragmentSaveImageBinding? = null
    private val binding get() = _binding!!

    private val shareResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            // Optional - called as soon as the user selects an option from the system share dialog
            if(it.resultCode == Activity.RESULT_OK){
                Toast.makeText(requireContext(), "Image shared", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSaveImageBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editedImegeVIew.setImageBitmap(args.editedBitmap)

        binding.savePhotoImageView.setOnClickListener {

            val job = lifecycleScope.launch {
                saveMediaToStorage(args.editedBitmap)
            }

            lifecycleScope.launch {
                job.join()
                Toast.makeText(requireContext(), "Saved to Pictures", Toast.LENGTH_SHORT).show()
            }

            findNavController().navigate(R.id.action_saveImageFragment_to_chooseImageFragment)
        }

        binding.sharePhotoImageView.setOnClickListener {

            val job = lifecycleScope.launch {
                shareImage(args.editedBitmap)
            }

            lifecycleScope.launch {
                job.join()
            }
        }

        binding.editAnotherImageTextView.setOnClickListener {
            findNavController().navigate(R.id.action_saveImageFragment_to_chooseImageFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun saveMediaToStorage(bitmap: Bitmap) {
        //Generating a file name
        val filename = createFileName()

        //Output stream
        var fos: OutputStream? = null

        //For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //getting the contentResolver
            context?.contentResolver?.also { resolver ->

                //Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    //putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                //Inserting the contentValues to contentResolver and getting the Uri
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                //Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            //These for devices running on android < Q
            //So I don't think an explanation is needed here
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            //Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
    }

    private fun shareImage(image: Bitmap) {

        val filename = createFileName()

        val cachePath = File(requireActivity().externalCacheDir, CACHE_DIRECTORY)
        cachePath.mkdirs()

        val imageFile = File(cachePath, filename).also { file ->
            FileOutputStream(file).use { fileOutputStream ->
                image.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream)
            }
        }.apply {
            deleteOnExit()
        }

        val shareImageFileUri: Uri = FileProvider.getUriForFile(
            requireActivity(),
            requireContext().applicationContext.packageName + ".provider",
            imageFile
        )
       // val shareMessage: String = "Your message that should get attached to the shared message."

        // Create the intent
        val intent = Intent(Intent.ACTION_SEND).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_STREAM, shareImageFileUri)
           // putExtra(Intent.EXTRA_TEXT, shareMessage)
            type = "image/jpeg"
        }

        // Initialize the share chooser
        val chooserTitle = getString(R.string.share_your_iamge)
        val chooser = Intent.createChooser(intent, chooserTitle)
        val resInfoList: List<ResolveInfo> =
            requireContext().packageManager.queryIntentActivities(
                chooser,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        for (resolveInfo in resInfoList) {
            val packageName: String = resolveInfo.activityInfo.packageName
            requireContext().grantUriPermission(
                packageName,
                shareImageFileUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

        shareResult.launch(chooser)
    }

    private fun createFileName() = "${System.currentTimeMillis()}.jpg"

    companion object {
        private const val CACHE_DIRECTORY = "our_images/"
    }
}