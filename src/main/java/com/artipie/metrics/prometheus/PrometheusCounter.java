/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 artipie.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.artipie.metrics.prometheus;

import com.artipie.metrics.Counter;

/**
 * {@link Counter} implementation storing data in memory.
 *
 * @since 0.8
 */
public final class PrometheusCounter implements Counter {

    /**
     * Current counter value.
     */
    private io.prometheus.client.Counter counter;

    public PrometheusCounter(io.prometheus.client.Counter counter) {
        this.counter = counter;
    }

    @Override
    public void add(final long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(
                String.format("Amount should not be negative: %d", amount)
            );
        }
        this.counter.inc(amount);
    }

    @Override
    public void inc() {
        this.counter.inc();
    }

    /**
     * Get counter value.
     *
     * @return Counter value.
     */
    public long value() {
        return (long) this.counter.get();
    }
}
