package com.valerinofawwaz.miniplayer
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.io.File
import java.io.IOException

class RecorderFragment : Fragment() {
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var outputFile: String = ""
    private var isRecording = false

    companion object {
        const val REQUEST_MIC = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recorder, container, false)

        outputFile = "${requireContext().externalCacheDir?.absolutePath}/rekaman_memo.mp4"

        val btnRekam = view.findViewById<Button>(R.id.btnRekam)
        val btnPutar = view.findViewById<Button>(R.id.btnPutar)
        val tvStatus = view.findViewById<TextView>(R.id.tvStatus)

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_MIC
            )
        }

        btnRekam.setOnClickListener {
            if (!isRecording) {
                if (mulaiRekam()) {
                    tvStatus.text = "Sedang merekam..."
                    btnRekam.text = "Stop Rekam"
                }
            } else {
                hentiRekam()
                tvStatus.text = "Rekaman selesai. Tekan Putar untuk mendengarkan."
                btnRekam.text = "Mulai Rekam"
            }
        }

        btnPutar.setOnClickListener {
            if (File(outputFile).exists()) {
                putarHasilRekaman()
            } else {
                Toast.makeText(requireContext(), "Belum ada rekaman.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun createMediaRecorder(context: Context): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
    }

    private fun mulaiRekam(): Boolean {
        return try {
            recorder = createMediaRecorder(requireContext()).apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setOutputFile(outputFile)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioSamplingRate(44100)
                setAudioEncodingBitRate(128000)
                prepare()
                start()
            }
            isRecording = true
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun hentiRekam() {
        try {
            recorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            recorder = null
            isRecording = false
        }
    }

    private fun putarHasilRekaman() {
        player?.release()
        player = MediaPlayer().apply {
            try {
                setDataSource(outputFile)
                prepare()
                start()
                setOnCompletionListener { release() }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recorder?.release()
        player?.release()
    }
}