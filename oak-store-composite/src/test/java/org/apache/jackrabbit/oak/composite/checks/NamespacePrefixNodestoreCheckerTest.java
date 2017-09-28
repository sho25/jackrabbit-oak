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
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|InitialContent
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
name|Tree
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
name|NamespacePrefixNodestoreChecker
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
name|plugins
operator|.
name|tree
operator|.
name|TreeUtil
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

begin_class
specifier|public
class|class
name|NamespacePrefixNodestoreCheckerTest
block|{
annotation|@
name|Rule
specifier|public
name|ExpectedException
name|exception
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
specifier|private
name|MemoryNodeStore
name|mount
decl_stmt|;
specifier|private
name|NamespacePrefixNodestoreChecker
name|checker
decl_stmt|;
specifier|private
name|Context
name|context
decl_stmt|;
specifier|private
name|MountInfoProvider
name|mip
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|prepareRepository
parameter_list|()
throws|throws
name|Exception
block|{
name|MemoryNodeStore
name|root
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|mount
operator|=
operator|new
name|MemoryNodeStore
argument_list|()
expr_stmt|;
name|NodeBuilder
name|rootBuilder
init|=
name|root
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
operator|new
name|InitialContent
argument_list|()
operator|.
name|initialize
argument_list|(
name|rootBuilder
argument_list|)
expr_stmt|;
name|root
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
name|mip
operator|=
name|Mounts
operator|.
name|newBuilder
argument_list|()
operator|.
name|readOnlyMount
argument_list|(
literal|"first"
argument_list|,
literal|"/first"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|checker
operator|=
operator|new
name|NamespacePrefixNodestoreChecker
argument_list|()
expr_stmt|;
name|context
operator|=
name|checker
operator|.
name|createContext
argument_list|(
name|root
argument_list|,
name|mip
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|invalidNamespacePrefix_node
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|builder
init|=
name|mount
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"libs"
argument_list|)
operator|.
name|child
argument_list|(
literal|"foo:first"
argument_list|)
expr_stmt|;
name|mount
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
literal|"invalid namespace prefix foo"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"/libs/foo:first"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|check
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|Tree
name|tree
init|=
name|TreeUtil
operator|.
name|getTree
argument_list|(
name|TreeFactory
operator|.
name|createReadOnlyTree
argument_list|(
name|mount
operator|.
name|getRoot
argument_list|()
argument_list|)
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|ErrorHolder
name|errorHolder
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
literal|"first"
argument_list|)
argument_list|,
name|mount
argument_list|)
argument_list|,
name|tree
argument_list|,
name|errorHolder
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|errorHolder
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|invalidNamespacePrefix_property
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|builder
init|=
name|mount
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"libs"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo:prop"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|mount
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
literal|"invalid namespace prefix foo"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"/libs"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|validNamespacePrefix
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|builder
init|=
name|mount
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"libs"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:prop"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|mount
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
name|check
argument_list|(
literal|"/libs/jcr:prop"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|noNamespacePrefix
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|builder
init|=
name|mount
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"libs"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|mount
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
name|check
argument_list|(
literal|"/libs"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

