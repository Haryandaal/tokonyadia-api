package com.enigma.tokonyadia_api.controller;

import com.enigma.tokonyadia_api.dto.request.RegisterStoreAdminRequest;
import com.enigma.tokonyadia_api.dto.request.SearchRequest;
import com.enigma.tokonyadia_api.dto.response.StoreAdminResponse;
import com.enigma.tokonyadia_api.dto.response.WebResponse;
import com.enigma.tokonyadia_api.service.StoreAdminService;
import com.enigma.tokonyadia_api.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/store-admins")
public class StoreAdminController {

    private final StoreAdminService storeAdminService;

    @PostMapping
    public ResponseEntity<WebResponse<StoreAdminResponse>> register(@RequestBody RegisterStoreAdminRequest customer) {
        StoreAdminResponse storeAdminResponse = storeAdminService.create(customer);
        return ResponseUtil.buildResponse(HttpStatus.CREATED, "Store Admin created", storeAdminResponse);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<WebResponse<StoreAdminResponse>> getStoreAdminById(@PathVariable(name = "id") String id) {
        StoreAdminResponse response = storeAdminService.getOne(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Store admin found", response);
    }

    @PutMapping("{id}")
    public ResponseEntity<WebResponse<StoreAdminResponse>> updateStoreAdmin(@PathVariable(name = "id") String id, @RequestBody RegisterStoreAdminRequest request) {
        StoreAdminResponse response = storeAdminService.update(id, request);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Store admin updated", response);
    }

    @GetMapping
    public ResponseEntity<WebResponse<List<StoreAdminResponse>>> search(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "q", required = false) String q
    ) {

        SearchRequest request = SearchRequest.builder()
                .query(q)
                .page(page)
                .size(size)
                .sort(sort)
                .build();

        Page<StoreAdminResponse> responses = storeAdminService.search(request);
        return ResponseUtil.buildResponsePage(HttpStatus.OK, "Store admin found", responses);
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<WebResponse<String>> deleteStoreAdminById(@PathVariable(name = "id") String id) {
        storeAdminService.removeById(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Store admin deleted", null);
    }

}
