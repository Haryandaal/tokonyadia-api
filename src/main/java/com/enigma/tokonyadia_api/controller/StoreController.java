package com.enigma.tokonyadia_api.controller;

import com.enigma.tokonyadia_api.dto.request.SearchRequest;
import com.enigma.tokonyadia_api.dto.request.StoreRequest;
import com.enigma.tokonyadia_api.dto.response.ProductInStoreResponse;
import com.enigma.tokonyadia_api.dto.response.StoreResponse;
import com.enigma.tokonyadia_api.dto.response.WebResponse;
import com.enigma.tokonyadia_api.service.StoreService;
import com.enigma.tokonyadia_api.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/stores")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(
        name = "Store",
        description = "APIs for creating, retrieving, updating, and removing stores "
)
public class StoreController {
    private static class WebResponseStoreResponse extends WebResponse<StoreResponse> {}

    private final StoreService storeService;

    @Operation(
            summary = "Create store",
            description = "This endpoint is for creating stores and can only be done by authenticated store admins and super admins",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created store successfully", content = @Content(schema = @Schema(implementation = WebResponseStoreResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(schema = @Schema(implementation = WebResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Access Denied", content = @Content(schema = @Schema(implementation = WebResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = WebResponse.class))),
            }
    )
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STORE_ADMIN')")
    @PostMapping(path = "{storeAdminId}/add")
    public ResponseEntity<WebResponse<StoreResponse>> createStore(@PathVariable String storeAdminId, @RequestBody StoreRequest request) {
        StoreResponse storeResponse = storeService.create(storeAdminId, request);
        return ResponseUtil.buildResponse(HttpStatus.CREATED, "Store created", storeResponse);
    }

    @Operation(
            summary = "Get store by id",
            description = "This endpoint is for retrieving store by id store",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Retrieved store successfully", content = @Content(schema = @Schema(implementation = WebResponseStoreResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Store not found (invalid id)", content = @Content(schema = @Schema(implementation = WebResponse.class))),
            }
    )
    @GetMapping(path = "{id}")
    public ResponseEntity<WebResponse<StoreResponse>> getStoreById(@PathVariable(name = "id") String id) {
        StoreResponse response = storeService.getOne(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Store found", response);
    }

    @GetMapping(path = "{storeId}/products")
    public ResponseEntity<WebResponse<List<?>>> getProductByStoreId(@PathVariable(name = "storeId") String storeId) {
        List<ProductInStoreResponse> responses = storeService.getByStoreId(storeId);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Product found", responses);
    }

    @GetMapping()
    public ResponseEntity<WebResponse<List<StoreResponse>>> search(
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

        Page<StoreResponse> responses = storeService.search(request);
        return ResponseUtil.buildResponsePage(HttpStatus.OK, "Store found", responses);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STORE_ADMIN')")
    @PutMapping(path = "{id}")
    public ResponseEntity<WebResponse<StoreResponse>> updateById(@PathVariable(name = "id") String id, @RequestBody StoreRequest request) {
        StoreResponse response = storeService.updateById(id, request);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Store updated", response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'STORE_ADMIN')")
    @DeleteMapping(path = "{id}")
    public ResponseEntity<WebResponse<String>> deleteStoreById(@PathVariable(name = "id") String id) {
        storeService.removeById(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Store deleted", null);
    }
}
