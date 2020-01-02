package org.asheesh.beewarehelloworld

import android.Manifest
import android.content.pm.PackageManager
import org.beeware.rubicon.Python
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val storagePermission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        System.loadLibrary("python3.7m")
        System.loadLibrary("rubicon")


        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                storagePermission, 1);

        } else {
            Log.v("LOG-TAG:D","Permission is granted");
            // val paths: Stream<Path>  = Files.walk(Paths.get("/"))
            // paths.forEach(System.out::println)
            if (true) {
                val pythonStart = Python.init(null, ".", null)
                if (pythonStart > 0) {
                    System.err.println("Got an error initializing Python")
                    return
                }
                Log.w("hi", "hw python")
                // TODO: Call Python.eval() but no such function exists, rofl.
            }
        }


        sample_text.text = stringFromJNI()
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
