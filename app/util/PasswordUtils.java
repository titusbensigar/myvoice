package util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import play.Logger;
import play.Play;

public class PasswordUtils {
	
	public static byte[] generateSalt() {
		SecureRandom random = null;
		
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			noSuchAlgorithmException.printStackTrace();
		}
		
		// Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
		if (random != null) {
			byte[] salt = new byte[Play.application().configuration().getInt("password.hashing.PBKDF2.salt.length")];
			random.nextBytes(salt);
			
			return salt;
		} else {
			return null;
		}		
	}
	
	public static byte[] getEncryptedPassword(String password, byte[] userSalt)
			throws Exception {
		if (password == null || userSalt == null) {
			return null;
		}
		
		byte[] encryptedPassword = null;
		KeySpec keySpec = null;
		SecretKeyFactory secretKeyFactory = null;
		
		// PBKDF2 with SHA-1 as the hashing algorithm
		String algorithm = Play.application().configuration().getString("password.hashing.PBKDF2.algorithm");
		
		// SHA-1 generates 160 bit hashes, so that's what makes sense here
		int derivedKeyLength = Play.application().configuration().getInt("password.hashing.PBKDF2.key.length");
		
		// Number of iterations for creating the Hash
		int iterations = Play.application().configuration().getInt("password.hashing.PBKDF2.iterations");
		
		try {
			secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
			
			/*
			 * Encrypt with User salt
			 */
			keySpec = new PBEKeySpec(password.toCharArray(), userSalt, iterations, derivedKeyLength);
	
			encryptedPassword = secretKeyFactory.generateSecret(keySpec).getEncoded();
			
			/*
			 * Encrypt with "System Salt"
			 */			
			String systemSalt = Play.application().configuration().getString("password.hashing.PBKDF2.system.salt");
			
			keySpec = new PBEKeySpec(getCharacterArray(encryptedPassword), systemSalt.getBytes(), iterations, derivedKeyLength);
			
			encryptedPassword = secretKeyFactory.generateSecret(keySpec).getEncoded();
		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			noSuchAlgorithmException.printStackTrace();
			throw new Exception(noSuchAlgorithmException);
		} catch (InvalidKeySpecException invalidKeySpecException) {
			invalidKeySpecException.printStackTrace();
			throw new Exception(invalidKeySpecException);
		}
		
		return encryptedPassword;
	}
	
	public static boolean authenticate(String attemptedPassword,
			byte[] encryptedPassword, byte[] userSalt) {
		byte[] encryptedAttemptedPassword = null;
				
		// Encrypt the clear-text password using the same salt that was used to encrypt the original password
		try {
			encryptedAttemptedPassword = getEncryptedPassword(attemptedPassword, userSalt);
		} catch (Exception exception) {
			exception.printStackTrace();
		}		
//		Logger.info("encryptedAttemptedPassword -> "+ encryptedAttemptedPassword );
//		Logger.info("encryptedPassword -> "+ encryptedPassword );
//		Logger.info("userSalt -> "+ userSalt );
		// Authentication succeeds if encrypted password that the user entered Wis equal to the stored hash
		if (encryptedPassword != null && userSalt != null) {
			return Arrays.equals(encryptedPassword, encryptedAttemptedPassword);
		} else {
			return false;
		}
	}
	
	
	public static char[] getCharacterArray(byte[] byteArray) {
		if (byteArray == null || byteArray.length <= 0) {
			return new char[0];
		}

		char[] charArray = new char[byteArray.length];
		for (int i = 0; i<byteArray.length; i++) {
			charArray[i] = (char) byteArray[i];
		}
		return charArray;
	}
}
