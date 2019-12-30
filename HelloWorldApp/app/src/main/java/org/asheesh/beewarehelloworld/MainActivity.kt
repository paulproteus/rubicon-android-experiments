package org.asheesh.beewarehelloworld

import org.beeware.rubicon.Python
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        System.loadLibrary("python3.7m")
        System.loadLibrary("rubicon")
        val pythonStart = Python.init(null, ".", null)
        if (pythonStart > 0) {
            System.err.println("Got an error initializing Python")
            return
        }
        Log.w("hi", "hw python")
        // TODO: Call Python.eval() but no such function exists, rofl.
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
