package fr.flavien;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class AESUtil {
    public static final String CIPHER_ALGORITHM = "AES";
    public static final String KEY_ALGORITHM = "AES";
    public static final String PASS_HASH_ALGORITHM = "SHA-256";

    public static String encrypt(String data, String password) {
        try {
            Cipher cipher = buildCipher(password, Cipher.ENCRYPT_MODE);
            byte[] dataToSend = data.getBytes("UTF-8");
            byte[] encryptedData = cipher.doFinal(dataToSend);
            return Base64.getEncoder().encodeToString(encryptedData);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String encryptedValue, String password) {
        try {
            Cipher cipher = buildCipher(password, Cipher.DECRYPT_MODE);
            byte[] encryptedData = Base64.getDecoder().decode(encryptedValue);
            byte[] data = cipher.doFinal(encryptedData);
            return new String(data, "UTF-8");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Cipher buildCipher(String password, int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        Key key = buildKey(password);
        cipher.init(mode, key);

        return cipher;
    }

    private static Key buildKey(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digester = MessageDigest.getInstance(PASS_HASH_ALGORITHM);
        digester.update(String.valueOf(password).getBytes("UTF-8"));
        byte[] key = Arrays.copyOfRange(digester.digest(), 0, 16);
        return new SecretKeySpec(key, KEY_ALGORITHM);
    }
}
