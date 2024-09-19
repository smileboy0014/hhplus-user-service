package com.hhplus.hhplususerservice.infra.user;

import com.hhplus.hhplususerservice.domain.user.User;
import com.hhplus.hhplususerservice.infra.common.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users")
public class UserEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private BigDecimal balance;

    @CreatedDate
    private LocalDateTime createdAt;

    //    @Version
//    private int version; //낙관적 락 적용

    public static UserEntity from(User user) {
        return UserEntity.builder()
                .userId(user.getUserId() != null ? user.getUserId() : null)
                .balance(user.getBalance())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public User toDomain() {
        return User.builder()
                .userId(userId)
                .balance(balance)
                .createdAt(createdAt)
                .build();
    }
}
