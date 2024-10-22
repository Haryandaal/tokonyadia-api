package com.enigma.tokonyadia_api.controller;

import com.enigma.tokonyadia_api.dto.request.RegisterCustomerRequest;
import com.enigma.tokonyadia_api.dto.response.CustomerResponse;
import com.enigma.tokonyadia_api.dto.response.WebResponse;
import com.enigma.tokonyadia_api.entity.Customer;
import com.enigma.tokonyadia_api.service.CustomerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    void shouldReturn201WhenCreateNewCustomer() throws Exception {
        RegisterCustomerRequest request = new RegisterCustomerRequest();
        request.setName("test");
        request.setEmail("test@test.com");
        request.setPassword("test");
        request.setAddress("test");
        request.setPhone("test");

        String requestBodyJSON = objectMapper.writeValueAsString(request);

        CustomerResponse expectedResponse = new CustomerResponse();
        expectedResponse.setId("test1");
        expectedResponse.setName(request.getName());
        expectedResponse.setEmail(request.getEmail());
        expectedResponse.setAddress(request.getAddress());
        expectedResponse.setPhone(request.getPhone());

        Mockito.when(customerService.create(Mockito.any())).thenReturn(expectedResponse);

        mockMvc
                .perform(
                        post("/api/customers")
                                .content(requestBodyJSON)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isCreated())
                .andDo(result -> {
                    String responseJSON = result.getResponse().getContentAsString();
                    WebResponse<?> response = objectMapper.readValue(responseJSON, new TypeReference<>() {
                    });
                    assertEquals(HttpStatus.CREATED.value(), response.getStatus());
                    assertNotNull(response.getData());
                });
    }

    @Test
    @WithMockUser(username = "budi", roles = "CUSTOMER")
    void shouldReturn200WhenGetAllMenu() throws Exception {
        List<CustomerResponse> menus = List.of(
                new CustomerResponse("1", "tes", "tes", "3222", "tes", "333")
        );
        PageImpl<CustomerResponse> mockResponses = new PageImpl<>(
                menus,
                PageRequest.of(1, 10),
                menus.size()
        );

        Mockito.when(customerService.search(Mockito.any()))
                .thenReturn(mockResponses);

        mockMvc.perform(
                get("/api/customers")
                        .requestAttr("page", "1")
                        .requestAttr("size", "10")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    WebResponse<List<CustomerResponse>> response = objectMapper.readValue(jsonResponse, new TypeReference<>() {
                    });
                    assertEquals(HttpStatus.OK.value(), response.getStatus());
                    assertEquals(1, response.getData().size());
                });
    }
}