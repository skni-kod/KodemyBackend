package unit.infrastructure.store

import org.spockframework.util.Assert
import pl.sknikod.kodemybackend.infrastructure.database.Tag
import pl.sknikod.kodemybackend.infrastructure.database.TagRepository
import pl.sknikod.kodemybackend.infrastructure.store.TagStore
import pl.sknikod.kodemycommons.exception.AlreadyExists409Exception
import pl.sknikod.kodemycommons.exception.NotFound404Exception
import spock.lang.Specification


class TagStoreSpec extends Specification {
    def tagRepository = Mock(TagRepository)

    def tag = new Tag("name")

    def tagStore = new TagStore(tagRepository)

    def "shouldFindAllByIdIn"() {
        given:
        tagRepository.findTagsByIdIn(List.of(1L))
                >> Set.of(tag)

        when:
        def result = tagStore.findAllByIdIn(List.of(1L)).get()

        then:
        Assert.that(result == Set.of(tag))
    }

    def "shouldThrowWhenFindAllByIdIn"() {
        given:
        tagRepository.findTagsByIdIn(List.of(1L))
                >> new HashSet<Tag>()

        when:
        tagStore.findAllByIdIn(List.of(1L)).get()

        then:
        thrown(NotFound404Exception)
    }

    def "shouldSave"() {
        given:
        tagRepository.existsByName(tag.name) >> false
        tagRepository.save(_ as Tag) >> tag

        when:
        def result = tagStore.save(tag.name).get()

        then:
        Assert.that(result == tag)
    }

    def "shouldThrowWhenSave"() {
        given:
        tagRepository.existsByName(tag.name) >> true

        when:
        tagStore.save(tag.name).get()

        then:
        thrown(AlreadyExists409Exception)
    }

    def "shouldFindAll"() {
        given:
        tagRepository.findAll() >> new ArrayList<Tag>(List.of(tag))

        when:
        def result = tagStore.findAll().get()

        then:
        Assert.that(result == List.of(tag))
    }
}
