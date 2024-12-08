package unit.infrastructure.module.tag

import org.spockframework.util.Assert
import pl.sknikod.kodemybackend.infrastructure.module.tag.TagController
import pl.sknikod.kodemybackend.infrastructure.module.tag.TagService
import pl.sknikod.kodemybackend.infrastructure.module.tag.model.TagAddRequest
import pl.sknikod.kodemybackend.infrastructure.module.tag.model.TagAddResponse
import spock.lang.Specification

class TagControllerSpec extends Specification {
    def tagService = Mock(TagService)

    def tagController = new TagController(tagService)

    def response = new TagAddResponse(1L, "name")

    def "shouldAddTag"() {
        given:
        def tagRequest = new TagAddRequest()
        tagRequest.name = "name"
        tagService.addTag(tagRequest) >> response

        when:
        def result = tagController.addTag(tagRequest)

        then:
        Assert.that(result.body == response)
    }

    def "shouldShowTags"() {
        given:
        tagService.showTags() >> new ArrayList<>(List.of(response))

        when:
        def result = tagController.showTags()

        then:
        Assert.that(result.body == List.of(response))
    }
}
