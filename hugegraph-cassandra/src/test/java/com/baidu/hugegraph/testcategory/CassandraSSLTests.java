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

package com.baidu.hugegraph.testcategory;

/**
 * This is a JUnit category for tests that need to run against Cassandra configured for SSL-based client authentication.
 *
 * If you rename or move this class, then you must also update mentions of it in the Cassandra module's pom.xml.
 */
public interface CassandraSSLTests {
}
