package ru.toffeantyri.customviewexample

import android.app.Activity
import android.os.Bundle
import ru.toffeantyri.customviewexample.databinding.MainActivityBinding

class MainActivity : Activity() {

    private lateinit var binding: MainActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}

