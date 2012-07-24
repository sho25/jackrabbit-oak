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
name|mk
operator|.
name|index
package|;
end_package

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
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|mk
operator|.
name|core
operator|.
name|MicroKernelImpl
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
comment|/**  * Tests the indexing mechanism.  */
end_comment

begin_class
specifier|public
class|class
name|IndexTest
block|{
specifier|private
specifier|final
name|MicroKernel
name|mk
init|=
operator|new
name|MicroKernelImpl
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Indexer
name|indexer
init|=
name|Indexer
operator|.
name|getInstance
argument_list|(
name|mk
argument_list|)
decl_stmt|;
block|{
name|indexer
operator|.
name|init
parameter_list|()
constructor_decl|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|createIndexAfterAddingData
parameter_list|()
block|{
name|PropertyIndex
name|indexOld
init|=
name|indexer
operator|.
name|createPropertyIndex
argument_list|(
literal|"x"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+ \"test\": { \"test2\": { \"id\": 1 }, \"id\": 1 }"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+ \"test3\": { \"test2\": { \"id\": 2 }, \"id\": 2 }"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|indexOld
operator|.
name|getPath
argument_list|(
literal|"x"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
expr_stmt|;
name|PropertyIndex
name|index
init|=
name|indexer
operator|.
name|createPropertyIndex
argument_list|(
literal|"id"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|index
operator|.
name|getPaths
argument_list|(
literal|"1"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/test"
argument_list|,
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/test/test2"
argument_list|,
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nonUnique
parameter_list|()
block|{
name|PropertyIndex
name|index
init|=
name|indexer
operator|.
name|createPropertyIndex
argument_list|(
literal|"id"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+ \"test\": { \"test2\": { \"id\": 1 }, \"id\": 1 }"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+ \"test3\": { \"test2\": { \"id\": 2 }, \"id\": 2 }"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|index
operator|.
name|getPaths
argument_list|(
literal|"1"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/test"
argument_list|,
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/test/test2"
argument_list|,
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nestedAddNode
parameter_list|()
block|{
name|PropertyIndex
name|index
init|=
name|indexer
operator|.
name|createPropertyIndex
argument_list|(
literal|"id"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+ \"test\": { \"test2\": { \"id\": 2 }, \"id\": 1 }"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/test"
argument_list|,
name|index
operator|.
name|getPath
argument_list|(
literal|"1"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/test/test2"
argument_list|,
name|index
operator|.
name|getPath
argument_list|(
literal|"2"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|move
parameter_list|()
block|{
name|PropertyIndex
name|index
init|=
name|indexer
operator|.
name|createPropertyIndex
argument_list|(
literal|"id"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+ \"test\": { \"test2\": { \"id\": 2 }, \"id\": 1 }"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/test"
argument_list|,
name|index
operator|.
name|getPath
argument_list|(
literal|"1"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/test/test2"
argument_list|,
name|index
operator|.
name|getPath
argument_list|(
literal|"2"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"> \"test\": \"moved\""
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/moved"
argument_list|,
name|index
operator|.
name|getPath
argument_list|(
literal|"1"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/moved/test2"
argument_list|,
name|index
operator|.
name|getPath
argument_list|(
literal|"2"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|copy
parameter_list|()
block|{
name|PropertyIndex
name|index
init|=
name|indexer
operator|.
name|createPropertyIndex
argument_list|(
literal|"id"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+ \"test\": { \"test2\": { \"id\": 2 }, \"id\": 1 }"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/test"
argument_list|,
name|index
operator|.
name|getPath
argument_list|(
literal|"1"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/test/test2"
argument_list|,
name|index
operator|.
name|getPath
argument_list|(
literal|"2"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"* \"test\": \"copied\""
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|index
operator|.
name|getPaths
argument_list|(
literal|"1"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/copied"
argument_list|,
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/test"
argument_list|,
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|it
operator|=
name|index
operator|.
name|getPaths
argument_list|(
literal|"2"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/copied/test2"
argument_list|,
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/test/test2"
argument_list|,
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|ascending
parameter_list|()
block|{
name|BTree
name|tree
init|=
operator|new
name|BTree
argument_list|(
name|indexer
argument_list|,
literal|"test"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|tree
operator|.
name|setMinSize
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|print
argument_list|(
name|mk
argument_list|,
name|tree
argument_list|)
expr_stmt|;
name|int
name|len
init|=
literal|30
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
name|len
condition|;
name|i
operator|++
control|)
block|{
name|log
argument_list|(
literal|"#insert "
operator|+
name|i
argument_list|)
expr_stmt|;
name|tree
operator|.
name|add
argument_list|(
literal|""
operator|+
name|i
argument_list|,
literal|"p"
operator|+
name|i
argument_list|)
expr_stmt|;
comment|// print(mk, tree);
block|}
comment|// indexer.commitChanges();
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
comment|// log("#test " + i);
name|Cursor
name|c
init|=
name|tree
operator|.
name|findFirst
argument_list|(
literal|""
operator|+
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|""
operator|+
name|i
argument_list|,
name|c
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|print
argument_list|(
name|mk
argument_list|,
name|tree
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
name|len
condition|;
name|i
operator|++
control|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"not found when removing "
operator|+
name|i
argument_list|,
name|tree
operator|.
name|remove
argument_list|(
literal|""
operator|+
name|i
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// print(mk, tree);
block|}
comment|// indexer.commitChanges();
name|print
argument_list|(
name|mk
argument_list|,
name|tree
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
name|len
condition|;
name|i
operator|++
control|)
block|{
name|Cursor
name|c
init|=
name|tree
operator|.
name|findFirst
argument_list|(
literal|""
operator|+
name|i
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|c
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|duplicateKeyUnique
parameter_list|()
block|{
name|duplicateKey
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|duplicateKeyNonUnique
parameter_list|()
block|{
name|duplicateKey
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|duplicateKey
parameter_list|(
name|boolean
name|unique
parameter_list|)
block|{
name|BTree
name|tree
init|=
operator|new
name|BTree
argument_list|(
name|indexer
argument_list|,
literal|"test"
argument_list|,
name|unique
argument_list|)
decl_stmt|;
name|tree
operator|.
name|setMinSize
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// add
name|tree
operator|.
name|add
argument_list|(
literal|"1"
argument_list|,
literal|"p1"
argument_list|)
expr_stmt|;
if|if
condition|(
name|unique
condition|)
block|{
try|try
block|{
name|tree
operator|.
name|add
argument_list|(
literal|"1"
argument_list|,
literal|"p2"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
else|else
block|{
name|tree
operator|.
name|add
argument_list|(
literal|"1"
argument_list|,
literal|"p2"
argument_list|)
expr_stmt|;
block|}
comment|// search
name|Cursor
name|c
init|=
name|tree
operator|.
name|findFirst
argument_list|(
literal|"1"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|c
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"p1"
argument_list|,
name|c
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|unique
condition|)
block|{
name|Assert
operator|.
name|assertFalse
argument_list|(
name|c
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|c
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"p2"
argument_list|,
name|c
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|c
operator|.
name|remove
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|Assert
operator|.
name|assertFalse
argument_list|(
name|c
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|c
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
comment|// remove
name|tree
operator|.
name|remove
argument_list|(
literal|"1"
argument_list|,
literal|"p1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|random
parameter_list|()
block|{
name|BTree
name|tree
init|=
operator|new
name|BTree
argument_list|(
name|indexer
argument_list|,
literal|"test"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|tree
operator|.
name|setMinSize
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|treeMap
init|=
operator|new
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|log
argument_list|(
literal|"op #"
operator|+
name|i
argument_list|)
expr_stmt|;
comment|// print(mk, tree);
name|int
name|x
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|boolean
name|exists
init|=
name|treeMap
operator|.
name|containsKey
argument_list|(
name|x
argument_list|)
decl_stmt|;
name|Cursor
name|c
init|=
name|tree
operator|.
name|findFirst
argument_list|(
literal|""
operator|+
name|x
argument_list|)
decl_stmt|;
name|boolean
name|gotExists
init|=
name|c
operator|.
name|hasNext
argument_list|()
decl_stmt|;
name|String
name|x2
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|gotExists
condition|)
block|{
name|x2
operator|=
name|c
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|x2
operator|.
name|equals
argument_list|(
literal|""
operator|+
name|x
argument_list|)
condition|)
block|{
name|gotExists
operator|=
literal|false
expr_stmt|;
block|}
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"find "
operator|+
name|x
argument_list|,
name|exists
argument_list|,
name|gotExists
argument_list|)
expr_stmt|;
if|if
condition|(
name|exists
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|""
operator|+
name|x
argument_list|,
name|x2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|treeMap
operator|.
name|get
argument_list|(
name|x
argument_list|)
argument_list|,
name|c
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|boolean
name|add
init|=
name|r
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|add
condition|)
block|{
if|if
condition|(
operator|!
name|exists
condition|)
block|{
name|log
argument_list|(
literal|"#insert "
operator|+
name|x
operator|+
literal|" = p"
operator|+
name|i
argument_list|)
expr_stmt|;
name|treeMap
operator|.
name|put
argument_list|(
name|x
argument_list|,
literal|"p"
operator|+
name|i
argument_list|)
expr_stmt|;
name|tree
operator|.
name|add
argument_list|(
literal|""
operator|+
name|x
argument_list|,
literal|"p"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|exists
condition|)
block|{
name|log
argument_list|(
literal|"#remove "
operator|+
name|x
argument_list|)
expr_stmt|;
name|treeMap
operator|.
name|remove
argument_list|(
name|x
argument_list|)
expr_stmt|;
name|tree
operator|.
name|remove
argument_list|(
literal|""
operator|+
name|x
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|static
name|void
name|log
parameter_list|(
name|String
name|s
parameter_list|)
block|{
comment|// System.out.println(s);
block|}
specifier|static
name|void
name|print
parameter_list|(
name|MicroKernel
name|mk
parameter_list|,
name|BTree
name|tree
parameter_list|)
block|{
name|String
name|head
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|String
name|t
init|=
name|mk
operator|.
name|getNodes
argument_list|(
name|Indexer
operator|.
name|INDEX_CONFIG_PATH
argument_list|,
name|head
argument_list|,
literal|100
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|log
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|Cursor
name|c
init|=
name|tree
operator|.
name|findFirst
argument_list|(
literal|"0"
argument_list|)
decl_stmt|;
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
name|c
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
name|c
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|log
argument_list|(
name|buff
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

