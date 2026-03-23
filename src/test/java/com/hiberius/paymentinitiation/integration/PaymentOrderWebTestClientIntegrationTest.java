package com.hiberius.paymentinitiation.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * REST integration tests using {@link WebTestClient} bound to {@link MockMvc} (in-memory servlet stack).
 * This is the Spring Framework 6+ approach for MVC apps; no real HTTP port is opened.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PaymentOrderWebTestClientIntegrationTest {

    private static final String BASE = "/payment-initiation/payment-orders";

    @Autowired
    private MockMvc mockMvc;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = MockMvcWebTestClient.bindTo(mockMvc).build();
    }

    @Test
    void postCreatePaymentOrderReturns201WithContractBodyAndLocation() {
        String body = """
                {
                  "externalReference": "EXT-WTC-1",
                  "debtorAccount": { "iban": "EC12DEBTOR" },
                  "creditorAccount": { "iban": "EC98CREDITOR" },
                  "instructedAmount": { "amount": 100.50, "currency": "USD" },
                  "requestedExecutionDate": "2026-12-31"
                }
                """;

        EntityExchangeResult<byte[]> result =
                webTestClient
                        .post()
                        .uri(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(body)
                        .exchange()
                        .expectStatus()
                        .isCreated()
                        .expectHeader()
                        .exists("Location")
                        .expectHeader()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                        .expectBody()
                        .jsonPath("$.paymentOrderId")
                        .exists()
                        .jsonPath("$.status")
                        .isEqualTo("RECEIVED")
                        .jsonPath("$.externalReference")
                        .isEqualTo("EXT-WTC-1")
                        .jsonPath("$.createdAt")
                        .exists()
                        .returnResult();

        String location = result.getResponseHeaders().getFirst("Location");
        assertThat(location).isNotNull();
        String id = JsonPath.read(new String(result.getResponseBody()), "$.paymentOrderId");
        assertThat(location).endsWith("/" + id);
    }

    @Test
    void getPaymentOrderByIdReturns200WithSnapshot() {
        String id = createOrderAndReturnId("EXT-GET");

        webTestClient
                .get()
                .uri(BASE + "/{id}", id)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.paymentOrderId")
                .isEqualTo(id)
                .jsonPath("$.externalReference")
                .isEqualTo("EXT-GET")
                .jsonPath("$.debtorAccount.iban")
                .isEqualTo("EC12DEBTOR")
                .jsonPath("$.creditorAccount.iban")
                .isEqualTo("EC98CREDITOR")
                .jsonPath("$.instructedAmount.amount")
                .isEqualTo(99.99)
                .jsonPath("$.instructedAmount.currency")
                .isEqualTo("USD")
                .jsonPath("$.remittanceInformation")
                .isEqualTo("Ref GET")
                .jsonPath("$.requestedExecutionDate")
                .isEqualTo("2026-12-31")
                .jsonPath("$.status")
                .isEqualTo("RECEIVED")
                .jsonPath("$.createdAt")
                .exists()
                .jsonPath("$.statusChangedAt")
                .exists();
    }

    @Test
    void getPaymentOrderStatusReturns200AndAlignsWithFullOrder() {
        String id = createMinimalOrder("EXT-STATUS");

        String full =
                webTestClient
                        .get()
                        .uri(BASE + "/{id}", id)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(String.class)
                        .returnResult()
                        .getResponseBody();

        String statusFromOrder = JsonPath.read(full, "$.status");
        String statusChangedAt = JsonPath.read(full, "$.statusChangedAt");

        webTestClient
                .get()
                .uri(BASE + "/{id}/status", id)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.paymentOrderId")
                .isEqualTo(id)
                .jsonPath("$.status")
                .isEqualTo(statusFromOrder)
                .jsonPath("$.lastUpdated")
                .isEqualTo(statusChangedAt);
    }

    @Test
    void getUnknownPaymentOrderReturns404ProblemJson() {
        webTestClient
                .get()
                .uri(BASE + "/{id}", "PO-NOT-THERE")
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectHeader()
                .contentTypeCompatibleWith("application/problem+json")
                .expectBody()
                .jsonPath("$.type")
                .isEqualTo("https://api.bank/errors/not-found")
                .jsonPath("$.title")
                .isEqualTo("Payment order not found")
                .jsonPath("$.status")
                .isEqualTo(404)
                .jsonPath("$.detail")
                .exists()
                .jsonPath("$.instance")
                .isEqualTo("/payment-initiation/payment-orders/PO-NOT-THERE");
    }

    @Test
    void invalidRequestReturns400ProblemJson() {
        webTestClient
                .post()
                .uri(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{ not json")
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectHeader()
                .contentTypeCompatibleWith("application/problem+json")
                .expectBody()
                .jsonPath("$.status")
                .isEqualTo(400)
                .jsonPath("$.code")
                .isEqualTo("VALIDATION_ERROR");
    }

    @Test
    void postSameDebtorAndCreditorReturns422ProblemJson() {
        String body = """
                {
                  "externalReference": "EXT-BR",
                  "debtorAccount": { "iban": "ACC1" },
                  "creditorAccount": { "iban": "acc1" },
                  "instructedAmount": { "amount": 10, "currency": "USD" },
                  "requestedExecutionDate": "2026-12-31"
                }
                """;
        webTestClient
                .post()
                .uri(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus()
                .isEqualTo(422)
                .expectBody()
                .jsonPath("$.status")
                .isEqualTo(422)
                .jsonPath("$.code")
                .isEqualTo("BUSINESS_RULE_VIOLATION");
    }

    @Test
    void postLowercaseCurrencyRejectedWith400BeforeMapper() {
        String body = """
                {
                  "externalReference": "EXT-X",
                  "debtorAccount": { "iban": "EC1" },
                  "creditorAccount": { "iban": "EC2" },
                  "instructedAmount": { "amount": 10, "currency": "usd" },
                  "requestedExecutionDate": "2026-12-31"
                }
                """;
        webTestClient
                .post()
                .uri(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.status")
                .isEqualTo(400);
    }

    private String createOrderAndReturnId(String externalReference) {
        String body =
                """
                {
                  "externalReference": "%s",
                  "debtorAccount": { "iban": "EC12DEBTOR" },
                  "creditorAccount": { "iban": "EC98CREDITOR" },
                  "instructedAmount": { "amount": 99.99, "currency": "USD" },
                  "remittanceInformation": "Ref GET",
                  "requestedExecutionDate": "2026-12-31"
                }
                """
                        .formatted(externalReference);

        String json =
                webTestClient
                        .post()
                        .uri(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(body)
                        .exchange()
                        .expectStatus()
                        .isCreated()
                        .expectBody(String.class)
                        .returnResult()
                        .getResponseBody();

        return JsonPath.read(json, "$.paymentOrderId");
    }

    private String createMinimalOrder(String externalReference) {
        String body =
                """
                {
                  "externalReference": "%s",
                  "debtorAccount": { "iban": "EC12DEBTOR" },
                  "creditorAccount": { "iban": "EC98CREDITOR" },
                  "instructedAmount": { "amount": 10, "currency": "USD" },
                  "requestedExecutionDate": "2026-12-31"
                }
                """
                        .formatted(externalReference);

        String json =
                webTestClient
                        .post()
                        .uri(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(body)
                        .exchange()
                        .expectStatus()
                        .isCreated()
                        .expectBody(String.class)
                        .returnResult()
                        .getResponseBody();

        return JsonPath.read(json, "$.paymentOrderId");
    }
}
