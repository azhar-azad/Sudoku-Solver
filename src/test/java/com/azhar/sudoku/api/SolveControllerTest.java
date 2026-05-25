package com.azhar.sudoku.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SolveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void solveValidPuzzleReturns200WithSolution() throws Exception {
        String body = """
                  {"board":[
                    [5,3,0,0,7,0,0,0,0],
                    [6,0,0,1,9,5,0,0,0],
                    [0,9,8,0,0,0,0,6,0],
                    [8,0,0,0,6,0,0,0,3],
                    [4,0,0,8,0,3,0,0,1],
                    [7,0,0,0,2,0,0,0,6],
                    [0,6,0,0,0,0,2,8,0],
                    [0,0,0,4,1,9,0,0,5],
                    [0,0,0,0,8,0,0,7,9]
                  ]}""";

        mockMvc.perform(post("/api/solve")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.solved").value(true))
                .andExpect(jsonPath("$.finalBoard").isArray())
                .andExpect(jsonPath("$.stepCount").isNumber());
    }

    @Test
    void solveInvalidPuzzleReturns400WithError() throws Exception {
        // Two 7s in the same row
        String body = """
                  {"board":[
                    [7,7,0,0,0,0,0,0,0],
                    [0,0,0,0,0,0,0,0,0],
                    [0,0,0,0,0,0,0,0,0],
                    [0,0,0,0,0,0,0,0,0],
                    [0,0,0,0,0,0,0,0,0],
                    [0,0,0,0,0,0,0,0,0],
                    [0,0,0,0,0,0,0,0,0],
                    [0,0,0,0,0,0,0,0,0],
                    [0,0,0,0,0,0,0,0,0]
                  ]}""";

        mockMvc.perform(post("/api/solve")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.solved").value(false))
                .andExpect(jsonPath("$.error").isString());
    }

    @Test
    void solveUnsolvablePuzzleReturns200WithNoSolution() throws Exception {
        // Row 0 filled with 1-8, col 8 in all other rows blocks the only missing digit
        String body = """
                  {"board":[
                    [1,2,3,4,5,6,7,8,0],
                    [0,0,0,0,0,0,0,0,9],
                    [0,0,0,0,0,0,0,0,0],
                    [0,0,0,0,0,0,0,0,0],
                    [0,0,0,0,0,0,0,0,0],
                    [0,0,0,0,0,0,0,0,0],
                    [0,0,0,0,0,0,0,0,0],
                    [0,0,0,0,0,0,0,0,0],
                    [0,0,0,0,0,0,0,0,0]
                  ]}""";

        mockMvc.perform(post("/api/solve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.solved").value(false))
            .andExpect(jsonPath("$.error").value("Sudoku has no solution"));
    }
}
