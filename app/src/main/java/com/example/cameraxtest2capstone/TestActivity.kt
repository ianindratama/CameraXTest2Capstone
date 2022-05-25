package com.example.cameraxtest2capstone

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.cameraxtest2capstone.databinding.ActivityMainBinding
import com.example.cameraxtest2capstone.databinding.ActivityTestBinding
import java.io.File

class TestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val filePath = intent.getSerializableExtra("tesPath") as String

        val myFile = File(filePath)

        val result = rotateBitmap(BitmapFactory.decodeFile(myFile.path))

        binding.imagePredicted.setImageBitmap(result)

        binding.imagePredicted.visibility = View.VISIBLE

    }
}