package pl.sknikod.kodemybackend.infrastructure.module;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.sknikod.kodemybackend.infrastructure.common.lan.LanNetworkHandler;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.*;
import pl.sknikod.kodemybackend.infrastructure.dao.*;
import pl.sknikod.kodemybackend.infrastructure.database.GradeRepository;
import pl.sknikod.kodemybackend.infrastructure.database.MaterialRepository;
import pl.sknikod.kodemybackend.infrastructure.module.category.CategoryService;
import pl.sknikod.kodemybackend.infrastructure.module.grade.MaterialGradeService;
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialCreateService;
import pl.sknikod.kodemybackend.infrastructure.module.material.producer.MaterialCreatedProducer;
import pl.sknikod.kodemybackend.infrastructure.module.material.producer.MaterialStatusUpdatedProducer;
import pl.sknikod.kodemybackend.infrastructure.module.material.producer.MaterialUpdatedProducer;
import pl.sknikod.kodemybackend.infrastructure.module.material_by_user.MaterialGetByUserService;
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialGetByIdService;
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialIndexService;
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialStatusService;
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialUpdateService;
import pl.sknikod.kodemybackend.infrastructure.module.section.SectionService;
import pl.sknikod.kodemybackend.infrastructure.module.tag.TagService;
import pl.sknikod.kodemybackend.infrastructure.module.type.TypeService;

@Configuration
public class ModuleBeanConfiguration {
    @Bean
    public CategoryService categoryService(
            CategoryDao categoryDao, CategoryMapper categoryMapper) {
        return new CategoryService(categoryDao, categoryMapper);
    }

    @Bean
    public MaterialGradeService materialGradeService(
            MaterialDao materialDao, GradeMapper gradeMapper, GradeDao gradeDao
    ) {
        return new MaterialGradeService(materialDao, gradeMapper, gradeDao);
    }

    @Bean
    public MaterialCreateService materialCreateService(
            MaterialDao materialDao,
            TypeDao typeDao,
            MaterialCreateService.MaterialCreateMapper createMaterialMapper,
            CategoryDao categoryDao,
            TagDao tagDao,
            MaterialCreatedProducer materialCreatedProducer
    ) {
        return new MaterialCreateService(
                materialDao, typeDao, createMaterialMapper,
                categoryDao, tagDao, materialCreatedProducer
        );
    }

    @Bean
    public MaterialGetByUserService materialAdminGetService(
            MaterialDao materialDao, LanNetworkHandler lanNetworkHandler) {
        return new MaterialGetByUserService(materialDao, lanNetworkHandler);
    }

    @Bean
    public MaterialGetByIdService materialGetService(
            GradeDao gradeDao, MaterialDao materialDao,
            LanNetworkHandler lanNetworkHandler
    ) {
        return new MaterialGetByIdService(gradeDao, materialDao, lanNetworkHandler);
    }

    @Bean
    public MaterialIndexService materialOSearchService(
            GradeRepository gradeRepository, MaterialRepository materialRepository, MaterialUpdatedProducer materialUpdatedProducer
    ) {
        return new MaterialIndexService(gradeRepository, materialRepository, materialUpdatedProducer);
    }

    @Bean
    public MaterialStatusService materialStatusService(MaterialDao materialDao, MaterialStatusUpdatedProducer materialStatusUpdatedProducer) {
        return new MaterialStatusService(materialDao, materialStatusUpdatedProducer);
    }

    @Bean
    public MaterialUpdateService materialUpdateService(
            MaterialUpdateService.MaterialUpdateMapper updateMaterialMapper,
            CategoryDao categoryDao,
            TypeDao typeDao,
            TagDao tagDao,
            MaterialDao materialDao,
            MaterialUpdatedProducer materialUpdatedProducer,
            GradeDao gradeDao
    ) {
        return new MaterialUpdateService(
                updateMaterialMapper, categoryDao, typeDao,
                tagDao, materialDao, materialUpdatedProducer, gradeDao
        );
    }

    @Bean
    public SectionService sectionService(
            SectionDao sectionDao, SectionMapper sectionMapper) {
        return new SectionService(sectionDao, sectionMapper);
    }

    @Bean
    public TagService tagService(
            TagMapper tagMapper, TagDao typeRepositoryHandler) {
        return new TagService(typeRepositoryHandler, tagMapper);
    }

    @Bean
    public TypeService typeService(
            TypeDao typeDao, TypeMapper typeMapper) {
        return new TypeService(typeDao, typeMapper);
    }
}
