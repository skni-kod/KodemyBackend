package pl.sknikod.kodemysearch.infrastructure.search;

import org.apache.commons.lang3.StringUtils;
import org.opensearch.index.query.*;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.sort.FieldSortBuilder;
import org.opensearch.search.sort.SortOrder;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SearchBuilder {
    private final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    private final BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

    public SearchBuilder(SearchCriteria criteria) {
        withContentPhrase(criteria.getContentField());
        withPhraseFields(criteria.getPhraseFields());
        withRangeFields(criteria.getRangeFields());
        withPage(criteria.getPage());
    }

    private void withContentPhrase(SearchCriteria.ContentField contentField) {
        Function<String, QueryStringQueryBuilder> queryStringBuilderFunction = fieldValue -> QueryBuilders.queryStringQuery(fieldValue).type(MultiMatchQueryBuilder.Type.PHRASE).escape(true).lenient(true);

        Optional.ofNullable(contentField)
                .map(SearchCriteria.ContentField::getValue)
                .filter(StringUtils::isNotBlank)
                .ifPresent(phrase -> {
                    QueryStringQueryBuilder stringQueryBuilder = queryStringBuilderFunction.apply(phrase);
                    this.boolQueryBuilder.filter()
                            .add(QueryBuilders.boolQuery().should(stringQueryBuilder));
                });
    }

    private void withPhraseFields(List<SearchCriteria.PhraseField> phraseFields) {
        Function<SearchCriteria.PhraseField, QueryBuilder> queryBuilderFunction = phraseField -> phraseField.isWildcard() ?
                QueryBuilders.wildcardQuery(phraseField.getName(), phraseField.getValue()) :
                QueryBuilders.matchPhraseQuery(phraseField.getName(), phraseField.getValue());

        Optional.ofNullable(phraseFields)
                .stream()
                .flatMap(Collection::stream)
                .map(phraseField -> {
                    QueryBuilder queryBuilder = queryBuilderFunction.apply(phraseField);
                    return !phraseField.isMustNot() ? queryBuilder : QueryBuilders.boolQuery().mustNot(queryBuilder);
                })
                .forEach(boolQueryBuilder.filter()::add);
    }

    private void withRangeFields(List<SearchCriteria.RangeField<?>> rangeFields) {
        Optional.ofNullable(rangeFields)
                .stream()
                .flatMap(Collection::stream)
                .map(rangeField -> QueryBuilders.rangeQuery(rangeField.getName())
                        .from(rangeField.getFrom())
                        .to(rangeField.getTo()))
                .forEach(boolQueryBuilder.filter()::add);
    }

    private void withPage(Pageable page) {
        BiFunction<String, SortOrder, FieldSortBuilder> fieldSortBuilderFunction = (fieldName, sortOrder) ->
                new FieldSortBuilder(fieldName).order(sortOrder).unmappedType("boolean");

        Optional.ofNullable(page).ifPresent(pageable -> {
            sourceBuilder.size(pageable.getPageSize()).from(page.getPageNumber() * page.getPageSize());
            pageable.getSort().forEach(order -> sourceBuilder.sort(fieldSortBuilderFunction.apply(
                    order.getProperty(),
                    SortOrder.fromString(order.getDirection().name())
            )));
        });
    }

    SearchSourceBuilder toSearchSourceBuilder() {
        return sourceBuilder.query(boolQueryBuilder).trackTotalHits(true);
    }
}
