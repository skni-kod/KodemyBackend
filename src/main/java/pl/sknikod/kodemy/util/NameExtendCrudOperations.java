package pl.sknikod.kodemy.util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface NameExtendCrudOperations<RES, REQ> extends BaseCrudOperations<RES, REQ> {
    @SwaggerResponse.Read
    @GetMapping("/{name}")
    ResponseEntity<RES> getByName(@PathVariable String name);

    @SwaggerResponse.Delete
    @DeleteMapping("/{name}")
    void deleteByName(@PathVariable String name);

    @SwaggerResponse.Update
    @PutMapping("/{name}")
    ResponseEntity<RES> update(@PathVariable String name, @RequestBody REQ body);
}
