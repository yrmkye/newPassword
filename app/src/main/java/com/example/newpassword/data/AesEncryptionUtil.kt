package com.example.newpassword.data

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * AES加密解密工具类
 */
object AesEncryptionUtil {
    // 定义加密算法名称
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    // 定义密钥，长度为16字节
    private const val DEFAULT_KEY = "0123456789abcdef"
    // 定义初始向量，长度为16字节
    private const val IV = "your_iv_16_bytes"
    /**
     * 加密数据
     * @param data 待加密的数据
     * @return 加密后的数据，如果加密失败返回null
     */
    fun encryptWithKey(data: String, ): String {
        // 创建密钥
        val secretKeySpec = SecretKeySpec(DEFAULT_KEY.toByteArray(), "AES")
        // 创建初始向量
        val ivParameterSpec = IvParameterSpec(IV.toByteArray())
        // 获取加密器实例
        val cipher = Cipher.getInstance(ALGORITHM)
        // 初始化加密器为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
        // 加密数据并返回
        return Base64.encodeToString(cipher.doFinal(data.toByteArray()), Base64.DEFAULT)
    }
    /**
     * 解密数据
     * @param encryptedData 待解密的数据
     * @return 解密后的数据，如果解密失败返回null
     */
    fun decryptWithKey(encryptedData: String, ): String {
        // 创建密钥
        val secretKeySpec = SecretKeySpec(DEFAULT_KEY.toByteArray(), "AES")
        // 创建初始向量
        val ivParameterSpec = IvParameterSpec(IV.toByteArray())
        // 获取加密器实例
        val cipher = Cipher.getInstance(ALGORITHM)
        // 初始化加密器为解密模式
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
        // 解密数据并返回
        return String(cipher.doFinal(Base64.decode(encryptedData, Base64.DEFAULT)))

    }
}
