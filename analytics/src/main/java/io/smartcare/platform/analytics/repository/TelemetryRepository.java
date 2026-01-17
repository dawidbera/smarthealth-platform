package io.smartcare.platform.analytics.repository;

import io.smartcare.platform.analytics.domain.TelemetryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TelemetryRepository extends JpaRepository<TelemetryRecord, Long> {
    List<TelemetryRecord> findBySerialNumber(String serialNumber);
}
