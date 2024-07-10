package com.oncall.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.oncall.R
import com.oncall.databinding.ActivityMainBinding
import com.oncall.service.NotificationService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getNotificationPermission()
        setupClickListeners()
        setupObservables()
        viewModel.logIsRunning()
    }

    private fun setupObservables() {
        viewModel.onGetIsRunning.observe(this) { isRunning ->
            displayComponentsBasedOnStatus(isRunning)
        }
    }

    private fun getNotificationPermission() {
        val permission = Manifest.permission.POST_NOTIFICATIONS
        when {
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> Unit

            shouldShowRequestPermissionRationale(permission) -> Unit
            else -> Unit
        }
    }

    private fun setupClickListeners() {
        binding.btnActivate.setOnClickListener {
            sendService(true)
        }
        binding.btnDeactivate.setOnClickListener {
            sendService(false)
        }
        binding.btnClose.setOnClickListener {
            finishAndRemoveTask()
        }
    }

    private fun sendService(value: Boolean) {
        val serviceIntent = Intent(this, NotificationService::class.java)
        serviceIntent.putExtra(NotificationService.RUN_SERVICE_KEY, value)
        this.startService(serviceIntent)
    }

    private fun displayComponentsBasedOnStatus(isRunning: Boolean) {
        when (isRunning) {
            true -> displayActiveOnCall()
            false -> displayInactiveOnCall()
        }
    }

    private fun displayActiveOnCall() {
        binding.btnActivate.backgroundTintList = getColorStateList(R.color.green_activated)
        binding.btnActivate.setTextColor(getColor(R.color.white))
        binding.btnActivate.iconTint = getColorStateList(R.color.white)
        binding.btnDeactivate.backgroundTintList = getColorStateList(R.color.grey_deactivated)
        binding.btnDeactivate.setTextColor(getColor(R.color.white_trans70))
        binding.btnDeactivate.iconTint = getColorStateList(R.color.white_trans70)
        binding.txtStatus.text = getString(R.string.active)
        binding.txtAdditional1.text = getString(R.string.active_additional_1)
        binding.txtEnding1.text = getString(R.string.active_ending_1)
        binding.txtAdditional2.visibility = View.VISIBLE
        binding.txtEnding2.visibility = View.VISIBLE
    }

    private fun displayInactiveOnCall() {
        binding.btnActivate.backgroundTintList = getColorStateList(R.color.grey_deactivated)
        binding.btnActivate.setTextColor(getColor(R.color.white_trans70))
        binding.btnActivate.iconTint = getColorStateList(R.color.white_trans70)
        binding.btnDeactivate.backgroundTintList = getColorStateList(R.color.green_activated)
        binding.btnDeactivate.setTextColor(getColor(R.color.white))
        binding.btnDeactivate.iconTint = getColorStateList(R.color.white)
        binding.txtStatus.text = getString(R.string.inactive)
        binding.txtAdditional1.text = getString(R.string.inactive_additional_1)
        binding.txtEnding1.text = getString(R.string.inactive_ending_1)
        binding.txtAdditional2.visibility = View.GONE
        binding.txtEnding2.visibility = View.GONE
    }
}
