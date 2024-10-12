package com.example.onlineshop.Activity

import android.content.Intent
import android.os.Bundle
import com.example.onlineshop.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity() {
    private lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            startBtn.setOnClickListener{
                startActivity(Intent(this@IntroActivity, SignInActivity::class.java))
            }
        }

    }
}