package com.fng.uitils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fng.enums.StringEnum;
import com.fng.exception.AppException;

import java.util.*;

public class JwtUtil {

    /**
     * 用户ID生成token
     * @param userId
     * @return
     */
    public static String createToken(String userId) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(StringEnum.SECRET.name());
            Map<String, Object> m = new HashMap<>();
            m.put("alg", "HS256");
            m.put("typ", "JWT");
            //签名时间
            Date nowDate = new Date();

            //过期时间Date对象
            Date expire = getAfterDate(nowDate, 0, 0, 0, 0, 0, 60);
            String token = JWT.create().
                    //设置头部
                    withHeader(m)
                    // 设置 载荷 Payload
                    .withClaim("userId", userId)
                    .withIssuer(StringEnum.ISSUSER.name())
                    .withSubject(StringEnum.SUBJECT.name())
                    .withAudience(StringEnum.AUDIENCE.name())
                    //签名时间
                    .withIssuedAt(nowDate)
                    //过期时间
                    .withExpiresAt(expire)
                    .sign(algorithm);
            return token;
        }catch (JWTCreationException exception){
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * 验证token
     * @return
     */
    public static String verifyToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(StringEnum.SECRET.name());
            JWTVerifier build = JWT.require(algorithm)
                    .withIssuer(StringEnum.ISSUSER.name())
                    .build();
            DecodedJWT verify = build.verify(token);
            //获取声明信息
            Map<String, Claim> claims = verify.getClaims();
            Claim claim = claims.get("userId");
            //转为字符串
            return claim.asString();
        }catch (JWTCreationException e){
            throw new AppException("token过期");
        }
    }


    /**
     * 获取某个时间点的日期对象
     */
    public static Date getAfterDate(Date date, int year, int month, int day, int hour, int minute, int second) {
        if (date == null) {
            date = new Date();
        }

        Calendar cal = new GregorianCalendar();

        cal.setTime(date);
        if (year != 0) {
            cal.add(Calendar.YEAR, year);
        }
        if (month != 0) {
            cal.add(Calendar.MONTH, month);
        }
        if (day != 0) {
            cal.add(Calendar.DATE, day);
        }
        if (hour != 0) {
            cal.add(Calendar.HOUR_OF_DAY, hour);
        }
        if (minute != 0) {
            cal.add(Calendar.MINUTE, minute);
        }
        if (second != 0) {
            cal.add(Calendar.SECOND, second);
        }
        return cal.getTime();
    }

}
