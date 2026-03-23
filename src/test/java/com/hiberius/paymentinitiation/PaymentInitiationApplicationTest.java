package com.hiberius.paymentinitiation;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
class PaymentInitiationApplicationTest {

    @Test
    void contextLoads() {
        // smoke test for mvn verify / JaCoCo baseline
    }

    @Test
    void mainDelegatesToSpringApplication() {
        try (MockedStatic<SpringApplication> spring = mockStatic(SpringApplication.class)) {
            PaymentInitiationApplication.main(new String[]{});
            spring.verify(() -> SpringApplication.run(eq(PaymentInitiationApplication.class), any(String[].class)));
        }
    }
}
