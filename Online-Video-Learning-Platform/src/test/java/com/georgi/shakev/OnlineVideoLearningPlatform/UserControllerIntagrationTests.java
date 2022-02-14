package com.georgi.shakev.OnlineVideoLearningPlatform;

import com.georgi.shakev.OnlineVideoLearningPlatform.Dto.UserResponseDto;
import com.georgi.shakev.OnlineVideoLearningPlatform.service.UserService;
import com.georgi.shakev.OnlineVideoLearningPlatform.web.UserController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;

@DisplayName("User Controller Integration Tests")
@Nested
@WebMvcTest(UserController.class)
class UserControllerIntegrationTests {

    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;

    final static String USERNAME = "admin";
    final static Long ID = 1L;

    @Test
    @DisplayName("Should Get User By Username.")
    @WithMockUser(username = USERNAME, roles = "ADMIN")
    void shouldGtUserByUsername() throws Exception{
        UserResponseDto expectedResponse = new UserResponseDto();
        expectedResponse.setUsername(USERNAME);
        expectedResponse.getRoles().add("ROLE_ADMIN");
        expectedResponse.setId(ID);

        when(userService.getUser(USERNAME)).thenReturn(expectedResponse);

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/users/admin"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", expectedResponse))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userRequest"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("username"));
    }
}
