package pl.sknikod.kodemybackend.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Tag;
import pl.sknikod.kodemybackend.infrastructure.module.tag.model.TagAddResponse;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TagMapper {
    List<TagAddResponse> map(Collection<Tag> tag);

    TagAddResponse map(Tag tag);
}
