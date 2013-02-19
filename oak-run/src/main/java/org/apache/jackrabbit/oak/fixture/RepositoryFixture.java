begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|fixture
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
import|;
end_import

begin_interface
specifier|public
interface|interface
name|RepositoryFixture
block|{
comment|/**      * Checks whether this fixture is currently available. For example      * a database-based fixture would only be available when the underlying      * database service is running.      *      * @return {@code true} iff the fixture is available      */
name|boolean
name|isAvailable
parameter_list|()
function_decl|;
comment|/**      * Creates a new repository cluster with as many nodes as the given      * array has elements. References to the cluster nodes are stored in      * the given array. The initial state of the cluster consists of just      * the default repository content included by the implementation. The      * caller of this method should have exclusive access to the created      * cluster. The caller is also responsible for calling      * {@link #tearDownCluster(Repository[])} when the test cluster is      * no longer needed.      *      * @param cluster array to which references to all nodes of the      *                created cluster should be stored      * @throws Exception if the cluster could not be set up      */
name|void
name|setUpCluster
parameter_list|(
name|Repository
index|[]
name|cluster
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Ensures that all content changes seen by one of the given cluster      * nodes are seen also by all the other given nodes. Used to help      * testing features like eventual consistency where the normal APIs      * don't make strong enough guarantees to enable writing a test case      * without a potentially unbounded wait for changes to propagate      * across the cluster.      *      * @param nodes cluster nodes to be synchronized      */
name|void
name|syncRepositoryCluster
parameter_list|(
name|Repository
modifier|...
name|nodes
parameter_list|)
function_decl|;
comment|/**      * Releases resources associated with the given repository cluster.      * The caller of {@link #setUpCluster(Repository[])} shall call this      * method once the cluster is no longer needed.      *      * @param cluster array containing references to all nodes of the cluster      */
name|void
name|tearDownCluster
parameter_list|(
name|Repository
index|[]
name|cluster
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

