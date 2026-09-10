package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * Self-contained procedural audio synthesizer using Android AudioTrack.
 * Generates rich retro and candy-arcade sound effects without external audio files.
 */
class VaultSoundEngine(private val context: Context? = null) {

  var isMuted: Boolean = false

  private val sampleRate = 44100
  private val scope = CoroutineScope(Dispatchers.Default)

  /**
   * Generates a tone and plays it asynchronously.
   */
  private fun synthesizeTone(
    frequencies: List<Pair<Double, Double>>, // Frequency to duration in ms
    waveform: Waveform = Waveform.SINE,
    volume: Float = 0.35f
  ) {
    if (isMuted) return

    scope.launch {
      try {
        val totalMs = frequencies.sumOf { it.second }
        val totalSamples = (sampleRate * (totalMs / 1000.0)).toInt()
        val buffer = ShortArray(totalSamples)

        var currentSample = 0
        for ((freq, durationMs) in frequencies) {
          val samplesForTone = (sampleRate * (durationMs / 1000.0)).toInt()
          for (i in 0 until samplesForTone) {
            val t = i.toDouble() / sampleRate
            // Envelope: fast attack, exponential decay
            val envelope = (1.0 - (i.toDouble() / samplesForTone)).coerceIn(0.0, 1.0)
            val sampleVal = when (waveform) {
              Waveform.SINE -> sin(2.0 * PI * freq * t)
              Waveform.SQUARE -> if (sin(2.0 * PI * freq * t) >= 0) 0.6 else -0.6
              Waveform.TRIANGLE -> (2.0 / PI) * Math.asin(sin(2.0 * PI * freq * t))
            }
            if (currentSample < totalSamples) {
              buffer[currentSample] = (sampleVal * envelope * volume * Short.MAX_VALUE).toInt().toShort()
              currentSample++
            }
          }
        }

        val audioTrack = AudioTrack.Builder()
          .setAudioAttributes(
            AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_GAME)
              .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
              .build()
          )
          .setAudioFormat(
            AudioFormat.Builder()
              .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
              .setSampleRate(sampleRate)
              .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
              .build()
          )
          .setBufferSizeInBytes(buffer.size * 2)
          .setTransferMode(AudioTrack.MODE_STATIC)
          .build()

        audioTrack.write(buffer, 0, buffer.size)
        audioTrack.play()
        // Release after playback completes
        Thread.sleep(totalMs.toLong() + 50)
        audioTrack.release()
      } catch (e: Exception) {
        // Audio synthesis fallback
      }
    }
  }

  // --- Universal Platform Sound FX ---

  fun playTap() {
    synthesizeTone(listOf(Pair(620.0, 35.0)), Waveform.SINE, 0.25f)
  }

  fun playPop() {
    synthesizeTone(
      listOf(
        Pair(480.0, 25.0),
        Pair(740.0, 35.0)
      ),
      Waveform.TRIANGLE,
      0.3f
    )
  }

  fun playSnap() {
    synthesizeTone(listOf(Pair(880.0, 20.0)), Waveform.SQUARE, 0.15f)
  }

  fun playScore() {
    synthesizeTone(
      listOf(
        Pair(587.33, 40.0), // D5
        Pair(880.00, 70.0)  // A5
      ),
      Waveform.SINE,
      0.35f
    )
  }

  fun playSuccess() {
    synthesizeTone(
      listOf(
        Pair(523.25, 60.0),  // C5
        Pair(659.25, 60.0),  // E5
        Pair(783.99, 80.0),  // G5
        Pair(1046.50, 140.0) // C6
      ),
      Waveform.TRIANGLE,
      0.35f
    )
  }

  fun playVictory() {
    synthesizeTone(
      listOf(
        Pair(523.25, 70.0),
        Pair(659.25, 70.0),
        Pair(783.99, 70.0),
        Pair(1046.50, 220.0)
      ),
      Waveform.SINE,
      0.4f
    )
  }

  fun playError() {
    synthesizeTone(
      listOf(
        Pair(220.0, 60.0),
        Pair(180.0, 80.0)
      ),
      Waveform.SQUARE,
      0.25f
    )
  }

  fun playGameOver() {
    synthesizeTone(
      listOf(
        Pair(330.0, 90.0),
        Pair(293.66, 90.0),
        Pair(261.63, 110.0),
        Pair(196.00, 180.0)
      ),
      Waveform.TRIANGLE,
      0.35f
    )
  }

  fun playPour() {
    synthesizeTone(
      listOf(
        Pair(320.0, 30.0),
        Pair(440.0, 40.0),
        Pair(560.0, 50.0)
      ),
      Waveform.SINE,
      0.3f
    )
  }

  enum class Waveform {
    SINE,
    SQUARE,
    TRIANGLE
  }
}
