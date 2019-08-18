/*
 * Copyright (c) 2019, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ballerinalang.langserver.compiler;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.ballerinalang.langserver.compiler.common.modal.BallerinaFile;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.wso2.ballerinalang.compiler.SourceDirectory;
import org.wso2.ballerinalang.compiler.tree.BLangPackage;
import org.wso2.ballerinalang.compiler.util.CompilerContext;
import org.wso2.ballerinalang.compiler.util.CompilerOptions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.ballerinalang.compiler.CompilerOptionName.COMPILER_PHASE;
import static org.ballerinalang.compiler.CompilerOptionName.PRESERVE_WHITESPACE;
import static org.ballerinalang.compiler.CompilerOptionName.SKIP_TESTS;
import static org.ballerinalang.compiler.CompilerOptionName.TEST_ENABLED;

/**
 * Provides a thin caching layer on-top of the LSCompiler.
 * <p>
 * This Cache heavily depends on the LSP protocol for the cache eviction such that didChange, didOpen and didClose
 * clears the related entries from the cache.
 *
 * @since 1.0.0
 */
public class LSCompilerCache {
    private static final long MAX_CACHE_COUNT = 10L;
    private static Map<Key, CacheEntry> packageMap;

    static {
        Cache<Key, CacheEntry> cache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(MAX_CACHE_COUNT).build();
        LSCompilerCache.packageMap = cache.asMap();
    }

    private LSCompilerCache() {
    }

    /**
     * Returns cached BLangPackage.
     *
     * @param key     unique {@link Key}
     * @param context {@link LSContext}
     * @return {@link BallerinaFile}
     */
    public static CacheEntry get(Key key, LSContext context) {
        CacheEntry cacheEntry = packageMap.get(key);
        if (cacheEntry == null) {
            return null;
        }
        context.put(DocumentServiceKeys.COMPILER_CONTEXT_KEY, cacheEntry.compilerContext);
        return cacheEntry;
    }

    /**
     * Adds BLangPackage into cache.
     *
     * @param key           unique {@link Key}
     * @param bLangPackages {@link org.wso2.ballerinalang.compiler.tree.BLangPackage}
     * @param context       {@link LSContext}
     */
    public static void put(Key key, Either<BLangPackage, List<BLangPackage>> bLangPackages, LSContext context) {
        CompilerContext compilerContext = context.get(DocumentServiceKeys.COMPILER_CONTEXT_KEY);
        packageMap.put(key, new CacheEntry(bLangPackages, compilerContext));
    }

    /**
     * Clears all cache entries with this source root.
     *
     * @param sourceRoot source root
     */
    public static void clearAll(String sourceRoot) {
        // Remove matching entries in parallel #threadSafe
        packageMap.keySet().parallelStream()
                .filter(p -> p.sourceRoot.equals(sourceRoot))
                .forEach(k -> packageMap.remove(k));
    }

    /**
     * Clears all cache entries.
     */
    public static void clearAll() {
        packageMap.clear();
    }

    public static void markOutDated(Key key) {
        CacheEntry cacheEntry = packageMap.get(key);
        cacheEntry.isOutdated = true;
        packageMap.put(key, cacheEntry);
    }

    /**
     * Represents a composite cache key.
     */
    public static class Key {
        private final String sourceRoot;
        private final String errorStrategy;

        private final String compilerPhase;
        private final String preserveWhitespace;
        private final String testEnabled;
        private final String skipTests;
        private final String sourceDirectory;

        public Key(String sourceRoot, LSContext context) {
            this.sourceRoot = sourceRoot;
            CompilerContext compilerContext = context.get(DocumentServiceKeys.COMPILER_CONTEXT_KEY);
            CompilerOptions options = CompilerOptions.getInstance(compilerContext);
            this.compilerPhase = options.get(COMPILER_PHASE);
            this.preserveWhitespace = options.get(PRESERVE_WHITESPACE);
            this.testEnabled = options.get(TEST_ENABLED);
            this.skipTests = options.get(SKIP_TESTS);
            DefaultErrorStrategy defaultErrorStrategy = compilerContext.get(DefaultErrorStrategy.class);
            this.errorStrategy = defaultErrorStrategy != null ? defaultErrorStrategy.getClass().getName() : null;
            SourceDirectory sourceDirectory = compilerContext.get(SourceDirectory.class);
            this.sourceDirectory = sourceDirectory != null ? sourceDirectory.getClass().getName() : null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o || !(o instanceof Key)) {
                return false;
            }
            Key key = (Key) o;
            return (key.sourceRoot.equals(sourceRoot)
                    && errorStrategy != null && errorStrategy.equals(key.errorStrategy)
                    && compilerPhase != null && compilerPhase.equals(key.compilerPhase)
                    && preserveWhitespace != null && preserveWhitespace.equals(key.preserveWhitespace)
                    && testEnabled != null && testEnabled.equals(key.testEnabled)
                    && skipTests != null && skipTests.equals(key.skipTests)
                    && sourceDirectory != null && sourceDirectory.equals(key.sourceDirectory));
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(
                    new String[]{sourceRoot, errorStrategy, compilerPhase, preserveWhitespace, testEnabled, skipTests,
                            sourceDirectory});
        }

        @Override
        public String toString() {
            return String.format("%s, %s", sourceRoot,
                                 errorStrategy != null ? errorStrategy.substring(errorStrategy.lastIndexOf(".") + 1) :
                                         "");
        }
    }

    /**
     * Represents a cache entry.
     */
    public static class CacheEntry {
        private Either<BLangPackage, List<BLangPackage>> bLangPackages;
        private CompilerContext compilerContext;
        private boolean isOutdated = false;

        CacheEntry(Either<BLangPackage, List<BLangPackage>> bLangPackages,
                   CompilerContext compilerContext) {
            this.bLangPackages = bLangPackages;
            this.compilerContext = compilerContext;
        }

        /**
         * Returns cached BLangPackages.
         *
         * @return {@link BLangPackage}
         */
        public Either<BLangPackage, List<BLangPackage>> get() {
            return bLangPackages;
        }

        /**
         * Returns True, if cache entry is outdated.
         *
         * @return True, if cache entry is outdated, False otherwise
         */
        public boolean isOutdated() {
            return isOutdated;
        }
    }
}
