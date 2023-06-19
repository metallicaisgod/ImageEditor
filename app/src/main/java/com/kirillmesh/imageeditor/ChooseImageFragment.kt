package com.kirillmesh.imageeditor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kirillmesh.imageeditor.databinding.FragmentChooseImageBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ChooseImageFragment : Fragment() {

    private var _binding: FragmentChooseImageBinding? = null
    private val binding get() = _binding!!

    private lateinit var getPhotoLauncher: ActivityResultLauncher<Intent>

    lateinit var currentPhotoPath: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getPhotoLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    if (data == null) {
                        val photo = BitmapFactory.decodeFile(
                            currentPhotoPath,
                            Options().also { it.inSampleSize = 4 }
                        )
                        val orientation: Int = ExifInterface(currentPhotoPath).getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED
                        )
                        val rotatedBitmap = when (orientation) {
                            ExifInterface.ORIENTATION_ROTATE_90 -> Utils.rotateImage(photo, 90f)
                            ExifInterface.ORIENTATION_ROTATE_180 -> Utils.rotateImage(photo, 180f)
                            ExifInterface.ORIENTATION_ROTATE_270 -> Utils.rotateImage(photo, 270f)
                            ExifInterface.ORIENTATION_NORMAL -> photo
                            else -> photo
                        }
                        callEditFragment(rotatedBitmap)
                    } else {
                        val photoUri = data.data
                        photoUri?.let {
                            val photo = if (Build.VERSION.SDK_INT > 27) {
                                // on newer versions of Android, use the new decodeBitmap method
                                val source: ImageDecoder.Source =
                                    ImageDecoder.createSource(
                                        requireActivity().contentResolver,
                                        it
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
                                    requireActivity().contentResolver,
                                    it
                                )
                            }
                            callEditFragment(Utils.resizePhoto(photo, 4))
                        }
                    }
                }
            }
    }

    private fun callEditFragment(photo: Bitmap?) {

        photo?.let {
            findNavController().navigate(
                ChooseImageFragmentDirections.actionChooseImageFragmentToEditImageFragment(
                    it
                )
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChooseImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createPhotoImageView.setOnClickListener {
            takePhotoFromCamera()
        }
        binding.createPhotoButton.setOnClickListener {
            takePhotoFromCamera()
        }

        binding.choosePhotoImageView.setOnClickListener {
            choosePhotoFromGallery()
        }
        binding.choosePhotoButton.setOnClickListener {
            choosePhotoFromGallery()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun takePhotoFromCamera() {

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Log.d("ChooseFragment", ex.message.toString())
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        requireContext().getString(R.string.provider_authority),
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    getPhotoLauncher.launch(takePictureIntent)
                }
            }
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun choosePhotoFromGallery() {

        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        if (galleryIntent.resolveActivity(requireActivity().packageManager) != null) {
            getPhotoLauncher.launch(galleryIntent)
        }
    }

}