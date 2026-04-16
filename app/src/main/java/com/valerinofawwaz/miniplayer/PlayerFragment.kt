package com.valerinofawwaz.miniplayer
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment

class PlayerFragment : Fragment() {
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var seekBar: SeekBar
    private lateinit var tvCurrent: TextView
    private lateinit var tvDurasi: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_player, container, false)

        seekBar = view.findViewById(R.id.seekBar)
        tvCurrent = view.findViewById(R.id.tvCurrent)
        tvDurasi = view.findViewById(R.id.tvDurasi)

        try {
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.apple).apply {
                setOnPreparedListener {
                    seekBar.max = duration
                    tvDurasi.text = formatWaktu(duration)
                }
                setOnCompletionListener { resetPlayer() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        view.findViewById<Button>(R.id.btnPlay).setOnClickListener {
            mediaPlayer?.let { mp ->
                if (!mp.isPlaying) {
                    mp.start()
                    updateSeekBar()
                }
            }
        }

        view.findViewById<Button>(R.id.btnPause).setOnClickListener {
            mediaPlayer?.takeIf { it.isPlaying }?.pause()
        }

        view.findViewById<Button>(R.id.btnStop).setOnClickListener { resetPlayer() }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                    tvCurrent.text = formatWaktu(progress)
                }
            }
            override fun onStartTrackingTouch(sb: SeekBar) {}
            override fun onStopTrackingTouch(sb: SeekBar) {}
        })

        return view
    }

    private fun updateSeekBar() {
        handler.postDelayed({
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) {
                    seekBar.progress = mp.currentPosition
                    tvCurrent.text = formatWaktu(mp.currentPosition)
                    updateSeekBar()
                }
            }
        }, 500)
    }

    private fun resetPlayer() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            prepare()
            seekTo(0)
        }
        handler.removeCallbacksAndMessages(null)
        seekBar.progress = 0
        tvCurrent.text = "0:00"
    }

    private fun formatWaktu(ms: Int): String {
        val detik = (ms / 1000) % 60
        val menit = ms / 60000
        return "%d:%02d".format(menit, detik)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        mediaPlayer?.release()
        mediaPlayer = null
    }
}