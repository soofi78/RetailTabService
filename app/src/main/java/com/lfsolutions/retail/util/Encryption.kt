package com.lfsolutions.retail.util

import android.util.Base64
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import java.io.UnsupportedEncodingException
import java.security.*
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * Created by Taimur.
 */
class Encryption private constructor(private val mBuilder: Builder) {
    @Throws(
        UnsupportedEncodingException::class,
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class,
        InvalidKeySpecException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class
    )
    fun encrypt(data: String?): String? {
        if (data == null) return null
        val secretKey = getSecretKey(hashTheKey(mBuilder.key))
        val dataBytes = mBuilder.charsetName?.let { charset(it) }?.let { data.toByteArray(it) }
        val cipher = Cipher.getInstance(mBuilder.algorithm)
        cipher.init(
            Cipher.ENCRYPT_MODE,
            secretKey,
            mBuilder.ivParameterSpec,
            mBuilder.secureRandom
        )
        return Base64.encodeToString(cipher.doFinal(dataBytes), mBuilder.base64Mode)
    }

    fun encryptOrNull(data: String?): String? {
        return try {
            encrypt(data)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun encryptAsync(data: String?, callback: Callback?) {
        if (callback == null) return
        Thread {
            try {
                val encrypt = encrypt(data)
                if (encrypt == null) {
                    callback.onError(Exception("null returned, it normally occurs when you send a null data"))
                }
                callback.onSuccess(encrypt)
            } catch (e: Exception) {
                callback.onError(e)
            }
        }.start()
    }

    @Throws(
        UnsupportedEncodingException::class,
        NoSuchAlgorithmException::class,
        InvalidKeySpecException::class,
        NoSuchPaddingException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class
    )
    fun decrypt(data: String?): String? {
        if (data == null) return null
        val dataBytes = Base64.decode(data, mBuilder.base64Mode)
        val secretKey = getSecretKey(hashTheKey(mBuilder.key))
        val cipher = Cipher.getInstance(mBuilder.algorithm)
        cipher.init(
            Cipher.DECRYPT_MODE,
            secretKey,
            mBuilder.ivParameterSpec,
            mBuilder.secureRandom
        )
        val dataBytesDecrypted = cipher.doFinal(dataBytes)
        return String(dataBytesDecrypted)
    }

    fun decryptOrNull(data: String?): String? {
        return try {
            decrypt(data)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun decryptAsync(data: String?, callback: Callback?) {
        if (callback == null) return
        Thread {
            try {
                val decrypt = decrypt(data)
                if (decrypt == null) {
                    callback.onError(Exception("null returned, it normally occurs when you send a null data"))
                }
                callback.onSuccess(decrypt)
            } catch (e: Exception) {
                callback.onError(e)
            }
        }.start()
    }

    @Throws(
        NoSuchAlgorithmException::class,
        UnsupportedEncodingException::class,
        InvalidKeySpecException::class
    )
    private fun getSecretKey(key: CharArray): SecretKey {
        val factory = SecretKeyFactory.getInstance(mBuilder.secretKeyType)
        val spec: KeySpec = PBEKeySpec(
            key,
            mBuilder.charsetName?.let { charset(it) }?.let { mBuilder.salt?.toByteArray(it) },
            mBuilder.iterationCount,
            mBuilder.keyLength
        )
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, mBuilder.keyAlgorithm)
    }

    @Throws(UnsupportedEncodingException::class, NoSuchAlgorithmException::class)
    private fun hashTheKey(key: String?): CharArray {
        val messageDigest = MessageDigest.getInstance(mBuilder.digestAlgorithm)
        messageDigest.update(mBuilder.charsetName?.let { charset(it) }
            ?.let { key!!.toByteArray(it) })
        return Base64.encodeToString(messageDigest.digest(), Base64.NO_PADDING).toCharArray()
    }

    interface Callback {
        fun onSuccess(result: String?)
        fun onError(exception: Exception?)
    }

    class Builder {
        lateinit var iv: ByteArray
        public var keyLength = 0
        public var base64Mode = 0
        public var iterationCount = 0
        public var salt: String? = null
        public var key: String? = null
        public var algorithm: String? = null
        public var keyAlgorithm: String? = null
        public var charsetName: String? = null
        public var secretKeyType: String? = null
        public var digestAlgorithm: String? = null
        public var secureRandomAlgorithm: String? = null
        public var secureRandom: SecureRandom? = null
        public var ivParameterSpec: IvParameterSpec? = null

        @Throws(NoSuchAlgorithmException::class)
        fun build(): Encryption {
            secureRandom = SecureRandom.getInstance(secureRandomAlgorithm)
            ivParameterSpec = IvParameterSpec(iv)
            return Encryption(this)
        }

        companion object {
            fun getDefaultBuilder(key: String?, salt: String?, iv: ByteArray): Builder {
                val builder = Builder()
                builder.iv = iv
                builder.key = key
                builder.salt = salt
                builder.keyLength = 128
                builder.keyAlgorithm = "AES"
                builder.charsetName = "UTF8"
                builder.iterationCount = 1
                builder.digestAlgorithm = "SHA1"
                builder.base64Mode = Base64.DEFAULT
                builder.algorithm = "AES/CBC/PKCS5Padding"
                builder.secureRandomAlgorithm = "SHA1PRNG"
                builder.secretKeyType = "PBKDF2WithHmacSHA1"
                return builder
            }
        }
    }

    companion object {
        val K = Main.app.getString(R.string.key)
        val S = Main.app.getString(R.string.salt)
        val IV = ByteArray(16)

        @JvmStatic
        val default: Encryption?
            get() = try {
                Builder.getDefaultBuilder(K, S, IV).build()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
                null
            }
    }
}