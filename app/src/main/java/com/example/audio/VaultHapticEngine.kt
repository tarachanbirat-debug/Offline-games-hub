package com.example.audio

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Haptic feedback manager providing crisp, responsive tactile impulses.
 * Safely handles unsupported hardware or muted state.
 */
class VaultHapticEngine(context: Context) {

  var isMuted: Boolean = false

  private val vibrator: Vibrator? = try {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
      manager?.defaultVibrator
    } else {
      @Suppress("DEPRECATION")
      context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
  } catch (e: Exception) {
    null
  }

  fun vibrateTap() {
    if (isMuted || vibrator?.hasVibrator() != true) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(15)
      }
    } catch (_: Exception) {}
  }

  fun vibrateMove() {
    if (isMuted || vibrator?.hasVibrator() != true) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(12, 100))
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(12)
      }
    } catch (_: Exception) {}
  }

  fun vibrateSuccess() {
    if (isMuted || vibrator?.hasVibrator() != true) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val timings = longArrayOf(0, 40, 40, 60, 40, 80)
        val amplitudes = intArrayOf(0, 140, 0, 180, 0, 255)
        vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(longArrayOf(0, 40, 40, 60, 40, 80), -1)
      }
    } catch (_: Exception) {}
  }

  fun vibrateCrash() {
    if (isMuted || vibrator?.hasVibrator() != true) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(80)
      }
    } catch (_: Exception) {}
  }
}
