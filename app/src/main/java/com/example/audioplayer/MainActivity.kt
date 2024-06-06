    package com.example.audioplayer

    import android.annotation.SuppressLint
    import android.content.Intent
    import android.os.Bundle
    import android.os.Handler
    import android.util.Log
    import android.view.View
    import android.widget.Button
    import android.widget.ImageButton
    import android.widget.SeekBar
    import androidx.activity.enableEdgeToEdge
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.view.ViewCompat
    import androidx.core.view.WindowInsetsCompat
    import androidx.core.view.isVisible
    import androidx.databinding.DataBindingUtil
    import androidx.databinding.ObservableInt
    import com.doublesymmetry.kotlinaudio.models.AudioItem
    import com.doublesymmetry.kotlinaudio.models.AudioPlayerState
    import com.doublesymmetry.kotlinaudio.models.DefaultAudioItem
    import com.doublesymmetry.kotlinaudio.models.MediaSessionCallback
    import com.doublesymmetry.kotlinaudio.models.MediaType
    import com.doublesymmetry.kotlinaudio.models.NotificationButton
    import com.doublesymmetry.kotlinaudio.models.NotificationConfig
    import com.doublesymmetry.kotlinaudio.models.PlayerConfig
    import com.doublesymmetry.kotlinaudio.models.RepeatMode
    import com.doublesymmetry.kotlinaudio.players.QueuedAudioPlayer
    import com.example.audioplayer.databinding.ActivityMainBinding
    import com.example.audioplayer.ui.FragmentActivity
    import com.example.audioplayer.utilities.AudioPlayerActivity
    import kotlinx.coroutines.MainScope
    import kotlinx.coroutines.flow.collectLatest
    import kotlinx.coroutines.launch
    import java.util.Timer
    import java.util.TimerTask
    import java.util.concurrent.TimeUnit

    class MainActivity : AppCompatActivity(),SeekBar.OnSeekBarChangeListener {
        enum class SeekDirection { Forward, Backward }
//        private lateinit var player: QueuedAudioPlayer
        val observableDuration = ObservableInt(0)
        val observablePosition = ObservableInt(0)
        val observableTickTime: ObservableString = ObservableString("")
        private var wasPlayingBeforeSeek = false
        private var timer: Timer? = null

        private lateinit var runnable: Runnable
        private  var handler= Handler()
        private var binding:ActivityMainBinding?=null

//        private val job = Job()
        private val scope = MainScope()
        @SuppressLint("MissingInflatedId")
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding= DataBindingUtil.setContentView(this,R.layout.activity_main)

            enableEdgeToEdge()
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

//            player = QueuedAudioPlayer(
//                this, playerConfig = PlayerConfig(
//                    interceptPlayerActionsTriggeredExternally = true,
//                    handleAudioBecomingNoisy = true,
//                    handleAudioFocus = true
//                )
//            )
//            player.add(tracks)
//            player.playerOptions.repeatMode = RepeatMode.ALL

//            binding?.seekbar?.progress=0
//            binding?.seekbar?.max= player.duration.toInt()
//          runnable=0 Runnable {
//              binding?.seekbar?.progress= player.position.toInt()
//              handler.postDelayed(runnable,1000)
//          }
//            handler.postDelayed(runnable,10000)
//
            binding?.nextButton?.setOnClickListener {
                val intent = Intent(this, FragmentActivity::class.java)
                startActivity(intent)
            }
            binding?.mediaPlayer?.setOnClickListener {

                val intent = Intent(this, FragmentActivity::class.java)
                startActivity(intent)
            }

            val playButton: ImageButton = findViewById(R.id.media_button)

            val nextButton: ImageButton = findViewById(R.id.btnPlaynext)
            val prevButton: ImageButton = findViewById(R.id.btnPlayPrevious)

//            binding?.musicTitle?.text=player.currentItem?.title.toString()
//            binding?.imageView?.let { it1 ->
//                Glide.with(this@MainActivity)
//                    .load(player.currentItem?.artwork)
//                    .apply(RequestOptions.bitmapTransform(BlurTransformation(100)))
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .placeholder(R.drawable.ic_image_placeholder)
//                    .error(R.drawable.ic_image_placeholder) // Uri of the picture
//                    .into(it1)
//            }


            playButton.setOnClickListener {
                if ((applicationContext as AudioPlayerActivity).player.isPlaying) {
                    binding?.mediaButton?.setImageResource(R.drawable.ic_vector_play)
                    (applicationContext as AudioPlayerActivity).player.pause()

                } else {
                    (applicationContext as AudioPlayerActivity).player.play()

                    binding?.mediaButton?.setImageResource(R.drawable.ic_vector_pause)
                }
//                playButton.visibility = View.GONE
//                pauseButton.visibility=View.VISIBLE
            }

//            pauseButton.setOnClickListener {
//                    player.pause()
//                playButton.visibility = View.VISIBLE
//                pauseButton.visibility=View.GONE
//            }
            nextButton.setOnClickListener {
                (applicationContext as AudioPlayerActivity).player.next()
            }

            prevButton.setOnClickListener {
                (applicationContext as AudioPlayerActivity).player.previous()
            }

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
            private fun setupNotification() {
                val notificationConfig = NotificationConfig(
                    listOf(
                        NotificationButton.PLAY_PAUSE(),
                        NotificationButton.NEXT(isCompact = true),
                        NotificationButton.PREVIOUS(isCompact = true),
                        NotificationButton.SEEK_TO
                    ), accentColor = null, smallIcon = null, pendingIntent = null

                )
                (applicationContext as AudioPlayerActivity).player.notificationManager.createNotification(notificationConfig)

            }

    //very very important
        private fun observeEvents() {


        scope.launch {
            ((applicationContext as AudioPlayerActivity).player.event.onPlayerActionTriggeredExternally).collectLatest {//this is the inbuilt functions
                when (it) {
                    MediaSessionCallback.PLAY -> (applicationContext as AudioPlayerActivity).player.play()
                    MediaSessionCallback.PAUSE -> (applicationContext as AudioPlayerActivity).player.pause()
                    MediaSessionCallback.NEXT -> (applicationContext as AudioPlayerActivity).player.next()
                    MediaSessionCallback.PREVIOUS -> (applicationContext as AudioPlayerActivity).player.previous()
                    MediaSessionCallback.STOP -> (applicationContext as AudioPlayerActivity).player.stop()
                    MediaSessionCallback.FORWARD -> seek(SeekDirection.Forward)
                    MediaSessionCallback.REWIND -> seek(SeekDirection.Backward)
                    is MediaSessionCallback.SEEK -> (applicationContext as AudioPlayerActivity).player.seek(
                        it.positionMs,
                        TimeUnit.MILLISECONDS
                    )


                    else -> Log.d("noti", "Event not handled")
                }
            }
        }
        scope.launch {
            ((applicationContext as AudioPlayerActivity).player.event.stateChange).collectLatest {//this is the inbuilt functions
                when (it) {
                    AudioPlayerState.PLAYING -> {
                        binding?.title?.text=(applicationContext as AudioPlayerActivity).player.currentItem?.title.toString()
                        binding?.subtitle?.text=(applicationContext as AudioPlayerActivity).player.currentItem?.artist
//                        binding?.seekbar?.progress=player.position.toInt()
//                        binding?.seekbar?.max= player.duration.toInt()
//                        binding?.imageView?.let { it1 ->
//                            Glide.with(this@MainActivity)
//                                .load(player.currentItem?.artwork)
//                                .apply(RequestOptions.bitmapTransform(BlurTransformation(100)))
//                                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                .placeholder(R.drawable.ic_image_placeholder)
//                                .error(R.drawable.ic_image_placeholder) // Uri of the picture
//                                .into(it1)
//                        }
//                        if (timer != null) {
//                            timer?.cancel()
//                        }
//                        timer = Timer()
//                        timer?.schedule(ProgressUpdate(), 0, 1000)
//                        binding?.playButton?.visibility = View.GONE
//                        binding?.pauseButton?.visibility=View.VISIBLE
//                        binding?.seekbar?.setOnSeekBarChangeListener(this@MainActivity)
                    }
                    AudioPlayerState.PAUSED->{
//                        binding?.playButton?.visibility = View.VISIBLE
//                        binding?.pauseButton?.visibility=View.GONE
                    }

                    else -> Log.d("noti", "Event not handled")
                }
            }
        }
        scope.launch {
            ((applicationContext as AudioPlayerActivity).player.event.audioItemTransition).collectLatest {//this is the inbuilt functions
                (
                   setAudioPlayer((applicationContext as AudioPlayerActivity).player.currentItem)
                )

                binding?.title?.text=(applicationContext as AudioPlayerActivity).player.currentItem?.title.toString()
                binding?.subtitle?.text=(applicationContext as AudioPlayerActivity).player.currentItem?.artist
//                observablePosition.set(player.position.toInt())
//                observableDuration.set(player.duration.toInt())
//                binding?.imageView?.let { it1 ->
//                    Glide.with(this@MainActivity)
//                        .load(player.currentItem?.artwork)
//                        .apply(RequestOptions.bitmapTransform(BlurTransformation(100)))
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .placeholder(R.drawable.ic_image_placeholder)
//                        .error(R.drawable.ic_image_placeholder) // Uri of the picture
//                        .into(it1)
//                }
//                binding?.seekbar?.setOnSeekBarChangeListener(this@MainActivity)
            }
        }
    }
        private fun setAudioPlayer(currentItem: AudioItem?) {
            if (currentItem != null) {
//            Glide.with(this@ActivityMain)
//                .load(currentItem.artwork)
//                .placeholder(R.drawable.ic_image_placeholder)
//                .error(R.drawable.ic_image_placeholder) // Uri of the picture
//                .into(mBinding.coverImage);
                binding?.title?.text = currentItem.title
                binding?.subtitle?.text = currentItem.artist
                binding?.mediaButton?.setImageResource(
                    if ((applicationContext as AudioPlayerActivity).player.isPlaying) {
                        R.drawable.ic_vector_pause
                    } else {
                        R.drawable.ic_vector_play
                    }
                )

            }
        }

        override fun onResume() {
            super.onResume()
            if (!(applicationContext as AudioPlayerActivity).player.isPlaying) {
                binding?.mediaPlayer?.visibility=View.GONE
            }
            else{
                binding?.mediaPlayer?.visibility=View.VISIBLE
            }

                binding?.title?.text=(applicationContext as AudioPlayerActivity).player.currentItem?.title
                binding?.subtitle?.text=(applicationContext as AudioPlayerActivity).player.currentItem?.artist

                Log.d("isplaying","true")

            setupNotification()
            observeEvents()
            Log.d("back","Called")
        }

        private fun seek(direction: SeekDirection) {
            val seekTime = when (direction) {
                SeekDirection.Forward -> (applicationContext as AudioPlayerActivity).player.position + 1000
                SeekDirection.Backward -> (applicationContext as AudioPlayerActivity).player.position - 1000L
            }

            (applicationContext as AudioPlayerActivity).player.seek(
                seekTime,
                TimeUnit.MILLISECONDS)}

        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                val position: Long = progress.toLong()
                Log.d("Progress", "$position")
                (applicationContext as AudioPlayerActivity).player.seek(position, TimeUnit.MILLISECONDS)

            }
        }


        override fun onStartTrackingTouch(p0: SeekBar?) {

        }

        override fun onStopTrackingTouch(p0: SeekBar?) {

        }




        inner class ProgressUpdate : TimerTask() {
            override fun run() {
                runOnUiThread { // This code will always run on the UI thread, therefore is safe to modify UI elements.
                  observablePosition.set((applicationContext as AudioPlayerActivity).player.position.toInt())
                   observableTickTime.set(getTimeString((applicationContext as AudioPlayerActivity).player.position))
                }
            }
        }


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

