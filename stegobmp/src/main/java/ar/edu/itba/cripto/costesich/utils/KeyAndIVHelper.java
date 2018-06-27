package ar.edu.itba.cripto.costesich.utils;

import ar.edu.itba.cripto.costesich.cli.AlgoMode;

import javax.crypto.Cipher;
import java.security.MessageDigest;

public class KeyAndIVHelper {
    private final byte[] key;
    private final byte[] iv;
    private static final int KEY_IDX = 0;
    private static final int IV_IDX = 1;

    public KeyAndIVHelper(String password, AlgoMode algo, Cipher cipher, MessageDigest digest) {
        var ivSize = cipher.getBlockSize();
        var keySize = algo.getPadSize();
        var both = evpBytesToKey(keySize, ivSize, digest, password.getBytes(), 1);
        this.iv = both[IV_IDX];
        this.key = both[KEY_IDX];
    }


    /* taken from:
       - https://stackoverflow.com/questions/11783062/how-to-decrypt-file-in-java-encrypted-with-openssl-command-using-aes/11786924#11786924
       - https://gist.github.com/luosong/5523434
    */
    private static byte[][] evpBytesToKey(int keyLen, int ivLen, MessageDigest md, byte[] data, int count){
        byte[][] both = new byte[2][];
        byte[] key = new byte[keyLen];
        int key_ix = 0;
        byte[] iv = new byte[ivLen];
        int iv_ix = 0;
        both[KEY_IDX] = key;
        both[IV_IDX] = iv;
        byte[] md_buf = null;
        int nkey = keyLen;
        int niv = ivLen;
        int i = 0;
        if (data == null) {
            return both;
        }
        int addmd = 0;
        for (;;) {
            md.reset();
            if (addmd++ > 0) {
                md.update(md_buf);
            }
            md.update(data);
            md_buf = md.digest();
            for (i = 1; i < count; i++) {
                md.reset();
                md.update(md_buf);
                md_buf = md.digest();
            }
            i = 0;
            if (nkey > 0) {
                for (;;) {
                    if (nkey == 0)
                        break;
                    if (i == md_buf.length)
                        break;
                    key[key_ix++] = md_buf[i];
                    nkey--;
                    i++;
                }
            }
            if (niv > 0 && i != md_buf.length) {
                for (;;) {
                    if (niv == 0)
                        break;
                    if (i == md_buf.length)
                        break;
                    iv[iv_ix++] = md_buf[i];
                    niv--;
                    i++;
                }
            }
            if (nkey == 0 && niv == 0) {
                break;
            }
        }
        for (i = 0; i < md_buf.length; i++) {
            md_buf[i] = 0;
        }
        return both;
    }

    public byte[] getKey() {
        return key;
    }

    public byte[] getIv() {
        return iv;
    }
}
