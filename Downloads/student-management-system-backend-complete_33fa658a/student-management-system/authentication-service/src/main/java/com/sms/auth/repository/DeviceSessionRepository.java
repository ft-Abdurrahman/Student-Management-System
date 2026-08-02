package com.sms.auth.repository;

import com.sms.auth.entity.DeviceSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceSessionRepository extends JpaRepository<DeviceSession, Long> {
    Optional<DeviceSession> findByUserIdAndDeviceId(Long userId, String deviceId);
    List<DeviceSession> findByUserId(Long userId);
}
