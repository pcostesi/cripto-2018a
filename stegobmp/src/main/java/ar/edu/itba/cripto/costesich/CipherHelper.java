package ar.edu.itba.cripto.costesich;

import ar.edu.itba.cripto.costesich.cli.AlgoMode;
import ar.edu.itba.cripto.costesich.cli.BlockMode;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class CipherHelper {
    final AlgoMode algo;
    final BlockMode mode;
    final byte[] key;
    final byte[] initVector;

    public CipherHelper(AlgoMode algo, BlockMode mode, String password, String initVector) {
        try {
            this.algo = algo;
            this.mode = mode;
            this.key = deriveKey(algo, password);
            this.initVector = deriveIV(algo, initVector);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("MD5 not available :(");
        }
    }


    private byte[] deriveKey(AlgoMode algo, String password) throws NoSuchAlgorithmException {
        var digest = MessageDigest.getInstance("MD5");
        digest.update(password.getBytes());

        int keyLength = 0;
        switch (algo) {
            case des:
                keyLength = 8;
                break;
            case aes128:
                keyLength = 16;
                break;
            case aes192:
                keyLength = 24;
                break;
            case aes256:
                keyLength = 32;
                break;
            default:
                throw new IllegalArgumentException("Invalid key length");
        }
        return Arrays.copyOf(digest.digest(), keyLength);
    }

    private byte[] deriveIV(AlgoMode algo, String initVector) throws NoSuchAlgorithmException {
        var digest = MessageDigest.getInstance("MD5");
        digest.update(initVector.getBytes());

        int ivLength = 0;
        switch (algo) {
            case des:
                ivLength = 8;
                break;
            case aes128:
            case aes192:
            case aes256:
                ivLength = 16;
                break;
            default:
                throw new IllegalArgumentException("Invalid iv length");
        }
        return Arrays.copyOf(digest.digest(), ivLength);
    }

    public Cipher getEncryptionCipher() {
        return getCipher(Cipher.ENCRYPT_MODE);
    }

    public Cipher getDecryptionCipher() {
        return getCipher(Cipher.DECRYPT_MODE);
    }

    private Cipher getCipher(int encryptionMode) {
        String algoName = algo == AlgoMode.des ? "DES" : "AES";
        String modeName = mode.name().toUpperCase();
        String cipherInstance = algoName + "/" + modeName + "/" + "PKCS5Padding";

        try {
            var initVectorSpec = new IvParameterSpec(initVector);
            var cipher = Cipher.getInstance(cipherInstance);
            var keySpec = new SecretKeySpec(key, algoName);

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
