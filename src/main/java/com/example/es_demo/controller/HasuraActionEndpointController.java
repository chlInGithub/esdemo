package com.example.es_demo.controller;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hasuraActionAPI")
public class HasuraActionEndpointController {
    @Data
    public static class UserVO{
        String name;
        Integer age;
        String idCardNo;
    }

    @Data
    public static class APIResult{
        String name;
        Integer age;
        String idCardNo;
    }

    @RequestMapping("checkUser")
    public APIResult searchPerson(UserVO userVO){
        APIResult apiResult = new APIResult();
        BeanUtils.copyProperties(userVO, apiResult);
        return apiResult;
    }

    @Data
    public static class LoginVO{
        String name;
        String pass;
    }

    @Data
    public static class LoginJwtVO{
        String jwt;
    }

    @PostMapping("login")
    public LoginJwtVO login(LoginVO loginVO){
        String jwt = jwt();
        LoginJwtVO loginJwtVO = new LoginJwtVO();
        loginJwtVO.setJwt(jwt);
        return loginJwtVO;
    }

    static final String jwtSecretString = "S22zAIgXiQmMoywh/jGzm3Tx4o/M74UVarnFKbqNSBc=";
    static String jwt() {
        Map<String, Object> hasuraClaim = new HashMap<>();
        hasuraClaim.put("x-hasura-allowed-roles", new String[]{"user", "editor"});
        hasuraClaim.put("x-hasura-default-role", "anonymous");
        hasuraClaim.put("x-hasura-user-id", "80");
        SecretKey key = Keys.hmacShaKeyFor(jwtSecretString.getBytes(StandardCharsets.UTF_8));
        String compact = Jwts.builder().claim("https://hasura.io/jwt/claims", hasuraClaim).signWith(key, SignatureAlgorithm.HS256)
                .compact();
        Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(compact);
        return compact;
    }

    static void deJwt(String jwt){
        SecretKey key = Keys.hmacShaKeyFor(jwtSecretString.getBytes(StandardCharsets.UTF_8));
        Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt);
        System.out.println();
    }

    public static void main(String[] args) {
        String jwt = jwt();
        deJwt(jwt);
        System.out.println(jwt);
    }

}
