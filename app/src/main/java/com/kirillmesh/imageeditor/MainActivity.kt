package com.kirillmesh.imageeditor

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.kirillmesh.imageeditor.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

   // private lateinit var appBarConfiguration: AppBarConfiguration
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

        //setSupportActionBar(binding.toolbar)

        //val navController = findNavController(R.id.nav_host_fragment_content_main)
        //appBarConfiguration = AppBarConfiguration(navController.graph)
        //setupActionBarWithNavController(navController, appBarConfiguration)
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        return when (item.itemId) {
//            R.id.action_settings -> true
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }

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