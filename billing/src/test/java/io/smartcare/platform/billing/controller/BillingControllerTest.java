package io.smartcare.platform.billing.controller;

import io.smartcare.platform.billing.domain.Invoice;
import io.smartcare.platform.billing.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BillingController.class)
class BillingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvoiceRepository invoiceRepository;

    @Test
    void getAllInvoices_ShouldReturnList() throws Exception {
        Invoice invoice = Invoice.builder()
                .id(1L)
                .appointmentId(101L)
                .amount(new BigDecimal("150.00"))
                .status("PENDING")
                .build();
        
        when(invoiceRepository.findAll()).thenReturn(List.of(invoice));

        mockMvc.perform(get("/billing/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].appointmentId").value(101))
                .andExpect(jsonPath("$[0].amount").value(150.00));
    }
}
