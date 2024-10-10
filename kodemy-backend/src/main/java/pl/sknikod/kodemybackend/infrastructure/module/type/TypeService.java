package pl.sknikod.kodemybackend.infrastructure.module.type;

import lombok.AllArgsConstructor;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.TypeMapper;
import pl.sknikod.kodemybackend.infrastructure.dao.TypeDao;
import pl.sknikod.kodemybackend.infrastructure.module.type.model.SingleTypeResponse;
import pl.sknikod.kodemycommon.exception.content.ExceptionUtil;

import java.util.List;

@AllArgsConstructor
public class TypeService {
    private final TypeDao typeDao;
    private final TypeMapper typeMapper;

    public List<SingleTypeResponse> getAllTypes() {
        return typeDao.findAll()
                .map(typeMapper::map)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }
}
