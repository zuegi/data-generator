package ch.wesr.data.random.datagenerator;


import ch.wesr.data.random.datagenerator.config.JSONConfigReader;
import ch.wesr.data.random.datagenerator.config.SimulationConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(DataGeneratorController.class)
public class DataGeneratorControllerTest {

    public static final String API = "/api";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    DataGenerator dataGenerator;

    @Test
    public void testSetSimConfig() throws Exception {
        // given
        String json = readFile(TestHelper.SIMULATION_CONFIG_NORMAL_SIMULATION, StandardCharsets.UTF_8);
        // when
        mockMvc.perform(post(API +DataGeneratorController.SET_SIM_CONFIG)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());

    }

    @Test
    public void testGetSimConfig() throws Exception {

        mockMvc.perform(get(API +DataGeneratorController.GET_SIM_CONFIG))
                .andExpect(status().isOk());
    }

    @Test
    public void testSetWorkflowConfig() throws Exception {
        String json = readFile(TestHelper.WORKFLOW_CONFIG_TEST, StandardCharsets.UTF_8);

        mockMvc.perform(post(API +DataGeneratorController.SET_WORKFLOW_CONFIG)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }



    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}