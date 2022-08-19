package com.shadow.jse_notification_service.monitoring.info;

import com.shadow.jse_notification_service.JSENotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.Base64Utils;

import static com.shadow.jse_notification_service.constants.TestConstants.MANAGEMENT_ENDPOINT;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = JSENotificationService.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ApplicationInfoContributorTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    @Test
    public void givenRequestForAppInfo_whenSuccessfulRequest_thenValidateJsonFields() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get(MANAGEMENT_ENDPOINT+"/info")
                .header(HttpHeaders.AUTHORIZATION, "Basic "+ Base64Utils.encodeToString(String.format("%s:%s", username, password).getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.app-name").hasJsonPath())
                .andExpect(jsonPath("$.app-version").hasJsonPath())
                .andExpect(jsonPath("$.app-host").hasJsonPath())
                .andExpect(jsonPath("$.app-port").hasJsonPath())
                .andDo(print());
    }

}