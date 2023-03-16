package pl.sknikod.kodemy.util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@SwaggerResponse
public interface BaseCrudOperations<REQ, RES> {
    @SwaggerResponse.Read
    @GetMapping
    ResponseEntity<List<RES>> getAll();

    @SwaggerResponse.Read
    @GetMapping("/{id}")
    ResponseEntity<RES> getById(@PathVariable Long id);

    @SwaggerResponse.Create
    @PostMapping
    ResponseEntity<RES> create(@RequestBody REQ body, HttpServletRequest request);

    @SwaggerResponse.Delete
    @DeleteMapping("/{id}")
    void deleteById(@PathVariable Long id);

    @SwaggerResponse.Update
    @PutMapping("/{id}")
    ResponseEntity<RES> update(@PathVariable Long id, @RequestBody REQ body);
}
