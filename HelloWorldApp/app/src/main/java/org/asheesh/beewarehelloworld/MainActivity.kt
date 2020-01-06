package org.asheesh.beewarehelloworld

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.system.Os
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import org.beeware.rubicon.Python
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val storagePermission = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        System.loadLibrary("python3.7m")
        System.loadLibrary("rubicon")

        // Get asset
        val destDir = applicationContext.externalCacheDir!!
        val zis = ZipInputStream(assets.open("pythonhome.zip"))
        var zipEntry = zis.nextEntry
        val buf = ByteArray(1024)
        while (zipEntry != null) {
            val outputFile = File(destDir.absolutePath + "/" + zipEntry)
            System.out.println(outputFile)
            if (zipEntry.isDirectory) {
                outputFile.mkdirs()
                zipEntry = zis.nextEntry
                continue
            }
            val fos = FileOutputStream(outputFile)
            var len: Int
            while (zis.read(buf).also { len = it } > 0) {
                fos.write(buf, 0, len)
            }
            fos.close()
            zipEntry = zis.nextEntry
        }
        zis.closeEntry()
        zis.close()
        // Unpack Python into cache directory -- use applicationContext.externalCacheDir
        Os.setenv("PYTHONHOME", applicationContext.externalCacheDir!!.absolutePath, true)

        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                storagePermission, 1
            )

        } else {
            Log.v("LOG-TAG:D", "Permission is granted")
            // val paths: Stream<Path>  = Files.walk(Paths.get("/"))
            // paths.forEach(System.out::println)
            if (true) {
                val pythonStart = Python.init(null, ".", null)
                if (pythonStart > 0) {
                    System.err.println("Got an error initializing Python")
                    return
                }
                Log.w("hi", "hello, python is alive")
                // TODO: Call Python.eval() but no such function exists, rofl.
                Python.run(
                    applicationContext.externalCacheDir!!.absolutePath + "/lib/python3.7/pydoc.py",
                    arrayOf()
                )
                Log.w("hi", "hello, python is alive and ran pydoc3")
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
