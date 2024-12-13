package pl.sknikod.kodemysearch.module.material

import pl.sknikod.kodemysearch.SuperclassSpec
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialUpdateStatusService
import spock.lang.Subject

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static org.assertj.core.api.Assertions.assertThat
import static pl.sknikod.kodemysearch.module.material.MaterialHelper.defaultMaterialUpdateStatusService

class MaterialUpdateStatusServiceWiremockSpec extends SuperclassSpec {

    @Subject
    MaterialUpdateStatusService materialUpdateStatusService = defaultMaterialUpdateStatusService()

    def setupSpec() {
        WIREMOCK.start()
        MaterialHelper.setupWireMockStubs(WIREMOCK)
    }

    def cleanupSpec() {
        WIREMOCK.stop()
    }

    def "should return correct id when update status"() {
        given: "A mocked OpenSearch response"
            updateStatusWiremockStubForId("5")
        when:
            def id = materialUpdateStatusService.updateStatus('{"id":5,"status":"BANNED"}')
        then:
            assertThat(id).isEqualTo("5")
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
