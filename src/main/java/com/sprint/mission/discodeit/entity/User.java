package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdateEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User extends BaseUpdateEntity {

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;
    //
    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "profile_id", columnDefinition="uuid")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private BinaryContent profile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserStatus status;

    public User(String username, String email, String password, BinaryContent profile) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
    }

    public void update(String newUsername, String newEmail, String newPassword, BinaryContent newProfile) {
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
        }
        if (newProfile != null && !newProfile.equals(this.profile)) {
            this.profile = newProfile;
        }
    }

    // UserStatusd와 User의 연관관계 편의 메서드
    public void setStatus(UserStatus status) {
        if (this.status != null) {
            this.status.setUser(null);
        }

        this.status = status;

        if (status != null && status.getUser() != this) {
            status.setUser(this);
        }
    }
}
