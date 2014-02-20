begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

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
name|Iterator
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
name|Random
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
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|com
operator|.
name|mongodb
operator|.
name|DBCollection
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
name|GarbageCollectableBlobStore
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
name|plugins
operator|.
name|blob
operator|.
name|MarkSweepGarbageCollector
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

begin_comment
comment|/**  * Tests for MongoMK GC  */
end_comment

begin_class
specifier|public
class|class
name|MongoBlobGCTest
extends|extends
name|AbstractMongoConnectionTest
block|{
specifier|public
name|HashSet
argument_list|<
name|String
argument_list|>
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|HashSet
argument_list|<
name|String
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|DocumentNodeStore
name|s
init|=
name|mk
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|NodeBuilder
name|a
init|=
name|s
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|int
name|number
init|=
literal|10
decl_stmt|;
comment|// track the number of the assets to be deleted
name|List
argument_list|<
name|Integer
argument_list|>
name|processed
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|Random
name|rand
init|=
operator|new
name|Random
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|int
name|n
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|number
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|processed
operator|.
name|contains
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|processed
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|number
condition|;
name|i
operator|++
control|)
block|{
name|Blob
name|b
init|=
name|s
operator|.
name|createBlob
argument_list|(
name|randomStream
argument_list|(
name|i
argument_list|,
literal|4160
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|processed
operator|.
name|contains
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|idIter
init|=
operator|(
operator|(
name|GarbageCollectableBlobStore
operator|)
name|s
operator|.
name|getBlobStore
argument_list|()
operator|)
operator|.
name|resolveChunks
argument_list|(
name|b
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|idIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|set
operator|.
name|add
argument_list|(
name|idIter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|a
operator|.
name|child
argument_list|(
literal|"c"
operator|+
name|i
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"x"
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
name|s
operator|.
name|merge
argument_list|(
name|a
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
for|for
control|(
name|int
name|id
range|:
name|processed
control|)
block|{
name|delete
argument_list|(
literal|"c"
operator|+
name|id
argument_list|)
expr_stmt|;
block|}
return|return
name|set
return|;
block|}
specifier|private
name|void
name|delete
parameter_list|(
name|String
name|nodeId
parameter_list|)
block|{
name|DBCollection
name|coll
init|=
name|mongoConnection
operator|.
name|getDB
argument_list|()
operator|.
name|getCollection
argument_list|(
literal|"nodes"
argument_list|)
decl_stmt|;
name|BasicDBObject
name|blobNodeObj
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
name|blobNodeObj
operator|.
name|put
argument_list|(
literal|"_id"
argument_list|,
literal|"1:/"
operator|+
name|nodeId
argument_list|)
expr_stmt|;
name|coll
operator|.
name|remove
argument_list|(
name|blobNodeObj
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|gc
parameter_list|()
throws|throws
name|Exception
block|{
name|HashSet
argument_list|<
name|String
argument_list|>
name|set
init|=
name|setUp
argument_list|()
decl_stmt|;
name|DocumentNodeStore
name|s
init|=
name|mk
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|MarkSweepGarbageCollector
name|gc
init|=
operator|new
name|MarkSweepGarbageCollector
argument_list|()
decl_stmt|;
name|gc
operator|.
name|init
argument_list|(
name|s
argument_list|,
literal|"./target"
argument_list|,
literal|2048
argument_list|,
literal|true
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|gc
operator|.
name|collectGarbage
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|existing
init|=
name|iterate
argument_list|()
decl_stmt|;
name|boolean
name|empty
init|=
name|Sets
operator|.
name|intersection
argument_list|(
name|set
argument_list|,
name|existing
argument_list|)
operator|.
name|isEmpty
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|empty
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|iterate
parameter_list|()
throws|throws
name|Exception
block|{
name|GarbageCollectableBlobStore
name|store
init|=
operator|(
name|GarbageCollectableBlobStore
operator|)
name|mk
operator|.
name|getNodeStore
argument_list|()
operator|.
name|getBlobStore
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|cur
init|=
name|store
operator|.
name|getAllChunkIds
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|existing
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
while|while
condition|(
name|cur
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|existing
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|cur
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|existing
return|;
block|}
specifier|static
name|InputStream
name|randomStream
parameter_list|(
name|int
name|seed
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
name|r
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
return|;
block|}
block|}
end_class

end_unit

