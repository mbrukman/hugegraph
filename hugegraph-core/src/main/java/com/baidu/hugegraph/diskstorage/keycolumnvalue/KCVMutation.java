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

package com.baidu.hugegraph.diskstorage.keycolumnvalue;

import com.google.common.base.Functions;
import com.baidu.hugegraph.diskstorage.Entry;
import com.baidu.hugegraph.diskstorage.Mutation;
import com.baidu.hugegraph.diskstorage.StaticBuffer;
import com.baidu.hugegraph.diskstorage.keycolumnvalue.cache.KCVEntryMutation;

import java.util.List;

/**
 * {@link Mutation} type for {@link KeyColumnValueStore}.
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public class KCVMutation extends Mutation<Entry, StaticBuffer> {

    public KCVMutation(List<Entry> additions, List<StaticBuffer> deletions) {
        super(additions, deletions);
    }

    @Override
    public void consolidate() {
        super.consolidate(KCVEntryMutation.ENTRY2COLUMN_FCT, Functions.<StaticBuffer> identity());
    }

    @Override
    public boolean isConsolidated() {
        return super.isConsolidated(KCVEntryMutation.ENTRY2COLUMN_FCT, Functions.<StaticBuffer> identity());
    }
}
