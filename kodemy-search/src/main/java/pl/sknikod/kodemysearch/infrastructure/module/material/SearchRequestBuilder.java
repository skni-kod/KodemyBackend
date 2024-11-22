package pl.sknikod.kodemysearch.infrastructure.module.material;

import io.jsonwebtoken.lang.Assert;
import io.jsonwebtoken.lang.Strings;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.SortOptions;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.query_dsl.*;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class SearchRequestBuilder {
    private static final int MAX_INTEGER = 2147483647;

    private final String indexName;

    private final int from;
    private final int size;
    private final List<SortOptions> sortOptions = new ArrayList<>();

    private final List<Query> mustQueries = new ArrayList<>();
    private final List<Query> shouldQueries = new ArrayList<>();
    private final List<Query> mustNotQueries = new ArrayList<>();

    public SearchRequestBuilder(String indexName, SearchCriteria criteria) {
        Assert.notNull(indexName, "indexName cannot be null");
        Assert.notNull(criteria, "criteria cannot be null");

        this.indexName = indexName;

        Pageable pageable = criteria.getPageable();
        this.from = pageable.getPageNumber() * pageable.getPageSize();
        this.size = pageable.getPageSize();

        with(pageable);
        any(criteria.getAnyPhrase());
        criteria.getPhraseFields().forEach(this::append);
        criteria.getRangeFields().forEach(this::append);
    }

    private void with(Pageable pageable) {
        pageable.getSort().forEach(order -> {
            sortOptions.add(new SortOptions.Builder()
                    .field(builder -> builder
                            .field(order.getProperty())
                            .order(order.isAscending() ? SortOrder.Asc : SortOrder.Desc))
                    .build()
            );
        });
    }

    private void any(String anyPhrase) {
        if (!Strings.hasText(anyPhrase)) {
            return;
        }
        var query = WildcardQuery.of(w -> w
                .field("title")
                .caseInsensitive(false)
                .value("*" + anyPhrase.toLowerCase() + "*")
        ).toQuery();
        mustQueries.add(query);
    }

    private void append(SearchCriteria.PhraseField field) {
        if (field == null || !Strings.hasText(field.getValue())) {
            return;
        }
        var query = field.isWildcard()
                ? WildcardQuery.of(w -> w.field(field.getName()).value(field.getValue())).toQuery()
                : MatchPhraseQuery.of(m -> m.field(field.getName()).query(field.getValue())).toQuery();
        (field.isMustNot() ? mustNotQueries : mustQueries).add(query);
    }

    private void append(SearchCriteria.RangeField<?> field) {
        if (field == null || (field.getFrom() == null && field.getTo() == null)) {
            return;
        }
        RangeQuery.Builder rangeQueryBuilder = new RangeQuery.Builder().field(field.getName());
        if (field.getFrom() != null) rangeQueryBuilder.gte(JsonData.of(field.getFrom()));
        if (field.getTo() != null) rangeQueryBuilder.lte(JsonData.of(field.getTo()));
        mustQueries.add(rangeQueryBuilder.build().toQuery());
    }

    public SearchRequest build() {
        return new SearchRequest.Builder()
                .index(indexName)
                .query(query -> query.bool(b -> b
                        .must(mustQueries)
                        .should(shouldQueries)
                        .mustNot(mustNotQueries)
                ))
                .from(from)
                .size(size)
                .sort(sortOptions)
                .trackTotalHits(builder -> builder.count(MAX_INTEGER))
                .build();
    }
}
