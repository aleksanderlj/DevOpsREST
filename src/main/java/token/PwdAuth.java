package token;

import org.mindrot.jbcrypt.BCrypt;

public class PwdAuth {

    public static String hash(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public static boolean verify(String plaintext, String hashed){
        return BCrypt.checkpw(plaintext, hashed);
    }

    /* PBKDF2
    public static String hashPassword(String pwd) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        KeySpec spec = new PBEKeySpec(pwd.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(factory.generateSecret(spec).getEncoded());
    }
     */
}
