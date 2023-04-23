package jonad.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
public class Jonad<T> implements Monad<T>
{
    private final T val;

    /**
     * Wrap the given value in a Monad
     * @param val the given value
     * @return a new Monad
     */
    public static <T> Monad<T> of(final T val)
    {
        return new Jonad<>(val);
    }

    /**
     * Wrap the given value in a Monad if present
     * otherwise return an empty Monad
     * @param val the given value
     * @return a new Monad
     */
    @SuppressWarnings("unchecked")
    public static <T, U> Monad<U> orEmpty(final T val)
    {
        if (val == null)
        {
            return Jonad.empty();
        }

        if (val instanceof Optional)
        {
            return (Monad<U>) Jonad.of(val)
                    .map(Optional.class::cast)
                    .filter(Predicate.not(Optional::isEmpty))
                    .map(Optional::get);
        }

        return (Monad<U>) Jonad.of(val);
    }

    /**
     * Wrap the given value from the supplier in a Monad
     * @param f the supplier function providing the value to wrap
     * @return a new Monad
     */
    public static <T> Monad<T> fromSupplier(final Supplier<T> f)
    {
        return new Jonad<>(f.get());
    }

    /**
     * Create a new empty Monad
     * @return a new empty Monad
     */
    public static <T> Monad<T> empty()
    {
        return new Jonad<>(null);
    }

    @Override
    public <U> Monad<U> map(final Function<T, U> f)
    {
        if (val == null)
        {
            return Jonad.empty();
        }

        return Jonad.of(f.apply(val));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U> Monad<U> flatMap(final Function<? super T, ? extends Monad<? extends U>> f)
    {
        if (val == null)
        {
            return Jonad.empty();
        }

        return (Jonad<U>) f.apply(val);
    }

    @Override
    public Monad<T> filter(final Predicate<? super T> f)
    {
        if (val == null || !f.test(val))
        {
            return Jonad.empty();
        }

        return this;
    }

    @Override
    public Monad<T> filterWhen(final Function<? super T, ? extends Monad<? extends Boolean>> f)
    {
        if (val == null || f.apply(val).filter(res -> res).isEmpty())
        {
            return Jonad.empty();
        }

        return this;
    }

    @Nullable
    @Override
    public T getOrNull()
    {
        return val;
    }

    @Override
    public Optional<T> toOptional()
    {
        return Optional.ofNullable(val);
    }

    @Override
    public Stream<T> stream()
    {
        return val == null ? Stream.empty() : Stream.of(val);
    }

    @Override
    public T getOrDefault(final T t)
    {
        return val == null ? t : val;
    }

    @Override
    public T orElseGet(final Supplier<T> f)
    {
        return val == null ? f.get() : val;
    }

    @Override
    public <E extends Throwable> T orElseThrow(final Supplier<? extends E> f) throws E
    {
        if (val == null)
        {
            throw f.get();
        }

        return val;
    }

    @Override
    public boolean isEmpty()
    {
        return val == null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U> Monad<T> doIfEmpty(final Consumer<U> f)
    {
        if (val == null)
        {
            f.accept((U) val);
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U> Monad<T> doIfPresent(final Consumer<U> f)
    {
        if (val != null)
        {
            f.accept((U) val);
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Throwable> Monad<T> doOnError(final Consumer<E> f)
    {
        if (val instanceof Throwable)
        {
            f.accept((E) val);
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Throwable> Monad<T> doOnError(final Class<E> e, final Consumer<? super E> f)
    {
        if (e.isInstance(val))
        {
            f.accept((E) val);
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U> Monad<T> doOnErrorMatching(final Predicate<? super Throwable> p, final Consumer<U> f)
    {
        if (val instanceof Throwable && p.test((Throwable) val))
        {
            f.accept((U) val);
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Throwable, U> Monad<U> onErrorMap(final Function<E, U> f)
    {
        if (val instanceof Throwable)
        {
            return Jonad.of(f.apply((E) val));
        }

        return (Monad<U>) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Throwable, U> Monad<U> onErrorMapMatching(final Predicate<E> p, final Function<E, U> f)
    {
        if (val instanceof Throwable && p.test((E) val))
        {
            return Jonad.of(f.apply((E) val));
        }

        return (Monad<U>) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Throwable, U> Monad<U> onErrorFlatMap(final Function<E, Monad<U>> f)
    {
        if (val instanceof Throwable)
        {
            return f.apply((E) val);
        }

        return (Monad<U>) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends Throwable, U> Monad<U> onErrorFlatMapMatching(final Predicate<E> p, final Function<E, Monad<U>> f)
    {
        if (val instanceof Throwable && p.test((E) val))
        {
            return f.apply((E) val);
        }

        return (Monad<U>) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U> Monad<U> tryMap(final Function<T, U> f)
    {
        if (val == null)
        {
            return (Monad<U>) this;
        }

        try
        {
            return Jonad.of(f.apply(val));
        }
        // CSOFF: IllegalCatch
        catch (Exception e)
        {
            return (Monad<U>) this;
        }
        // CSON: IllegalCatch
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U> Monad<U> switchIfEmpty(final Monad<U> u)
    {
        if (val == null)
        {
            return u;
        }

        return (Monad<U>) Jonad.of(val);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U> Monad<U> defaultIfEmpty(final U u)
    {
        if (val == null)
        {
            return Jonad.of(u);
        }

        return (Monad<U>) Jonad.of(val);
    }
}
