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
name|plugins
operator|.
name|document
operator|.
name|bundlor
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
name|api
operator|.
name|Type
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
name|MemoryNodeStore
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
name|commit
operator|.
name|CommitInfo
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
name|commit
operator|.
name|EmptyHook
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|MoreExecutors
operator|.
name|sameThreadExecutor
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonList
import|;
end_import

begin_import
import|import static
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
name|document
operator|.
name|bundlor
operator|.
name|BundlingConfigHandler
operator|.
name|BUNDLOR
import|;
end_import

begin_import
import|import static
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
name|document
operator|.
name|bundlor
operator|.
name|BundlingConfigHandler
operator|.
name|DOCUMENT_NODE_STORE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_class
specifier|public
class|class
name|BundlingConfigHandlerTest
block|{
specifier|private
name|BundlingConfigHandler
name|configHandler
init|=
operator|new
name|BundlingConfigHandler
argument_list|()
decl_stmt|;
specifier|private
name|MemoryNodeStore
name|nodeStore
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|defaultSetup
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNotNull
argument_list|(
name|configHandler
operator|.
name|getRegistry
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|configHandler
operator|.
name|newBundlingHandler
argument_list|()
argument_list|)
expr_stmt|;
comment|//Close should work without init also
name|configHandler
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|detectRegistryUpdate
parameter_list|()
throws|throws
name|Exception
block|{
name|configHandler
operator|.
name|initialize
argument_list|(
name|nodeStore
argument_list|,
name|sameThreadExecutor
argument_list|()
argument_list|)
expr_stmt|;
name|addBundlorConfigForAsset
argument_list|()
expr_stmt|;
name|BundledTypesRegistry
name|registry
init|=
name|configHandler
operator|.
name|getRegistry
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|registry
operator|.
name|getBundlors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|DocumentBundlor
name|assetBundlor
init|=
name|registry
operator|.
name|getBundlors
argument_list|()
operator|.
name|get
argument_list|(
literal|"app:Asset"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|assetBundlor
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addBundlorConfigForAsset
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|builder
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|bundlor
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"jcr:system"
argument_list|)
operator|.
name|child
argument_list|(
name|DOCUMENT_NODE_STORE
argument_list|)
operator|.
name|child
argument_list|(
name|BUNDLOR
argument_list|)
decl_stmt|;
name|bundlor
operator|.
name|child
argument_list|(
literal|"app:Asset"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|DocumentBundlor
operator|.
name|PROP_PATTERN
argument_list|,
name|singletonList
argument_list|(
literal|"metadata"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|nodeStore
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
