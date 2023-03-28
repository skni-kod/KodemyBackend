package pl.sknikod.kodemy.util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@SwaggerResponse
public interface BaseCrudOperations<REQ, RES> {
    @SwaggerResponse.ReadRequest
    @GetMapping
    ResponseEntity<List<RES>> getAll();

    @SwaggerResponse.ReadRequest
    @GetMapping("/{id}")
    ResponseEntity<RES> getById(@PathVariable Long id);

    @SwaggerResponse.CreateRequest
    @PostMapping
    ResponseEntity<RES> create(@RequestBody REQ body, HttpServletRequest request);

    @SwaggerResponse.DeleteRequest
    @DeleteMapping("/{id}")
    void deleteById(@PathVariable Long id);

    @SwaggerResponse.UpdateRequest
    @PutMapping("/{id}")
    ResponseEntity<RES> update(@PathVariable Long id, @RequestBody REQ body);
}
