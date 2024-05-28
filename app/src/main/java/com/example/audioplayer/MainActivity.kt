package com.example.audioplayer

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.doublesymmetry.kotlinaudio.models.AudioPlayerState
import com.doublesymmetry.kotlinaudio.models.DefaultAudioItem
import com.doublesymmetry.kotlinaudio.models.MediaSessionCallback
import com.doublesymmetry.kotlinaudio.models.MediaType
import com.doublesymmetry.kotlinaudio.models.NotificationButton
import com.doublesymmetry.kotlinaudio.models.NotificationConfig
import com.doublesymmetry.kotlinaudio.models.PlayerConfig
import com.doublesymmetry.kotlinaudio.models.RepeatMode
import com.doublesymmetry.kotlinaudio.players.AudioPlayer
import com.doublesymmetry.kotlinaudio.players.QueuedAudioPlayer
import com.google.android.exoplayer2.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    enum class SeekDirection { Forward, Backward }
    private lateinit var player: QueuedAudioPlayer
    var title: String = ""
    var artist: String = ""
    var artwork: String = ""
    var duration: Long = 0
    var isLive: Boolean = false
    private lateinit var exoPlayer: ExoPlayer

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        player = QueuedAudioPlayer(
            this, playerConfig = PlayerConfig(
                interceptPlayerActionsTriggeredExternally = true,
                handleAudioBecomingNoisy = true,
                handleAudioFocus = true
            )
        )
        player.add(tracks)
        player.playerOptions.repeatMode = RepeatMode.ALL

        setupNotification()
        observeEvents()




        val playButton: Button = findViewById(R.id.playButton)
        val pauseButton: Button = findViewById(R.id.pauseButton)
        val nextButton: Button = findViewById(R.id.nextButton)
        val prevButton: Button = findViewById(R.id.previousButton)

        playButton.setOnClickListener {
            player.play()
        }

        pauseButton.setOnClickListener {
                player.pause()
        }
        nextButton.setOnClickListener {
            player.next()
        }

        prevButton.setOnClickListener {
            player.previous()
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
            player.notificationManager.createNotification(notificationConfig)

        }

//very very important
    private fun observeEvents() {
        scope.launch {
            (player.event.onPlayerActionTriggeredExternally).collectLatest {//this is the inbuil functions
                when (it) {
                    MediaSessionCallback.PLAY -> player.play()
                    MediaSessionCallback.PAUSE -> player.pause()
                    MediaSessionCallback.NEXT -> player.next()
                    MediaSessionCallback.PREVIOUS -> player.previous()
                    MediaSessionCallback.STOP -> player.stop()
                    MediaSessionCallback.FORWARD -> seek(SeekDirection.Forward)
                    MediaSessionCallback.REWIND -> seek(SeekDirection.Backward)
                    is MediaSessionCallback.SEEK -> player.seek(
                        it.positionMs,
                        TimeUnit.MILLISECONDS
                    )
                    else -> Log.d("noti", "Event not handled")
                }
            }
        }
    }
    private fun seek(direction: SeekDirection) {
        val seekTime = when (direction) {
            SeekDirection.Forward -> player.position + 1000
            SeekDirection.Backward -> player.position - 1000L
        }

        player.seek(
            seekTime,
            TimeUnit.MILLISECONDS)}


    }