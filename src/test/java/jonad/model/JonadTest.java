package jonad.model;

import lombok.SneakyThrows;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class JonadTest
{
    private static final String S_VAL_1 = "1";

    private static final String S_VAL_2 = "2";

    private static final Integer I_VAL_1 = 1;

    private static final String CALLED_FUNCTION = "Called function";

    private static final Exception EXCEPTION = new Exception("");

    private static final Consumer<String> FAIL_IF_CALLED = val -> Assertions.fail(CALLED_FUNCTION);

    private static final Consumer<Exception> ERR_FAIL_IF_CALLED = err -> Assertions.fail(CALLED_FUNCTION);

    @Nested
    @DisplayName("Or empty tests")
    class OrEmpty
    {
        @Test
        void itCreatesFromOptional()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_1), Jonad.orEmpty(Optional.of(S_VAL_1)));
        }

        @Test
        void itCreatesFromOptionalWhenValueNotPresent()
        {
            Assertions.assertEquals(Jonad.empty(), Jonad.orEmpty(Optional.empty()));
        }

        @Test
        void itCreatesFromValue()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_1), Jonad.orEmpty(S_VAL_1));
        }

        @Test
        void itCreatesEmptyFromNull()
        {
            Assertions.assertEquals(Jonad.empty(), Jonad.orEmpty(null));
        }
    }

    @Nested
    @DisplayName("From Supplier tests")
    class FromSupplier
    {
        @Test
        void itCreatesFromSupplier()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_1), Jonad.fromSupplier(() -> S_VAL_1));
        }

        @Test
        void itCreatesEmptyFromEmptySupplier()
        {
            Assertions.assertEquals(Jonad.empty(), Jonad.fromSupplier(() -> null));
        }
    }

    @Nested
    @DisplayName("To Optional tests")
    class ToOptional
    {
        @Test
        void itTransformsToOptional()
        {
            Assertions.assertEquals(Optional.of(S_VAL_1), Jonad.of(S_VAL_1).toOptional());
        }

        @Test
        void itTransformsToStreamWhenValueNotPresent()
        {
            Assertions.assertEquals(Optional.empty(), Jonad.empty().toOptional());
        }
    }

    @Nested
    @DisplayName("Stream tests")
    class StreamTests
    {
        @Test
        void itTransformsToStream()
        {
            Assertions.assertIterableEquals(Stream.of(S_VAL_1).collect(Collectors.toList()),
                    Jonad.of(S_VAL_1).stream().collect(Collectors.toList()));
        }

        @Test
        void itTransformsToStreamWhenValueNotPresent()
        {
            Assertions.assertEquals(Collections.emptyList(), Jonad.empty().stream().collect(Collectors.toList()));
        }
    }

    @Nested
    @DisplayName("Map tests")
    class Map
    {
        @Test
        void itMaps()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_2), Jonad.of(S_VAL_1).map(res -> S_VAL_2));
        }

        @Test
        void itMapsTypes()
        {
            Assertions.assertEquals(Jonad.of(I_VAL_1), Jonad.of(S_VAL_1).map(res -> I_VAL_1));
        }

        @Test
        void itDoesntMapWhenNoValuePresent()
        {
            Assertions.assertEquals(Jonad.empty(), Jonad.empty().map(res -> S_VAL_2));
        }
    }

    @Nested
    @DisplayName("Flat Map tests")
    class FlatMap
    {
        @Test
        void itFlatMaps()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_2), Jonad.of(S_VAL_1).flatMap(res -> Jonad.of(S_VAL_2)));
        }

        @Test
        void itFlatMapsTypes()
        {
            Assertions.assertEquals(Jonad.of(I_VAL_1), Jonad.of(S_VAL_1).flatMap(res -> Jonad.of(I_VAL_1)));
        }

        @Test
        void itFlatMapsToEmpty()
        {
            Assertions.assertEquals(Jonad.empty(), Jonad.of(S_VAL_1).flatMap(res -> Jonad.empty()));
        }

        @Test
        void itDoesntFlatMapWhenNoValuePresent()
        {
            Assertions.assertEquals(Jonad.empty(), Jonad.empty().flatMap(res -> Jonad.of(S_VAL_1)));
        }
    }

    @Nested
    @DisplayName("Get Or Null tests")
    class GetOrNull
    {
        @Test
        void itGetsWhenJonadHasValue()
        {
            Assertions.assertEquals(S_VAL_1, Jonad.of(S_VAL_1).getOrNull());
        }

        @Test
        void itGetsNullWhenNoValuePresent()
        {
            Assertions.assertNull(Jonad.empty().getOrNull());
        }
    }

    @Nested
    @DisplayName("Get Or Optional Tests")
    class GetOrOptional
    {
        @Test
        void itGetsWhenJonadHasValue()
        {
            Assertions.assertEquals(Optional.of(S_VAL_1), Jonad.of(S_VAL_1).toOptional());
        }

        @Test
        void itGetsOptionalEmptyWhenNoValuePresent()
        {
            Assertions.assertEquals(Optional.empty(), Jonad.empty().toOptional());
        }
    }

    @Nested
    @DisplayName("Is Empty tests")
    class IsEmpty
    {
        @Test
        void itIsEmpty()
        {
            Assertions.assertTrue(Jonad.empty().isEmpty());
        }

        @Test
        void itIsNotEmpty()
        {
            Assertions.assertFalse(Jonad.of(S_VAL_1).isEmpty());
        }
    }

    @Nested
    @DisplayName("Get or default tests")
    class GetOrDefault
    {
        @Test
        void itGetsWhenValuePresent()
        {
            Assertions.assertEquals(S_VAL_1, Jonad.of(S_VAL_1).getOrDefault(S_VAL_2));
        }

        @Test
        void itGetsDefaultWhenValueNotPresent()
        {
            Assertions.assertEquals(S_VAL_2, Jonad.empty().getOrDefault(S_VAL_2));

        }
    }

    @Nested
    @DisplayName("Or Else Get tests")
    class OrElseGet
    {
        @Test
        void itGetsWhenValuePresent()
        {
            Assertions.assertEquals(S_VAL_1, Jonad.of(S_VAL_1).orElseGet(() -> S_VAL_2));
        }

        @Test
        void itGetsFromSupplierWhenValueNotPresent()
        {
            Assertions.assertEquals(S_VAL_2, Jonad.empty().orElseGet(() -> S_VAL_2));
        }
    }

    @Nested
    @DisplayName("Or Else Throw tests")
    class OrElseThrow
    {
        @Test
        void itThrowsWhenValueNotPresent()
        {
            Assertions.assertThrows(Exception.class, () -> Jonad.empty().orElseThrow(() -> EXCEPTION));
        }

        @Test
        @SneakyThrows
        void itGetsValueWhenPresent()
        {
            Assertions.assertEquals(S_VAL_1, Jonad.of(S_VAL_1).orElseThrow(() -> EXCEPTION));
        }
    }

    @Nested
    @DisplayName("Filter tests")
    class Filter
    {
        @Test
        void itRetainsValuesWhenFilterPredicateTrue()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_1), Jonad.of(S_VAL_1).filter(val -> true));
        }

        @Test
        void itDoesntInvokeFilterWhenNoValuePresent()
        {
            Assertions.assertEquals(Jonad.empty(), Jonad.empty()
                    .filter(val ->
                    {
                        Assertions.fail("Invoked filter predicate");
                        return false;
                    }));
        }

        @Test
        void itRetainsValuesWhenChainedFilterPredicateTrue()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_1), Jonad.of(S_VAL_1)
                    .filter(Predicate.not(String::isEmpty))
                    .filter(val -> true));
        }

        @Test
        void itReturnsEmptyWhenFilterPredicateFalse()
        {
            Assertions.assertEquals(Jonad.empty(), Jonad.of(S_VAL_1).filter(val -> false));
        }
    }

    @Nested
    @DisplayName("Filter When tests")
    class FilterWhen
    {
        @Test
        void itRetainsValuesWhenFilterPredicateTrue()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_1), Jonad.of(S_VAL_1).filterWhen(val -> Jonad.of(Boolean.TRUE)));
        }

        @Test
        void itDoesntInvokeFilterWhenNoValuePresent()
        {
            Assertions.assertEquals(Jonad.empty(), Jonad.empty().filterWhen(val ->
            {
                Assertions.fail(CALLED_FUNCTION);
                return Jonad.of(Boolean.TRUE);
            }));
        }


        @Test
        void itRetainsValuesWhenChainedFilterPredicateTrue()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_1), Jonad.of(S_VAL_1)
                    .filterWhen(val -> Jonad.of(I_VAL_1 == 1))
                    .filterWhen(val -> Jonad.of(Boolean.TRUE)));
        }

        @Test
        void itReturnsEmptyWhenFunctionMonadEmpty()
        {
            Assertions.assertEquals(Jonad.empty(), Jonad.of(S_VAL_1).filterWhen(val -> Jonad.empty()));
        }

        @Test
        void itReturnsEmptyWhenFilterPredicateFalse()
        {
            Assertions.assertEquals(Jonad.empty(), Jonad.of(S_VAL_1).filterWhen(val -> Jonad.of(Boolean.FALSE)));
        }
    }

    @Nested
    @DisplayName("Do if empty tests")
    class DoIfEmpty
    {
        @Test
        void itCallsFunctionWhenEmpty()
        {
            final Mutable<String> mutable = new MutableObject<>(S_VAL_1);

            Assertions.assertEquals(Jonad.empty(), Jonad.empty().doIfEmpty(val -> mutable.setValue(S_VAL_2)));
            Assertions.assertEquals(S_VAL_2, mutable.getValue());
        }

        @Test
        void itDoesntCallFunctionWhenNotEmpty()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_1), Jonad.of(S_VAL_1).doIfEmpty(FAIL_IF_CALLED));
        }
    }

    @Nested
    @DisplayName("Do if present tests")
    class DoIfPresent
    {
        @Test
        void itCallsFunctionWhenValuePresent()
        {
            final Mutable<String> mutable = new MutableObject<>(S_VAL_1);

            Assertions.assertEquals(Jonad.of(S_VAL_1), Jonad.of(S_VAL_1).doIfPresent(val -> mutable.setValue(S_VAL_2)));
            Assertions.assertEquals(S_VAL_2, mutable.getValue());
        }

        @Test
        void itDoesntCallFunctionWhenEmpty()
        {
            Assertions.assertEquals(Jonad.empty(), Jonad.empty().doIfPresent(FAIL_IF_CALLED));
        }
    }

    @Nested
    @DisplayName("Do on error tests")
    class DoOnError
    {
        @Test
        void itCallsFunctionWhenErrorPresent()
        {
            final Mutable<String> mutable = new MutableObject<>(S_VAL_1);

            Assertions.assertEquals(Jonad.of(EXCEPTION), Jonad.of(EXCEPTION).doOnError(err -> mutable.setValue(S_VAL_2)));
            Assertions.assertEquals(S_VAL_2, mutable.getValue());
        }

        @Test
        void itDoesntCallFunctionWhenErrorNotPresent()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_1), Jonad.of(S_VAL_1).doOnError(ERR_FAIL_IF_CALLED));
        }

        @Test
        void itDoesntCallFunctionWhenEmpty()
        {
            Assertions.assertEquals(Jonad.empty(), Jonad.empty().doOnError(ERR_FAIL_IF_CALLED));
        }
    }

    @Nested
    @DisplayName("Do on error matching class tests")
    class DoOnErrorMatchingClass
    {
        @Test
        void itCallsFunctionWhenErrorPresentAndMatchesClass()
        {
            final Mutable<String> mutable = new MutableObject<>(S_VAL_1);

            Assertions.assertEquals(Jonad.of(EXCEPTION), Jonad.of(EXCEPTION)
                    .doOnError(Exception.class, err -> mutable.setValue(S_VAL_2)));
            Assertions.assertEquals(S_VAL_2, mutable.getValue());
        }

        @Test
        void itDoesntCallFunctionWhenErrorPresentButClassDoesntMatch()
        {
            Assertions.assertEquals(Jonad.of(EXCEPTION), Jonad.of(EXCEPTION)
                    .doOnError(RuntimeException.class, ERR_FAIL_IF_CALLED));
        }

        @Test
        void itDoesntCallFunctionWhenErrorNotPresent()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_1), Jonad.of(S_VAL_1).doOnError(Exception.class, ERR_FAIL_IF_CALLED));
        }

        @Test
        void itDoesntCallFunctionWhenEmpty()
        {
            Assertions.assertEquals(Jonad.empty(), Jonad.empty().doOnError(Exception.class, ERR_FAIL_IF_CALLED));
        }
    }

    @Nested
    @DisplayName("Do on error matching tests")
    class DoOnErrorMatching
    {
        @Test
        void itCallsFunctionWhenErrorPresentAndMatching()
        {
            final Mutable<String> mutable = new MutableObject<>(S_VAL_1);

            Assertions.assertEquals(Jonad.of(EXCEPTION), Jonad.of(EXCEPTION)
                    .doOnErrorMatching(throwable -> true, err -> mutable.setValue(S_VAL_2)));
            Assertions.assertEquals(S_VAL_2, mutable.getValue());
        }

        @Test
        void itDoesntCallFunctionWhenErrorPresentButNotMatching()
        {
            Assertions.assertEquals(Jonad.of(EXCEPTION), Jonad.of(EXCEPTION)
                    .doOnErrorMatching(throwable -> false, ERR_FAIL_IF_CALLED));
        }

        @Test
        void itDoesntCallFunctionWhenErrorNotPresent()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_1), Jonad.of(S_VAL_1)
                    .doOnErrorMatching(throwable -> true, ERR_FAIL_IF_CALLED));
        }

        @Test
        void itDoesntCallFunctionWhenEmpty()
        {
            Assertions.assertEquals(Jonad.empty(), Jonad.empty()
                    .doOnErrorMatching(throwable -> true, ERR_FAIL_IF_CALLED));
        }
    }

    @Nested
    @DisplayName("On error map tests")
    class OnErrorMap
    {
        @Test
        void itMapsWhenErrorPresent()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_2), Jonad.of(EXCEPTION).onErrorMap(err -> S_VAL_2));
        }

        @Test
        void itDoesntMapWhenErrorNotPresent()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_1), Jonad.of(S_VAL_1).onErrorMap(err -> S_VAL_2));
        }

        @Test
        void itDoesntMapWhenEmpty()
        {
            Assertions.assertEquals(Jonad.empty(), Jonad.empty().onErrorMap(err -> S_VAL_2));
        }
    }

    @Nested
    @DisplayName("On error map matching tests")
    class OnErrorMapMatching
    {
        @Test
        void itMapsWhenErrorPresentAndMatching()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_2), Jonad.of(EXCEPTION)
                    .onErrorMapMatching(e -> true, err -> S_VAL_2));
        }

        @SneakyThrows
        @Test
        void itDoesntMapWhenErrorPresentButNotMatching()
        {
            Assertions.assertEquals(Jonad.of(EXCEPTION), Jonad.of(EXCEPTION)
                    .onErrorMapMatching(e -> false, err -> S_VAL_2));
        }

        @Test
        void itDoesntMapWhenErrorNotPresent()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_1), Jonad.of(S_VAL_1).onErrorMapMatching(e -> true, err -> S_VAL_2));
        }

        @Test
        void itDoesntMapWhenEmpty()
        {
            Assertions.assertEquals(Jonad.empty(), Jonad.empty().onErrorMapMatching(e -> true, err -> S_VAL_2));
        }
    }
    @Nested
    @DisplayName("On error flat map tests")
    class OnErrorFlatMap
    {
        @Test
        void itMapsWhenErrorPresent()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_2), Jonad.of(EXCEPTION).onErrorFlatMap(err -> Jonad.of(S_VAL_2)));
        }

        @Test
        void itDoesntMapWhenErrorNotPresent()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_1), Jonad.of(S_VAL_1).onErrorFlatMap(err -> Jonad.of(S_VAL_2)));
        }

        @Test
        void itDoesntMapWhenEmpty()
        {
            Assertions.assertEquals(Jonad.empty(), Jonad.empty().onErrorFlatMap(err -> Jonad.of(S_VAL_2)));
        }
    }

    @Nested
    @DisplayName("On error flat map matching tests")
    class OnErrorFlatMapMatching
    {
        @Test
        void itMapsWhenErrorPresentAndMatching()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_2), Jonad.of(EXCEPTION)
                    .onErrorFlatMapMatching(e -> true, err -> Jonad.of(S_VAL_2)));
        }

        @Test
        void itDoesntMapWhenErrorPresentButNotMatching()
        {
            Assertions.assertEquals(Jonad.of(EXCEPTION), Jonad.of(EXCEPTION)
                    .onErrorFlatMapMatching(e -> false, err -> Jonad.of(S_VAL_2)));
        }

        @Test
        void itDoesntMapWhenErrorNotPresent()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_1), Jonad.of(S_VAL_1)
                    .onErrorFlatMapMatching(e -> true, err -> Jonad.of(S_VAL_2)));
        }

        @Test
        void itDoesntMapWhenEmpty()
        {
            Assertions.assertEquals(Jonad.empty(), Jonad.empty()
                    .onErrorFlatMapMatching(e -> true, err -> Jonad.of(S_VAL_2)));
        }
    }

    @Nested
    @DisplayName("Try map tests")
    class TryMapTests
    {
        @Test
        void itMapsWhenErrorNotThrown()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_2), Jonad.of(S_VAL_1).tryMap(val -> S_VAL_2));
        }

        @Test
        void itDoesntMapWhenErrorThrownByMappingFunction()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_1), Jonad.of(S_VAL_1).tryMap(val -> throwsWhenCalled()));
        }

        @Test
        void itDoesntMapWhenEmpty()
        {
            Assertions.assertEquals(Jonad.empty(), Jonad.empty().tryMap(val -> Jonad.of(S_VAL_2)));
        }

    }

    @Nested
    @DisplayName("Switch if empty")
    class SwitchIfEmpty
    {
        @Test
        void itSwitchesToAnAlternativeMonadWhenNoValuePresent()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_1), Jonad.empty().switchIfEmpty(Jonad.of(S_VAL_1)));
        }

        @Test
        void itSwitchesToAnAlternativeMonadOfDistinctTypeWhenNoValuePresent()
        {
            Assertions.assertEquals(Jonad.of(I_VAL_1), Jonad.empty().switchIfEmpty(Jonad.of(I_VAL_1)));
        }

        @Test
        void itDoesntSwitchWhenValuePresent()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_1), Jonad.of(S_VAL_1).switchIfEmpty(Jonad.of(S_VAL_2)));
        }
    }

    @Nested
    @DisplayName("Default if empty tests")
    class DefaultIfEmpty
    {
        @Test
        void itSwitchesToAnAlternativeValueWhenNoValuePresent()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_1), Jonad.empty().defaultIfEmpty(S_VAL_1));
        }

        @Test
        void itSwitchesToAnAlternativeOfDistinctTypeValueWhenNoValuePresent()
        {
            Assertions.assertEquals(Jonad.of(I_VAL_1), Jonad.empty().defaultIfEmpty(I_VAL_1));
        }

        @Test
        void itDoesntSwitchWhenValuePresent()
        {
            Assertions.assertEquals(Jonad.of(S_VAL_1), Jonad.of(S_VAL_1).defaultIfEmpty(S_VAL_2));
        }
    }

    private String throwsWhenCalled()
    {
        throw new RuntimeException("");
    }
}