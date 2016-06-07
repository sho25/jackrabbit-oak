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
name|segment
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
name|segment
operator|.
name|SegmentWriterBuilder
operator|.
name|segmentWriterBuilder
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
name|assertFalse
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|base
operator|.
name|Suppliers
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
name|Oak
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
name|segment
operator|.
name|compaction
operator|.
name|SegmentGCOptions
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
name|segment
operator|.
name|memory
operator|.
name|MemoryStore
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
name|security
operator|.
name|OpenSecurityProvider
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
name|NodeState
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
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|CompactorTest
block|{
specifier|private
name|MemoryStore
name|memoryStore
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|openSegmentStore
parameter_list|()
throws|throws
name|IOException
block|{
name|memoryStore
operator|=
operator|new
name|MemoryStore
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCompactor
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeStore
name|store
init|=
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|memoryStore
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|init
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|SegmentWriter
name|writer
init|=
name|segmentWriterBuilder
argument_list|(
literal|"c"
argument_list|)
operator|.
name|withGeneration
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|(
name|memoryStore
argument_list|)
decl_stmt|;
name|Compactor
name|compactor
init|=
operator|new
name|Compactor
argument_list|(
name|memoryStore
operator|.
name|getReader
argument_list|()
argument_list|,
name|writer
argument_list|,
name|memoryStore
operator|.
name|getBlobStore
argument_list|()
argument_list|,
name|Suppliers
operator|.
name|ofInstance
argument_list|(
literal|false
argument_list|)
argument_list|,
name|SegmentGCOptions
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|addTestContent
argument_list|(
name|store
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|NodeState
name|initial
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|SegmentNodeState
name|after
init|=
name|compactor
operator|.
name|compact
argument_list|(
name|initial
argument_list|,
name|store
operator|.
name|getRoot
argument_list|()
argument_list|,
name|initial
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
argument_list|,
name|after
argument_list|)
expr_stmt|;
name|addTestContent
argument_list|(
name|store
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|after
operator|=
name|compactor
operator|.
name|compact
argument_list|(
name|initial
argument_list|,
name|store
operator|.
name|getRoot
argument_list|()
argument_list|,
name|initial
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCancel
parameter_list|()
throws|throws
name|Throwable
block|{
comment|// Create a Compactor that will cancel itself as soon as possible. The
comment|// early cancellation is the reason why the returned SegmentNodeState
comment|// doesn't have the child named "b".
name|NodeStore
name|store
init|=
name|SegmentNodeStoreBuilders
operator|.
name|builder
argument_list|(
name|memoryStore
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|SegmentWriter
name|writer
init|=
name|segmentWriterBuilder
argument_list|(
literal|"c"
argument_list|)
operator|.
name|withGeneration
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|(
name|memoryStore
argument_list|)
decl_stmt|;
name|Compactor
name|compactor
init|=
operator|new
name|Compactor
argument_list|(
name|memoryStore
operator|.
name|getReader
argument_list|()
argument_list|,
name|writer
argument_list|,
name|memoryStore
operator|.
name|getBlobStore
argument_list|()
argument_list|,
name|Suppliers
operator|.
name|ofInstance
argument_list|(
literal|true
argument_list|)
argument_list|,
name|SegmentGCOptions
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|SegmentNodeState
name|sns
init|=
name|compactor
operator|.
name|compact
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
argument_list|,
name|addChild
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|"b"
argument_list|)
argument_list|,
name|store
operator|.
name|getRoot
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|sns
operator|.
name|hasChildNode
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|NodeState
name|addChild
parameter_list|(
name|NodeState
name|current
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|NodeBuilder
name|builder
init|=
name|current
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|getNodeState
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|void
name|init
parameter_list|(
name|NodeStore
name|store
parameter_list|)
block|{
operator|new
name|Oak
argument_list|(
name|store
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
operator|.
name|createContentRepository
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|addTestContent
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|int
name|index
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|builder
init|=
name|store
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
literal|"test"
operator|+
name|index
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"child"
operator|+
name|index
argument_list|)
expr_stmt|;
name|store
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

