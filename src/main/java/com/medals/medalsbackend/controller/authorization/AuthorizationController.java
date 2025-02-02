package com.medals.medalsbackend.controller.authorization;

import com.medals.medalsbackend.dto.authorization.UserLoginDto;
import com.medals.medalsbackend.entity.UserEntity;
import com.medals.medalsbackend.exceptions.GenericAPIRequestException;
import com.medals.medalsbackend.security.jwt.JwtUtils;
import com.medals.medalsbackend.service.user.UserEntityService;
import com.medals.medalsbackend.service.user.login.EmailDoesntExistException;
import com.medals.medalsbackend.service.user.login.JwtService;
import com.medals.medalsbackend.service.user.login.LoginDoesntMatchException;
import com.medals.medalsbackend.service.user.login.LoginEntryService;
import lombok.RequiredArgsConstructor;
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
                    .domain("localhost")
                    .maxAge(jwtUtils.getRefreshTokenValidityDuration() / 1000)
                    .build();

            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, responseCookie.getValue())
                    .body("Success");
        } catch (EmailDoesntExistException | LoginDoesntMatchException e) {
            throw GenericAPIRequestException.builder()
                    .message("No User with the supplied email address/password found")
                    .statusCode(HttpStatus.NOT_FOUND)
                    .build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserEntity(@PathVariable Long id) throws GenericAPIRequestException {
        return ResponseEntity.ok().body(userEntityService.findById(id).orElseThrow(() -> new UsernameNotFoundException(id.toString())));
    }
}
