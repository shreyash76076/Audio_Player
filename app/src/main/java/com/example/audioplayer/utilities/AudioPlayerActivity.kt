package com.example.audioplayer.utilities

import android.app.Application
import com.doublesymmetry.kotlinaudio.models.CacheConfig
import com.doublesymmetry.kotlinaudio.models.PlayerConfig
import com.doublesymmetry.kotlinaudio.players.QueuedAudioPlayer
import com.example.audioplayer.R
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache

class AudioPlayerActivity : Application() {
    lateinit var player: QueuedAudioPlayer
    override fun onCreate() {
        super.onCreate()
        initPlayer()
    }
    private fun initPlayer() {
        player = QueuedAudioPlayer(
            baseContext,
            playerConfig = PlayerConfig(
                interceptPlayerActionsTriggeredExternally = true,
                handleAudioBecomingNoisy = true,
                handleAudioFocus = true
            ),
            cacheConfig = CacheConfig(exoPlayerCacheSize, getString(R.string.app_name)),
        )
    }
    companion object {

        lateinit var instance: AudioPlayerActivity
        lateinit var simpleCache: SimpleCache
        const val exoPlayerCacheSize: Long = 90 * 1024 * 1024
        lateinit var leastRecentlyUsedCacheEvictor: LeastRecentlyUsedCacheEvictor
        lateinit var exoDatabaseProvider: StandaloneDatabaseProvider}

}