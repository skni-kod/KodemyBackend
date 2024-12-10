package pl.sknikod.kodemysearch.module.material

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import pl.sknikod.kodemysearch.SuperclassSpec
import pl.sknikod.kodemysearch.configuration.LogbookConfiguration
import pl.sknikod.kodemysearch.configuration.OpenSearchConfiguration
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialSearchService
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialSearchService$MaterialSearchMapperImpl
import pl.sknikod.kodemysearch.infrastructure.module.material.model.MaterialPageable
import pl.sknikod.kodemysearch.infrastructure.rest.MaterialControllerDefinition
import pl.sknikod.kodemysearch.infrastructure.store.MaterialSearchStore
import pl.sknikod.kodemysearch.util.opensearch.OpenSearchClientEnhanced
import spock.lang.Shared
import spock.lang.Subject

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static org.assertj.core.api.Assertions.assertThat
import static pl.sknikod.kodemysearch.module.material.MaterialSearchHelper.createMaterialSearchService

class MaterialSearchServiceWiremockSpec extends SuperclassSpec {

    @Subject
    MaterialSearchService materialSearchService = createMaterialSearchService()

    def setupSpec() {
        WIREMOCK.start()
        setupWireMockStubs()
    }

    def cleanupSpec() {
        WIREMOCK.stop()
    }

    def "should search materials and map results correctly"() {
        given: "A mocked OpenSearch response"
        WIREMOCK.stubFor(post(urlPathMatching("/materials/_search"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        {
                            "took": 123,
                            "timed_out": false,
                            "_shards": {
                              "total": 5,
                              "successful": 5,
                              "skipped": 0,
                              "failed": 0
                            },
                            "hits": {
                                "total": {"value": 2, "relation": "eq"},
                                "hits": [
                                    {
                                        "_source": {
                                            "id": 1,
                                            "title": "Material 1",
                                            "description": "Description 1",
                                            "status": "APPROVED",
                                            "active": true,
                                            "avgGrade": 4.5,
                                            "author": {"id": 100, "username": "Author1"},
                                            "createdDate": "2024-12-07T10:00:00Z",
                                            "sectionId": 10,
                                            "categoryId": 20,
                                            "tags": [{"id": 1, "name": "Tag1"}]
                                        }
                                    },
                                    {
                                        "_source": {
                                            "id": 2,
                                            "title": "Material 2",
                                            "description": "Description 2",
                                            "status": "DRAFT",
                                            "isActive": false,
                                            "avgGrade": 3.0,
                                            "author": {"id": 200, "username": "Author2"},
                                            "createdDate": "2024-12-07T10:00:00Z",
                                            "sectionId": 11,
                                            "categoryId": 21,
                                            "tags": [{"id": 2, "name": "Tag2"}]
                                        }
                                    }
                                ]
                            }
                        }
                        """)))

        and: "Search criteria and pageable parameters"
        def searchParams = new MaterialControllerDefinition.MaterialFilterSearchParams(phrase: "Material")
        Pageable pageable = PageRequest.of(0, 10)

        when: "The search method is called"
        Page<MaterialPageable> result = materialSearchService.search(searchParams, pageable)

        then: "The results are correctly mapped"
        assertThat(result).isNotNull()
        assertThat(result.content).hasSize(2)

        with(result.content[0]) {
            assertThat(id).isEqualTo(1)
            assertThat(title).isEqualTo("Material 1")
            assertThat(description).isEqualTo("Description 1")
            assertThat(isActive).isTrue()
            assertThat(avgGrade).isEqualTo(4.5f)
            assertThat(author.username).isEqualTo("Author1")
            assertThat(sectionId).isEqualTo(10)
            assertThat(categoryId).isEqualTo(20)
            assertThat(tags).hasSize(1)
            assertThat(tags[0].name).isEqualTo("Tag1")
        }

        with(result.content[1]) {
            assertThat(id).isEqualTo(2)
            assertThat(title).isEqualTo("Material 2")
            assertThat(description).isEqualTo("Description 2")
            assertThat(isActive).isFalse()
            assertThat(avgGrade).isEqualTo(3.0f)
            assertThat(author.username).isEqualTo("Author2")
            assertThat(sectionId).isEqualTo(11)
            assertThat(categoryId).isEqualTo(21)
            assertThat(tags).hasSize(1)
            assertThat(tags[0].name).isEqualTo("Tag2")
        }

        and: "The correct request was sent to OpenSearch"
        WIREMOCK.verify(postRequestedFor(urlPathMatching("/materials/_search"))
                .withRequestBody(matching(".*")))
    }


    private static void setupWireMockStubs() {
        WIREMOCK.stubFor(head(urlPathEqualTo("/materials"))
                .willReturn(aResponse().withStatus(200)))

        WIREMOCK.stubFor(put(urlPathEqualTo("/materials"))
                .willReturn(aResponse().withStatus(201).withBody("""
                {
                    "acknowledged": true,
                    "shards_acknowledged": true,
                    "index": "materials"
                }
                """)))

        WIREMOCK.stubFor(head(urlPathEqualTo("/materials/_alias/materials-alias"))
                .willReturn(aResponse().withStatus(404)))

        WIREMOCK.stubFor(post(urlPathEqualTo("/_aliases"))
                .willReturn(aResponse().withStatus(200).withBody("""
                {
                    "acknowledged": true
                }
                """)))
    }

}
