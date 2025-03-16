package com.medals.medalsbackend.service.websockets;

import com.medals.medalsbackend.entity.performancerecording.DisciplineRatingMetric;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingMetricWebsocketMessageService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendRatingMetricCreation(DisciplineRatingMetric disciplineRatingMetric) {
        messagingTemplate.convertAndSend("/topics/rating-metric/creation", disciplineRatingMetric);
    }

    public void sendRatingMetricUpdate(DisciplineRatingMetric updated) {
        messagingTemplate.convertAndSend("/topics/rating-metric/update", updated);
    }

    public void sendRatingMetricDeletion(Long ratingMetricId) {
        messagingTemplate.convertAndSend("/topics/rating-metric/deletion", ratingMetricId);
    }

}
