package com.example.v2_medtime.notifications

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object AlarmeAudioPlayer {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private val handler = Handler(Looper.getMainLooper())
    private val pararAutomaticamente = Runnable { parar() }

    @Synchronized
    fun iniciar(context: Context) {
        parar()
        val appContext = context.applicationContext

        val uriAlarme = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        runCatching {
            val atributos = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            mediaPlayer = MediaPlayer().apply {
                setDataSource(appContext, uriAlarme)
                setAudioAttributes(atributos)
                isLooping = true
                prepare()
                start()
            }
        }

        runCatching {
            val vib = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = appContext.getSystemService(VibratorManager::class.java)
                manager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            vibrator = vib
            val padrao = longArrayOf(0L, 600L, 400L)
            vib?.vibrate(VibrationEffect.createWaveform(padrao, 0))
        }

        // Segurança: encerra após 60 segundos caso ninguém responda
        handler.postDelayed(pararAutomaticamente, 60_000L)
    }

    @Synchronized
    fun parar() {
        handler.removeCallbacks(pararAutomaticamente)
        runCatching {
            mediaPlayer?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
        }
        mediaPlayer = null

        runCatching {
            vibrator?.cancel()
        }
        vibrator = null
    }
}