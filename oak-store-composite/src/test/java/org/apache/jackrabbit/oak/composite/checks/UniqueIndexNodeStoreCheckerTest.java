begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
operator|.
name|checks
package|;
end_package

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
name|index
operator|.
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
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
name|index
operator|.
name|IndexUtils
operator|.
name|createIndexDefinition
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
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
name|composite
operator|.
name|MountedNodeStore
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
name|UniqueIndexNodeStoreChecker
operator|.
name|Context
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
name|IndexUpdateProvider
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
name|property
operator|.
name|PropertyIndexEditorProvider
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
name|tree
operator|.
name|factories
operator|.
name|TreeFactory
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
name|EditorHook
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
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|ExpectedException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
import|;
end_import

begin_class
specifier|public
class|class
name|UniqueIndexNodeStoreCheckerTest
block|{
annotation|@
name|Rule
specifier|public
specifier|final
name|ExpectedException
name|exception
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
specifier|private
name|MountInfoProvider
name|mip
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|prepare
parameter_list|()
block|{
name|mip
operator|=
name|Mounts
operator|.
name|newBuilder
argument_list|()
operator|.
name|readOnlyMount
argument_list|(
literal|"libs"
argument_list|,
literal|"/libs"
argument_list|,
literal|"/libs2"
argument_list|)
operator|.
name|readOnlyMount
argument_list|(
literal|"apps"
argument_list|,
literal|"/apps"
argument_list|,
literal|"/apps2"
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
name|uuidConflict_twoStores
parameter_list|()
throws|throws
name|Exception
block|{
name|MemoryNodeStore
name|globalStore
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|MemoryNodeStore
name|mountedStore
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|populateStore
argument_list|(
name|globalStore
argument_list|,
name|b
lambda|->
name|b
operator|.
name|child
argument_list|(
literal|"first"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|populateStore
argument_list|(
name|mountedStore
argument_list|,
name|b
lambda|->
name|b
operator|.
name|child
argument_list|(
literal|"libs"
argument_list|)
operator|.
name|child
argument_list|(
literal|"first"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|UniqueIndexNodeStoreChecker
name|checker
init|=
operator|new
name|UniqueIndexNodeStoreChecker
argument_list|()
decl_stmt|;
name|Context
name|ctx
init|=
name|checker
operator|.
name|createContext
argument_list|(
name|globalStore
argument_list|,
name|mip
argument_list|)
decl_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|IllegalRepositoryStateException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"1 errors were found"
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"clash for value bar: 'duplicate unique index entry'"
argument_list|)
expr_stmt|;
name|ErrorHolder
name|error
init|=
operator|new
name|ErrorHolder
argument_list|()
decl_stmt|;
name|checker
operator|.
name|check
argument_list|(
operator|new
name|MountedNodeStore
argument_list|(
name|mip
operator|.
name|getMountByName
argument_list|(
literal|"libs"
argument_list|)
argument_list|,
name|mountedStore
argument_list|)
argument_list|,
name|TreeFactory
operator|.
name|createReadOnlyTree
argument_list|(
name|mountedStore
operator|.
name|getRoot
argument_list|()
argument_list|)
argument_list|,
name|error
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|error
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|uuidConflict_threeStores
parameter_list|()
throws|throws
name|Exception
block|{
name|MemoryNodeStore
name|globalStore
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|MemoryNodeStore
name|mountedStore
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|MemoryNodeStore
name|mountedStore2
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|populateStore
argument_list|(
name|globalStore
argument_list|,
name|b
lambda|->
name|b
operator|.
name|child
argument_list|(
literal|"first"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|populateStore
argument_list|(
name|globalStore
argument_list|,
name|b
lambda|->
name|b
operator|.
name|child
argument_list|(
literal|"second"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
expr_stmt|;
name|populateStore
argument_list|(
name|mountedStore
argument_list|,
name|b
lambda|->
name|b
operator|.
name|child
argument_list|(
literal|"libs"
argument_list|)
operator|.
name|child
argument_list|(
literal|"first"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|populateStore
argument_list|(
name|mountedStore2
argument_list|,
name|b
lambda|->
name|b
operator|.
name|child
argument_list|(
literal|"apps"
argument_list|)
operator|.
name|child
argument_list|(
literal|"first"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
expr_stmt|;
name|UniqueIndexNodeStoreChecker
name|checker
init|=
operator|new
name|UniqueIndexNodeStoreChecker
argument_list|()
decl_stmt|;
name|Context
name|ctx
init|=
name|checker
operator|.
name|createContext
argument_list|(
name|globalStore
argument_list|,
name|mip
argument_list|)
decl_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|IllegalRepositoryStateException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"2 errors were found"
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"clash for value bar: 'duplicate unique index entry'"
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"clash for value baz: 'duplicate unique index entry'"
argument_list|)
expr_stmt|;
name|ErrorHolder
name|error
init|=
operator|new
name|ErrorHolder
argument_list|()
decl_stmt|;
name|checker
operator|.
name|check
argument_list|(
operator|new
name|MountedNodeStore
argument_list|(
name|mip
operator|.
name|getMountByName
argument_list|(
literal|"libs"
argument_list|)
argument_list|,
name|mountedStore
argument_list|)
argument_list|,
name|TreeFactory
operator|.
name|createReadOnlyTree
argument_list|(
name|mountedStore
operator|.
name|getRoot
argument_list|()
argument_list|)
argument_list|,
name|error
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|checker
operator|.
name|check
argument_list|(
operator|new
name|MountedNodeStore
argument_list|(
name|mip
operator|.
name|getMountByName
argument_list|(
literal|"apps"
argument_list|)
argument_list|,
name|mountedStore2
argument_list|)
argument_list|,
name|TreeFactory
operator|.
name|createReadOnlyTree
argument_list|(
name|mountedStore
operator|.
name|getRoot
argument_list|()
argument_list|)
argument_list|,
name|error
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|error
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|noConflict
parameter_list|()
throws|throws
name|Exception
block|{
name|MemoryNodeStore
name|globalStore
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|MemoryNodeStore
name|mountedStore
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|populateStore
argument_list|(
name|globalStore
argument_list|,
name|b
lambda|->
name|b
operator|.
name|child
argument_list|(
literal|"first"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
expr_stmt|;
name|populateStore
argument_list|(
name|mountedStore
argument_list|,
name|b
lambda|->
name|b
operator|.
name|child
argument_list|(
literal|"libs"
argument_list|)
operator|.
name|child
argument_list|(
literal|"first"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|UniqueIndexNodeStoreChecker
name|checker
init|=
operator|new
name|UniqueIndexNodeStoreChecker
argument_list|()
decl_stmt|;
name|Context
name|ctx
init|=
name|checker
operator|.
name|createContext
argument_list|(
name|globalStore
argument_list|,
name|mip
argument_list|)
decl_stmt|;
name|ErrorHolder
name|error
init|=
operator|new
name|ErrorHolder
argument_list|()
decl_stmt|;
name|checker
operator|.
name|check
argument_list|(
operator|new
name|MountedNodeStore
argument_list|(
name|mip
operator|.
name|getMountByName
argument_list|(
literal|"libs"
argument_list|)
argument_list|,
name|mountedStore
argument_list|)
argument_list|,
name|TreeFactory
operator|.
name|createReadOnlyTree
argument_list|(
name|mountedStore
operator|.
name|getRoot
argument_list|()
argument_list|)
argument_list|,
name|error
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|error
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
comment|/**      * Tests that if a mount has an index clash but the path does not belong to the mount no error is reported      */
annotation|@
name|Test
specifier|public
name|void
name|noConflict_mountHasDuplicateOutsideOfPath
parameter_list|()
throws|throws
name|Exception
block|{
name|MemoryNodeStore
name|globalStore
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|MemoryNodeStore
name|mountedStore
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|populateStore
argument_list|(
name|globalStore
argument_list|,
name|b
lambda|->
name|b
operator|.
name|child
argument_list|(
literal|"first"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|populateStore
argument_list|(
name|mountedStore
argument_list|,
name|b
lambda|->
name|b
operator|.
name|child
argument_list|(
literal|"second"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|UniqueIndexNodeStoreChecker
name|checker
init|=
operator|new
name|UniqueIndexNodeStoreChecker
argument_list|()
decl_stmt|;
name|Context
name|ctx
init|=
name|checker
operator|.
name|createContext
argument_list|(
name|globalStore
argument_list|,
name|mip
argument_list|)
decl_stmt|;
name|ErrorHolder
name|error
init|=
operator|new
name|ErrorHolder
argument_list|()
decl_stmt|;
name|checker
operator|.
name|check
argument_list|(
operator|new
name|MountedNodeStore
argument_list|(
name|mip
operator|.
name|getMountByName
argument_list|(
literal|"libs"
argument_list|)
argument_list|,
name|mountedStore
argument_list|)
argument_list|,
name|TreeFactory
operator|.
name|createReadOnlyTree
argument_list|(
name|mountedStore
operator|.
name|getRoot
argument_list|()
argument_list|)
argument_list|,
name|error
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|error
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
comment|/**      * Tests that if a mount has an index clash but the path does not belog to the global mount no error is reported        *       */
annotation|@
name|Test
specifier|public
name|void
name|noConflict_globalMountHasDuplicateOutsideOfPath
parameter_list|()
throws|throws
name|Exception
block|{
name|MemoryNodeStore
name|globalStore
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|MemoryNodeStore
name|mountedStore
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|populateStore
argument_list|(
name|globalStore
argument_list|,
name|b
lambda|->
name|b
operator|.
name|child
argument_list|(
literal|"libs"
argument_list|)
operator|.
name|child
argument_list|(
literal|"first"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|populateStore
argument_list|(
name|mountedStore
argument_list|,
name|b
lambda|->
name|b
operator|.
name|child
argument_list|(
literal|"libs"
argument_list|)
operator|.
name|child
argument_list|(
literal|"second"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|UniqueIndexNodeStoreChecker
name|checker
init|=
operator|new
name|UniqueIndexNodeStoreChecker
argument_list|()
decl_stmt|;
name|Context
name|ctx
init|=
name|checker
operator|.
name|createContext
argument_list|(
name|globalStore
argument_list|,
name|mip
argument_list|)
decl_stmt|;
name|ErrorHolder
name|error
init|=
operator|new
name|ErrorHolder
argument_list|()
decl_stmt|;
name|checker
operator|.
name|check
argument_list|(
operator|new
name|MountedNodeStore
argument_list|(
name|mip
operator|.
name|getMountByName
argument_list|(
literal|"libs"
argument_list|)
argument_list|,
name|mountedStore
argument_list|)
argument_list|,
name|TreeFactory
operator|.
name|createReadOnlyTree
argument_list|(
name|mountedStore
operator|.
name|getRoot
argument_list|()
argument_list|)
argument_list|,
name|error
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|error
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|populateStore
parameter_list|(
name|NodeStore
name|ns
parameter_list|,
name|Consumer
argument_list|<
name|NodeBuilder
argument_list|>
name|action
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|builder
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|index
init|=
name|createIndexDefinition
argument_list|(
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
argument_list|,
literal|"foo"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|index
operator|.
name|setProperty
argument_list|(
literal|"entryCount"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|action
operator|.
name|accept
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|ns
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
operator|new
name|EditorHook
argument_list|(
operator|new
name|IndexUpdateProvider
argument_list|(
operator|new
name|PropertyIndexEditorProvider
argument_list|()
operator|.
name|with
argument_list|(
name|mip
argument_list|)
argument_list|)
argument_list|)
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

