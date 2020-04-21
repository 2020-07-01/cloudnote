package com.Util;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.SecureRandom;

/**
 * @program: ASEUtils
 * @description: ase加密解密算法工具类
 * @create: 2020-01-16 11:11
 **/
public class ASEUtils {

    /**
     * 密钥长度: 128, 192 or 256
     */
    private static final int KEY_SIZE = 128;

    /**
     * 加密/解密算法名称
     */
    private static final String ALGORITHM = "AES";

    /**
     * 随机数生成器（RNG）算法名称
     */
    private static final String RNG_ALGORITHM = "SHA1PRNG";

    /**
     * 数据密钥对象
     *
     * @param key
     * @return
     * @throws Exception
     */
    private static SecretKey generateKey(byte[] key) throws Exception {

        // 创建安全随机数生成器
        SecureRandom random = SecureRandom.getInstance(RNG_ALGORITHM);
        // 设置 密钥key的字节数组 作为安全随机数生成器的种子
        random.setSeed(key);

        // 创建 AES算法生成器
        KeyGenerator gen = KeyGenerator.getInstance(ALGORITHM);

        // 初始化算法生成器
        gen.init(KEY_SIZE, random);

        // 生成 AES密钥对象, 也可以直接创建密钥对象: return new SecretKeySpec(key, ALGORITHM);
        return gen.generateKey();

    }

    /**
     * 加密
     *
     * @param plainBytes
     * @param key
     * @return
     * @throws Exception
     */
    public static String encrypt(byte[] plainBytes, byte[] key) throws Exception {
        // 生成密钥对象
        SecretKey secKey = generateKey(key);
        // 获取 AES 密码器
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        // 初始化密码器（加密模型）
        cipher.init(Cipher.ENCRYPT_MODE, secKey);
        // 加密数据, 返回密文
        byte[] cipherBytes = cipher.doFinal(plainBytes);

        return Base64.encode(cipherBytes);
    }

    /**
     * 解密
     *
     * @param cipherBytes
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] cipherBytes, byte[] key) throws Exception {
        // 生成密钥对象
        SecretKey secKey = generateKey(key);
        // 获取 AES 密码器
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        // 初始化密码器（解密模型）
        cipher.init(Cipher.DECRYPT_MODE, secKey);
        // 解密数据, 返回明文
        byte[] plainBytes = cipher.doFinal(cipherBytes);

        return plainBytes;
    }




}