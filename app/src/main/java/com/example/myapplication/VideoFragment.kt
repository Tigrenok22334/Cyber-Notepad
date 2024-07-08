package com.example.myapplication

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.fragment.app.Fragment

class VideoFragment : Fragment() {

    private lateinit var videoView: VideoView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_video, container, false)
        videoView = view.findViewById(R.id.videoView)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val videoUri =
            Uri.parse("android.resource://${requireActivity().packageName}/" + R.raw.video) // замените your_video на имя вашего видеофайла в папке res/raw
        videoView.setVideoURI(videoUri)
        videoView.setOnPreparedListener { mp -> mp.isLooping = true }
        videoView.start()
    }
}