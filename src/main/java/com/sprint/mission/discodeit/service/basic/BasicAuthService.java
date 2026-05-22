package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final SessionRegistry sessionRegistry;

  public void expireUserSessions(UUID userId) {
    sessionRegistry.getAllPrincipals().stream()
        .filter(DiscodeitUserDetails.class::isInstance)
        .map(DiscodeitUserDetails.class::cast)
        .filter(principal -> principal.getUserDto().id().equals(userId))
        .forEach(principal ->
            sessionRegistry.getAllSessions(principal, false)
                .forEach(SessionInformation::expireNow)
        );
  }
}