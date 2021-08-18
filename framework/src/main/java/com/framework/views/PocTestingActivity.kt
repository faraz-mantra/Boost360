package com.framework.views

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGImageView
import com.framework.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class PocTestingActivity : AppCompatActivity() {

    private val TAG = "PocTestingActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poc_testing)
        val imageView = findViewById<SVGImageView>(R.id.iv_main)


        readFile()?.let {
            imageView.setSVG(SVG.getFromString(it))

        }

        
    }

    fun readFile(): String? {

        var reader:BufferedReader? = null;
        try {
            reader = BufferedReader(
                    InputStreamReader(assets.open("frame_14.svg"))
            );

            // do reading, usually loop until end of file reading
            var mLine:StringBuilder = StringBuilder()
            var line:String?=""
            while (line!= null) {
                line = reader.readLine()
                //process line
                if (line!=null)
                    mLine.append(line)
            }

            val result =mLine.toString()
            val modified = result.replace("SMILEY DENTAL CLINIC","Suman Clinic")
            Log.i(TAG, "readFile: "+modified)

            return modified

        } catch (e:IOException) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (e:IOException) {
                    //log the exception
                }
            }
        }
        return null
    }
}

