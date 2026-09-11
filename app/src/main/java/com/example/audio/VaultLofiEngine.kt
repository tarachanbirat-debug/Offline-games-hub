package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.*
import kotlin.math.PI
import kotlin.math.sin

/**
 * Unified Background Lo-Fi Audio Service for arcade games.
 * Generates relaxing procedural lo-fi ambient background tracks and chord loops
 * tailored to game genres (Chill, Synthwave, Retro Chiptune), playing ONLY when a game is active.
 */
class VaultLofiEngine(private val context: Context? = null) {

  var isMuted: Boolean = false
    set(value) {
      field = value
      if (value) {
        stopLofi()
      }
    }

  private var isPlaying: Boolean = false
  private var playbackJob: Job? = null
  private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

  enum class LofiMood {
    CHILL_BEATS,      // Relaxing puzzle / strategy ambient lo-fi
    CYBER_SYNTH,      // Action / racer synthwave lo-fi
    RETRO_CHILL       // Classic arcade chill lo-fi chords
  }

  fun startLofi(mood: LofiMood = LofiMood.CHILL_BEATS) {
    if (isMuted || isPlaying) return
    isPlaying = true

    playbackJob = scope.launch {
      while (isPlaying && !isMuted) {
        try {
          playLofiLoop(mood)
          delay(1200) // Brief pause between loop progressions
        } catch (e: CancellationException) {
          break
        } catch (_: Exception) {
          break
        }
      }
    }
  }

  fun stopLofi() {
    isPlaying = false
    playbackJob?.cancel()
    playbackJob = null
  }

  private suspend fun playLofiLoop(mood: LofiMood) {
    if (isMuted || !isPlaying) return

    val sampleRate = 22050 // Lower sample rate for warm lo-fi warmth
    val chordFrequencies = when (mood) {
      LofiMood.CHILL_BEATS -> listOf(
        listOf(261.63, 329.63, 392.00, 523.25), // C Major 7th
        listOf(220.00, 261.63, 329.63, 440.00), // A minor 7th
        listOf(174.61, 220.00, 261.63, 349.23), // F Major 7th
        listOf(196.00, 246.94, 293.66, 392.00)  // G Major 7th
      )
      LofiMood.CYBER_SYNTH -> listOf(
        listOf(220.00, 329.63, 440.00, 523.25), // Synth minor
        listOf(196.00, 293.66, 392.00, 493.88), // Synth G
        listOf(174.61, 261.63, 349.23, 440.00), // Synth F
        listOf(164.81, 246.94, 329.63, 392.00)  // Synth E
      )
      LofiMood.RETRO_CHILL -> listOf(
        listOf(293.66, 369.99, 440.00, 587.33), // Retro D
        listOf(246.94, 329.63, 392.00, 493.88), // Retro B
        listOf(220.00, 277.18, 329.63, 440.00), // Retro A
        listOf(196.00, 246.94, 293.66, 392.00)  // Retro G
      )
    }

    val noteDurationMs = 1200L
    val samplesPerNote = (sampleRate * (noteDurationMs / 1000.0)).toInt()

    for (chord in chordFrequencies) {
      if (!isPlaying || isMuted) break

      val buffer = ShortArray(samplesPerNote)
      for (i in 0 until samplesPerNote) {
        val t = i.toDouble() / sampleRate
        // Low-pass filtered warm lo-fi envelope
        val envelope = sin((i.toDouble() / samplesPerNote) * PI).coerceIn(0.0, 1.0)
        
        var sampleVal = 0.0
        for (freq in chord) {
          // Soft sine wave with harmonic warmth
          sampleVal += sin(2.0 * PI * freq * t) * 0.35
          sampleVal += sin(2.0 * PI * (freq * 1.5) * t) * 0.15 // Fifth harmonic
        }
        
        buffer[i] = (sampleVal * envelope * 0.15 * Short.MAX_VALUE).toInt().toShort()
      }

      try {
        val audioTrack = AudioTrack.Builder()
          .setAudioAttributes(
            AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_GAME)
              .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
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
        delay(noteDurationMs)
        audioTrack.release()
      } catch (_: Exception) {
        break
      }
    }
  }
}
