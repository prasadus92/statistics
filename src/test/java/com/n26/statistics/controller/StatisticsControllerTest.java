package com.n26.statistics.controller;

import com.n26.statistics.controllers.StatisticsController;
import com.n26.statistics.domains.ImmutableTransactionStatistic;
import com.n26.statistics.domains.TransactionStatistic;
import com.n26.statistics.services.StatisticsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(StatisticsController.class)
public class StatisticsControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private StatisticsService statisticsService;


    @Test
    public void shouldReturnSampleStatistics() throws Exception {
        when(statisticsService.getTransactionStatistics()).thenReturn(ImmutableTransactionStatistic.builder()
                .count(5)
                .sum(143.8)
                .max(63.0)
                .min(1.2)
                .build());

        mvc.perform(get("/statistics").accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("count", is(5)))
                .andExpect(jsonPath("sum", is(143.8)))
                .andExpect(jsonPath("avg", is(28.76)))
                .andExpect(jsonPath("max", is(63.0)))
                .andExpect(jsonPath("min", is(1.2)));
    }

    @Test
    public void shouldReturnZeroStatistics() throws Exception {
        when(statisticsService.getTransactionStatistics()).thenReturn(TransactionStatistic.INITIAL_VALUE);

        mvc.perform(get("/statistics").accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("count", is(0)))
                .andExpect(jsonPath("sum", is(0.0)))
                .andExpect(jsonPath("avg", is("NaN")))
                .andExpect(jsonPath("max", is("NaN")))
                .andExpect(jsonPath("min", is("NaN")));
    }
}
