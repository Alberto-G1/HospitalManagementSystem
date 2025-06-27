package org.medcare.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordUtil {

    /**
     * Hashes a plain-text password using the SHA-256 algorithm.
     *  password The plain-text password to hash.
     * returns A Base64-encoded string representing the hashed password.
     */
    public static String hashPassword(String password) {
        try {
            // Get an instance of the SHA-256 message digest algorithm
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Compute the hash of the password bytes
            byte[] hashedBytes = md.digest(password.getBytes());

            // Encode the raw byte hash into a user-friendly Base64 string
            return Base64.getEncoder().encodeToString(hashedBytes);

        } catch (NoSuchAlgorithmException e) {

            throw new RuntimeException("Could not find SHA-256 hashing algorithm", e);
        }
    }

    /**
     * Checks if a given plain-text password matches a stored hashed password.
     *
     * plainPassword The password entered by the user during login.
     * hashedPassword The hash stored in the database for that user.
     * returns true if the passwords match, false otherwise.
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        // Prevent NullPointerExceptions
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }

        // Hash the plain-text password from the login attempt
        String hashedPlainPassword = hashPassword(plainPassword);

        // Compare the newly generated hash with the one from the database
        return hashedPlainPassword.equals(hashedPassword);
    }
}