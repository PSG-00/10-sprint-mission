package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidCredentialException extends UserException{

    public InvalidCredentialException() {
        super(ErrorCode.INVALID_CREDENTIALS);
    }
}
