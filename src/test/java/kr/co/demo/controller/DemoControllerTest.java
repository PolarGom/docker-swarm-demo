package kr.co.demo.controller;

import kr.co.demo.CommonControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 데모 컨트롤러 테스트
 *
 * @author Demo
 * @since 2021-12-01
 */
public class DemoControllerTest extends CommonControllerTest {

    @Test
    @DisplayName("호스트 이름 가져오기 테스트")
    public void testGetHostName() throws Exception {

        this.mockMvc.perform(get("/hostname"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
}
