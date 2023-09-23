package pl.sknikod.kodemy.infrastructure.search;

import lombok.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Value
public class SearchCriteria {
    SearchCriteria.ContentField contentField;
    List<PhraseField> phraseFields;
    List<SearchCriteria.RangeField<?>> rangeFields;
    Pageable page;

    @AllArgsConstructor
    @Getter(value = AccessLevel.PROTECTED)
    private static class Field {
        String name;
    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    public static class RangeField<T> extends Field {
        T from;
        T to;

        public RangeField(String name, T from, T to) {
            super(name);
            this.from = from;
            this.to = to;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Value
    public static class PhraseField extends Field {
        String value;
        boolean wildcard;
        boolean mustNot;

        public PhraseField(String name, String value, boolean wildcard, boolean mustNot) {
            super(name);
            this.value = value;
            this.wildcard = wildcard;
            this.mustNot = mustNot;
        }
    }

    @Value
    public static class ContentField {
        String value;
    }
}
