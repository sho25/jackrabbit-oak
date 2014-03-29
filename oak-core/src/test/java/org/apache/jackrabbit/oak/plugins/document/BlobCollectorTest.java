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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|Blob
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
name|document
operator|.
name|util
operator|.
name|Utils
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
name|PropertyBuilder
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
name|After
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
name|MongoBlobGCTest
operator|.
name|randomStream
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

begin_class
specifier|public
class|class
name|BlobCollectorTest
block|{
specifier|private
name|DocumentNodeStore
name|store
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
specifier|private
name|BlobCollector
name|blobCollector
init|=
operator|new
name|BlobCollector
argument_list|(
name|store
argument_list|)
decl_stmt|;
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|store
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCollect
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|b1
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Blob
argument_list|>
name|blobs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|b1
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|child
argument_list|(
literal|"y"
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|b1
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
comment|//1. Set some single value Binary property
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|b1
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|Blob
name|b
init|=
name|store
operator|.
name|createBlob
argument_list|(
name|randomStream
argument_list|(
name|i
argument_list|,
literal|4096
argument_list|)
argument_list|)
decl_stmt|;
name|b1
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|child
argument_list|(
literal|"y"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"b"
operator|+
name|i
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|blobs
operator|.
name|add
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|b1
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
comment|//2. Set some multi value property
name|PropertyBuilder
argument_list|<
name|Blob
argument_list|>
name|p1
init|=
name|PropertyBuilder
operator|.
name|array
argument_list|(
name|Type
operator|.
name|BINARY
argument_list|)
operator|.
name|setName
argument_list|(
literal|"barr"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|Blob
name|b
init|=
name|store
operator|.
name|createBlob
argument_list|(
name|randomStream
argument_list|(
name|i
argument_list|,
literal|4096
argument_list|)
argument_list|)
decl_stmt|;
name|p1
operator|.
name|addValue
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|blobs
operator|.
name|add
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
name|b1
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|b1
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|child
argument_list|(
literal|"y"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|p1
operator|.
name|getPropertyState
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|b1
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
comment|//3. Create some new rev for the property b1 and b2
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|b1
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
comment|//Change the see to create diff binary
name|Blob
name|b
init|=
name|store
operator|.
name|createBlob
argument_list|(
name|randomStream
argument_list|(
name|i
operator|+
literal|1
argument_list|,
literal|4096
argument_list|)
argument_list|)
decl_stmt|;
name|b1
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|child
argument_list|(
literal|"y"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"b"
operator|+
name|i
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|blobs
operator|.
name|add
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|b1
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
name|NodeDocument
name|doc
init|=
name|store
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/x/y"
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Blob
argument_list|>
name|collectedBlobs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|blobCollector
operator|.
name|collect
argument_list|(
name|doc
argument_list|,
name|collectedBlobs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|blobs
operator|.
name|size
argument_list|()
argument_list|,
name|collectedBlobs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|HashSet
argument_list|<
name|Blob
argument_list|>
argument_list|(
name|blobs
argument_list|)
argument_list|,
operator|new
name|HashSet
argument_list|<
name|Blob
argument_list|>
argument_list|(
name|collectedBlobs
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

