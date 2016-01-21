begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|commons
operator|.
name|FixturesHelper
operator|.
name|Fixture
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|state
operator|.
name|NodeStore
import|;
end_import

begin_comment
comment|/**  * NodeStore fixture for parametrized tests.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|NodeStoreFixture
block|{
comment|/**      * Creates a new empty {@link NodeStore} instance. An implementation must      * ensure the returned node store is indeed empty and is independent from      * instances returned from previous calls to this method.      *      * @return a new node store instance.      */
specifier|public
specifier|abstract
name|NodeStore
name|createNodeStore
parameter_list|()
function_decl|;
comment|/**      * Create a new cluster node that is attached to the same backend storage.      *       * @param clusterNodeId the cluster node id      * @return the node store, or null if clustering is not supported      */
specifier|public
name|NodeStore
name|createNodeStore
parameter_list|(
name|int
name|clusterNodeId
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|dispose
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|)
block|{     }
specifier|public
name|boolean
name|isAvailable
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

