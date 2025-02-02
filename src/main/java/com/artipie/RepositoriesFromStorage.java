/*
 * The MIT License (MIT) Copyright (c) 2020-2021 artipie.com
 * https://github.com/artipie/artipie/LICENSE.txt
 */
package com.artipie;

import com.artipie.asto.Content;
import com.artipie.asto.Key;
import com.artipie.asto.Storage;
import com.artipie.asto.ext.PublisherAs;
import com.artipie.repo.ConfigFile;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import hu.akarnokd.rxjava2.interop.SingleInterop;
import io.reactivex.Single;
import java.util.Objects;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

/**
 * Artipie repositories created from {@link Settings}.
 *
 * @since 0.13
 */
public final class RepositoriesFromStorage implements Repositories {
    /**
     * Cache for config files.
     */
    private static LoadingCache<FilesContent, Single<String>> configs;

    /**
     * Cache for aliases.
     */
    private static LoadingCache<FilesContent, Single<StorageAliases>> aliases;

    static {
        System.setProperty(
            ArtipieProperties.CONFIG_TIMEOUT,
            new ArtipieProperties().configCacheTimeout()
        );
        final int timeout = Integer.getInteger(ArtipieProperties.CONFIG_TIMEOUT, 2 * 60 * 1000);
        RepositoriesFromStorage.configs = CacheBuilder.newBuilder()
            .expireAfterWrite(timeout, TimeUnit.MILLISECONDS)
            .softValues()
            .build(
                new CacheLoader<>() {
                    @Override
                    public Single<String> load(final FilesContent config) {
                        return config.configContent();
                    }
                }
            );
        RepositoriesFromStorage.aliases = CacheBuilder.newBuilder()
            .expireAfterWrite(timeout, TimeUnit.MILLISECONDS)
            .softValues()
            .build(
                new CacheLoader<>() {
                    @Override
                    public Single<StorageAliases> load(final FilesContent alias) {
                        return alias.aliases();
                    }
                }
            );
    }

    /**
     * Storage.
     */
    private final Storage storage;

    /**
     * Ctor.
     *
     * @param storage Storage.
     */
    public RepositoriesFromStorage(final Storage storage) {
        this.storage = storage;
    }

    @Override
    public CompletionStage<RepoConfig> config(final String name) {
        final FilesContent pair = new FilesContent(new Key.From(name), this.storage);
        return Single.zip(
            RepositoriesFromStorage.configs.getUnchecked(pair),
            RepositoriesFromStorage.aliases.getUnchecked(pair),
            (data, alias) -> SingleInterop.fromFuture(
                RepoConfig.fromPublisher(alias, pair.key, new Content.From(data.getBytes()))
            )
        ).flatMap(self -> self).to(SingleInterop.get());
    }

    /**
     * Extra class for obtaining aliases and content of configuration file.
     * @since 0.22
     */
    private static final class FilesContent {
        /**
         * Key.
         */
        private final Key key;

        /**
         * Storage.
         */
        private final Storage storage;

        /**
         * Ctor.
         * @param key Key
         * @param storage Storage
         */
        private FilesContent(final Key key, final Storage storage) {
            this.key = key;
            this.storage = storage;
        }

        @Override
        public int hashCode() {
            return this.key.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean res;
            if (obj == this) {
                res = true;
            } else if (obj instanceof FilesContent) {
                final FilesContent data = (FilesContent) obj;
                res = Objects.equals(this.key, data.key)
                    && Objects.equals(data.storage, this.storage);
            } else {
                res = false;
            }
            return res;
        }

        /**
         * Obtains content of configuration file.
         * @return Content of configuration file.
         */
        Single<String> configContent() {
            return Single.fromFuture(
                new ConfigFile(this.key).valueFrom(this.storage)
                    .thenApply(PublisherAs::new)
                    .thenCompose(PublisherAs::asciiString)
                    .toCompletableFuture()
            );
        }

        /**
         * Obtains aliases from storage by key.
         * @return Aliases from storage by key.
         */
        Single<StorageAliases> aliases() {
            return Single.fromFuture(
                StorageAliases.find(this.storage, this.key)
            );
        }
    }
}
