package com.example.ui.util

import android.content.Context
import android.media.AudioManager
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.net.Uri
import android.util.Log

object SoundHelper {
    private var toneGenerator: ToneGenerator? = null
    var isSoundEnabled: Boolean = true

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
        } catch (e: Throwable) {
            Log.e("SoundHelper", "Init ToneGenerator failed", e)
        }
    }

    /**
     * Bíp ngắn cho thao tác bấm nút
     */
    fun playClickSound() {
        if (!isSoundEnabled) return
        try {
            // Sử dụng tiếng bíp nhẹ nhàng cho thao tác click
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 70)
        } catch (e: Throwable) {
            Log.e("SoundHelper", "Play click sound failed", e)
        }
    }

    /**
     * Chime dễ chịu khi xử lý xong đơn hàng
     */
    fun playSuccessSound(context: Context) {
        if (!isSoundEnabled) return
        try {
            // Chơi âm báo hệ thống mặc định (để có trải nghiệm âm thanh mượt mà, quen thuộc nhất)
            val notificationUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(context, notificationUri)
            if (ringtone != null) {
                ringtone.play()
            } else {
                // Dự phòng nếu không lấy được âm báo hệ thống
                playFallbackSuccessChime()
            }
        } catch (e: Throwable) {
            Log.e("SoundHelper", "Play notification sound failed, trying fallback", e)
            playFallbackSuccessChime()
        }
    }

    private fun playFallbackSuccessChime() {
        try {
            // Phát một chuỗi 2 âm cao liên tiếp tạo cảm giác thành công
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_PIP, 200)
        } catch (ex: Throwable) {
            Log.e("SoundHelper", "Play fallback success chime failed", ex)
        }
    }
}
