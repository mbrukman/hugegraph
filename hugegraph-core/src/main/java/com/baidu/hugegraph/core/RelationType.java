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

package com.baidu.hugegraph.core;


import com.baidu.hugegraph.core.schema.HugeGraphSchemaType;

/**
 * RelationType defines the schema for {@link HugeGraphRelation}. RelationType can be configured through {@link com.baidu.hugegraph.core.schema.RelationTypeMaker} to
 * provide data verification, better storage efficiency, and higher retrieval performance.
 * <br />
 * Each {@link HugeGraphRelation} has a unique type which defines many important characteristics of that relation.
 * <br />
 * RelationTypes are constructed through {@link com.baidu.hugegraph.core.schema.RelationTypeMaker} which is accessed in the context of a {@link HugeGraphTransaction}
 * via {@link com.baidu.hugegraph.core.HugeGraphTransaction#makePropertyKey(String)} for property keys or {@link com.baidu.hugegraph.core.HugeGraphTransaction#makeEdgeLabel(String)}
 * for edge labels. Identical methods exist on {@link HugeGraph}.
 * Note, relation types will only be visible once the transaction in which they were created has been committed.
 * <br />
 * RelationType names must be unique in a graph database. Many methods allow the name of the type as an argument
 * instead of the actual type reference. That also means, that edge labels and property keys may not have the same name.
 *
 * @see HugeGraphRelation
 * @see com.baidu.hugegraph.core.schema.RelationTypeMaker
 * @see <a href="http://s3.thinkaurelius.com/docs/titan/current/schema.html">"Schema and Data Modeling" manual chapter</a>
 */
public interface RelationType extends HugeGraphVertex, HugeGraphSchemaType {

    /**
     * Checks if this relation type is a property key
     *
     * @return true, if this relation type is a property key, else false.
     * @see PropertyKey
     */
    public boolean isPropertyKey();

    /**
     * Checks if this relation type is an edge label
     *
     * @return true, if this relation type is a edge label, else false.
     * @see EdgeLabel
     */
    public boolean isEdgeLabel();

}