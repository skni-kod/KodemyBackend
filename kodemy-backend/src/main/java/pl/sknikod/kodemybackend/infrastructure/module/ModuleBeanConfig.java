package pl.sknikod.kodemybackend.infrastructure.module;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.sknikod.kodemybackend.infrastructure.common.lan.LanNetworkHandler;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.*;
import pl.sknikod.kodemybackend.infrastructure.database.handler.*;
import pl.sknikod.kodemybackend.infrastructure.database.repository.GradeRepository;
import pl.sknikod.kodemybackend.infrastructure.database.repository.MaterialRepository;
import pl.sknikod.kodemybackend.infrastructure.module.category.CategoryUseCase;
import pl.sknikod.kodemybackend.infrastructure.module.grade.MaterialGradeUseCase;
import pl.sknikod.kodemybackend.infrastructure.module.material.MaterialRabbitProducer;
import pl.sknikod.kodemybackend.infrastructure.module.material.add.MaterialCreateUseCase;
import pl.sknikod.kodemybackend.infrastructure.module.material.get.MaterialGetUseCase;
import pl.sknikod.kodemybackend.infrastructure.module.material.get.MaterialGetByIdUseCase;
import pl.sknikod.kodemybackend.infrastructure.module.material.index.MaterialOSearchUseCase;
import pl.sknikod.kodemybackend.infrastructure.module.material.update.MaterialStatusUseCase;
import pl.sknikod.kodemybackend.infrastructure.module.material.update.MaterialUpdateUseCase;
import pl.sknikod.kodemybackend.infrastructure.module.section.SectionUseCase;
import pl.sknikod.kodemybackend.infrastructure.module.tag.TagUseCase;
import pl.sknikod.kodemybackend.infrastructure.module.type.TypeUseCase;

@Configuration
public class ModuleBeanConfig {
    @Bean
    public CategoryUseCase categoryUseCase(
            CategoryRepositoryHandler categoryRepositoryHandler, CategoryMapper categoryMapper) {
        return new CategoryUseCase(categoryRepositoryHandler, categoryMapper);
    }

    @Bean
    public MaterialGradeUseCase materialGradeUseCase(
            MaterialRepositoryHandler materialRepositoryHandler, GradeMapper gradeMapper, GradeRepositoryHandler gradeRepositoryHandler
    ) {
        return new MaterialGradeUseCase(materialRepositoryHandler, gradeMapper, gradeRepositoryHandler);
    }

    @Bean
    public MaterialCreateUseCase materialCreateUseCase(
            MaterialRepositoryHandler materialRepositoryHandler,
            TypeRepositoryHandler typeRepositoryHandler,
            MaterialCreateUseCase.MaterialCreateMapper createMaterialMapper,
            CategoryRepositoryHandler categoryRepositoryHandler,
            TagRepositoryHandler tagRepositoryHandler,
            MaterialRabbitProducer materialProduce
    ) {
        return new MaterialCreateUseCase(
                materialRepositoryHandler, typeRepositoryHandler, createMaterialMapper,
                categoryRepositoryHandler, tagRepositoryHandler, materialProduce
        );
    }

    @Bean
    public MaterialGetUseCase materialAdminGetUseCase(
            MaterialRepositoryHandler materialRepositoryHandler, LanNetworkHandler lanNetworkHandler) {
        return new MaterialGetUseCase(materialRepositoryHandler, lanNetworkHandler);
    }

    @Bean
    public MaterialGetByIdUseCase materialGetUseCase(
            GradeRepositoryHandler gradeRepositoryHandler, MaterialRepositoryHandler materialRepositoryHandler,
            LanNetworkHandler lanNetworkHandler
    ) {
        return new MaterialGetByIdUseCase(gradeRepositoryHandler, materialRepositoryHandler, lanNetworkHandler);
    }

    @Bean
    public MaterialOSearchUseCase materialOSearchUseCase(
            GradeRepository gradeRepository, MaterialRepository materialRepository,
            MaterialRabbitProducer materialProducer
    ) {
        return new MaterialOSearchUseCase(gradeRepository, materialRepository, materialProducer);
    }

    @Bean
    public MaterialStatusUseCase materialStatusUseCase(MaterialRepositoryHandler materialRepositoryHandler) {
        return new MaterialStatusUseCase(materialRepositoryHandler);
    }

    @Bean
    public MaterialUpdateUseCase materialUpdateUseCase(
            MaterialUpdateUseCase. MaterialUpdateMapper updateMaterialMapper,
            CategoryRepositoryHandler categoryRepositoryHandler,
            TypeRepositoryHandler typeRepositoryHandler,
            TagRepositoryHandler tagRepositoryHandler,
            MaterialRepositoryHandler materialRepositoryHandler,
            MaterialRabbitProducer materialProducer,
            GradeRepositoryHandler gradeRepositoryHandler
    ){
        return new MaterialUpdateUseCase(
                updateMaterialMapper, categoryRepositoryHandler, typeRepositoryHandler,
                tagRepositoryHandler, materialRepositoryHandler, materialProducer, gradeRepositoryHandler
        );
    }

    @Bean
    public SectionUseCase sectionUseCase(
            SectionRepositoryHandler sectionRepositoryHandler, SectionMapper sectionMapper) {
        return new SectionUseCase(sectionRepositoryHandler, sectionMapper);
    }

    @Bean
    public TagUseCase tagUseCase(
            TagMapper tagMapper, TagRepositoryHandler typeRepositoryHandler) {
        return new TagUseCase(typeRepositoryHandler, tagMapper);
    }

    @Bean
    public TypeUseCase typeUseCase(
            TypeRepositoryHandler typeRepositoryHandler, TypeMapper typeMapper) {
        return new TypeUseCase(typeRepositoryHandler, typeMapper);
    }
}
