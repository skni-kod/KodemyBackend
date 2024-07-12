package pl.sknikod.kodemysearch.infrastructure.module.material;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Getter
public class SearchCriteria {
    ContentField contentField;
    List<PhraseField> phraseFields = new ArrayList<>();
    List<RangeField<?>> rangeFields = new ArrayList<>();
    Pageable pageable;

    public SearchCriteria(@NonNull String content, @NonNull Pageable pageable) {
        this.contentField = new ContentField(content);
        this.pageable = pageable;
    }

    public void addPhraseField(PhraseField field) {
        phraseFields.add(field);
    }

    public void addRangeField(RangeField<?> field) {
        rangeFields.add(field);
    }

    @Getter
    private abstract static class Field {
        private final String name;

        public Field(String name) {
            this.name = name;
        }
    }

    @Getter
    public static class RangeField<T> extends Field {
        private final T from;
        private final T to;

        public RangeField(String name, T from, T to) {
            super(name);
            this.from = from;
            this.to = to;
        }
    }

    @Getter
    public static class PhraseField extends Field {
        private final String value;
        private final boolean wildcard;
        private final boolean mustNot;

        public PhraseField(String name, String value, boolean wildcard, boolean mustNot) {
            super(name);
            this.value = value;
            this.wildcard = wildcard;
            this.mustNot = mustNot;
        }
    }

    @Getter
    public static class ContentField {
        private final String value;

        public ContentField(String value) {
            this.value = value;
        }
    }
}
