package unit.infrastructure.module.tag

import io.vavr.control.Try
import org.spockframework.util.Assert
import pl.sknikod.kodemybackend.infrastructure.database.Tag
import pl.sknikod.kodemybackend.infrastructure.mapper.TagMapper
import pl.sknikod.kodemybackend.infrastructure.module.tag.TagService
import pl.sknikod.kodemybackend.infrastructure.module.tag.model.TagAddRequest
import pl.sknikod.kodemybackend.infrastructure.module.tag.model.TagAddResponse
import pl.sknikod.kodemybackend.infrastructure.store.TagStore
import spock.lang.Specification

class TagServiceSpec extends Specification {
    def tagStore = Mock(TagStore)
    def tagMapper = Mock(TagMapper)

    def tagService = new TagService(tagStore, tagMapper)

    def tag = new Tag()
    def response = new TagAddResponse(1L, "name")

    def "shouldAddTag"() {
        given:
        tag.id = 1L
        tag.name = "name"
        tagStore.save("name") >> Try.success(tag)
        tagMapper.map(tag) >> response

        when:
        def request = new TagAddRequest()
        request.name = "name"
        def result = tagService.addTag(request)

        then:
        Assert.that(result == response)
    }

    def "shouldShowTags"() {
        given:
        tagStore.findAll() >> Try.success(new ArrayList<>(List.of(tag)))
        tagMapper.map(_ as List) >> new ArrayList<>(List.of(response))

        when:
        def result = tagService.showTags()

        then:
        Assert.that(result == List.of(response))
    }
}
