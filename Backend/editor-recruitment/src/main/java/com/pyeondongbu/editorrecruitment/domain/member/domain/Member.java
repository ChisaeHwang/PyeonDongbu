package com.pyeondongbu.editorrecruitment.domain.member.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.pyeondongbu.editorrecruitment.domain.member.domain.details.MemberDetails;
import com.pyeondongbu.editorrecruitment.domain.member.domain.role.Role;
import com.pyeondongbu.editorrecruitment.global.exception.MemberException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static com.pyeondongbu.editorrecruitment.domain.member.domain.MemberState.ACTIVE;
import static com.pyeondongbu.editorrecruitment.global.exception.ErrorCode.*;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
// Auditing을 위한 리스너 추가, 상황에 따라 나중에 따로 관리 할 수 있으니 삭제 가능성 있음
public class Member {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Enumerated(value = STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, unique = true, length = 30)
    private String socialLoginId;

    @Column(nullable = false, unique = true, length = 20)
    private String nickname;

    @Column(nullable = false)
    private String imageUrl;

    @Enumerated(value = STRING)
    private MemberState status = ACTIVE; // 기본값 설정 예시

    @Column(nullable = false)
    private LocalDateTime lastLoginDate;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "details_id")
    private MemberDetails details;

    @Builder
    public Member(
            final Long id,
            final Role role,
            final String socialLoginId,
            final String nickname,
            final String imageUrl
    ) {
        validateSocialLoginId(socialLoginId);
        validateNickname(nickname);
        validateImageUrl(imageUrl);

        this.id = id;
        this.role = role;
        this.socialLoginId = socialLoginId;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.status = ACTIVE;
        this.lastLoginDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }


    public static Member of(
            final String socialLoginId,
            final String nickname,
            final String imageUrl,
            final Role role
    ) {
        return Member.builder()
                .socialLoginId(socialLoginId)
                .nickname(nickname)
                .imageUrl(imageUrl)
                .role(role)
                .build();
    }


    public void updateLastLoginDate() {
        this.lastLoginDate = LocalDateTime.now();
    }

    public boolean nickNameCheck(final String nickname) {
        return !this.nickname.equals(nickname);
    }

    public void setDetails(MemberDetails details) {
        this.details = details;
    }

    private void validateSocialLoginId(String socialLoginId) {
        if (socialLoginId == null || socialLoginId.trim().isEmpty() || socialLoginId.length() > 30) {
            throw new MemberException(INVALID_SOCIAL_LOGIN_ID);
        }
    }

    private void validateNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty() || nickname.length() > 20) {
            throw new MemberException(INVALID_NICK_NAME);
        }
    }

    private void validateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new MemberException(INVALID_IMAGE_URL);
        }
    }
}

