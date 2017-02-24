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

package com.baidu.hugegraph.graphdb.query.vertex;

import com.baidu.hugegraph.core.HugeGraphVertex;
import com.baidu.hugegraph.core.VertexList;

/**
 * Extends on the {@link VertexList} interface by provided methods to add elements to the list which is needed during
 * query execution when the result list is created.
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public interface VertexListInternal extends VertexList {

    /**
     * Adds the provided vertex to this list.
     *
     * @param n
     */
    public void add(HugeGraphVertex n);

    /**
     * Copies all vertices in the given vertex list into this list.
     *
     * @param vertices
     */
    public void addAll(VertexList vertices);

}
