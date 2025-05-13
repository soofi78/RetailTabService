package com.videotel.digital.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.lfsolutions.retail.util.Encryption
import java.io.ByteArrayOutputStream

class Base64Manager {
    companion object {
        fun encodeImage(bm: Bitmap): String? {
            val baos = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val b = baos.toByteArray()
            return Encryption.default?.encrypt(Base64.encodeToString(b, Base64.DEFAULT))
        }

        fun decodeImage(image: String): Bitmap {
            val decodedString: ByteArray =
                Base64.decode(Encryption.default?.decrypt(image), Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        }
    }
}