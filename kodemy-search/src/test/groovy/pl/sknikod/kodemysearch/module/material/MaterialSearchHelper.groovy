package pl.sknikod.kodemysearch.module.material

import pl.sknikod.kodemysearch.configuration.LogbookConfiguration
import pl.sknikod.kodemysearch.configuration.OpenSearchConfiguration
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialSearchService
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialSearchService$MaterialSearchMapperImpl
import pl.sknikod.kodemysearch.infrastructure.store.MaterialSearchStore
import pl.sknikod.kodemysearch.util.opensearch.OpenSearchClientEnhanced

class MaterialSearchHelper {

    static MaterialSearchService createMaterialSearchService(String host = "http://localhost:9999",
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

        def materialSearchStore = new MaterialSearchStore(openSearchClient)

        return new MaterialSearchService(
                materialSearchStore,
                new MaterialSearchService$MaterialSearchMapperImpl()
        )
    }
}
