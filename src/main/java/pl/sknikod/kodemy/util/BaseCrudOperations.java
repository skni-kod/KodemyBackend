package pl.sknikod.kodemy.util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@BaseApiResponses
public interface BaseCrudOperations<REQ, RES> {
    @BaseApiResponses.Read
    @GetMapping
    ResponseEntity<List<RES>> getAll();

    @BaseApiResponses.Read
    ResponseEntity<RES> getById(@PathVariable Long id);

    @BaseApiResponses.Create
    @PostMapping
    ResponseEntity<RES> create(@RequestBody REQ body, HttpServletRequest request);

    @BaseApiResponses.Delete
    @DeleteMapping("/{id}")
    void deleteById(@PathVariable Long id);

    @BaseApiResponses.Update
    @PutMapping("/{id}")
    ResponseEntity<RES> update(@PathVariable Long id, @RequestBody REQ body);
}
