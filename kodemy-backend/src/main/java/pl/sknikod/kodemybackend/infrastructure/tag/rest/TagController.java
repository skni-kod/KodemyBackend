package pl.sknikod.kodemybackend.infrastructure.tag.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import pl.sknikod.kodemybackend.infrastructure.tag.TagService;

import java.net.URI;
import java.util.List;

@Controller
@AllArgsConstructor
public class TagController implements TagControllerDefinition {
    private TagService tagService;

    @Override
    @PreAuthorize("isAuthenticated() and hasAuthority('CAN_MODIFY_TAGS')")
    public ResponseEntity<TagAddResponse> addTag(TagAddRequest tag) {
        var tagResponse = tagService.addTag(tag);
        return ResponseEntity
                .created(URI.create("/api/tags/" + tagResponse.getId()))
                .body(tagResponse);
    }

    @Override
    public ResponseEntity<List<TagAddResponse>> showTags() {
        return ResponseEntity.status(HttpStatus.OK).body(tagService.showTags());
    }
}
