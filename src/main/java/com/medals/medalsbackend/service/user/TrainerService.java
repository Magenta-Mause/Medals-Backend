package com.medals.medalsbackend.service.user;

import com.medals.medalsbackend.DummyData;
import com.medals.medalsbackend.entity.users.Trainer;
import com.medals.medalsbackend.exceptions.InternalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerService {

    private final UserEntityService userEntityService;
    private final Environment environment;
    @Value("${app.dummies.enable}")
    private boolean insertDummies;

    @EventListener(ApplicationReadyEvent.class)
    public void instantiateDummies() {
        if (!insertDummies || Arrays.stream(environment.getActiveProfiles()).toList().contains("test")) {
            return;
        }

        log.info("Inserting {} dummy trainers", DummyData.TRAINERS.size());
        DummyData.TRAINERS.forEach(trainer -> {
            try {
                createTrainer(trainer);
            } catch (InternalException internalException) {
                log.error(internalException.getMessage(), internalException);
            }
        });
    }

    public Trainer createTrainer(Trainer trainer) throws InternalException {
        userEntityService.save(trainer.getEmail(), trainer);
        return trainer;
    }

    public List<Trainer> getAllTrainers() {
        return userEntityService.getAllTrainers();
    }
}
