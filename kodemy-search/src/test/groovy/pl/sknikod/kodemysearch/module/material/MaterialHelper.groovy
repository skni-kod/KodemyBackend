package pl.sknikod.kodemysearch.module.material

import com.github.tomakehurst.wiremock.WireMockServer
import pl.sknikod.kodemysearch.configuration.LogbookConfiguration
import pl.sknikod.kodemysearch.configuration.OpenSearchConfiguration
import pl.sknikod.kodemysearch.infrastructure.module.ModuleBeanConfiguration
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialSearchService
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialSearchService$MaterialSearchMapperImpl
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialUpdateStatusService
import pl.sknikod.kodemysearch.infrastructure.store.MaterialSearchStore
import pl.sknikod.kodemysearch.util.opensearch.OpenSearchClientEnhanced

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.head
import static com.github.tomakehurst.wiremock.client.WireMock.head
import static com.github.tomakehurst.wiremock.client.WireMock.post
import static com.github.tomakehurst.wiremock.client.WireMock.put
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo

class MaterialHelper {

    static MaterialSearchService defaultMaterialSearchService() {
        return new MaterialSearchService(
                createMaterialSearchStore(), new MaterialSearchService$MaterialSearchMapperImpl()
        )
    }

    static MaterialUpdateStatusService defaultMaterialUpdateStatusService() {
        return new MaterialUpdateStatusService(
                createMaterialSearchStore(), new ModuleBeanConfiguration().objectMapper()
        )
    }

    private static MaterialSearchStore createMaterialSearchStore(String host = "http://localhost:9999",
                                                                 String username = "admin",
                                                                 String password = "admin",
                                                                 String indexName = "materials",
                                                                 String aliasName = "materials-alias") {
        def properties = new OpenSearchConfiguration.OpenSearchProperties(
                host: host,
                username: username,
                password: password,
                indices: [
                        "materials": new OpenSearchClientEnhanced.Index(
                                name: indexName,
                                alias: aliasName
                        )
                ]
        )

        def openSearchClient = new OpenSearchConfiguration().openSearchClient(
                properties,
                new LogbookConfiguration().logbook()
        )

        return new MaterialSearchStore(openSearchClient)
    }

    static void setupWireMockStubs(WireMockServer WIREMOCK) {
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
