package pl.sknikod.kodemysearch.infrastructure.module.material;

import io.jsonwebtoken.lang.Assert;
import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.SortOptions;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.query_dsl.*;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchBuilder {
    private int from = 0;
    private int size = 10;
    private final List<SortOptions> sortOptions = new ArrayList<>();
    private final List<Query> mustQueries = new ArrayList<>();
    private final List<Query> shouldQueries = new ArrayList<>();
    private final List<Query> mustNotQueries = new ArrayList<>();

    private SearchBuilder(SearchCriteria criteria) {
        withContentPhrase(criteria.getContentField());
        withPhraseFields(criteria.getPhraseFields());
        withRangeFields(criteria.getRangeFields());
        withPageable(criteria.getPageable());
    }

    public static SearchBuilder from(SearchCriteria criteria) {
        return new SearchBuilder(criteria);
    }

    private void withContentPhrase(SearchCriteria.ContentField field) {
        Optional.ofNullable(field)
                .map(SearchCriteria.ContentField::getValue)
                .filter(StringUtils::isNotBlank)
                .ifPresent(phrase -> {
                    var query = MatchQuery.of(m -> m
                            .field("content").query(FieldValue.of(phrase))
                    ).toQuery();
                    mustQueries.add(query);
                });
    }

    private void withPhraseFields(List<SearchCriteria.PhraseField> fields) {
        fields.forEach(field -> {
            MatchQuery.Builder matchQueryBuilder = new MatchQuery.Builder()
                    .field(field.getName())
                    .query(FieldValue.of(field.getValue()));
            if (field.isMustNot()) {
                mustNotQueries.add(matchQueryBuilder.build().toQuery());
            } else if (field.isWildcard()) {
                var query = WildcardQuery.of(w -> w.field(field.getName()).value(field.getValue())).toQuery();
                shouldQueries.add(query);
            } else {
                mustQueries.add(matchQueryBuilder.build().toQuery());
            }
        });
    }

    private void withRangeFields(List<SearchCriteria.RangeField<?>> fields) {
        fields.forEach(field -> {
            RangeQuery.Builder rangeQueryBuilder = new RangeQuery.Builder().field(field.getName());
            if (field.getFrom() != null) rangeQueryBuilder.gte(JsonData.of(field.getFrom()));
            if (field.getTo() != null) rangeQueryBuilder.lte(JsonData.of(field.getTo()));
            mustQueries.add(rangeQueryBuilder.build().toQuery());
        });
    }

    private void withPageable(Pageable pageable) {
        this.from = pageable.getPageNumber() * pageable.getPageSize();
        this.size = pageable.getPageSize();
        pageable.getSort().forEach(order -> {
            var sortOption = SortOptions.of(s -> s.field(f -> f
                    .field(order.getProperty())
                    .order(order.isAscending() ? SortOrder.Asc : SortOrder.Desc)
            ));
            sortOptions.add(sortOption);
        });
    }

    public SearchRequest toSearchRequest(String indexName) {
        Assert.notNull(indexName, "indexName cannot be null");
        Query query = new BoolQuery.Builder()
                .must(mustQueries)
                .should(shouldQueries)
                .mustNot(mustQueries)
                .build().toQuery();
        return new SearchRequest.Builder()
                .index(indexName)
                .query(query)
                .from(from).size(size)
                .sort(sortOptions)
                .build();
    }
}
