// Copyright 2017 HugeGraph Authors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.baidu.hugegraph.diskstorage.cassandra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Map;

import com.baidu.hugegraph.diskstorage.BackendException;
import com.baidu.hugegraph.diskstorage.configuration.Configuration;
import com.baidu.hugegraph.diskstorage.configuration.ModifiableConfiguration;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.baidu.hugegraph.diskstorage.KeyColumnValueStoreTest;
import com.baidu.hugegraph.diskstorage.keycolumnvalue.StoreFeatures;
import com.baidu.hugegraph.testcategory.OrderedKeyStoreTests;
import com.baidu.hugegraph.testcategory.UnorderedKeyStoreTests;

public abstract class AbstractCassandraStoreTest extends KeyColumnValueStoreTest {

    private static final Logger log = LoggerFactory.getLogger(AbstractCassandraStoreTest.class);
    private static final String TEST_CF_NAME = "testcf";
    private static final String DEFAULT_COMPRESSOR_PACKAGE = "org.apache.cassandra.io.compress";

    public abstract ModifiableConfiguration getBaseStorageConfiguration();

    public abstract AbstractCassandraStoreManager openStorageManager(Configuration c) throws BackendException;

    @Test
    @Category({ UnorderedKeyStoreTests.class })
    public void testUnorderedConfiguration() {
        if (!manager.getFeatures().hasUnorderedScan()) {
            log.warn("Can't test key-unordered features on incompatible store.  "
                    + "This warning could indicate reduced test coverage and "
                    + "a broken JUnit configuration.  Skipping test {}.", name.getMethodName());
            return;
        }

        StoreFeatures features = manager.getFeatures();
        assertFalse(features.isKeyOrdered());
        assertFalse(features.hasLocalKeyPartition());
    }

    @Test
    @Category({ OrderedKeyStoreTests.class })
    public void testOrderedConfiguration() {
        if (!manager.getFeatures().hasOrderedScan()) {
            log.warn("Can't test key-ordered features on incompatible store.  "
                    + "This warning could indicate reduced test coverage and "
                    + "a broken JUnit configuration.  Skipping test {}.", name.getMethodName());
            return;
        }

        StoreFeatures features = manager.getFeatures();
        assertTrue(features.isKeyOrdered());
    }

    @Test
    public void testDefaultCFCompressor() throws BackendException {

        final String cf = TEST_CF_NAME + "_snappy";

        AbstractCassandraStoreManager mgr = openStorageManager();

        mgr.openDatabase(cf);

        Map<String, String> defaultCfCompressionOps = new ImmutableMap.Builder<String, String>()
                .put("sstable_compression",
                        DEFAULT_COMPRESSOR_PACKAGE + "."
                                + AbstractCassandraStoreManager.CF_COMPRESSION_TYPE.getDefaultValue())
                .put("chunk_length_kb", "64").build();

        assertEquals(defaultCfCompressionOps, mgr.getCompressionOptions(cf));
    }

    @Test
    public void testCustomCFCompressor() throws BackendException {

        final String cname = "DeflateCompressor";
        final int ckb = 128;
        final String cf = TEST_CF_NAME + "_gzip";

        ModifiableConfiguration config = getBaseStorageConfiguration();
        config.set(AbstractCassandraStoreManager.CF_COMPRESSION_TYPE, cname);
        config.set(AbstractCassandraStoreManager.CF_COMPRESSION_BLOCK_SIZE, ckb);

        AbstractCassandraStoreManager mgr = openStorageManager(config);

        // N.B.: clearStorage() truncates CFs but does not delete them
        mgr.openDatabase(cf);

        final Map<String, String> expected = ImmutableMap.<String, String> builder()
                .put("sstable_compression", DEFAULT_COMPRESSOR_PACKAGE + "." + cname)
                .put("chunk_length_kb", String.valueOf(ckb)).build();

        assertEquals(expected, mgr.getCompressionOptions(cf));
    }

    @Test
    public void testDisableCFCompressor() throws BackendException {

        final String cf = TEST_CF_NAME + "_nocompress";

        ModifiableConfiguration config = getBaseStorageConfiguration();
        config.set(AbstractCassandraStoreManager.CF_COMPRESSION, false);
        AbstractCassandraStoreManager mgr = openStorageManager(config);

        // N.B.: clearStorage() truncates CFs but does not delete them
        mgr.openDatabase(cf);

        assertEquals(Collections.emptyMap(), mgr.getCompressionOptions(cf));
    }

    @Test
    public void testTTLSupported() throws Exception {
        StoreFeatures features = manager.getFeatures();
        assertTrue(features.hasCellTTL());
    }

    @Override
    public AbstractCassandraStoreManager openStorageManager() throws BackendException {
        return openStorageManager(getBaseStorageConfiguration());
    }
}
