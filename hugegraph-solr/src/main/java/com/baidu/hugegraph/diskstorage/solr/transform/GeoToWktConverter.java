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

package com.baidu.hugegraph.diskstorage.solr.transform;

import com.baidu.hugegraph.core.attribute.Geoshape;
import com.baidu.hugegraph.diskstorage.BackendException;
import com.baidu.hugegraph.diskstorage.PermanentBackendException;

public class GeoToWktConverter {
    /**
     * {@link com.baidu.hugegraph.core.attribute.Geoshape} stores Points in the String format: point[X.0,Y.0]. Solr
     * needs it to be in Well-Known Text format: POINT(X.0 Y.0)
     */
    public static String convertToWktString(Geoshape fieldValue) throws BackendException {
        if (fieldValue.getType() == Geoshape.Type.POINT) {
            Geoshape.Point point = fieldValue.getPoint();
            return "POINT(" + point.getLongitude() + " " + point.getLatitude() + ")";
        } else {
            throw new PermanentBackendException("Cannot index " + fieldValue.getType());
        }
    }
}
