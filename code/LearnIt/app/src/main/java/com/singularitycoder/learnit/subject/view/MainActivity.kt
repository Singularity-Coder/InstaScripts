package com.singularitycoder.learnit.subject.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.singularitycoder.learnit.R
import com.singularitycoder.learnit.databinding.ActivityMainBinding
import com.singularitycoder.learnit.helpers.constants.FragmentsTag
import com.singularitycoder.learnit.helpers.constants.IntentExtraKey
import com.singularitycoder.learnit.helpers.constants.IntentKey
import com.singularitycoder.learnit.helpers.konfetti.Presets
import com.singularitycoder.learnit.helpers.konfetti.image.ImageUtil
import com.singularitycoder.learnit.helpers.showScreen
import com.singularitycoder.learnit.lockscreen.LockScreenActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val alarmReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != IntentKey.ALARM_BROADCAST) return
            val activityIntent = Intent(this@MainActivity, LockScreenActivity::class.java).apply {
                putExtra(IntentExtraKey.TOPIC_ID_3, intent.getLongExtra(IntentExtraKey.TOPIC_ID_2, 0L))
            }
            startActivity(activityIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.insetsController?.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.purple_700)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        showScreen(
            fragment = MainFragment.newInstance(),
            tag = FragmentsTag.MAIN,
            isAdd = true,
            isAddToBackStack = false
        )

        LocalBroadcastManager.getInstance(this).registerReceiver(
            /* receiver = */ alarmReceiver,
            /* filter = */ IntentFilter(IntentKey.ALARM_BROADCAST)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this)?.unregisterReceiver(alarmReceiver)
    }

    fun festive() {
        val drawable = AppCompatResources.getDrawable(applicationContext, R.drawable.baseline_favorite_24)
        val drawableShape = ImageUtil.loadDrawable(drawable!!)
        binding.konfettiView.start(Presets.festive(drawableShape))
    }

    fun explode() {
        binding.konfettiView.start(Presets.explode())
    }

    fun parade() {
        binding.konfettiView.start(Presets.parade())
    }

    fun rain() {
        binding.konfettiView.start(Presets.rain())
    }
}