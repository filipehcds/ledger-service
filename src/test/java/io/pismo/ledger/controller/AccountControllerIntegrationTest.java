package io.pismo.ledger.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pismo.ledger.dto.request.CreateAccountRequest;
import io.pismo.ledger.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /accounts - Should create account successfully")
    void shouldCreateAccountSuccessfully() throws Exception {
        var request = new CreateAccountRequest("12345678900");

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.account_id", notNullValue()))
                .andExpect(jsonPath("$.document_number", is("12345678900")));
    }

    @Test
    @DisplayName("POST /accounts - Should return 400 when document number is blank")
    void shouldReturn400WhenDocumentNumberIsBlank() throws Exception {
        var request = new CreateAccountRequest("");

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.fieldErrors[0].field", is("documentNumber")));
    }

    @Test
    @DisplayName("POST /accounts - Should return 409 when document number already exists")
    void shouldReturn409WhenDocumentNumberAlreadyExists() throws Exception {
        var request = new CreateAccountRequest("12345678900");

        // Create first account
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Try to create second account with same document number
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.message", is("Account already exists with document number: 12345678900")));
    }

    @Test
    @DisplayName("GET /accounts/{accountId} - Should return account successfully")
    void shouldReturnAccountSuccessfully() throws Exception {
        var request = new CreateAccountRequest("12345678900");

        // Create account first
        var result = mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        var responseJson = result.getResponse().getContentAsString();
        var accountId = objectMapper.readTree(responseJson).get("account_id").asLong();

        // Get account
        mockMvc.perform(get("/accounts/{accountId}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account_id", is((int) accountId)))
                .andExpect(jsonPath("$.document_number", is("12345678900")));
    }

    @Test
    @DisplayName("GET /accounts/{accountId} - Should return 404 when account not found")
    void shouldReturn404WhenAccountNotFound() throws Exception {
        mockMvc.perform(get("/accounts/{accountId}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Account not found with id: 999")));
    }
}
