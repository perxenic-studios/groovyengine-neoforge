package io.github.luckymcdev.groovyengine.util;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

// Minified version adapted from Bookshelf
public class CachedSupplier<T> implements Supplier<T> {

    private final Supplier<T> delegate;
    private boolean cached = false;
    @Nullable
    private T cachedValue;

    protected CachedSupplier(Supplier<T> delegate) {
        this.delegate = delegate;
    }

    /**
     * Create a new instance of a {@link CachedSupplier} that caches the result of the given delegate supplier.
     *
     * @param delegate the supplier to cache
     * @param <T> the type of the value returned by the supplier
     * @return a new instance of a {@link CachedSupplier} that caches the result of the given delegate supplier
     */
    public static <T> CachedSupplier<T> cache(Supplier<T> delegate) {
        return new CachedSupplier<>(delegate);
    }

    /**
     * Return the cached value, if available, or compute and cache the value
     * using the given delegate supplier.
     *
     * @return the cached value, if available, or the computed value
     */
    @Override
    public T get() {
        if (!this.isCached()) {
            this.cachedValue = this.delegate.get();
            this.cached = true;
        }
        return cachedValue;
    }

    /**
     * Check if the supplier has been cached.
     *
     * @return true if the supplier has been cached, false otherwise
     */
    public boolean isCached() {
        return this.cached;
    }
}