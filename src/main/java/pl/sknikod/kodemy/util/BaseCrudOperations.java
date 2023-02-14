package pl.sknikod.kodemy.util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface BaseCrudOperations<REQ, RES> {
    @GetMapping
    public ResponseEntity<List<RES>> getAll();

    @GetMapping("/{id}")
    public ResponseEntity<RES> getById(@PathVariable Long id);

    @PostMapping
    public ResponseEntity<RES> create(@RequestBody REQ body, HttpServletRequest request);

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id);

    @PutMapping("/{id}")
    public ResponseEntity<RES> update(@PathVariable Long id,@RequestBody REQ body);
}
