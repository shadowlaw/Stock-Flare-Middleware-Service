package com.shadow.stock_flare_middleware_service.config;

import com.shadow.stock_flare_middleware_service.JSEMiddlewareService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.Base64Utils;

import static com.shadow.stock_flare_middleware_service.constants.TestConstants.MANAGEMENT_ENDPOINT;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = JSEMiddlewareService.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    @Test
    void givenRequestToActuatorEndpoint_whenNoAuthCredentialsAreProvided_ThenReturnUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(MANAGEMENT_ENDPOINT+"/health"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    void givenRequestToActuatorEndpoint_whenAuthCredentialsAreProvided_ThenReturnUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(MANAGEMENT_ENDPOINT+"/health")
                        .header(HttpHeaders.AUTHORIZATION, "Basic "+ Base64Utils.encodeToString(String.format("%s:%s", username, password).getBytes())))
                .andExpect(status().isOk())
                .andDo(print());
    }

}