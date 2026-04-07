package com.gamedleuv.ui.components

import android.media.MediaPlayer
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun VideoBg(
    videoResId: Int,
    modifier: Modifier = Modifier
) {
    val isInPreview = LocalInspectionMode.current
    if (isInPreview) {
        Box(modifier = modifier.background(Color.DarkGray))
        return
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mediaPlayer = remember { MediaPlayer() }
    var surfaceReady = remember { false }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (surfaceReady && !mediaPlayer.isPlaying) {
                        mediaPlayer.start()
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.pause()
                    }
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            SurfaceView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleX = 1.4f
                scaleY = 1.4f

                holder.addCallback(object : SurfaceHolder.Callback {
                    override fun surfaceCreated(holder: SurfaceHolder) {
                        surfaceReady = true
                        try {
                            val afd = ctx.resources.openRawResourceFd(videoResId)
                            mediaPlayer.apply {
                                reset()
                                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                                afd.close()
                                setSurface(holder.surface)
                                isLooping = true
                                setVolume(0f, 0f)
                                setOnPreparedListener { mp -> mp.start() }
                                setOnErrorListener { _, what, extra ->
                                    android.util.Log.e("VideoBg", "Error: what=$what extra=$extra")
                                    false
                                }
                                prepareAsync()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

                    override fun surfaceDestroyed(holder: SurfaceHolder) {
                        surfaceReady = false
                        if (mediaPlayer.isPlaying) mediaPlayer.pause()
                    }
                })
            }
        },
        modifier = modifier
    )
}