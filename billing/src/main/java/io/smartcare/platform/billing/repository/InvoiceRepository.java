package io.smartcare.platform.billing.repository;

import io.smartcare.platform.billing.domain.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
