package com.lifeos.learning.service;

import com.lifeos.learning.dto.CreateLearningItemRequest;
import com.lifeos.learning.dto.CreateLearningSessionRequest;
import com.lifeos.learning.dto.LearningItemResponse;
import com.lifeos.learning.dto.LearningSessionResponse;
import com.lifeos.learning.dto.UpdateLearningItemRequest;
import com.lifeos.learning.dto.UpdateLearningProgressRequest;
import com.lifeos.learning.dto.UpdateLearningStatusRequest;
import com.lifeos.learning.entity.LearningStatus;

import java.util.List;

public interface LearningService {

    List<LearningItemResponse> getLearningItems(Long userId, LearningStatus status);

    LearningItemResponse getLearningItem(Long userId, Long id);

    LearningItemResponse createLearningItem(Long userId, CreateLearningItemRequest request);

    LearningItemResponse updateLearningItem(Long userId, Long id, UpdateLearningItemRequest request);

    LearningItemResponse updateLearningProgress(Long userId, Long id, UpdateLearningProgressRequest request);

    LearningItemResponse updateLearningStatus(Long userId, Long id, UpdateLearningStatusRequest request);

    void deleteLearningItem(Long userId, Long id);

    LearningSessionResponse createLearningSession(Long userId, Long learningItemId, CreateLearningSessionRequest request);

    List<LearningSessionResponse> getLearningSessions(Long userId, Long learningItemId);

    void deleteLearningSession(Long userId, Long learningItemId, Long sessionId);
}
