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
    videoResId: Int, // Al llamar a VideoBg recibe la id(nombre del archivo alojado en res/raw)
    modifier: Modifier = Modifier
) {
    val isInPreview = LocalInspectionMode.current //evita que se vea en preview para no causar errores
    if (isInPreview) {
        Box(modifier = modifier.background(Color(0xFF000000))) //en caso de que este en preview asigna el fondo normal sin animacion (solo admite compilado)
        return
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current //lifeCycle se encarga de que el video se reproduzca aún saliendo de la app y reingresando, sin esto simplemente aparece en negro
    val mediaPlayer = remember { MediaPlayer() } //mediaPlayer se encarga de manejar la reproduccion del video
    var surfaceReady = remember { false }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (surfaceReady && !mediaPlayer.isPlaying) { //se evalua que si el evento se reanuda (vuelve a abrir la app) le da play al video
                        mediaPlayer.start()
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    if (mediaPlayer.isPlaying) { // en caso de que el usuario no este en la app pausa el video para evitar problemas
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
                    ViewGroup.LayoutParams.MATCH_PARENT, // se encarga de que el fondo se ajuste a la pantalla, sin esto aparece en un recuadro pequeño
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleX = 1.4f
                scaleY = 1.4f // ajustamos tanto la escala en X y Y para que tenga cierto zoom y no se vean tan alejados los elementos

                holder.addCallback(object : SurfaceHolder.Callback {
                    override fun surfaceCreated(holder: SurfaceHolder) { // esta funcion se encarga de que se le aplique el video al fondo
                        surfaceReady = true
                        try {
                            val afd = ctx.resources.openRawResourceFd(videoResId) //busca que el archivo se encuentre en los recursos Raw
                            mediaPlayer.apply { //configura mediaPlayer para:
                                reset() //limpiar estados previos
                                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length) // apuntar al archivo del video
                                afd.close()
                                setSurface(holder.surface) //sincronizar el video con la superficie
                                isLooping = true
                                setVolume(0f, 0f)
                                setOnPreparedListener { mp -> mp.start() } // cuando esté listo el video, lo reproduce automaticamente
                                setOnErrorListener { _, what, extra ->
                                    android.util.Log.e("VideoBg", "Error: what=$what extra=$extra")
                                    false
                                }
                                prepareAsync() // Lo reproducimos de forma asincronica para que tarde el menor tiempo posible en comenzar
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {} //lo dejamos vacio pues no es necesario de momento

                    override fun surfaceDestroyed(holder: SurfaceHolder) { //cuando se destruye (salimos de la pantalla) pausa el video para liberar recursos y evitar que se dañe el fondo
                        surfaceReady = false
                        if (mediaPlayer.isPlaying) mediaPlayer.pause()
                    }
                })
            }
        },
        modifier = modifier
    )
}