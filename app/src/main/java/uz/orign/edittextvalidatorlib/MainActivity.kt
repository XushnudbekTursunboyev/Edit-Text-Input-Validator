package uz.orign.edittextvalidatorlib

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import uz.orign.edittextvalidatorlib.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}