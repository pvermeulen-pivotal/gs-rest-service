package io.pivotal.geode.beacon.rest.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BeaconControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void noParamGreetingShouldReturnDefaultMessage() throws Exception {

        this.mockMvc.perform(post("/beacon")).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("{\n" + 
                		"  \"customerId\": \"1234567890\",\n" +
                		"  \"deviceId\": \"AEFD123A\",\n" +
                		"  \"uuid\": \"f7826da64fa24e98\",\n" + 
                		"  \"major\": \"64000\",\n" + 
                		"  \"minor\": \"100\",\n" + 
                		"  \"signalPower\": \"-60\"\n" + 
                		"}"));
    }

    @Test
    public void paramGreetingShouldReturnTailoredMessage() throws Exception {

        this.mockMvc.perform(get("/beacon"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("{\n" + 
                		"  \"customerId\": \"1234567890\",\n" + 
                		"  \"deviceId\": \"AEFD123A\",\n" +
                		"  \"uuid\": \"f7826da64fa24e98\",\n" + 
                		"  \"major\": \"64000\",\n" + 
                		"  \"minor\": \"100\",\n" + 
                		"  \"signalPower\": \"-60\"\n" + 
                		"}"));
    }

}
