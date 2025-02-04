package com.medals.medalsbackend.controller.authorization;

import com.medals.medalsbackend.dto.authorization.SetPasswordDto;
import com.medals.medalsbackend.dto.authorization.UserLoginDto;
import com.medals.medalsbackend.entity.users.LoginEntry;
import com.medals.medalsbackend.entity.users.UserEntity;
import com.medals.medalsbackend.exceptions.GenericAPIRequestException;
import com.medals.medalsbackend.exceptions.oneTimeCode.OneTimeCodeExpiredException;
import com.medals.medalsbackend.exceptions.oneTimeCode.OneTimeCodeNotFoundException;
import com.medals.medalsbackend.security.jwt.JwtTokenInvalidException;
import com.medals.medalsbackend.security.jwt.JwtUtils;
import com.medals.medalsbackend.service.user.UserEntityService;
import com.medals.medalsbackend.service.user.login.EmailDoesntExistException;
import com.medals.medalsbackend.service.user.login.LoginDoesntMatchException;
import com.medals.medalsbackend.service.user.login.LoginEntryService;
import com.medals.medalsbackend.service.user.login.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import static com.medals.medalsbackend.controller.BaseController.BASE_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(BASE_PATH + "/authorization")
public class AuthorizationController {

    private final LoginEntryService loginEntryService;
    private final JwtService jwtService;
    private final JwtUtils jwtUtils;
    private final UserEntityService userEntityService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDto userLoginDto) throws GenericAPIRequestException {
        try {
            String jwtRefreshToken = loginEntryService.buildJwtRefreshToken(userLoginDto.getEmail(), userLoginDto.getPassword());
            ResponseCookie responseCookie = ResponseCookie.from("refreshToken", jwtRefreshToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(jwtUtils.getRefreshTokenValidityDuration() / 1000)
                    .build();

            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .body("Success");
        } catch (EmailDoesntExistException | LoginDoesntMatchException e) {
            throw GenericAPIRequestException.builder()
                    .message("No User with the supplied email address/password found")
                    .statusCode(HttpStatus.NOT_FOUND)
                    .build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteCookie.toString()).body("Success");
    }

    @GetMapping("/token")
    public ResponseEntity<String> getToken(@CookieValue(name = "refreshToken") String refreshToken) throws JwtTokenInvalidException, EmailDoesntExistException {
        String userEmail = jwtService.getUserEmailFromRefreshToken(refreshToken);
        LoginEntry loginEntry = loginEntryService.getByEmail(userEmail).orElseThrow(() -> new EmailDoesntExistException(userEmail));
        String identityToken = jwtService.buildIdentityToken(loginEntry);
        return ResponseEntity.ok(identityToken);
    }

    @SneakyThrows
    @PostMapping("/setPassword")
    public ResponseEntity<String> setPassword(@RequestBody SetPasswordDto setPasswordDto) throws OneTimeCodeExpiredException, OneTimeCodeNotFoundException {
        loginEntryService.setPassword(setPasswordDto.getOneTimeCode(), setPasswordDto.getPassword());
        return ResponseEntity.ok("Success");
    }
}
