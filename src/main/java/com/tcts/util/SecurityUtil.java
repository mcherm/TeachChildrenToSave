package com.tcts.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import com.tcts.exception.InvalidBase64DataException;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


public class SecurityUtil {
	/**      **/
	public final static int ITERATION_NUMBER = 1000;

    /**
     * This generates a random salt to be used.
     */
    public static String generateSalt() {
        byte[] saltBytes = new byte[8]; // we want to generate 64 bits of salt
        SecureRandom random;
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch(NoSuchAlgorithmException err) {
            throw new RuntimeException("Sun's standard PRNG is missing. Cannot operate without it!");
        }
        random.nextBytes(saltBytes);
        return byteToBase64(saltBytes);
    }

    /**
     * Given a password (as entered by the user) and a salt (encoded in base64), this will
     * generate the hash of that password.
     *
     * @throws InvalidBase64DataException if the salt provided is not a valid base64 data string
     */
    public static String getHashedPassword(String password, String salt) throws InvalidBase64DataException {
        byte[] saltBytes = base64ToByte(salt);
        byte[] hashBytes = getHash(ITERATION_NUMBER, password, saltBytes);
        return byteToBase64(hashBytes);
    }

    /**
     * From a password, a number of iterations and a salt,
     * returns the corresponding digest
     * @param iterationNb int The number of iterations of the algorithm
     * @param password String The password to encrypt
     * @param salt byte[] The salt
     * @return byte[] The digested password
     */
    private static byte[] getHash(int iterationNb, String password, byte[] salt) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch(NoSuchAlgorithmException err) {
            throw new RuntimeException("Internal error: SHA-1 should always be available.");
        }
        digest.reset();
        digest.update(salt);
        byte[] input;
        try {
            input = digest.digest(password.getBytes("UTF-8"));
        } catch(UnsupportedEncodingException err) {
            throw new RuntimeException(("Internal error: UTF-8 will always be supported."));
        }
        for (int i = 0; i < iterationNb; i++) {
            digest.reset();
            input = digest.digest(input);
        }
        return input;
    }

    /**
     * From a base 64 representation, returns the corresponding byte[]
     * @param data String The base64 representation
     * @return byte[]
     * @throws IOException
     */
    private static byte[] base64ToByte(String data) throws InvalidBase64DataException {
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            return decoder.decodeBuffer(data);
        } catch(IOException err) {
            throw new InvalidBase64DataException();
        }
    }

    /**
     * From a byte[] returns a base 64 representation
     * @param data byte[]
     * @return String
     * @throws IOException
     */
    private static String byteToBase64(byte[] data){
        BASE64Encoder endecoder = new BASE64Encoder();
        return endecoder.encode(data);
    }
}
