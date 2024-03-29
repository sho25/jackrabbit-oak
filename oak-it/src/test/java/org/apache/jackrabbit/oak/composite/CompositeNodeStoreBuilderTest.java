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
name|composite
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|JcrConstants
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
name|IllegalRepositoryStateException
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
name|composite
operator|.
name|checks
operator|.
name|NodeStoreChecksService
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
name|composite
operator|.
name|checks
operator|.
name|NodeTypeMountedNodeStoreChecker
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
name|plugins
operator|.
name|memory
operator|.
name|PropertyStates
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
name|mount
operator|.
name|MountInfoProvider
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
name|mount
operator|.
name|Mounts
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

begin_class
specifier|public
class|class
name|CompositeNodeStoreBuilderTest
block|{
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|builderRejectsTooManyReadWriteStores_oneExtra
parameter_list|()
block|{
name|MountInfoProvider
name|mip
init|=
name|Mounts
operator|.
name|newBuilder
argument_list|()
operator|.
name|mount
argument_list|(
literal|"temp"
argument_list|,
literal|"/tmp"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
operator|new
name|CompositeNodeStore
operator|.
name|Builder
argument_list|(
name|mip
argument_list|,
operator|new
name|MemoryNodeStore
argument_list|()
argument_list|)
operator|.
name|addMount
argument_list|(
literal|"temp"
argument_list|,
operator|new
name|MemoryNodeStore
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|builderRejectsTooManyReadWriteStores_mixed
parameter_list|()
block|{
name|MountInfoProvider
name|mip
init|=
name|Mounts
operator|.
name|newBuilder
argument_list|()
operator|.
name|mount
argument_list|(
literal|"temp"
argument_list|,
literal|"/tmp"
argument_list|)
operator|.
name|readOnlyMount
argument_list|(
literal|"readOnly"
argument_list|,
literal|"/readOnly"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
operator|new
name|CompositeNodeStore
operator|.
name|Builder
argument_list|(
name|mip
argument_list|,
operator|new
name|MemoryNodeStore
argument_list|()
argument_list|)
operator|.
name|addMount
argument_list|(
literal|"temp"
argument_list|,
operator|new
name|MemoryNodeStore
argument_list|()
argument_list|)
operator|.
name|addMount
argument_list|(
literal|"readOnly"
argument_list|,
operator|new
name|MemoryNodeStore
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|builderAcceptsMultipleReadOnlyStores
parameter_list|()
block|{
name|MountInfoProvider
name|mip
init|=
name|Mounts
operator|.
name|newBuilder
argument_list|()
operator|.
name|readOnlyMount
argument_list|(
literal|"readOnly"
argument_list|,
literal|"/readOnly"
argument_list|)
operator|.
name|readOnlyMount
argument_list|(
literal|"readOnly2"
argument_list|,
literal|"/readOnly2"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
operator|new
name|CompositeNodeStore
operator|.
name|Builder
argument_list|(
name|mip
argument_list|,
operator|new
name|MemoryNodeStore
argument_list|()
argument_list|)
operator|.
name|addMount
argument_list|(
literal|"readOnly"
argument_list|,
operator|new
name|MemoryNodeStore
argument_list|()
argument_list|)
operator|.
name|addMount
argument_list|(
literal|"readOnly2"
argument_list|,
operator|new
name|MemoryNodeStore
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|mismatchBetweenMountsAndStoresIsRejected
parameter_list|()
block|{
name|MountInfoProvider
name|mip
init|=
name|Mounts
operator|.
name|newBuilder
argument_list|()
operator|.
name|mount
argument_list|(
literal|"temp"
argument_list|,
literal|"/tmp"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
operator|new
name|CompositeNodeStore
operator|.
name|Builder
argument_list|(
name|mip
argument_list|,
operator|new
name|MemoryNodeStore
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|NullPointerException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|mismatchBetweenMountNameAndStoreName
parameter_list|()
block|{
name|MountInfoProvider
name|mip
init|=
name|Mounts
operator|.
name|newBuilder
argument_list|()
operator|.
name|mount
argument_list|(
literal|"temp"
argument_list|,
literal|"/tmp"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
operator|new
name|CompositeNodeStore
operator|.
name|Builder
argument_list|(
name|mip
argument_list|,
operator|new
name|MemoryNodeStore
argument_list|()
argument_list|)
operator|.
name|addMount
argument_list|(
literal|"not-temp"
argument_list|,
operator|new
name|MemoryNodeStore
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalRepositoryStateException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|versionableNode
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|MemoryNodeStore
name|root
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|MemoryNodeStore
name|mount
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
comment|// create a child node that is versionable
comment|// note that we won't cover all checks here, we are only interested in seeing that at least one check is triggered
name|NodeBuilder
name|rootBuilder
init|=
name|mount
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|childNode
init|=
name|rootBuilder
operator|.
name|setChildNode
argument_list|(
literal|"readOnly"
argument_list|)
operator|.
name|setChildNode
argument_list|(
literal|"second"
argument_list|)
operator|.
name|setChildNode
argument_list|(
literal|"third"
argument_list|)
decl_stmt|;
name|childNode
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_ISCHECKEDOUT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|childNode
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|JcrConstants
operator|.
name|MIX_VERSIONABLE
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
expr_stmt|;
name|mount
operator|.
name|merge
argument_list|(
name|rootBuilder
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
name|MountInfoProvider
name|mip
init|=
name|Mounts
operator|.
name|newBuilder
argument_list|()
operator|.
name|readOnlyMount
argument_list|(
literal|"readOnly"
argument_list|,
literal|"/readOnly"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
operator|new
name|CompositeNodeStore
operator|.
name|Builder
argument_list|(
name|mip
argument_list|,
name|root
argument_list|)
operator|.
name|addMount
argument_list|(
literal|"readOnly"
argument_list|,
name|mount
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|NodeStoreChecksService
argument_list|(
name|mip
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|NodeTypeMountedNodeStoreChecker
argument_list|(
name|JcrConstants
operator|.
name|MIX_VERSIONABLE
argument_list|,
literal|"test error"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

