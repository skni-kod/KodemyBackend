package pl.sknikod.kodemysearch.module.material

import pl.sknikod.kodemysearch.SuperclassSpec
import pl.sknikod.kodemysearch.infrastructure.module.ModuleBeanConfiguration
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialAddUpdateService
import pl.sknikod.kodemysearch.infrastructure.module.material.model.MaterialIndexData
import pl.sknikod.kodemysearch.infrastructure.module.material.model.MaterialIndexEvent
import pl.sknikod.kodemysearch.infrastructure.module.material.model.MaterialStatus
import spock.lang.Subject

import java.time.Instant

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static org.assertj.core.api.Assertions.assertThat
import static pl.sknikod.kodemysearch.infrastructure.module.material.model.MaterialIndexEvent.*
import static pl.sknikod.kodemysearch.module.material.MaterialHelper.defaultMaterialAddUpdateService

class MaterialAddUpdateServiceWiremockSpec extends SuperclassSpec {

    @Subject
    MaterialAddUpdateService materialAddUpdateService = defaultMaterialAddUpdateService()

    def setupSpec() {
        WIREMOCK.start()
        MaterialHelper.setupWireMockStubs(WIREMOCK)
    }

    def cleanupSpec() {
        WIREMOCK.stop()
    }

    def "should index material"() {
        given: "Default MaterialIndexEvent"
            def materialIndexEvent = defaultMaterialIndexEvent()
        and: "A mocked OpenSearch response"
            def objectMapper = new ModuleBeanConfiguration().objectMapper()
            indexMaterialWiremockStubForMaterialIndexEvent(materialIndexEvent)
        when:
            def id = materialAddUpdateService.index(objectMapper.writeValueAsString(materialIndexEvent))
        then:
            assertThat(id).isEqualTo("5")
    }

    def "should reindex material"() {
        given: "Default MaterialIndexEvent"
            def materialIndexEvent = defaultMaterialIndexEvent()
        and: "A mocked OpenSearch response"
            def objectMapper = new ModuleBeanConfiguration().objectMapper()
            reindexMaterialWiremockStubForMaterialIndexEvent(materialIndexEvent)
        when:
            def id = materialAddUpdateService.reindex(objectMapper.writeValueAsString(materialIndexEvent))
        then:
            assertThat(id).isEqualTo("5")
    }


    private static MaterialIndexEvent defaultMaterialIndexEvent() {
        new MaterialIndexEvent(
                id: 5L,
                title: "Sample Title",
                description: "This is a description",
                status: "APPROVED",
                isActive: true,
                avgGrade: 4.5,
                author: new Author(
                        id: 1001L,
                        username: "author_name"
                ),
                createdDate: Instant.parse("2023-12-10T10:15:30Z"),
                sectionId: 101L,
                categoryId: 202L,
                tags: [
                        new Tag(id: 1L, name: "Java"),
                        new Tag(id: 2L, name: "Programming")
                ]
        )
    }

    private static void indexMaterialWiremockStubForMaterialIndexEvent(MaterialIndexEvent event) {
        def tagsJson = event.tags.collect { tag ->
            """{ "id": ${tag.id}, "name": "${tag.name}" }"""
        }.join(", ")

        WIREMOCK.stubFor(put(urlPathEqualTo("/materials/_doc/${event.id}"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        {
                            "_index": "material-index",
                            "_id": "${event.id}",
                            "_version": 1,
                            "_primary_term": 1,
                            "_seq_no": 1,
                            "result": "created",
                            "_shards": {
                                "total": 1,
                                "successful": 1,
                                "failed": 0
                            },
                            "data": {
                                "id": ${event.id},
                                "title": "${event.title}",
                                "description": "${event.description}",
                                "status": "${event.status}",
                                "isActive": ${event.active},
                                "avgGrade": ${event.avgGrade},
                                "author": {
                                    "id": ${event.author.id},
                                    "username": "${event.author.username}"
                                },
                                "createdDate": "${event.createdDate}",
                                "sectionId": ${event.sectionId},
                                "categoryId": ${event.categoryId},
                                "tags": [ $tagsJson ]
                            }
                        }
                    """)
                )
        )
    }

    private static void reindexMaterialWiremockStubForMaterialIndexEvent(MaterialIndexEvent event) {
        def tagsJson = event.tags.collect { tag ->
            """{ "id": ${tag.id}, "name": "${tag.name}" }"""
        }.join(", ")

        WIREMOCK.stubFor(post(urlPathEqualTo("/materials/_update/${event.id}"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        {
                            "_index": "material-index",
                            "_id": "${event.id}",
                            "_version": 2,
                            "_primary_term": 1,
                            "_seq_no": 1,
                            "result": "updated",
                            "_shards": {
                                "total": 1,
                                "successful": 1,
                                "failed": 0
                            },
                            "data": {
                                "id": ${event.id},
                                "title": "${event.title}",
                                "description": "${event.description}",
                                "status": "${event.status}",
                                "isActive": ${event.active},
                                "avgGrade": ${event.avgGrade},
                                "author": {
                                    "id": ${event.author.id},
                                    "username": "${event.author.username}"
                                },
                                "createdDate": "${event.createdDate}",
                                "sectionId": ${event.sectionId},
                                "categoryId": ${event.categoryId},
                                "tags": [ $tagsJson ]
                            }
                        }
                    """)
                )
        )
    }


    private static void updateStatusWiremockStubForId(String id) {
        WIREMOCK.stubFor(post(urlPathEqualTo("/materials/_update/$id"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "_index": "material-index",
                                "_id": "$id",
                                "_version": 1,
                                "_primary_term": 5,
                                "_seq_no": 5,
                                "result": "updated",
                                "_shards": {
                                    "total": 1,
                                    "successful": 1,
                                    "failed": 0
                                },
                                "data": {
                                    "id": $id,
                                    "title": "Sample Title",
                                    "description": "Sample description of the material.",
                                    "status": "BANNED",
                                    "isActive": true,
                                    "avgGrade": 4.5,
                                    "author": {
                                        "id": 1001,
                                        "username": "author_name"
                                    },
                                    "createdDate": "2023-12-10T10:15:30Z",
                                    "sectionId": 101,
                                    "categoryId": 202,
                                    "tags": [
                                        {
                                            "id": 1,
                                            "name": "Java"
                                        },
                                        {
                                            "id": 2,
                                            "name": "Programming"
                                        }
                                    ]
                                }
                            }
                             """)))
    }

}
