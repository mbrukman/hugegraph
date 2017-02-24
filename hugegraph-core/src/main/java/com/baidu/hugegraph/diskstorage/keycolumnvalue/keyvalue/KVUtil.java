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

package com.baidu.hugegraph.diskstorage.keycolumnvalue.keyvalue;

import com.baidu.hugegraph.diskstorage.*;
import com.baidu.hugegraph.diskstorage.keycolumnvalue.StoreTransaction;
import com.baidu.hugegraph.diskstorage.util.RecordIterator;
import com.baidu.hugegraph.diskstorage.util.StaticArrayEntry;
import com.baidu.hugegraph.diskstorage.util.StaticArrayEntryList;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Utility methods for interacting with {@link KeyValueStore}.
 *
 * @author Matthias Br&ouml;cheler (me@matthiasb.com);
 */
public class KVUtil {
    public static final RecordIterator<KeyValueEntry> EMPTY_ITERATOR = new RecordIterator<KeyValueEntry>() {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public KeyValueEntry next() {
            throw new NoSuchElementException();
        }

        @Override
        public void close() {

        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    };

    public static EntryList getSlice(OrderedKeyValueStore store, StaticBuffer keyStart, StaticBuffer keyEnd,
            StoreTransaction txh) throws BackendException {
        return convert(store.getSlice(new KVQuery(keyStart, keyEnd), txh));
    }

    public static EntryList getSlice(OrderedKeyValueStore store, StaticBuffer keyStart, StaticBuffer keyEnd, int limit,
            StoreTransaction txh) throws BackendException {
        return convert(store.getSlice(new KVQuery(keyStart, keyEnd, limit), txh));
    }

    public static EntryList convert(RecordIterator<KeyValueEntry> iter) throws BackendException {
        try {
            return StaticArrayEntryList.ofStaticBuffer(iter, KVEntryGetter.INSTANCE);
        } finally {
            try {
                iter.close();
            } catch (IOException e) {
                throw new TemporaryBackendException(e);
            }
        }
    }

    private static enum KVEntryGetter implements StaticArrayEntry.GetColVal<KeyValueEntry, StaticBuffer> {
        INSTANCE;

        @Override
        public StaticBuffer getColumn(KeyValueEntry element) {
            return element.getKey();
        }

        @Override
        public StaticBuffer getValue(KeyValueEntry element) {
            return element.getValue();
        }

        @Override
        public EntryMetaData[] getMetaSchema(KeyValueEntry element) {
            return StaticArrayEntry.EMPTY_SCHEMA;
        }

        @Override
        public Object getMetaData(KeyValueEntry element, EntryMetaData meta) {
            throw new UnsupportedOperationException("Unsupported meta data: " + meta);
        }
    };

}
