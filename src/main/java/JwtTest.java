import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import io.jsonwebtoken.*;
import org.junit.Test;

public class JwtTest {
        //JSONObject json = new JSONObject();

        final String key="doan";
        String json ="{"
                + "\"storeId\":\"4887\""
                + ",\"userId\":\"1188\""
                + ",\"productId\":\"8936046155386\""
                + "}";
        @Test
        public void testJWT() throws Exception {

            Gson gson = new Gson(); // khởi tạo Gson
            Info tmp = gson.fromJson(json, Info.class); // parse Gson về object
            System.out.println("storeId:"+ tmp.info_StoreId);
            System.out.println("userId:"+ tmp.info_UserId);
            System.out.println("productId:"+ tmp.info_ProductId);


            String token = generateJwtToken(key,tmp);
            assertTrue(token != null);
            System.out.println(token);
            printStructure(token,key);
            printBody(token,key);
        }

        @SuppressWarnings("deprecation")
        private String generateJwtToken(String key,Info tmp) {
            JwtBuilder builder = Jwts.builder();
            builder.setPayload(json);
            builder.signWith(SignatureAlgorithm.HS256, key);
            String token = builder.compact();
            return token;
        }

        private void printStructure(String token,String key) throws Exception {
            validateRequest(token,key);
            Jws parseClaimsJws = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            System.out.println("Header     : " + parseClaimsJws.getHeader());
            System.out.println("Body       : " + parseClaimsJws.getBody());
            System.out.println("Signature  : " + parseClaimsJws.getSignature());
        }

        private void printBody(String token,String key) {
            Claims body = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
            System.out.println(body.toString());
        }

        public static void validateRequest(String token,String serectKey) throws Exception {
            if (token == null) {
                throw new Exception("Token is required");
            }
            if (!Jwts.parser().setSigningKey(serectKey).parseClaimsJws(token).getHeader().getAlgorithm().equals("HS256"))
                throw new Exception("Invalid Signature Algorithm");
            try {
                Jwts.parser().setSigningKey(serectKey).parseClaimsJws(token);
            } catch (MalformedJwtException ex) {
                throw new Exception("Token invalid");
            } catch (ExpiredJwtException ex) {
                throw new Exception("Expired JWT token");
            } catch (UnsupportedJwtException ex) {
                throw new Exception("Unsupported JWT token");
            } catch (IllegalArgumentException ex) {
                throw new Exception("JWT claims string is empty.");
            }
        }
        public class Info
        {
            @SerializedName("storeId") // SerializedName giống với Key của Json
            public int info_StoreId;
            @SerializedName("userId")
            public String info_UserId;
            @SerializedName("productId")
            public String info_ProductId;
        }
    }
