package pl.sknikod.kodemy.util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface NameExtendCrudOperations<RES, REQ> extends BaseCrudOperations<RES, REQ> {
    @BaseApiResponses.Read
    @GetMapping("/{name}")
    ResponseEntity<RES> getByName(@PathVariable String name);

    @BaseApiResponses.Delete
    @DeleteMapping("/{name}")
    void deleteByName(@PathVariable Long id);

    @BaseApiResponses.Update
    @PutMapping("/{name}")
    ResponseEntity<RES> update(@PathVariable String name, @RequestBody REQ body);
}
