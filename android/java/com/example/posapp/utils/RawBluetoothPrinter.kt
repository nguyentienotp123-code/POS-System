package com.example.posapp.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object PrinterHelper {
    fun shareOrderImage(context: Context, bitmap: Bitmap) {
        try {
            // 1. Lưu ảnh vào bộ nhớ tạm
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val stream = FileOutputStream("$cachePath/image.png")
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            // 2. Lấy URI của ảnh qua FileProvider
            val imagePath = File(context.cacheDir, "images")
            val newFile = File(imagePath, "image.png")
            val contentUri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", newFile)

            // 3. Tạo Intent chia sẻ
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, contentUri)
                type = "image/png"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // Mở bảng chia sẻ để người dùng chọn "Fun Print"
            context.startActivity(Intent.createChooser(shareIntent, "Chọn Fun Print để in"))

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}