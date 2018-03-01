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
name|index
operator|.
name|indexer
operator|.
name|document
operator|.
name|flatfile
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|index
operator|.
name|indexer
operator|.
name|document
operator|.
name|NodeStateEntry
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
name|blob
operator|.
name|BlobStore
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
name|blob
operator|.
name|MemoryBlobStore
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
name|EqualsDiff
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
name|collect
operator|.
name|ImmutableList
operator|.
name|copyOf
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
name|commons
operator|.
name|PathUtils
operator|.
name|elements
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
name|index
operator|.
name|indexer
operator|.
name|document
operator|.
name|flatfile
operator|.
name|NodeStateEntryWriter
operator|.
name|getPath
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|NodeStateEntryWriterTest
block|{
specifier|private
name|BlobStore
name|blobStore
init|=
operator|new
name|MemoryBlobStore
argument_list|()
decl_stmt|;
specifier|private
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|newLines
parameter_list|()
block|{
name|NodeStateEntryWriter
name|nw
init|=
operator|new
name|NodeStateEntryWriter
argument_list|(
name|blobStore
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"foo2"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"foo3"
argument_list|,
literal|"text with \n new line"
argument_list|)
expr_stmt|;
name|String
name|line
init|=
name|nw
operator|.
name|toString
argument_list|(
operator|new
name|NodeStateEntry
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"/a"
argument_list|)
argument_list|)
decl_stmt|;
name|NodeStateEntryReader
name|nr
init|=
operator|new
name|NodeStateEntryReader
argument_list|(
name|blobStore
argument_list|)
decl_stmt|;
name|NodeStateEntry
name|ne
init|=
name|nr
operator|.
name|read
argument_list|(
name|line
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"/a"
argument_list|,
name|ne
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/a"
argument_list|,
name|NodeStateEntryWriter
operator|.
name|getPath
argument_list|(
name|line
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|EqualsDiff
operator|.
name|equals
argument_list|(
name|ne
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|multipleEntries
parameter_list|()
block|{
name|NodeStateEntryWriter
name|nw
init|=
operator|new
name|NodeStateEntryWriter
argument_list|(
name|blobStore
argument_list|)
decl_stmt|;
name|NodeBuilder
name|b1
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b1
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|NodeBuilder
name|b2
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b2
operator|.
name|setProperty
argument_list|(
literal|"foo2"
argument_list|,
literal|"bar2"
argument_list|)
expr_stmt|;
name|NodeStateEntry
name|e1
init|=
operator|new
name|NodeStateEntry
argument_list|(
name|b1
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"/a"
argument_list|)
decl_stmt|;
name|NodeStateEntry
name|e2
init|=
operator|new
name|NodeStateEntry
argument_list|(
name|b2
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"/a"
argument_list|)
decl_stmt|;
name|String
name|line1
init|=
name|nw
operator|.
name|toString
argument_list|(
name|e1
argument_list|)
decl_stmt|;
name|String
name|line2
init|=
name|nw
operator|.
name|toString
argument_list|(
name|e2
argument_list|)
decl_stmt|;
name|NodeStateEntryReader
name|nr
init|=
operator|new
name|NodeStateEntryReader
argument_list|(
name|blobStore
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|e1
argument_list|,
name|nr
operator|.
name|read
argument_list|(
name|line1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|e2
argument_list|,
name|nr
operator|.
name|read
argument_list|(
name|line2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|childOrderNotWritten
parameter_list|()
block|{
name|NodeStateEntryWriter
name|nw
init|=
operator|new
name|NodeStateEntryWriter
argument_list|(
name|blobStore
argument_list|)
decl_stmt|;
name|NodeBuilder
name|b1
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b1
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|b1
operator|.
name|setProperty
argument_list|(
literal|":childOrder"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|b1
operator|.
name|setProperty
argument_list|(
literal|":hidden"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|NodeStateEntry
name|e1
init|=
operator|new
name|NodeStateEntry
argument_list|(
name|b1
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"/a"
argument_list|)
decl_stmt|;
name|String
name|line
init|=
name|nw
operator|.
name|toString
argument_list|(
name|e1
argument_list|)
decl_stmt|;
name|NodeStateEntryReader
name|nr
init|=
operator|new
name|NodeStateEntryReader
argument_list|(
name|blobStore
argument_list|)
decl_stmt|;
name|NodeStateEntry
name|r1
init|=
name|nr
operator|.
name|read
argument_list|(
name|line
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|r1
operator|.
name|getNodeState
argument_list|()
operator|.
name|hasProperty
argument_list|(
literal|":hidden"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|r1
operator|.
name|getNodeState
argument_list|()
operator|.
name|hasProperty
argument_list|(
literal|":childOrder"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|pathElements
parameter_list|()
block|{
name|NodeStateEntryWriter
name|nw
init|=
operator|new
name|NodeStateEntryWriter
argument_list|(
name|blobStore
argument_list|)
decl_stmt|;
name|NodeBuilder
name|b1
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b1
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|NodeStateEntry
name|e1
init|=
operator|new
name|NodeStateEntry
argument_list|(
name|b1
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"/a/b/c/d"
argument_list|)
decl_stmt|;
name|String
name|json
init|=
name|nw
operator|.
name|asJson
argument_list|(
name|e1
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|pathElements
init|=
name|copyOf
argument_list|(
name|elements
argument_list|(
name|e1
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
name|nw
operator|.
name|toString
argument_list|(
name|pathElements
argument_list|,
name|json
argument_list|)
decl_stmt|;
name|NodeStateEntryReader
name|nr
init|=
operator|new
name|NodeStateEntryReader
argument_list|(
name|blobStore
argument_list|)
decl_stmt|;
name|NodeStateEntry
name|r1
init|=
name|nr
operator|.
name|read
argument_list|(
name|line
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|r1
operator|.
name|getNodeState
argument_list|()
operator|.
name|hasProperty
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/a/b/c/d"
argument_list|,
name|r1
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|pathElements_root
parameter_list|()
block|{
name|NodeStateEntryWriter
name|nw
init|=
operator|new
name|NodeStateEntryWriter
argument_list|(
name|blobStore
argument_list|)
decl_stmt|;
name|NodeBuilder
name|b1
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b1
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|NodeStateEntry
name|e1
init|=
operator|new
name|NodeStateEntry
argument_list|(
name|b1
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
name|String
name|json
init|=
name|nw
operator|.
name|asJson
argument_list|(
name|e1
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|pathElements
init|=
name|copyOf
argument_list|(
name|elements
argument_list|(
name|e1
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
name|nw
operator|.
name|toString
argument_list|(
name|pathElements
argument_list|,
name|json
argument_list|)
decl_stmt|;
name|NodeStateEntryReader
name|nr
init|=
operator|new
name|NodeStateEntryReader
argument_list|(
name|blobStore
argument_list|)
decl_stmt|;
name|NodeStateEntry
name|r1
init|=
name|nr
operator|.
name|read
argument_list|(
name|line
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|r1
operator|.
name|getNodeState
argument_list|()
operator|.
name|hasProperty
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/"
argument_list|,
name|r1
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|memUsage
parameter_list|()
block|{
name|NodeStateEntryWriter
name|nw
init|=
operator|new
name|NodeStateEntryWriter
argument_list|(
name|blobStore
argument_list|)
decl_stmt|;
name|NodeBuilder
name|b
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|String
name|json1
init|=
name|nw
operator|.
name|asJson
argument_list|(
name|b
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
literal|"foo1"
argument_list|,
literal|"bar1"
argument_list|)
expr_stmt|;
name|String
name|json2
init|=
name|nw
operator|.
name|asJson
argument_list|(
name|b
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|line1
init|=
name|nw
operator|.
name|toString
argument_list|(
name|copyOf
argument_list|(
name|elements
argument_list|(
literal|"/"
argument_list|)
argument_list|)
argument_list|,
name|json1
argument_list|)
decl_stmt|;
name|String
name|line2
init|=
name|nw
operator|.
name|toString
argument_list|(
name|copyOf
argument_list|(
name|elements
argument_list|(
literal|"/sub-node"
argument_list|)
argument_list|)
argument_list|,
name|json1
argument_list|)
decl_stmt|;
name|String
name|line3
init|=
name|nw
operator|.
name|toString
argument_list|(
name|copyOf
argument_list|(
name|elements
argument_list|(
literal|"/sub-node"
argument_list|)
argument_list|)
argument_list|,
name|json2
argument_list|)
decl_stmt|;
name|NodeStateEntryReader
name|nr
init|=
operator|new
name|NodeStateEntryReader
argument_list|(
name|blobStore
argument_list|)
decl_stmt|;
name|long
name|size1
init|=
name|nr
operator|.
name|read
argument_list|(
name|line1
argument_list|)
operator|.
name|estimatedMemUsage
argument_list|()
decl_stmt|;
name|long
name|size2
init|=
name|nr
operator|.
name|read
argument_list|(
name|line2
argument_list|)
operator|.
name|estimatedMemUsage
argument_list|()
decl_stmt|;
name|long
name|size3
init|=
name|nr
operator|.
name|read
argument_list|(
name|line3
argument_list|)
operator|.
name|estimatedMemUsage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Mem usage should be more than 0"
argument_list|,
name|size1
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Mem usage should increase with longer path"
argument_list|,
name|size2
operator|>
name|size1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Mem usage should increase with bigger node state"
argument_list|,
name|size3
operator|>
name|size2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

