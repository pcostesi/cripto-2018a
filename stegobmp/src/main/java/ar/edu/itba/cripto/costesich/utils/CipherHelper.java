package ar.edu.itba.cripto.costesich.utils;

import ar.edu.itba.cripto.costesich.cli.AlgoMode;
import ar.edu.itba.cripto.costesich.cli.BlockMode;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

public class CipherHelper {
    public static final String ALGORITHM = "SHA-256";
    private final AlgoMode algo;
    private final BlockMode mode;
    private final String password;
    private final MessageDigest digest;

    public CipherHelper(AlgoMode algo, BlockMode mode, String password) {
        try {
            this.algo = algo;
            this.mode = mode;
            this.password = password;
            this.digest = MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(ALGORITHM + " not available :(");
        }
    }

    public Cipher getEncryptionCipher() {
        return getCipher(Cipher.ENCRYPT_MODE);
    }

    public Cipher getDecryptionCipher() {
        return getCipher(Cipher.DECRYPT_MODE);
    }

    private Cipher getCipher(int encryptionMode) {

        var algoName = algo.getAlgoName();
        var modeName = mode.name().toUpperCase();
        var paddingName = encryptionMode == Cipher.ENCRYPT_MODE ? "PKCS5Padding" : "NoPadding";
        var cipherInstance = algoName + "/" + modeName + "/" + paddingName;

        try {
            var cipher = Cipher.getInstance(cipherInstance);
            var keyAndIv = new KeyAndIVHelper(password, algo, cipher, digest);
            var initVectorSpec = new IvParameterSpec(keyAndIv.getIv());
            var keySpec = new SecretKeySpec(keyAndIv.getKey(), algoName);

            switch (mode) {
                case ecb:
                    cipher.init(encryptionMode, keySpec);
                    break;
                case cbc:
                case cfb:
                case ofb:
                    cipher.init(encryptionMode, keySpec, initVectorSpec);
                    break;
                default:
                    throw new IllegalArgumentException("No such mode.");
            }
            return cipher;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Ooops");
    }


}
