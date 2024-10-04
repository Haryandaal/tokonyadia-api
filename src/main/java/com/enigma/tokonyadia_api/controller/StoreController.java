package com.enigma.tokonyadia_api.controller;

import com.enigma.tokonyadia_api.dto.request.StoreRequest;
import com.enigma.tokonyadia_api.dto.response.StoreResponse;
import com.enigma.tokonyadia_api.dto.response.WebResponse;
import com.enigma.tokonyadia_api.service.StoreService;
import com.enigma.tokonyadia_api.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping()
    public ResponseEntity<WebResponse<StoreResponse>> createStore(@RequestBody StoreRequest request) {
        StoreResponse storeResponse = storeService.create(request);
        return ResponseUtil.buildResponse(HttpStatus.CREATED, "Store created", storeResponse);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<WebResponse<StoreResponse>> getStoreById(@PathVariable(name = "id") String id) {
        StoreResponse response = storeService.getById(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Store found", response);
    }
}
