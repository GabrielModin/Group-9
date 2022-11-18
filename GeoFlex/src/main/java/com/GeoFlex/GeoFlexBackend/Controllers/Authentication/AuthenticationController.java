package com.GeoFlex.GeoFlexBackend.Controllers.Authentication;

import com.GeoFlex.GeoFlexBackend.Controllers.Admin.AdminCompanion;
import com.GeoFlex.GeoFlexBackend.DatabaseAccess.AuthenticationProcedures;
import com.GeoFlex.GeoFlexBackend.Model.Token;
import com.GeoFlex.GeoFlexBackend.Model.TokenType;
import com.GeoFlex.GeoFlexBackend.PoJo.Authentication.Login;
import com.GeoFlex.GeoFlexBackend.PoJo.Authentication.Register;
import com.google.gson.Gson;
import org.checkerframework.checker.units.qual.C;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@CrossOrigin
@RestController
@RequestMapping(value = "/authenticator")
public class AuthenticationController {
    public static Authenticator authenticator = new Authenticator();


    @RequestMapping(method = RequestMethod.POST, value = "login")
    public ResponseEntity<String> login(HttpServletResponse response, @RequestBody String body) {
        System.out.println("body : " + body);
        Gson gson = new Gson();
        Login login = gson.fromJson(body, Login.class);
        AuthenticationProcedures ap = new AuthenticationProcedures();

        String id = ap.getUserId(login.userName);
        String passwordHash = Authenticator.getHash(login.password, ap.getSalt(id));

        if (ap.login(id,passwordHash)) {
            Token token;
            if (login.expiery != null) {
                if (login.expiery.equals("WEEK")) {
                    token = new Token(TokenType.WEEK);
                } else if (login.expiery.equals("MONTH")) {
                    token = new Token(TokenType.MONTH);
                } else {
                    token = new Token(TokenType.DAY);
                }
            } else {
                token = new Token(TokenType.DAY);
            }

            authenticator.putToken(id, token);

            Cookie userId = new Cookie("user-id", id);
            Cookie tokenString = new Cookie("authentication-token", token.getToken());
            userId.setPath("/");
            tokenString.setPath("/");

            response.addCookie(userId);

            response.addCookie(tokenString);

            return new ResponseEntity<>("{\"OK\" : \"Sucessfully authenticated\"}", HttpStatus.OK);
        }
        return new ResponseEntity<>("{\"OK\" : \"Authentication unsucessfull, please retry\"}", HttpStatus.FORBIDDEN);
    }
    @RequestMapping(method = RequestMethod.POST, value = "register")
    public ResponseEntity<String> register(HttpServletResponse response,@RequestBody String body) {
        AuthenticationCompanion authenticationCompanion = new AuthenticationCompanion();
        Gson gson = new Gson();
        Register register = gson.fromJson(body, Register.class);
        String id = authenticationCompanion.register(register);
        if (id == null) {
            return new ResponseEntity<>("{\"error\" : \"could not register user, must have unique username AND email\"}", HttpStatus.BAD_REQUEST);
        } else {
            Cookie userId = new Cookie("user-id",id);
            Token token = new Token();
            authenticator.putToken(id,token);
            Cookie tokenString = new Cookie("authentication-token", token.getToken());

            userId.setPath("/");
            tokenString.setPath("/");

            response.addCookie(userId);
            response.addCookie(tokenString);
        }

        return new ResponseEntity<>("{\"success\" : \"registred user : "+register.userName+"\"}", HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PATCH, value = "user")
    public ResponseEntity<String> updateUser(HttpServletResponse response,@RequestBody String body) {
        AuthenticationCompanion authenticationCompanion = new AuthenticationCompanion();
        Gson gson = new Gson();
        Register register = gson.fromJson(body, Register.class);
        String id = authenticationCompanion.register(register);
        if (id == null) {
            return new ResponseEntity<>("{\"error\" : \"could not register user, must have unique username AND email\"}", HttpStatus.BAD_REQUEST);
        } else {
            Cookie userId = new Cookie("user-id",id);
            Token token = new Token();
            authenticator.putToken(id,token);
            Cookie tokenString = new Cookie("authentication-token", token.getToken());

            userId.setPath("/");
            tokenString.setPath("/");

            response.addCookie(userId);
            response.addCookie(tokenString);
        }

        return new ResponseEntity<>("{\"success\" : \"registred user : "+register.userName+"\"}", HttpStatus.OK);
    }
}
