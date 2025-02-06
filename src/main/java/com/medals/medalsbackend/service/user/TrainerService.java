package com.medals.medalsbackend.service.user;

import com.medals.medalsbackend.DummyData;
import com.medals.medalsbackend.entity.users.Trainer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerService {

    private final UserEntityService userEntityService;

    @EventListener(ApplicationReadyEvent.class)
    public void instantiateDummies() {
        DummyData.TRAINERS.forEach(this::createTrainer);
    }

    public Trainer createTrainer(Trainer trainer) {
        userEntityService.save(trainer.getEmail(), trainer);
        return trainer;
    }

    public List<Trainer> getAllTrainers() {
        return userEntityService.getAllTrainers();
    }
}
