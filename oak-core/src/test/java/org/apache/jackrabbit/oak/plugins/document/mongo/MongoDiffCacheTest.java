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
operator|.
name|mongo
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
name|com
operator|.
name|mongodb
operator|.
name|DB
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
name|DiffCache
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
name|DocumentMK
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
name|DocumentStore
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
name|Revision
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
name|Collection
operator|.
name|NODES
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
name|DocumentStoreFixture
operator|.
name|MONGO
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
name|mongo
operator|.
name|MongoDiffCache
operator|.
name|Diff
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
import|;
end_import

begin_comment
comment|/**  * Tests for the MongoDiffCache.  */
end_comment

begin_class
specifier|public
class|class
name|MongoDiffCacheTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|diff
parameter_list|()
block|{
name|Revision
name|from
init|=
name|Revision
operator|.
name|fromString
argument_list|(
literal|"r1-0-1"
argument_list|)
decl_stmt|;
name|Revision
name|to
init|=
name|Revision
operator|.
name|fromString
argument_list|(
literal|"r2-0-1"
argument_list|)
decl_stmt|;
name|Diff
name|diff
init|=
operator|new
name|Diff
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
decl_stmt|;
name|diff
operator|.
name|append
argument_list|(
literal|"/"
argument_list|,
literal|"^\"foo\":{}"
argument_list|)
expr_stmt|;
name|diff
operator|.
name|append
argument_list|(
literal|"/foo"
argument_list|,
literal|"^\"bar\":{}"
argument_list|)
expr_stmt|;
name|diff
operator|.
name|append
argument_list|(
literal|"/foo/bar"
argument_list|,
literal|"-\"qux\""
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"^\"foo\":{}"
argument_list|,
name|diff
operator|.
name|getChanges
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"^\"bar\":{}"
argument_list|,
name|diff
operator|.
name|getChanges
argument_list|(
literal|"/foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"-\"qux\""
argument_list|,
name|diff
operator|.
name|getChanges
argument_list|(
literal|"/foo/bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|diff
operator|.
name|getChanges
argument_list|(
literal|"/baz"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|merge
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"+"
argument_list|,
name|doMerge
argument_list|(
literal|"+"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"-"
argument_list|,
name|doMerge
argument_list|(
literal|"-"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"^"
argument_list|,
name|doMerge
argument_list|(
literal|"^"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"+"
argument_list|,
name|doMerge
argument_list|(
literal|"+"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"^"
argument_list|,
name|doMerge
argument_list|(
literal|"-"
argument_list|,
literal|"+"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"^"
argument_list|,
name|doMerge
argument_list|(
literal|"^"
argument_list|,
literal|"-"
argument_list|,
literal|"+"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"+"
argument_list|,
name|doMerge
argument_list|(
literal|"+"
argument_list|,
literal|"^"
argument_list|,
literal|"-"
argument_list|,
literal|"+"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"-"
argument_list|,
name|doMerge
argument_list|(
literal|"-"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"-"
argument_list|,
name|doMerge
argument_list|(
literal|"^"
argument_list|,
literal|"-"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|doMerge
argument_list|(
literal|"+"
argument_list|,
literal|"^"
argument_list|,
literal|"-"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"-"
argument_list|,
name|doMerge
argument_list|(
literal|"-"
argument_list|,
literal|"+"
argument_list|,
literal|"^"
argument_list|,
literal|"-"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"^"
argument_list|,
name|doMerge
argument_list|(
literal|"^"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"+"
argument_list|,
name|doMerge
argument_list|(
literal|"+"
argument_list|,
literal|"^"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"^"
argument_list|,
name|doMerge
argument_list|(
literal|"-"
argument_list|,
literal|"+"
argument_list|,
literal|"^"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"^"
argument_list|,
name|doMerge
argument_list|(
literal|"^"
argument_list|,
literal|"-"
argument_list|,
literal|"+"
argument_list|,
literal|"^"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"OAK-2735"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|sizeLimit
parameter_list|()
block|{
name|assumeTrue
argument_list|(
name|MONGO
operator|.
name|isAvailable
argument_list|()
argument_list|)
expr_stmt|;
name|DocumentStore
name|store
init|=
name|MONGO
operator|.
name|createDocumentStore
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|store
operator|instanceof
name|MongoDocumentStore
argument_list|)
expr_stmt|;
name|DB
name|db
init|=
operator|(
operator|(
name|MongoDocumentStore
operator|)
name|store
operator|)
operator|.
name|getDBCollection
argument_list|(
name|NODES
argument_list|)
operator|.
name|getDB
argument_list|()
decl_stmt|;
name|MongoDiffCache
name|diffCache
init|=
operator|new
name|MongoDiffCache
argument_list|(
name|db
argument_list|,
literal|32
argument_list|,
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
argument_list|)
decl_stmt|;
name|DiffCache
operator|.
name|Entry
name|entry
init|=
name|diffCache
operator|.
name|newEntry
argument_list|(
operator|new
name|Revision
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|,
operator|new
name|Revision
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|false
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|100
condition|;
name|j
operator|++
control|)
block|{
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|64
condition|;
name|k
operator|++
control|)
block|{
name|entry
operator|.
name|append
argument_list|(
literal|"/node-"
operator|+
name|i
operator|+
literal|"/node-"
operator|+
name|j
operator|+
literal|"/node-"
operator|+
name|k
argument_list|,
literal|"^\"foo\":{}"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|assertFalse
argument_list|(
name|entry
operator|.
name|done
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
specifier|private
name|String
name|doMerge
parameter_list|(
name|String
modifier|...
name|ops
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|opsList
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|ops
argument_list|)
decl_stmt|;
name|Diff
name|diff
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|opsList
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|String
name|op
init|=
name|opsList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|diff
operator|==
literal|null
condition|)
block|{
name|diff
operator|=
name|diffFromOp
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|diff
operator|.
name|mergeBeforeDiff
argument_list|(
name|diffFromOp
argument_list|(
name|op
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|diff
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|changes
init|=
name|diff
operator|.
name|getChanges
argument_list|(
literal|"/test"
argument_list|)
decl_stmt|;
if|if
condition|(
name|changes
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|changes
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|""
return|;
block|}
else|else
block|{
return|return
name|changes
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|String
name|changeFromOp
parameter_list|(
name|String
name|op
parameter_list|)
block|{
if|if
condition|(
name|op
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|""
return|;
block|}
name|String
name|changes
init|=
name|op
operator|+
literal|"\"child\""
decl_stmt|;
if|if
condition|(
operator|!
name|op
operator|.
name|equals
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|changes
operator|+=
literal|":{}"
expr_stmt|;
block|}
return|return
name|changes
return|;
block|}
specifier|private
specifier|static
name|Diff
name|diffFromOp
parameter_list|(
name|String
name|op
parameter_list|)
block|{
name|Revision
name|from
init|=
name|Revision
operator|.
name|fromString
argument_list|(
literal|"r1-0-1"
argument_list|)
decl_stmt|;
name|Revision
name|to
init|=
name|Revision
operator|.
name|fromString
argument_list|(
literal|"r2-0-1"
argument_list|)
decl_stmt|;
name|Diff
name|d
init|=
operator|new
name|Diff
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
decl_stmt|;
name|d
operator|.
name|append
argument_list|(
literal|"/test"
argument_list|,
name|changeFromOp
argument_list|(
name|op
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|d
return|;
block|}
block|}
end_class

end_unit

