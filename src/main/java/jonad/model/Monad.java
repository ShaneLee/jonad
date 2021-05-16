package jonad.model;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface Monad<T>
{
    /**
     * Transform the value of this monad into
     * a new value from the given function.
     * Does nothing if Monad has no value
     * @param f the mapping function
     */
     <U> Monad<U> map(Function<T, U> f);

    /**
     * If this Monad contains a value, apply
     * the given Monad-bearing function flattening
     * the result into a single Monad containing the
     * value of the Monad resulting from the function
     * @param f the function resulting in a Monad
     */
    <U> Monad<U> flatMap(Function<? super T, ? extends Monad<? extends U>> f);

    /**
     * If a value is present in this Monad, apply
     * the given predicate function to the value in
     * this Monad. If the predicate function evaluates
     * to true then the resulting Monad will retain the
     * value of the original Monad. Else an empty
     * Monad will be returned
     * @param f the predicate function
     */
    Monad<T> filter(Predicate<? super T> f);

    /**
     * If a value is present in this Monad, apply
     * the given flattening predicate function to the value in
     * this Monad. This can be thought of as the combination
     * of the {@link #flatMap(Function)} operator and the
     * {@link #filter(Predicate)} operator. If the predicate
     * function evaluates to true then the resulting Monad
     * will retain the value of the original Monad.
     * Else an empty Monad will be returned
     * @param f the predicate function
     */
    Monad<T> filterWhen(Function<? super T, ? extends Monad<? extends Boolean>> f);

    /**
     * If this Monad has a value, return it;
     * else return undefined
     */
    @Nullable T getOrNull();

    /**
     * Transform this Monad to an {@link java.util.Optional}
     */
    Optional<T> toOptional();

    /**
     * Transform this Monad to an {@link Stream}
     */
    Stream<T> stream();

    /**
     * If this Monad has a value, return it;
     * else return the non-null value provided
     * as an argument to this method
     * @param t the default value
     */
    T getOrDefault(final T t);

    /**
     * If this Monad doesn't have a value, invoke
     * the given supplier and return it's value
     * @param f the supplier function
     */
    T orElseGet(Supplier<T> f);

    /**
     * If this Monad doesn't have a value,
     * invoke the given supplier and throw the
     * error it provides
     * @param f the supplier function
     */
    <E extends Throwable> T orElseThrow(Supplier<? extends E> f) throws E;

    /**
     * Return true if this Monad is Empty
     */
    boolean isEmpty();

    /**
     * Perform a side-effect if this Monad doesn't
     * contain a value
     * @param f the function to apply
     */
    <U> Monad<T> doIfEmpty(Consumer<U> f);

    /**
     * Perform a side-effect if this Monad contains
     * a value
     * @param f the consumer function apply
     */
    <U> Monad<T> doIfPresent(Consumer<U> f);

    /**
     * Perform a side-effect if this Monad contains
     * an Error
     * @param f the error consumer
     */
    <E extends Throwable> Monad<T> doOnError(Consumer<E> f);

    /**
     * Perform a side-effect if this Monad contains
     * an Error of the given type
     * @param e the error type to match
     * @param f the error consumer
     */
    <E extends Throwable> Monad<T> doOnError(Class<E> e, Consumer<? super E> f);

    /**
     * Perform a side-effect if this Monad contains
     * an Error which matches the given predicate function
     * @param p the predicate function
     * @param f the error consumer
     */
    <U> Monad<T> doOnErrorMatching(Predicate<? super Throwable> p, Consumer<U> f);

    /**
     * Map to an alternative value if this Monad contains an
     * error, otherwise retain this Monad
     * @param f the mapping function providing the alternative
     * value
     */
    <E extends Throwable, U> Monad<U> onErrorMap(Function<E, U> f);

    /**
     * Map to an alternative value if this Monad contains an
     * error and it matches the given predicate function,
     * otherwise retain this Monad
     * @param p the predicate function
     * @param f the mapping function providing the alternative
     * value
     */
    <E extends Throwable, U> Monad<U> onErrorMapMatching(Predicate<E> p, Function<E, U> f);

    /**
     * Map to an alternative Monad if this Monad contains an
     * error, otherwise retain this Monad
     * @param f the mapping function providing the alternative
     * Monad
     */
    <E extends Throwable, U> Monad<U> onErrorFlatMap(Function<E, Monad<U>> f);

    /**
     * Map to an alternative Monad if this Monad contains an
     * error and it matches the given predicate function,
     * otherwise retain this Monad
     * @param p the predicate function
     * @param f the mapping function providing the alternative
     * Monad
     */
    <E extends Throwable, U> Monad<U> onErrorFlatMapMatching(Predicate<E> p, Function<E, Monad<U>> f);

    /**
     * Try and map to an alternative Monad if the given function throws
     * return the original otherwise map the value
     * @param f the mapping function providing the alternative Monad
     */
    <U> Monad<U> tryMap(Function<T, U> f);

    /**
     * Switch to an alternative Monad if the this Monad is empty
     * otherwise retain this value
     * @param u the alternative Monad
     */
    <U> Monad<U> switchIfEmpty(Monad<U> u);

    /**
     * Default to an alternative value if the this Monad is empty
     * otherwise retain this value
     * @param u the value to provide to a new Monad
     */
    <U> Monad<U> defaultIfEmpty(U u);
}
