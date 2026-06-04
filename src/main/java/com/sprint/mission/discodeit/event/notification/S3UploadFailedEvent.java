package com.sprint.mission.discodeit.event.notification;

import java.util.UUID;

public record S3UploadFailedEvent(
    String mdcRequestId,
    UUID binaryContentId,
    String errorMessage
) {
}