package com.enigma.tokonyadia_api.controller;

import com.enigma.tokonyadia_api.dto.request.CategoryRequest;
import com.enigma.tokonyadia_api.dto.response.CategoryResponse;
import com.enigma.tokonyadia_api.dto.response.ProductInStoreResponse;
import com.enigma.tokonyadia_api.dto.response.WebResponse;
import com.enigma.tokonyadia_api.entity.Category;
import com.enigma.tokonyadia_api.service.CategoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;


    @Test
    @WithMockUser(username = "admin", roles = {"SUPER_ADMIN"})
    void shouldReturn201WhenCreateNewCategory() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("test");

        String requestBodyJSON = objectMapper.writeValueAsString(request);

        CategoryResponse expectedResponse = new CategoryResponse();
        expectedResponse.setId("category-1");
        expectedResponse.setName(request.getName());
        expectedResponse.setProducts(new ArrayList<>());

        Mockito.when(categoryService.create(Mockito.any()))
                .thenReturn(expectedResponse);

        mockMvc
                .perform(
                        post("/api/categories")
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
    @WithMockUser(username = "storeadmin", roles = {"STORE_ADMIN"})
    void shouldReturn200WhenGetAllCategories() throws Exception {
        List<CategoryResponse> categories = List.of(
                new CategoryResponse("1", "Test1", new ArrayList<>()),
                new CategoryResponse("2", "Test2", new ArrayList<>())
        );

        Mockito.when(categoryService.getAll())
                .thenReturn(categories);

        mockMvc
                .perform(
                        get("/api/categories")
                )
                .andExpect(status().isOk())
                .andDo(result -> {
                    String responseJSON = result.getResponse().getContentAsString();
                    WebResponse<?> response = objectMapper.readValue(responseJSON, new TypeReference<>() {
                    });
                    assertEquals(HttpStatus.OK.value(), response.getStatus());
                });
    }
}