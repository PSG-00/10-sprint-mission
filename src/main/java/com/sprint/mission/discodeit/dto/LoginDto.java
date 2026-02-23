package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class LoginDto {
    @Schema(name = "LoginRequest", description = "로그인 정보")
    public record LoginRequest(
            @NotBlank(message = "사용자명은 필수입니다.")
            String username,
            @NotBlank(message = "비밀번호는 필수입니다.")
            String password
    ) {}
}
