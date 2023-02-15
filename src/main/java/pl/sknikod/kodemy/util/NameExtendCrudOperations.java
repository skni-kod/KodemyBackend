package pl.sknikod.kodemy.util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface NameExtendCrudOperations<RES, REQ> extends BaseCrudOperations<RES, REQ> {
    @GetMapping("/{name}")
    public ResponseEntity<RES> getByName(@PathVariable String name);

    @DeleteMapping("/{name}")
    public void deleteByName(@PathVariable Long id);

    @PutMapping("/{name}")
    public ResponseEntity<RES> update(@PathVariable String name, @RequestBody REQ body);
}
