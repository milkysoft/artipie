/*
 * The MIT License (MIT) Copyright (c) 2020-2021 artipie.com
 * https://github.com/artipie/artipie/LICENSE.txt
 */
package com.artipie.pypi;

import com.artipie.RepoConfig;
import com.artipie.http.Response;
import com.artipie.http.Slice;
import com.artipie.http.client.ClientSlices;
import com.artipie.pypi.http.PyProxySlice;
import com.artipie.repo.ProxyConfig;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;
import org.reactivestreams.Publisher;

/**
 * Pypi proxy slice.
 * @since 0.12
 */
public final class PypiProxy implements Slice {

    /**
     * HTTP client.
     */
    private final ClientSlices client;

    /**
     * Repository configuration.
     */
    private final RepoConfig cfg;

    /**
     * Ctor.
     *
     * @param client HTTP client.
     * @param cfg Repository configuration.
     */
    public PypiProxy(final ClientSlices client, final RepoConfig cfg) {
        this.client = client;
        this.cfg = cfg;
    }

    @Override
    public Response response(final String line, final Iterable<Map.Entry<String, String>> headers,
        final Publisher<ByteBuffer> body) {
        final Collection<? extends ProxyConfig.Remote> remotes = this.cfg.proxy().remotes();
        if (remotes.isEmpty()) {
            throw new IllegalArgumentException("No remotes specified");
        }
        if (remotes.size() > 1) {
            throw new IllegalArgumentException("Only one remote is allowed");
        }
        final ProxyConfig.Remote remote = remotes.iterator().next();
        return new PyProxySlice(
            this.client,
            URI.create(remote.url()),
            remote.auth(),
            remote.cache().orElseThrow(
                () -> new IllegalStateException("Python proxy requires proxy storage to be set")
            )
        ).response(line, headers, body);
    }
}
