package com.oxy.mmr.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.foundation.gestures.Orientation
import com.oxy.mmr.wrapper.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext

object MediaUtils {
    fun loadThumbs(
        context: Context,
        uri: Uri?,
        totalCount: Int = 28,
        oneByOne: Boolean = true,
        // target scaled bitmap pixel length
        // param: first int = width, second int = height
        dstWidth: (Int, Int) -> Int = { it, _ -> it },
        dstHeight: (Int, Int) -> Int = { _, it -> it }
    ): Flow<Resource<List<Bitmap?>>> = channelFlow {
        send(Resource.Loading)
        if (uri == null) {
            send(Resource.Failure("Uri is null."))
            return@channelFlow
        }
        try {
            var bitmaps = emptyList<Bitmap?>()
            MediaMetadataRetriever().use { retriever ->
                retriever.setDataSource(context, uri)
                val duration = retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_DURATION
                )?.toInt() ?: -1

                val deltaTime = duration / totalCount * 1000

                withContext(Dispatchers.IO) {
                    repeat(totalCount) { i ->
                        val bitmap = retriever.getFrameAtTime(
                            (deltaTime.toLong() * i),
                            MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                        )
                        bitmaps = bitmaps + bitmap?.let {
                            Bitmap.createScaledBitmap(
                                it,
                                dstWidth(it.width, it.height),
                                dstHeight(it.width, it.height),
                                true
                            )
                        }
                        bitmap?.recycle()
                        if (oneByOne) send(Resource.Success(bitmaps))
                    }
                }
                if (!oneByOne) send(Resource.Success(bitmaps))

            }
        } catch (e: Exception) {
            send(Resource.Failure(e.message.orEmpty()))
        }
    }

    suspend fun getDuration(
        context: Context,
        uri: Uri?
    ): Long = coroutineScope {
        MediaMetadataRetriever().use { retriever ->
            withContext(Dispatchers.IO) {
                retriever.setDataSource(context, uri)
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
                    ?: -1L
            }
        }
    }

    suspend fun recycleUseless(old: List<Bitmap>, new: List<Bitmap>) = coroutineScope {
        withContext(Dispatchers.IO) {
            old.forEach { bitmap ->
                if (bitmap !in new) bitmap.recycle()
            }
        }
    }

    suspend fun recycleNullableUseless(old: List<Bitmap?>, new: List<Bitmap?>) = coroutineScope {
        recycleUseless(old.filterNotNull(), new.filterNotNull())
    }

    suspend fun merge(
        bitmaps: List<Bitmap?>,
        orientation: Orientation = Orientation.Horizontal,
        shortPixel: Int = 48,
    ): Bitmap? = coroutineScope {
        if (bitmaps.all { it == null }) null
        else withContext(Dispatchers.Default) {
            when (orientation) {
                Orientation.Vertical -> bitmaps.mergeVertical(shortPixel)
                Orientation.Horizontal -> bitmaps.mergeHorizontal(shortPixel)
            }
        }
    }

    private fun List<Bitmap?>.mergeHorizontal(shortPixel: Int): Bitmap? {
        val oh = maxOf { it?.height ?: 0 }
        val ow = sumOf { it?.width ?: 0 }
        val h = shortPixel
        val r = oh / h
        val w = ow / r
        val pw = w / size
        return Bitmap.createBitmap(w, h, Bitmap.Config.RGBA_F16).apply {
            Canvas(this).apply {
                var total = 0
                forEach {
                    it?.let { drawBitmap(it, null, Rect(total, 0, total + pw, h), null) }
                    total += pw
                }
            }
        }
    }

    private fun List<Bitmap?>.mergeVertical(shortPixel: Int): Bitmap? {
        val ow = maxOf { it?.width ?: 0 }
        val oh = sumOf { it?.height ?: 0 }
        val w = shortPixel
        val r = ow / w
        val h = oh / r
        val ph = h / size

        return Bitmap.createBitmap(w, h, Bitmap.Config.RGBA_F16).apply {
            Canvas(this).apply {
                var total = 0
                forEach {
                    it?.let { drawBitmap(it, null, Rect(0, total, h, total + ph), null) }
                    total += ph
                }
            }
        }
    }
}