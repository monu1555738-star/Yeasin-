package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

class CallToneManager(private val context: Context) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private var ringtone: Ringtone? = null
    private var toneGenerator: ToneGenerator? = null
    private var isRinging = false

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_RING, 85)
        } catch (e: Exception) {
            Log.e("CallToneManager", "Failed to create ToneGenerator", e)
        }
    }

    fun startRinging() {
        if (isRinging) return
        isRinging = true
        try {
            val alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            ringtone = RingtoneManager.getRingtone(context, alertUri)?.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    audioAttributes = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                }
                play()
            }
        } catch (e: Exception) {
            Log.e("CallToneManager", "Error playing ringtone", e)
        }

        startVibrate()
    }

    fun stopRinging() {
        isRinging = false
        try {
            ringtone?.let {
                if (it.isPlaying) {
                    it.stop()
                }
            }
        } catch (e: Exception) {
            Log.e("CallToneManager", "Error stopping ringtone", e)
        }
        stopVibrate()
    }

    fun playDtmfTone(tone: Int = ToneGenerator.TONE_DTMF_1, durationMs: Int = 150) {
        try {
            toneGenerator?.startTone(tone, durationMs)
        } catch (e: Exception) {
            Log.e("CallToneManager", "ToneGenerator error", e)
        }
    }

    fun setSpeakerphone(enable: Boolean) {
        try {
            audioManager?.let { am ->
                am.mode = if (enable) AudioManager.MODE_IN_COMMUNICATION else AudioManager.MODE_NORMAL
                am.isSpeakerphoneOn = enable
            }
        } catch (e: Exception) {
            Log.e("CallToneManager", "Error setting speakerphone", e)
        }
    }

    fun setMicrophoneMute(muted: Boolean) {
        try {
            audioManager?.isMicrophoneMute = muted
        } catch (e: Exception) {
            Log.e("CallToneManager", "Error muting microphone", e)
        }
    }

    private fun startVibrate() {
        try {
            val pattern = longArrayOf(0, 800, 1000, 800, 1000)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(pattern, 0)
                }
            }
        } catch (e: Exception) {
            Log.e("CallToneManager", "Vibrate error", e)
        }
    }

    private fun stopVibrate() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.cancel()
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.cancel()
            }
        } catch (e: Exception) {
            Log.e("CallToneManager", "Vibrator cancel error", e)
        }
    }

    fun release() {
        stopRinging()
        toneGenerator?.release()
        toneGenerator = null
    }
}
