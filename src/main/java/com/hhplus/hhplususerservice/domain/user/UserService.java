package com.hhplus.hhplususerservice.domain.user;

import com.hhplus.hhplususerservice.domain.common.exception.CustomException;
import com.hhplus.hhplususerservice.domain.event.PaymentEvent;
import com.hhplus.hhplususerservice.domain.user.command.UserCommand;
import com.hhplus.hhplususerservice.support.aop.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static com.hhplus.hhplususerservice.domain.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher publisher;

    /**
     * 잔액 조회를 요청하면 유저의 잔액 정보를 반환한다.
     *
     * @param userId userId 정보
     * @return UserResponse 유저의 잔액 정보를 반환한다.
     */
    @Transactional(readOnly = true)
    public User getUser(Long userId) {
        Optional<User> user = userRepository.getUser(userId);

        if (user.isEmpty()) {
            throw new CustomException(USER_IS_NOT_FOUND, "유저가 존재하지 않습니다.");
        }
        return user.get();
    }

    /**
     * 잔액 충전을 요청하면 현재 잔액 정보를 반환한다.
     *
     * @param command userId, balance 정보
     * @return UserResponse 유저의 잔액 정보를 반환한다.
     */
    @Transactional
    @DistributedLock(key = "'userLock'.concat(':').concat(#command.userId())")
    public User chargeBalance(UserCommand.Create command) {
        User user = getUser(command.userId());

        user.chargeBalance(command.balance());
        Optional<User> chargedUser = userRepository.saveUser(user);

        if (chargedUser.isEmpty()) {
            throw new CustomException(USER_FAIL_TO_CHARGE,
                    "잔액 충전에 실패하였습니다");
        }

        return chargedUser.get();
    }

    @Transactional
    public void refund(Long userId, BigDecimal refundPoint) {
        User user = getUser(userId);
        user.chargeBalance(refundPoint);

        userRepository.saveUser(user);
    }

    @Transactional
    @DistributedLock(key = "'userLock'.concat(':').concat(#command.userId())")
    public void usePoint(UserCommand.Use command) {

        try {
            User user = getUser(Long.valueOf(command.userId()));
            user.useBalance(command.amount());
            userRepository.saveUser(user).orElseThrow(() -> new CustomException(USER_FAIL_TO_USE_POINT,
                    "유저가 포인트 사용에 실패하였습니다."));

            // 토큰 만료를 위한 이벤트 발행
            publisher.publishEvent(new PaymentEvent(this, command.reservationId(), command.userId(), command.paymentId(),
                    command.token(), command.amount(), PaymentEvent.EventConstants.DEDUCTION_COMPLETED));
        } catch (Exception e) {
            log.error(e.getMessage());
            publisher.publishEvent(new PaymentEvent(this, command.reservationId(), command.userId(), command.paymentId(),
                    command.token(), command.amount(), PaymentEvent.EventConstants.DEDUCTION_FAILED));
        }
    }
}
