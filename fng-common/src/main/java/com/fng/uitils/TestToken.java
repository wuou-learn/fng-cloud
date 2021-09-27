package com.fng.uitils;

/**
 * @Description
 * @Author wuou
 * @Date 2021/9/6 上午10:06
 * @Version 1.0.0
 */
public class TestToken {
    public static void main(String[] args) {
//        String wuou = JwtUtil.createToken("wuou");
//        System.out.println(wuou);
        String s = JwtUtil.verifyToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJmbmctdG9rZW4iLCJhdWQiOiJmbmctYXBwIiwiaXNzIjoiZm5nIiwiZXhwIjoxNjMwODk0MTIzLCJ1c2VySWQiOiJ3dW91IiwiaWF0IjoxNjMwODk0MTAzfQ.RTQBNEX09_UQ0v086YFwEVNvRptemmRdS6ycKJVZJqY");
        System.out.println(s);
    }
}
