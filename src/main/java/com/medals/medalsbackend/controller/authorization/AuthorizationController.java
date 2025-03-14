package com.medals.medalsbackend.controller.authorization;

import com.medals.medalsbackend.dto.authorization.ResetPasswordDto;
import com.medals.medalsbackend.dto.authorization.SetPasswordDto;
import com.medals.medalsbackend.dto.authorization.LoginUserDto;
import com.medals.medalsbackend.entity.users.LoginEntry;
import com.medals.medalsbackend.exception.GenericAPIRequestException;
import com.medals.medalsbackend.exception.JwtTokenInvalidException;
import com.medals.medalsbackend.exception.onetimecode.OneTimeCodeExpiredException;
import com.medals.medalsbackend.exception.onetimecode.OneTimeCodeNotFoundException;
import com.medals.medalsbackend.security.jwt.JwtTokenBody;
import com.medals.medalsbackend.security.jwt.JwtUtils;
import com.medals.medalsbackend.service.user.login.EmailDoesntExistException;
import com.medals.medalsbackend.service.user.login.LoginDoesntMatchException;
import com.medals.medalsbackend.service.user.login.LoginEntryService;
import com.medals.medalsbackend.service.user.login.jwt.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.medals.medalsbackend.controller.BaseController.BASE_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(BASE_PATH + "/authorization")
public class AuthorizationController {

    private final LoginEntryService loginEntryService;
    private final JwtService jwtService;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginUserDto loginUserDto) throws GenericAPIRequestException {
        try {
            String jwtRefreshToken = loginEntryService.buildJwtRefreshToken(loginUserDto.getEmail(), loginUserDto.getPassword());
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
        String userEmail = (String) jwtUtils.getTokenContentBody(refreshToken, JwtTokenBody.TokenType.REFRESH_TOKEN).get("sub");
        LoginEntry loginEntry = loginEntryService.getLoginEntry(userEmail);
        String identityToken = jwtService.buildIdentityToken(loginEntry);
        return ResponseEntity.ok(identityToken);
    }

    @PostMapping("/setPassword")
    public ResponseEntity<String> setPassword(@Valid @RequestBody SetPasswordDto setPasswordDto) throws OneTimeCodeExpiredException, OneTimeCodeNotFoundException {
        loginEntryService.setPassword(setPasswordDto.getOneTimeCode(), setPasswordDto.getPassword());
        return ResponseEntity.ok("Success");
    }

    @PostMapping("/resetPassword/{email}")
    public ResponseEntity<String> startResetPasswordFlow(@PathVariable String email) throws EmailDoesntExistException {
        loginEntryService.initiateResetPasswordRequest(email);
        return ResponseEntity.ok("Success");
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto) throws OneTimeCodeExpiredException, OneTimeCodeNotFoundException {
        loginEntryService.resetPassword(resetPasswordDto.getPassword(), resetPasswordDto.getToken());
        return ResponseEntity.ok("Success");
    }
}
