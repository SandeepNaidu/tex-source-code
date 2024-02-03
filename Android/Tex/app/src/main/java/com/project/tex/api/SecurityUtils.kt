package com.project.tex.api

import java.io.UnsupportedEncodingException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import java.security.spec.InvalidParameterSpecException
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec


object SecurityUtils {
    private var secret : SecretKeySpec? = null
//    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
//    fun generateKey(): SecretKey {
//        return SecretKeySpec(password.getBytes(), "AES").also {
//            secret = it
//        }
//    }

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        InvalidParameterSpecException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        UnsupportedEncodingException::class
    )

    fun encryptMsg(message: String, secret: SecretKey?): ByteArray? {
        /* Encrypt the message. */
        var cipher: Cipher? = null
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secret)
        return cipher.doFinal(message.toByteArray(charset("UTF-8")))
    }

//    @Throws(
//        NoSuchPaddingException::class,
//        NoSuchAlgorithmException::class,
//        InvalidParameterSpecException::class,
//        InvalidAlgorithmParameterException::class,
//        InvalidKeyException::class,
//        BadPaddingException::class,
//        IllegalBlockSizeException::class,
//        UnsupportedEncodingException::class
//    )
//    fun decryptMsg(cipherText: ByteArray?, secret: SecretKey?): String? {
//        /* Decrypt the message, given derived encContentValues and initialization vector. */
//        var cipher: Cipher? = null
//        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
//        cipher.init(Cipher.DECRYPT_MODE, secret)
//        return String(cipher.doFinal(cipherText), "UTF-8")
//    }
}