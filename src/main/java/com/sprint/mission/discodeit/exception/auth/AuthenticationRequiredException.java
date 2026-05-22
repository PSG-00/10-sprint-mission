package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class AuthenticationRequiredException extends DiscodeitException {

  private AuthenticationRequiredException(String path, Throwable cause) {
    super(ErrorCode.AUTHENTICATION_REQUIRED, Map.of("path", path), cause);
  }

  public static AuthenticationRequiredException withPath(String path, Throwable cause) {
    return new AuthenticationRequiredException(path, cause);
  }
}