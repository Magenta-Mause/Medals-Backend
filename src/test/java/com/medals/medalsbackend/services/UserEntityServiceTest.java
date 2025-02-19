package com.medals.medalsbackend.services;

import com.medals.medalsbackend.entity.users.Trainer;
import com.medals.medalsbackend.service.user.UserEntityService;
import com.medals.medalsbackend.service.user.login.LoginEntryService;
import com.medals.medalsbackend.service.onetimecode.OneTimeCodeCreationReason;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserEntityServiceTest {

    @Mock
    private LoginEntryService loginEntryService;
    @InjectMocks
    private UserEntityService userEntityService;


    @SneakyThrows
    @Test
    public void testUserEntityCreationCreatesLoginEntry() {
        userEntityService.save("test@gmail.com", Trainer.builder().email("test@gmail.com").firstName("tom").lastName("tailor").build());

        verify(loginEntryService, times(1)).createLoginEntry(eq("test@gmail.com"), eq(OneTimeCodeCreationReason.ACCOUNT_CREATED));
        verify(loginEntryService, times(1)).addUserToLogin(eq("test@gmail.com"), any());
    }

}
