package jwt;

import io.jsonwebtoken.*;
import model.User;
import util.PropFile;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;

public class JWTHandler {
    private static Key key;
    private static final int TOKEN_EXPIRY = 2880; // 2 days

    public static String generateJwtToken(User user){
        Calendar expiry = Calendar.getInstance();
        expiry.add(Calendar.MINUTE, TOKEN_EXPIRY);

        return Jwts.builder()
                .setSubject("verify")
                .claim("id", user.getId())
                .signWith(SignatureAlgorithm.HS512, getKey())
                .setExpiration(expiry.getTime())
                .compact();
    }

    public static Key getKey() {
        if (key == null){
            String s = PropFile.getProperty("jwtkey");
            key = new SecretKeySpec(s.getBytes(StandardCharsets.UTF_8), 0, s.length(), "HS512");
        }
        return key;
    }

    public static Jws<Claims> validateAndDecode(String token){
        return Jwts.parser()
                .setSigningKey(getKey())
                .parseClaimsJws(token);
    }
}
