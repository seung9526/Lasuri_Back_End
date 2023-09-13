package com.suutich.systems;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suutich.systems.controller.UserController;
import com.suutich.systems.model.User;
import com.suutich.systems.payload.request.AddUserRequest;
import com.suutich.systems.payload.request.LoginRequest;
import com.suutich.systems.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser // 인증된 사용자로 테스트 설정
    public void testRegister() throws Exception {
        // Given
        AddUserRequest addUserRequest = new AddUserRequest("test@test.com","test","test1", "");

        // When
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addUserRequest)))
                .andExpect(status().isCreated());

        // Then
        verify(userService).register(addUserRequest);
    }

    @Test
    public void testLogin() throws Exception {
        // Given
        String email = "test@test.com";
        String password = "test";

        // When
        MvcResult mvcResult = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(email, password))))
                .andReturn();

        // Then
        int status = mvcResult.getResponse().getStatus();
        String content = mvcResult.getResponse().getContentAsString();

        // 로그인이 성공하면, 여기에서 반환된 결과(content)를 분석하여 원하는 결과를 확인할 수 있습니다.
        // 예를 들어, JSON 응답에서 특정 필드를 검증하거나, 쿠키를 확인할 수 있습니다.

        // 로그인 성공을 확인하는 코드 예시:
        assertEquals("로그인이 성공하면 HTTP 상태 코드는 200 OK 여야 합니다.", 200, status);
        assertTrue("로그인 성공 메시지를 포함하는지 확인합니다.", content.contains("로그인 성공"));
    }


}
