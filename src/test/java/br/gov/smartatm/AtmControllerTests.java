/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.gov.smartatm;

import br.gov.smartatm.model.BankNotes;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { Application.class })
@WebAppConfiguration
public class AtmControllerTests {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired private ObjectMapper mapper;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void checkSetup() {
        ServletContext servletContext = wac.getServletContext();
        Assert.assertNotNull(servletContext);
        Assert.assertTrue(servletContext instanceof MockServletContext);
        Assert.assertNotNull(wac.getBean("atmController"));
    }

    @Test
    public void shouldCreateAndReturnListBankNotes() throws Exception {

        this.mockMvc.perform(delete("/api/atm/")).andExpect(status().isOk());

        List<BankNotes> notes = getNotes();

        String json = mapper.writeValueAsString(notes);

        String contentAsString = this.mockMvc.perform(post("/api/atm/supply")
                .contentType(APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(json, contentAsString);
    }

    @Test
    public void shouldReturnListBankNotes() throws Exception {

        this.mockMvc.perform(delete("/api/atm/")).andExpect(status().isOk());

        List<BankNotes> notes = getNotes();

        String json = mapper.writeValueAsString(notes);

        this.mockMvc.perform(post("/api/atm/supply")
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());

        String contentAsString = this.mockMvc.perform(get("/api/atm/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(json, contentAsString);
    }

    @Test
    public void shouldReturnACertainQuantityOfWithdrawOptionsToAGivenValue() throws Exception {
        this.mockMvc.perform(delete("/api/atm/")).andExpect(status().isOk());

        List<BankNotes> notes = getNotes();

        String json = mapper.writeValueAsString(notes);

        this.mockMvc.perform(post("/api/atm/supply/")
                .contentType(APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());

        String content = this.mockMvc.perform(get("/api/atm/options/180"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<List<BankNotes>> values = mapper.readValue(content, new TypeReference<List<List<BankNotes>>>() {});
        assertEquals(24, values.size());
    }

    @Test
    public void givenACertainOptionShouldWithdrawValue() throws Exception {

        this.mockMvc.perform(delete("/api/atm/")).andExpect(status().isOk());

        List<BankNotes> notes = getNotes();

        this.mockMvc.perform(post("/api/atm/supply/")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(notes)))
                .andExpect(status().isOk());

        String content = this.mockMvc.perform(get("/api/atm/options/100"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<List<BankNotes>> values = mapper.readValue(content, new TypeReference<List<List<BankNotes>>>() {});
        Random rand = new Random();
        List<BankNotes> withdrawOption = values.get(rand.nextInt(values.size()));

        String remainsNotesString = this.mockMvc.perform(post("/api/atm/withdraw/")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(withdrawOption)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<BankNotes> remains = mapper.readValue(remainsNotesString, new TypeReference<List<BankNotes>>(){});

        notes.forEach( initial -> {
            int rest = initial.getAmount() - getSame(withdrawOption, initial).getAmount();
            assertEquals(rest, getSame(remains, initial).getAmount());
        });

        assertEquals(11, values.size());
    }


    @Test
    public void shouldReturnTheBestWithdrawOption() throws Exception {

        this.mockMvc.perform(delete("/api/atm/")).andExpect(status().isOk());

        final List<BankNotes> notes = getNotes(10,8,6,4);

        this.mockMvc.perform(post("/api/atm/supply/")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(notes)))
                .andExpect(status().isOk());

        final String content = this.mockMvc.perform(get("/api/atm/best-option/180"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<BankNotes> bestOption = mapper.readValue(content, new TypeReference<List<BankNotes>>() {});

        final List<BankNotes> supposedBastOptionTo180 = getNotes(5,4,1,0);

        supposedBastOptionTo180.forEach(b -> {
            assertEquals(b.getAmount(), getSame(bestOption, b).getAmount());
        });

    }


    private BankNotes getSame(List<BankNotes> withdrawOption, BankNotes initial) {
        return withdrawOption.stream().filter(f -> f.equals(initial)).findAny().get();
    }


    public List<BankNotes> getNotes(){
        return Arrays.asList(
                BankNotes.builder().note(10).amount(10).build()
                , BankNotes.builder().note(20).amount(10).build()
                , BankNotes.builder().note(50).amount(10).build()
                , BankNotes.builder().note(100).amount(10).build()
        );
    }

    public List<BankNotes> getNotes(int amount10, int amount20, int amount50, int amount100){
        return Arrays.asList(
                BankNotes.builder().note(10).amount(amount10).build()
                , BankNotes.builder().note(20).amount(amount20).build()
                , BankNotes.builder().note(50).amount(amount50).build()
                , BankNotes.builder().note(100).amount(amount100).build()
        );
    }

}
