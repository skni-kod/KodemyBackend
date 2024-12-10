package pl.sknikod.kodemysearch.module.material


import org.springframework.data.domain.PageRequest
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialSearchService
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialSearchService$MaterialSearchMapperImpl
import pl.sknikod.kodemysearch.infrastructure.store.MaterialSearchStore
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static pl.sknikod.kodemysearch.infrastructure.rest.MaterialControllerDefinition.*

class MaterialSearchServiceSpec extends Specification {

    def materialSearchStore = Mock(MaterialSearchStore)

    @Subject
    MaterialSearchService materialSearchService = new MaterialSearchService(materialSearchStore, new MaterialSearchService$MaterialSearchMapperImpl())

    @Unroll
    def "should create search criteria from filter search params for #description"() {
        given:
        def filterSearchParams = params
        def pageable = PageRequest.of(0, 10)

        when:
        try {
            materialSearchService.search(filterSearchParams as MaterialFilterSearchParams, pageable)
        } catch (Exception ignored) {
            // We don't care about the result. We only check creating search criteria
        }

        then: "search criteria are created based on filter search params"
        1 * materialSearchStore.search({
            it.anyPhrase == filterSearchParams.phrase &&
            it.pageable == pageable &&
            it.phraseFields.size() == expected.phraseFieldsCount &&
            it.phraseFields.any { field -> field.name == "id" && field.value == filterSearchParams.id.toString() } &&
            it.phraseFields.any { field -> field.name == "sectionId" && field.value == filterSearchParams.sectionId.toString() } &&
            it.arrayFields.size() == expected.arrayFieldsCount &&
            it.arrayFields.any { field -> field.name == "categoryId" && field.values == filterSearchParams.categoryIds.collect { it.toString() } } &&
            it.rangeFields.size() == expected.rangeFieldsCount &&
            it.rangeFields.any { field -> field.name == "avgGrade" && field.from == filterSearchParams.minAvgGrade && field.to == filterSearchParams.maxAvgGrade }
        })

        where:
        [params, expected, description] << searchParamsProvider()
    }

    def searchParamsProvider() {
        return [[
                params: new MaterialFilterSearchParams(
                        phrase: "phrase1",
                        id: 1L,
                        sectionId: 1L,
                        categoryIds: [1L],
                        minAvgGrade: 2.2f,
                        maxAvgGrade: 4.2f
                ),
                expected: [
                        phraseFieldsCount: 2,
                        arrayFieldsCount: 1,
                        rangeFieldsCount: 1
                ],
                description: "Standard case with all fields populated"
            ],
            [
                params: new MaterialFilterSearchParams(
                        phrase: "searchText",
                        id: 10L,
                        sectionId: 5L,
                        categoryIds: [3L, 4L, 5L],
                        minAvgGrade: 1.0f,
                        maxAvgGrade: 5.0f
                ),
                expected: [
                        phraseFieldsCount: 2,
                        arrayFieldsCount: 1,
                        rangeFieldsCount: 1
                ],
                description: "Case with multiple categories"
            ]]
        }

}
