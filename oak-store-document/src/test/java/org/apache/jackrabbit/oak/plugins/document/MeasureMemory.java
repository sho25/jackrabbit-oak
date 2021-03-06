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
name|plugins
operator|.
name|document
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
name|document
operator|.
name|DocumentNodeState
operator|.
name|Children
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
name|fail
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|com
operator|.
name|mongodb
operator|.
name|BasicDBObject
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
name|PropertyState
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
name|PathUtils
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
name|RevisionsKey
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
name|BinaryPropertyState
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

begin_comment
comment|/**  * Simple test to measure how much memory a certain object uses.  */
end_comment

begin_class
specifier|public
class|class
name|MeasureMemory
block|{
specifier|static
specifier|final
name|boolean
name|TRACE
init|=
literal|true
decl_stmt|;
specifier|static
specifier|final
name|int
name|TEST_COUNT
init|=
literal|10000
decl_stmt|;
specifier|static
specifier|final
name|int
name|OVERHEAD
init|=
literal|24
decl_stmt|;
specifier|static
specifier|final
name|DocumentNodeStore
name|STORE
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
specifier|static
specifier|final
name|Blob
name|BLOB
decl_stmt|;
specifier|static
specifier|final
name|String
name|BLOB_VALUE
decl_stmt|;
static|static
block|{
try|try
block|{
name|BLOB
operator|=
name|STORE
operator|.
name|createBlob
argument_list|(
operator|new
name|RandomStream
argument_list|(
literal|1024
operator|*
literal|1024
argument_list|,
literal|42
argument_list|)
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|STORE
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
literal|"binary"
argument_list|)
operator|.
name|setProperty
argument_list|(
operator|new
name|BinaryPropertyState
argument_list|(
literal|"b"
argument_list|,
name|BLOB
argument_list|)
argument_list|)
expr_stmt|;
name|STORE
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
name|NodeState
name|n
init|=
name|STORE
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"binary"
argument_list|)
decl_stmt|;
name|BLOB_VALUE
operator|=
operator|(
operator|(
name|DocumentNodeState
operator|)
name|n
operator|)
operator|.
name|getPropertyAsString
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
annotation|@
name|Test
specifier|public
name|void
name|overhead
parameter_list|()
throws|throws
name|Exception
block|{
name|measureMemory
argument_list|(
operator|new
name|Callable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|call
parameter_list|()
block|{
return|return
operator|new
name|Object
index|[]
block|{
literal|""
block|,
name|OVERHEAD
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|node
parameter_list|()
throws|throws
name|Exception
block|{
name|measureMemory
argument_list|(
operator|new
name|Callable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|call
parameter_list|()
block|{
name|DocumentNodeState
name|n
init|=
name|generateNode
argument_list|(
literal|5
argument_list|)
decl_stmt|;
return|return
operator|new
name|Object
index|[]
block|{
name|n
block|,
name|n
operator|.
name|getMemory
argument_list|()
operator|+
name|OVERHEAD
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeWithoutProperties
parameter_list|()
throws|throws
name|Exception
block|{
name|measureMemory
argument_list|(
operator|new
name|Callable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|call
parameter_list|()
block|{
name|DocumentNodeState
name|n
init|=
name|generateNode
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
operator|new
name|Object
index|[]
block|{
name|n
block|,
name|n
operator|.
name|getMemory
argument_list|()
operator|+
name|OVERHEAD
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|basicObject
parameter_list|()
throws|throws
name|Exception
block|{
name|measureMemory
argument_list|(
operator|new
name|Callable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|call
parameter_list|()
block|{
name|BasicDBObject
name|n
init|=
name|generateBasicObject
argument_list|(
literal|15
argument_list|)
decl_stmt|;
return|return
operator|new
name|Object
index|[]
block|{
name|n
block|,
name|Utils
operator|.
name|estimateMemoryUsage
argument_list|(
name|n
argument_list|)
operator|+
name|OVERHEAD
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|basicObjectWithoutProperties
parameter_list|()
throws|throws
name|Exception
block|{
name|measureMemory
argument_list|(
operator|new
name|Callable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|call
parameter_list|()
block|{
name|BasicDBObject
name|n
init|=
name|generateBasicObject
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
operator|new
name|Object
index|[]
block|{
name|n
block|,
name|Utils
operator|.
name|estimateMemoryUsage
argument_list|(
name|n
argument_list|)
operator|+
name|OVERHEAD
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeChildManyChildren
parameter_list|()
throws|throws
name|Exception
block|{
name|measureMemory
argument_list|(
operator|new
name|Callable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|call
parameter_list|()
block|{
name|Children
name|n
init|=
name|generateNodeChild
argument_list|(
literal|100
argument_list|)
decl_stmt|;
return|return
operator|new
name|Object
index|[]
block|{
name|n
block|,
name|n
operator|.
name|getMemory
argument_list|()
operator|+
name|OVERHEAD
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeChild
parameter_list|()
throws|throws
name|Exception
block|{
name|measureMemory
argument_list|(
operator|new
name|Callable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|call
parameter_list|()
block|{
name|Children
name|n
init|=
name|generateNodeChild
argument_list|(
literal|5
argument_list|)
decl_stmt|;
return|return
operator|new
name|Object
index|[]
block|{
name|n
block|,
name|n
operator|.
name|getMemory
argument_list|()
operator|+
name|OVERHEAD
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeChildWithoutChildren
parameter_list|()
throws|throws
name|Exception
block|{
name|measureMemory
argument_list|(
operator|new
name|Callable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|call
parameter_list|()
block|{
name|Children
name|n
init|=
name|generateNodeChild
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
operator|new
name|Object
index|[]
block|{
name|n
block|,
name|n
operator|.
name|getMemory
argument_list|()
operator|+
name|OVERHEAD
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeWithBinaryProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|measureMemory
argument_list|(
operator|new
name|Callable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|call
parameter_list|()
block|{
name|DocumentNodeState
name|n
init|=
name|generateNodeWithBinaryProperties
argument_list|(
literal|3
argument_list|)
decl_stmt|;
return|return
operator|new
name|Object
index|[]
block|{
name|n
block|,
name|n
operator|.
name|getMemory
argument_list|()
operator|+
name|OVERHEAD
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|revisionsKey
parameter_list|()
throws|throws
name|Exception
block|{
name|measureMemory
argument_list|(
operator|new
name|Callable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|call
parameter_list|()
block|{
name|RevisionsKey
name|k
init|=
operator|new
name|RevisionsKey
argument_list|(
operator|new
name|RevisionVector
argument_list|(
name|Revision
operator|.
name|newRevision
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|,
operator|new
name|RevisionVector
argument_list|(
name|Revision
operator|.
name|newRevision
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|Object
index|[]
block|{
name|k
block|,
name|k
operator|.
name|getMemory
argument_list|()
operator|+
name|OVERHEAD
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|revisionsKey2
parameter_list|()
throws|throws
name|Exception
block|{
name|measureMemory
argument_list|(
operator|new
name|Callable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|call
parameter_list|()
block|{
name|RevisionsKey
name|k
init|=
operator|new
name|RevisionsKey
argument_list|(
operator|new
name|RevisionVector
argument_list|(
name|Revision
operator|.
name|newRevision
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|,
operator|new
name|RevisionVector
argument_list|(
name|Revision
operator|.
name|newRevision
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|Object
index|[]
block|{
name|k
block|,
name|k
operator|.
name|getMemory
argument_list|()
operator|+
name|OVERHEAD
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|revisionVector
parameter_list|()
throws|throws
name|Exception
block|{
name|measureMemory
argument_list|(
operator|new
name|Callable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|RevisionVector
name|rv
init|=
operator|new
name|RevisionVector
argument_list|(
name|Revision
operator|.
name|newRevision
argument_list|(
literal|0
argument_list|)
argument_list|,
name|Revision
operator|.
name|newRevision
argument_list|(
literal|1
argument_list|)
argument_list|,
name|Revision
operator|.
name|newRevision
argument_list|(
literal|2
argument_list|)
argument_list|,
name|Revision
operator|.
name|newRevision
argument_list|(
literal|3
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|Object
index|[]
block|{
name|rv
block|,
name|rv
operator|.
name|getMemory
argument_list|()
operator|+
name|OVERHEAD
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|revisionVectorSingle
parameter_list|()
throws|throws
name|Exception
block|{
name|measureMemory
argument_list|(
operator|new
name|Callable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|RevisionVector
name|rv
init|=
operator|new
name|RevisionVector
argument_list|(
name|Revision
operator|.
name|newRevision
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|Object
index|[]
block|{
name|rv
block|,
name|rv
operator|.
name|getMemory
argument_list|()
operator|+
name|OVERHEAD
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|revision
parameter_list|()
throws|throws
name|Exception
block|{
name|measureMemory
argument_list|(
operator|new
name|Callable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|Revision
name|r
init|=
name|Revision
operator|.
name|newRevision
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
operator|new
name|Object
index|[]
block|{
name|r
block|,
name|r
operator|.
name|getMemory
argument_list|()
operator|+
name|OVERHEAD
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|memoryDiffCacheKey
parameter_list|()
throws|throws
name|Exception
block|{
name|measureMemory
argument_list|(
operator|new
name|Callable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
specifier|private
name|int
name|counter
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|call
parameter_list|()
block|{
name|Path
name|p
init|=
name|Path
operator|.
name|fromString
argument_list|(
name|generatePath
argument_list|()
argument_list|)
decl_stmt|;
name|RevisionVector
name|rv1
init|=
operator|new
name|RevisionVector
argument_list|(
name|Revision
operator|.
name|newRevision
argument_list|(
literal|0
argument_list|)
argument_list|,
name|Revision
operator|.
name|newRevision
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|RevisionVector
name|rv2
init|=
operator|new
name|RevisionVector
argument_list|(
name|Revision
operator|.
name|newRevision
argument_list|(
literal|0
argument_list|)
argument_list|,
name|Revision
operator|.
name|newRevision
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|MemoryDiffCache
operator|.
name|Key
name|k
init|=
operator|new
name|MemoryDiffCache
operator|.
name|Key
argument_list|(
name|p
argument_list|,
name|rv1
argument_list|,
name|rv2
argument_list|)
decl_stmt|;
return|return
operator|new
name|Object
index|[]
block|{
name|k
block|,
name|k
operator|.
name|getMemory
argument_list|()
operator|+
name|OVERHEAD
block|}
return|;
block|}
specifier|private
name|String
name|generatePath
parameter_list|()
block|{
name|String
name|p
init|=
literal|"/"
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|p
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|p
argument_list|,
name|generateName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|p
return|;
block|}
specifier|private
name|String
name|generateName
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"node-%05d"
argument_list|,
name|counter
operator|++
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|path
parameter_list|()
throws|throws
name|Exception
block|{
name|measureMemory
argument_list|(
operator|new
name|Callable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
specifier|private
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|call
parameter_list|()
block|{
name|Path
name|p
init|=
name|Path
operator|.
name|fromString
argument_list|(
name|generatePath
argument_list|(
name|counter
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|Object
index|[]
block|{
name|p
block|,
name|p
operator|.
name|getMemory
argument_list|()
operator|+
name|OVERHEAD
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|pathRev
parameter_list|()
throws|throws
name|Exception
block|{
name|measureMemory
argument_list|(
operator|new
name|Callable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
specifier|private
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|call
parameter_list|()
block|{
name|Path
name|p
init|=
name|Path
operator|.
name|fromString
argument_list|(
name|generatePath
argument_list|(
name|counter
argument_list|)
argument_list|)
decl_stmt|;
name|RevisionVector
name|r
init|=
operator|new
name|RevisionVector
argument_list|(
name|Revision
operator|.
name|newRevision
argument_list|(
literal|1
argument_list|)
argument_list|,
name|Revision
operator|.
name|newRevision
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|PathRev
name|pr
init|=
operator|new
name|PathRev
argument_list|(
name|p
argument_list|,
name|r
argument_list|)
decl_stmt|;
return|return
operator|new
name|Object
index|[]
block|{
name|pr
block|,
name|pr
operator|.
name|getMemory
argument_list|()
operator|+
name|OVERHEAD
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|namePathRev
parameter_list|()
throws|throws
name|Exception
block|{
name|measureMemory
argument_list|(
operator|new
name|Callable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
specifier|private
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|call
parameter_list|()
block|{
name|String
name|name
init|=
name|generateName
argument_list|(
name|counter
argument_list|)
decl_stmt|;
name|Path
name|p
init|=
name|Path
operator|.
name|fromString
argument_list|(
name|generatePath
argument_list|(
name|counter
argument_list|)
argument_list|)
decl_stmt|;
name|RevisionVector
name|r
init|=
operator|new
name|RevisionVector
argument_list|(
name|Revision
operator|.
name|newRevision
argument_list|(
literal|1
argument_list|)
argument_list|,
name|Revision
operator|.
name|newRevision
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|NamePathRev
name|npr
init|=
operator|new
name|NamePathRev
argument_list|(
name|name
argument_list|,
name|p
argument_list|,
name|r
argument_list|)
decl_stmt|;
return|return
operator|new
name|Object
index|[]
block|{
name|npr
block|,
name|npr
operator|.
name|getMemory
argument_list|()
operator|+
name|OVERHEAD
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|measureMemory
parameter_list|(
name|Callable
argument_list|<
name|Object
index|[]
argument_list|>
name|c
parameter_list|)
throws|throws
name|Exception
block|{
name|LinkedList
argument_list|<
name|Object
argument_list|>
name|list
init|=
operator|new
name|LinkedList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|long
name|base
init|=
name|getMemoryUsed
argument_list|()
decl_stmt|;
name|long
name|mem
init|=
literal|0
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
name|TEST_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Object
index|[]
name|om
init|=
name|c
operator|.
name|call
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|om
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|mem
operator|+=
operator|(
name|Integer
operator|)
name|om
index|[
literal|1
index|]
expr_stmt|;
block|}
name|long
name|used
init|=
name|getMemoryUsed
argument_list|()
operator|-
name|base
decl_stmt|;
name|int
name|estimation
init|=
call|(
name|int
call|)
argument_list|(
literal|100
operator|*
name|mem
operator|/
name|used
argument_list|)
decl_stmt|;
name|String
name|message
init|=
operator|new
name|Error
argument_list|()
operator|.
name|getStackTrace
argument_list|()
index|[
literal|1
index|]
operator|.
name|getMethodName
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"used: "
operator|+
name|used
operator|+
literal|" calculated: "
operator|+
name|mem
operator|+
literal|"\n"
operator|+
literal|"estimation is "
operator|+
name|estimation
operator|+
literal|"%\n"
decl_stmt|;
if|if
condition|(
name|TRACE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|estimation
argument_list|<
literal|80
operator|||
name|estimation
argument_list|>
literal|160
condition|)
block|{
name|fail
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
comment|// need to keep the reference until here, otherwise
comment|// the list might be garbage collected too early
name|list
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|static
name|DocumentNodeState
name|generateNode
parameter_list|(
name|int
name|propertyCount
parameter_list|)
block|{
return|return
name|generateNode
argument_list|(
name|propertyCount
argument_list|,
name|Collections
operator|.
expr|<
name|PropertyState
operator|>
name|emptyList
argument_list|()
argument_list|)
return|;
block|}
specifier|static
name|DocumentNodeState
name|generateNode
parameter_list|(
name|int
name|propertyCount
parameter_list|,
name|List
argument_list|<
name|PropertyState
argument_list|>
name|extraProps
parameter_list|)
block|{
name|List
argument_list|<
name|PropertyState
argument_list|>
name|props
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|props
operator|.
name|addAll
argument_list|(
name|extraProps
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|propertyCount
condition|;
name|i
operator|++
control|)
block|{
name|String
name|key
init|=
literal|"property"
operator|+
name|i
decl_stmt|;
name|props
operator|.
name|add
argument_list|(
name|STORE
operator|.
name|createPropertyState
argument_list|(
name|key
argument_list|,
literal|"\"values "
operator|+
name|i
operator|+
literal|"\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|DocumentNodeState
argument_list|(
name|STORE
argument_list|,
name|Path
operator|.
name|fromString
argument_list|(
literal|"/hello/world"
argument_list|)
argument_list|,
operator|new
name|RevisionVector
argument_list|(
operator|new
name|Revision
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
argument_list|)
argument_list|,
name|props
argument_list|,
literal|false
argument_list|,
operator|new
name|RevisionVector
argument_list|(
operator|new
name|Revision
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|static
name|DocumentNodeState
name|generateNodeWithBinaryProperties
parameter_list|(
name|int
name|propertyCount
parameter_list|)
block|{
name|List
argument_list|<
name|PropertyState
argument_list|>
name|props
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
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
name|propertyCount
condition|;
name|i
operator|++
control|)
block|{
name|props
operator|.
name|add
argument_list|(
name|STORE
operator|.
name|createPropertyState
argument_list|(
literal|"binary"
operator|+
name|i
argument_list|,
operator|new
name|String
argument_list|(
name|BLOB_VALUE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|generateNode
argument_list|(
literal|0
argument_list|,
name|props
argument_list|)
return|;
block|}
specifier|static
name|BasicDBObject
name|generateBasicObject
parameter_list|(
name|int
name|propertyCount
parameter_list|)
block|{
name|BasicDBObject
name|n
init|=
operator|new
name|BasicDBObject
argument_list|(
operator|new
name|String
argument_list|(
literal|"_id"
argument_list|)
argument_list|,
operator|new
name|String
argument_list|(
literal|"/hello/world"
argument_list|)
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
name|propertyCount
condition|;
name|i
operator|++
control|)
block|{
name|n
operator|.
name|append
argument_list|(
literal|"property"
operator|+
name|i
argument_list|,
literal|"values "
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|n
return|;
block|}
specifier|static
name|Children
name|generateNodeChild
parameter_list|(
name|int
name|childCount
parameter_list|)
block|{
name|Children
name|n
init|=
operator|new
name|Children
argument_list|()
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
name|childCount
condition|;
name|i
operator|++
control|)
block|{
name|n
operator|.
name|children
operator|.
name|add
argument_list|(
literal|"child"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|n
return|;
block|}
specifier|private
specifier|static
name|long
name|getMemoryUsed
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
block|}
return|return
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|totalMemory
argument_list|()
operator|-
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|freeMemory
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|String
name|generatePath
parameter_list|(
name|AtomicInteger
name|counter
parameter_list|)
block|{
name|String
name|p
init|=
literal|"/"
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|p
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|p
argument_list|,
name|generateName
argument_list|(
name|counter
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|p
return|;
block|}
specifier|private
specifier|static
name|String
name|generateName
parameter_list|(
name|AtomicInteger
name|counter
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"node-%05d"
argument_list|,
name|counter
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

