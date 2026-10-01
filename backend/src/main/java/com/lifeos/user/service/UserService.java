package com.lifeos.user.service;

import com.lifeos.user.dto.UserProfileSummaryResponse;

public interface UserService {

    UserProfileSummaryResponse getProfileSummary(Long userId);

    void deleteCurrentUser(Long userId);
}
