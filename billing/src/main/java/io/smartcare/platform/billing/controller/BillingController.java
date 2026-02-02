package io.smartcare.platform.billing.controller;

import io.smartcare.platform.billing.domain.Invoice;
import io.smartcare.platform.billing.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/billing")
@RequiredArgsConstructor
public class BillingController {

    private final InvoiceRepository invoiceRepository;

    /**
     * Retrieves all invoices from the system.
     * 
     * @return a list of all existing invoices
     */
    @GetMapping("/invoices")
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }
}
