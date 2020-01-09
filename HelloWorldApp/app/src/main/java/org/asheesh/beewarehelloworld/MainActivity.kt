package org.asheesh.beewarehelloworld

import android.Manifest
import android.os.Bundle
import android.system.Os
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.beeware.rubicon.Python
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.FileAttribute
import java.nio.file.attribute.PosixFilePermission
import java.nio.file.attribute.PosixFilePermissions
import java.util.stream.Stream
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
        //System.loadLibrary("ffi")

        if (false) {
            // Experiment with launching Python manually
            // Seemingly stuck on SELinux execute_no_trans denied
            Os.setenv(
                "PYTHONHOME",
                applicationContext.dataDir!!.absolutePath,
                true
            )
            val python = File(applicationContext.dataDir!!.absolutePath + "/bin/python3.7")
            if (python.exists()) {
                python.setExecutable(true)
                python.setReadable(true)
            } else {
                throw Error("yikes python is gone wtf")
            }
            val ownerWritable =
                PosixFilePermissions.fromString("rwxr-xr-x")
            val permissions: FileAttribute<*> =
                PosixFilePermissions.asFileAttribute(ownerWritable)
            /*Os.setenv(
                "LD_LIBRARY_PATH",
                applicationContext.dataDir!!.absolutePath + "/lib",
                true
            )*/
            val pythonPath = applicationContext.dataDir!!.absolutePath + "/bin/python3.7"
            Files.setPosixFilePermissions(
                Paths.get(pythonPath),
                PosixFilePermissions.fromString("rwxr-xr-x")
            )
            println("pythonpath = $pythonPath")
            val shellCode =
                "cd " + applicationContext.dataDir!!.absolutePath + "/bin ; LD_LIBRARY_PATH=" + applicationContext.dataDir!!.absolutePath + "/lib" + " runcon -r object_r -t app_data_file ./python3.7 -V 2>&1"
            println("shellCode = $shellCode")
            val cmdArray =
                arrayOf(
                    "/system/bin/sh",
                    "-c",
                    shellCode
                )
            val p = Runtime.getRuntime().exec(cmdArray)
            val pResult = p.waitFor()
            println("result = $pResult")
            var filePerm: Set<PosixFilePermission?>? = null
            try {
                filePerm =
                    Files.getPosixFilePermissions(Paths.get(applicationContext.dataDir!!.absolutePath + "/bin/python3"))
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val permission: String = PosixFilePermissions.toString(filePerm)
            println("permission = $permission")
            val iostat =
                ProcessBuilder().inheritIO().command(
                    "/system/bin/sh",
                    "-c",
                    applicationContext.dataDir!!.absolutePath + "/bin/python3.7 -V"
                ).start()
            val exitCode = iostat.waitFor()
            println("exitCode = $exitCode")
            return
        }

        // Get asset
        if (true) {
            val destDir = applicationContext.dataDir
            val zis = ZipInputStream(assets.open("pythonhome-arch-indep.zip"))
            var zipEntry = zis.nextEntry
            val buf = ByteArray(1024 * 1024 * 4)
            while (zipEntry != null) {
                val outputFile = File(destDir.absolutePath + "/" + zipEntry)
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
            val python = File(applicationContext.dataDir!!.absolutePath + "/bin/python3")
            if (python.exists()) {
                python.setExecutable(true)
                python.setReadable(true)
            } else {
                throw Error("yikes python is gone wtf")
            }
        }
        // Unpack Python into cache directory -- use applicationContext.externalCacheDir
        Os.setenv(
            "PYTHONHOME",
            applicationContext.dataDir!!.absolutePath,
            true
        )
        Os.setenv("RUBICON_LIBRARY", applicationInfo.nativeLibraryDir + "/librubicon.so", true)
        Log.v(
            "python home",
            applicationContext.dataDir!!.absolutePath
        )
        Os.setenv("TMPDIR", applicationContext.cacheDir!!.absolutePath, true)

        /* if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                storagePermission, 1
            )

        } else { */
        Log.v("LOG-TAG:D", "Permission is granted")
        val paths: Stream<Path> = Files.walk(
            Paths.get(applicationInfo.nativeLibraryDir)
        )
        paths.forEach {
            Log.v("found lib pythong", it.toString())
        }
        if (true) {
            val pythonStart = Python.init(null, ".", null)
            if (pythonStart > 0) {
                System.err.println("Got an error initializing Python")
                return
            }
            Log.w("hi", "hello, python is alive")
            // TODO: Call Python.eval() but no such function exists, rofl.
            Python.run(
                applicationContext.dataDir!!.absolutePath + "/lib/testme.py",
                arrayOf()
            )
            Log.w("hi", "hello, python is alive and ran hello world")
        }
        //}

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
