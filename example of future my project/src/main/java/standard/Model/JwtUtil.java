package standard.Model;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class JwtUtil {

  
    private static final String SECRET_KEY = "your-secret-key"; 

    private static final long EXPIRATION_TIME = 3600000; 

    private static final Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

    /**
     * Generates a JWT token for the given username.
     *
     * @param username The username to be included in the token.
     * @return A JWT token.
     */
    public static String generateToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(algorithm);
    }

    /**
     * Extracts the username from a given JWT token.
     *
     * @param token The JWT token.
     * @return The username if the token is valid; null otherwise.
     */
public static String getUsernameFromToken(String token) {
    if (token == null) {
        System.err.println("Token is null.");
        return null;
    }
    try {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getSubject();
    } catch (JWTDecodeException e) {
        // Log or handle the exception as appropriate for your application.
        return null;
    }
}

    
    /**
     * Validates a JWT token.
     *
     * @param token The JWT token to validate.
     * @return true if the token is valid; false otherwise.
     */
    public static boolean validateToken(String token) {
        try {
            JWT.require(Algorithm.HMAC256(SECRET_KEY))
                .build()
                .verify(token);
            return true; // Token is valid
        } catch (JWTVerificationException e) {
            // Token verification failed
            return false;
        }
    }
    
}
