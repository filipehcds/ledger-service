package io.pismo.ledger.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pismo.ledger.domain.entity.Account;
import io.pismo.ledger.dto.request.CreateTransactionRequest;
import io.pismo.ledger.repository.AccountRepository;
import io.pismo.ledger.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        testAccount = accountRepository.save(new Account("12345678900"));
    }

    @ParameterizedTest
    @CsvSource({
            "1, 50.0",   // PURCHASE
            "2, 23.5",   // INSTALLMENT PURCHASE
            "3, 18.7",   // WITHDRAWAL
            "4, 60.0"    // PAYMENT
    })
    @DisplayName("POST /transactions - Should create transaction and return positive amount in response")
    void shouldCreateTransactionAndReturnPositiveAmount(int operationTypeId, String inputAmount) throws Exception {
        var request = new CreateTransactionRequest(
                testAccount.getAccountId(),
                operationTypeId,
                new BigDecimal(inputAmount)
        );

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transaction_id", notNullValue()))
                .andExpect(jsonPath("$.account_id", is(testAccount.getAccountId().intValue())))
                .andExpect(jsonPath("$.operation_type_id", is(operationTypeId)))
                .andExpect(jsonPath("$.amount", is(Double.parseDouble(inputAmount))));
    }

    @Test
    @DisplayName("POST /transactions - Should return 404 when account not found")
    void shouldReturn404WhenAccountNotFound() throws Exception {
        var request = new CreateTransactionRequest(999L, 1, new BigDecimal("50.0"));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Account not found with id: 999")));
    }

    @Test
    @DisplayName("POST /transactions - Should return 404 when operation type not found")
    void shouldReturn404WhenOperationTypeNotFound() throws Exception {
        var request = new CreateTransactionRequest(testAccount.getAccountId(), 99, new BigDecimal("50.0"));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Operation type not found with id: 99")));
    }

    @Test
    @DisplayName("POST /transactions - Should return 400 when amount is negative")
    void shouldReturn400WhenAmountIsNegative() throws Exception {
        var request = new CreateTransactionRequest(testAccount.getAccountId(), 1, new BigDecimal("-50.0"));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.fieldErrors[0].field", is("amount")));
    }

    @Test
    @DisplayName("POST /transactions - Should return 400 when required fields are missing")
    void shouldReturn400WhenRequiredFieldsAreMissing() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }
}
