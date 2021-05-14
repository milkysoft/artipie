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

import com.artipie.metrics.Gauge;

/**
 * {@link Gauge} implementation storing data in memory.
 *
 * @since 0.8
 */
public final class PrometheusGauge implements Gauge {

    /**
     * Current value.
     */
    private io.prometheus.client.Gauge current;

    /**
     * Current counter value.
     *
     * @param gauge is good
     */
    public PrometheusGauge(io.prometheus.client.Gauge gauge) {
        this.current = gauge;
    }

    @Override
    public void set(final long update) {
        this.current.set(update);
    }

    /**
     * Get gauge value.
     *
     * @return Gauge value.
     */
    public long value() {
        return (long) this.current.get();
    }
}
