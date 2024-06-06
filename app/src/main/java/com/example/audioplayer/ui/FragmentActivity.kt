package com.example.audioplayer.ui

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.doublesymmetry.kotlinaudio.models.AudioPlayerState
import com.doublesymmetry.kotlinaudio.models.DefaultAudioItem
import com.doublesymmetry.kotlinaudio.models.MediaSessionCallback
import com.doublesymmetry.kotlinaudio.models.MediaType
import com.doublesymmetry.kotlinaudio.models.NotificationButton
import com.doublesymmetry.kotlinaudio.models.NotificationConfig
import com.doublesymmetry.kotlinaudio.models.RepeatMode
import com.example.audioplayer.R
import com.example.audioplayer.databinding.ActivityFragmentBinding
import com.example.audioplayer.utilities.AudioPlayerActivity
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit

class FragmentActivity : AppCompatActivity() ,SeekBar.OnSeekBarChangeListener{
    private lateinit var binding:ActivityFragmentBinding
    private lateinit var runnable: Runnable
    private  var handler= Handler()
    private val scope = MainScope()
    private var timer: Timer? = null
    enum class SeekDirection { Forward, Backward }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= DataBindingUtil.setContentView(this,R.layout.activity_fragment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        (applicationContext  as AudioPlayerActivity).player.add(tracks)
        (applicationContext  as AudioPlayerActivity).player.playerOptions.repeatMode = RepeatMode.ALL



        binding.musicTitle.text =(applicationContext  as AudioPlayerActivity).player.currentItem?.title.toString()
        binding.imageView.let { it1 ->
            Glide.with(this)
                .load((applicationContext  as AudioPlayerActivity).player.currentItem?.artwork)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(100)))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder) // Uri of the picture
                .into(it1)
        }


        binding.playButton.setOnClickListener {
            (applicationContext  as AudioPlayerActivity).player.play()
            binding.playButton.visibility = View.GONE
            binding.pauseButton.visibility= View.VISIBLE
        }

        binding.pauseButton.setOnClickListener {
            (applicationContext  as AudioPlayerActivity).player.pause()
            binding. playButton.visibility = View.VISIBLE
            binding. pauseButton.visibility= View.GONE
        }
        binding.nextButton.setOnClickListener {
            (applicationContext  as AudioPlayerActivity).player.next()
        }

        binding.previousButton.setOnClickListener {
            (applicationContext  as AudioPlayerActivity).player.previous()
        }
        binding.seekbar.progress =0
        binding.seekbar.max = (applicationContext as AudioPlayerActivity).player.duration.toInt()
        runnable= Runnable {
            binding.seekbar.progress = (applicationContext  as AudioPlayerActivity).player.position.toInt()
            handler.postDelayed(runnable,1000)
        }
        handler.postDelayed(runnable,10000
        )
        setupNotification()
        observeEvents()


    }
    private fun setupNotification() {
        val notificationConfig = NotificationConfig(
            listOf(
                NotificationButton.PLAY_PAUSE(),
                NotificationButton.NEXT(isCompact = true),
                NotificationButton.PREVIOUS(isCompact = true),
                NotificationButton.SEEK_TO
            ), accentColor = null, smallIcon = null, pendingIntent = null

        )
        (applicationContext.applicationContext  as AudioPlayerActivity).player.notificationManager.createNotification(notificationConfig)

    }
    companion object {
        val tracks = listOf(
            DefaultAudioItem(
                "https://rntp.dev/example/Longing.mp3",
                MediaType.DEFAULT,
                title = "Longing",
                artwork = "https://rntp.dev/example/Longing.jpeg",
                artist = "David Chavez",
                duration = 143 * 1000,//seconds*miliseconds=minuites
            ),
            DefaultAudioItem(
                "https://rntp.dev/example/Soul%20Searching.mp3",
                MediaType.DEFAULT,
                title = "Soul Searching (Demo)",
                artwork = "https://rntp.dev/example/Soul%20Searching.jpeg",
                artist = "David Chavez",
                duration = 77 * 1000,
            ),
            DefaultAudioItem(
                "https://rntp.dev/example/Lullaby%20(Demo).mp3",
                MediaType.DEFAULT,
                title = "Lullaby (Demo)",
                artwork = "https://rntp.dev/example/Lullaby%20(Demo).jpeg",
                artist = "David Chavez",
                duration = 71 * 1000,
            ),
            DefaultAudioItem(
                "https://rntp.dev/example/Rhythm%20City%20(Demo).mp3",
                MediaType.DEFAULT,
                title = "Rhythm City (Demo)",
                artwork = "https://rntp.dev/example/Rhythm%20City%20(Demo).jpeg",
                artist = "David Chavez",
                duration = 106 * 1000,
            ),
            DefaultAudioItem(
                "https://rntp.dev/example/hls/whip/playlist.m3u8",
                MediaType.HLS,
                title = "Whip",
                artwork = "https://rntp.dev/example/hls/whip/whip.jpeg",
            ),
            DefaultAudioItem(
                "https://ais-sa5.cdnstream1.com/b75154_128mp3",
                MediaType.DEFAULT,
                title = "Smooth Jazz 24/7",
                artwork = "https://rntp.dev/example/smooth-jazz-24-7.jpeg",
                artist = "New York, NY",
            ),
            DefaultAudioItem(
                "https://traffic.libsyn.com/atpfm/atp545.mp3",
                title = "Chapters",
                artwork = "https://random.imagecdn.app/800/800?dummy=1",
            ),
        )
    }
    private fun observeEvents() {


        scope.launch {
            ((applicationContext.applicationContext  as AudioPlayerActivity).player.event.onPlayerActionTriggeredExternally).collectLatest {//this is the inbuilt functions
                when (it) {
                    MediaSessionCallback.PLAY -> (applicationContext.applicationContext  as AudioPlayerActivity).player.play()
                    MediaSessionCallback.PAUSE -> (applicationContext.applicationContext  as AudioPlayerActivity).player.pause()
                    MediaSessionCallback.NEXT -> (applicationContext.applicationContext  as AudioPlayerActivity).player.next()
                    MediaSessionCallback.PREVIOUS -> (applicationContext.applicationContext  as AudioPlayerActivity).player.previous()
                    MediaSessionCallback.STOP -> (applicationContext.applicationContext  as AudioPlayerActivity).player.stop()
                    MediaSessionCallback.FORWARD -> seek(SeekDirection.Forward)
                    MediaSessionCallback.REWIND -> seek(SeekDirection.Backward)
                    is MediaSessionCallback.SEEK -> (applicationContext.applicationContext  as AudioPlayerActivity).player.seek(
                        it.positionMs,
                        TimeUnit.MILLISECONDS
                    )


                    else -> Log.d("noti", "Event not handled")
                }
            }
        }
        scope.launch {
            ((applicationContext.applicationContext  as AudioPlayerActivity).player.event.stateChange).collectLatest {//this is the inbuilt functions
                when (it) {
                    AudioPlayerState.PLAYING -> {
                        updateUI()
                        updatePlayPauseButtons()
                        startSeekBarUpdate()

                    }
                    AudioPlayerState.PAUSED->{
                        updatePlayPauseButtons()
                    }

                    else -> Log.d("noti", "Event not handled")
                }
            }
        }
        scope.launch {
            ((applicationContext.applicationContext  as AudioPlayerActivity).player.event.audioItemTransition).collectLatest {//this is the inbuilt functions


                updateUI()
            }
        }
    }



    private fun updateUI() {
        if (isFinishing || isDestroyed) {
            return
        }
        binding.musicTitle.text = (applicationContext.applicationContext  as AudioPlayerActivity).player.currentItem?.title.toString()
        binding.seekbar.progress = (applicationContext.applicationContext  as AudioPlayerActivity).player.position.toInt()
        binding.seekbar.max = (applicationContext.applicationContext  as AudioPlayerActivity).player.duration.toInt()
        binding.imageView.let { imageView ->
            Glide.with(this)
                .load((applicationContext.applicationContext  as AudioPlayerActivity).player.currentItem?.artwork)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(100)))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .into(imageView)
        }
    }

    private fun updatePlayPauseButtons() {
        if ((applicationContext.applicationContext  as AudioPlayerActivity).player.isPlaying) {
            binding.playButton.visibility = View.GONE
            binding.pauseButton.visibility = View.VISIBLE
        } else {
            binding.playButton.visibility = View.VISIBLE
            binding.pauseButton.visibility = View.GONE
        }
    }
    private fun startSeekBarUpdate() {
        if (timer != null) {
            timer?.cancel()
        }
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    binding.seekbar.progress = (applicationContext.applicationContext  as AudioPlayerActivity).player.position.toInt()
                }
            }
        }, 0, 1000)
    }
//    inner class ProgressUpdate : TimerTask() {
//        override fun run() {
//            runOnUiThread { // This code will always run on the UI thread, therefore is safe to modify UI elements.
//                observablePosition.set(player.position.toInt())
//                observableTickTime.set(getTimeString(player.position))
//            }
//        }
//    }

    override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            val position: Long = progress.toLong()
            Log.d("Progress", "$position")
            (applicationContext.applicationContext  as AudioPlayerActivity).player.seek(position, TimeUnit.MILLISECONDS)

        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {

    }
    fun getTimeString(millis: Long): String {
        val hours = millis / (1000 * 60 * 60)
        val minutes = millis % (1000 * 60 * 60) / (1000 * 60)
        val seconds = millis % (1000 * 60 * 60) % (1000 * 60) / 1000
        return/* String.format("%02d", hours) + ":" + */String.format(
            "%02d",
            minutes
        ) + ":" + String.format("%02d", seconds)
    }
    private fun seek(direction: SeekDirection) {
        val seekTime = when (direction) {
        SeekDirection.Forward -> (applicationContext.applicationContext  as AudioPlayerActivity).player.position + 1000
            SeekDirection.Backward -> (applicationContext.applicationContext  as AudioPlayerActivity).player.position - 1000L
        }

        (applicationContext.applicationContext  as AudioPlayerActivity).player.seek(
            seekTime,
            TimeUnit.MILLISECONDS)}
}