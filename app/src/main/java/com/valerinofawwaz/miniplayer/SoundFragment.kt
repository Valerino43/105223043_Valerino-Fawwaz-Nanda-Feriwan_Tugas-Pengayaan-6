package com.valerinofawwaz.miniplayer
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class SoundFragment : Fragment() {
    private var soundPool: SoundPool? = null
    private var soundShoot = 0
    private var soundBoom = 0
    private var soundCoin = 0
    private var isLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sound, container, false)

        val audioAttr = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(audioAttr)
            .build()

        try {
            soundShoot = soundPool?.load(requireContext(), R.raw.apple, 1) ?: 0
            soundBoom = soundPool?.load(requireContext(), R.raw.apple, 1) ?: 0
            soundCoin = soundPool?.load(requireContext(), R.raw.apple, 1) ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
        }

        soundPool?.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) isLoaded = true
        }

        view.findViewById<Button>(R.id.btnShoot).setOnClickListener {
            if (isLoaded) playSound(soundShoot)
        }

        view.findViewById<Button>(R.id.btnBoom).setOnClickListener {
            if (isLoaded) playSound(soundBoom)
        }

        view.findViewById<Button>(R.id.btnCoin).setOnClickListener {
            if (isLoaded) playSound(soundCoin)
        }

        view.findViewById<Button>(R.id.btnSimultan).setOnClickListener {
            if (isLoaded) {
                playSound(soundShoot)
                playSound(soundBoom)
                playSound(soundCoin)
            }
        }

        return view
    }

    private fun playSound(soundId: Int) {
        soundPool?.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        soundPool?.release()
        soundPool = null
    }
}