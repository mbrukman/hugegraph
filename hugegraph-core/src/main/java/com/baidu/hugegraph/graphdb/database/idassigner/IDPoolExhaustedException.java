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

package com.baidu.hugegraph.graphdb.database.idassigner;

import com.baidu.hugegraph.core.HugeGraphException;

/**
 */

public class IDPoolExhaustedException extends HugeGraphException {

    public IDPoolExhaustedException(String msg) {
        super(msg);
    }

    public IDPoolExhaustedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public IDPoolExhaustedException(Throwable cause) {
        super(cause);
    }

}