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
name|spi
operator|.
name|lifecycle
package|;
end_package

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
name|api
operator|.
name|CommitFailedException
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
name|plugins
operator|.
name|index
operator|.
name|IndexHookManager
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
name|plugins
operator|.
name|index
operator|.
name|IndexHookProvider
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
name|plugins
operator|.
name|memory
operator|.
name|MemoryNodeState
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
name|NodeBuilder
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
name|NodeStoreBranch
import|;
end_import

begin_class
specifier|public
class|class
name|OakInitializer
block|{
specifier|public
specifier|static
name|void
name|initialize
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|RepositoryInitializer
name|initializer
parameter_list|,
name|IndexHookProvider
name|indexHook
parameter_list|)
block|{
comment|// TODO refactor initializer to be able to first #branch, then
comment|// #initialize, next #index and finally #merge
comment|// This means that the RepositoryInitializer should receive a
comment|// NodeStoreBranch as a param
name|initializer
operator|.
name|initialize
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|NodeStoreBranch
name|branch
init|=
name|store
operator|.
name|branch
argument_list|()
decl_stmt|;
name|NodeBuilder
name|root
init|=
name|branch
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
try|try
block|{
name|branch
operator|.
name|setRoot
argument_list|(
name|IndexHookManager
operator|.
name|of
argument_list|(
name|indexHook
argument_list|)
operator|.
name|processCommit
argument_list|(
name|MemoryNodeState
operator|.
name|EMPTY_NODE
argument_list|,
name|root
operator|.
name|getNodeState
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|branch
operator|.
name|merge
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

