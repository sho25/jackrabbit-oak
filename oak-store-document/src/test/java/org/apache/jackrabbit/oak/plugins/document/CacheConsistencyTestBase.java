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
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|List
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
name|java
operator|.
name|util
operator|.
name|UUID
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
name|assertNotNull
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
name|assertNull
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
name|Assert
operator|.
name|fail
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|CacheConsistencyTestBase
block|{
specifier|private
name|DocumentStoreFixture
name|fixture
decl_stmt|;
specifier|private
name|DocumentStore
name|ds
decl_stmt|;
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|removeMe
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
specifier|abstract
name|DocumentStoreFixture
name|getFixture
parameter_list|()
throws|throws
name|Exception
function_decl|;
specifier|public
specifier|abstract
name|void
name|setTemporaryUpdateException
parameter_list|(
name|String
name|msg
parameter_list|)
function_decl|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|fixture
operator|=
name|getFixture
argument_list|()
expr_stmt|;
name|ds
operator|=
name|fixture
operator|.
name|createDocumentStore
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|ds
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|id
range|:
name|removeMe
control|)
block|{
name|ds
operator|.
name|remove
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
name|ds
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
name|fixture
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExceptionInvalidatesCache
parameter_list|()
block|{
name|String
name|id1
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".testExceptionInvalidatesCache1"
decl_stmt|;
name|UpdateOp
name|up1
init|=
operator|new
name|UpdateOp
argument_list|(
name|id1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|up1
operator|.
name|set
argument_list|(
literal|"_test"
argument_list|,
literal|"oldvalue"
argument_list|)
expr_stmt|;
name|String
name|id2
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".testExceptionInvalidatesCache2"
decl_stmt|;
name|UpdateOp
name|up2
init|=
operator|new
name|UpdateOp
argument_list|(
name|id2
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|up2
operator|.
name|set
argument_list|(
literal|"_test"
argument_list|,
literal|"oldvalue"
argument_list|)
expr_stmt|;
name|String
name|id3
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".testExceptionInvalidatesCache3"
decl_stmt|;
name|UpdateOp
name|up3
init|=
operator|new
name|UpdateOp
argument_list|(
name|id3
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|up3
operator|.
name|set
argument_list|(
literal|"_test"
argument_list|,
literal|"oldvalue"
argument_list|)
expr_stmt|;
name|ds
operator|.
name|create
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|up1
argument_list|,
name|up2
argument_list|,
name|up3
argument_list|)
argument_list|)
expr_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
name|id1
argument_list|)
expr_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
name|id2
argument_list|)
expr_stmt|;
name|removeMe
operator|.
name|add
argument_list|(
name|id3
argument_list|)
expr_stmt|;
comment|// make sure cache is populated
name|NodeDocument
name|olddoc
init|=
name|ds
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id1
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"oldvalue"
argument_list|,
name|olddoc
operator|.
name|get
argument_list|(
literal|"_test"
argument_list|)
argument_list|)
expr_stmt|;
name|olddoc
operator|=
name|ds
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id2
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"oldvalue"
argument_list|,
name|olddoc
operator|.
name|get
argument_list|(
literal|"_test"
argument_list|)
argument_list|)
expr_stmt|;
name|olddoc
operator|=
name|ds
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id3
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"oldvalue"
argument_list|,
name|olddoc
operator|.
name|get
argument_list|(
literal|"_test"
argument_list|)
argument_list|)
expr_stmt|;
comment|// findAndUpdate
try|try
block|{
comment|// make sure cache is populated
name|olddoc
operator|=
name|ds
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id1
argument_list|)
expr_stmt|;
name|String
name|random
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|setTemporaryUpdateException
argument_list|(
name|random
argument_list|)
expr_stmt|;
try|try
block|{
name|up1
operator|=
operator|new
name|UpdateOp
argument_list|(
name|id1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|up1
operator|.
name|set
argument_list|(
literal|"_test"
argument_list|,
name|random
argument_list|)
expr_stmt|;
name|ds
operator|.
name|findAndUpdate
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|up1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have failed with DocumentStoreException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"should fail with enforced exception"
argument_list|,
name|ex
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
name|random
argument_list|)
expr_stmt|;
comment|// make sure cache was invalidated
name|NodeDocument
name|newdoc
init|=
name|ds
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id1
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|newdoc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|random
argument_list|,
name|newdoc
operator|.
name|get
argument_list|(
literal|"_test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|setTemporaryUpdateException
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// update
try|try
block|{
comment|// make sure cache is populated
name|olddoc
operator|=
name|ds
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id1
argument_list|)
expr_stmt|;
name|String
name|random
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|setTemporaryUpdateException
argument_list|(
name|random
argument_list|)
expr_stmt|;
try|try
block|{
name|up1
operator|=
operator|new
name|UpdateOp
argument_list|(
name|id1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|up1
operator|.
name|set
argument_list|(
literal|"_test"
argument_list|,
name|random
argument_list|)
expr_stmt|;
name|ds
operator|.
name|update
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|id1
argument_list|)
argument_list|,
name|up1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have failed with DocumentStoreException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"should fail with enforced exception"
argument_list|,
name|ex
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
name|random
argument_list|)
expr_stmt|;
comment|// make sure cache was invalidated
name|NodeDocument
name|newdoc
init|=
name|ds
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id1
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|newdoc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|random
argument_list|,
name|newdoc
operator|.
name|get
argument_list|(
literal|"_test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|setTemporaryUpdateException
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// createOrUpdate
try|try
block|{
comment|// make sure cache is populated
name|olddoc
operator|=
name|ds
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id1
argument_list|)
expr_stmt|;
name|String
name|random
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|setTemporaryUpdateException
argument_list|(
name|random
argument_list|)
expr_stmt|;
try|try
block|{
name|up1
operator|=
operator|new
name|UpdateOp
argument_list|(
name|id1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|up1
operator|.
name|set
argument_list|(
literal|"_test"
argument_list|,
name|random
argument_list|)
expr_stmt|;
name|ds
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|up1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have failed with DocumentStoreException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"should fail with enforced exception"
argument_list|,
name|ex
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
name|random
argument_list|)
expr_stmt|;
comment|// make sure cache was invalidated
name|NodeDocument
name|newdoc
init|=
name|ds
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id1
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|newdoc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|random
argument_list|,
name|newdoc
operator|.
name|get
argument_list|(
literal|"_test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|setTemporaryUpdateException
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// createOrUpdate multiple
try|try
block|{
comment|// make sure cache is populated
name|olddoc
operator|=
name|ds
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id1
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|olddoc
argument_list|)
expr_stmt|;
name|olddoc
operator|=
name|ds
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id2
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|olddoc
argument_list|)
expr_stmt|;
name|olddoc
operator|=
name|ds
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id3
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|olddoc
argument_list|)
expr_stmt|;
name|String
name|random
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|setTemporaryUpdateException
argument_list|(
name|random
argument_list|)
expr_stmt|;
try|try
block|{
name|up1
operator|=
operator|new
name|UpdateOp
argument_list|(
name|id1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|up1
operator|.
name|set
argument_list|(
literal|"_test"
argument_list|,
name|random
argument_list|)
expr_stmt|;
name|up2
operator|=
operator|new
name|UpdateOp
argument_list|(
name|id2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|up2
operator|.
name|set
argument_list|(
literal|"_test"
argument_list|,
name|random
argument_list|)
expr_stmt|;
name|up3
operator|=
operator|new
name|UpdateOp
argument_list|(
name|id3
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|up3
operator|.
name|set
argument_list|(
literal|"_test"
argument_list|,
name|random
argument_list|)
expr_stmt|;
name|ds
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|up1
argument_list|,
name|up2
argument_list|,
name|up3
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have failed with DocumentStoreException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"should fail with enforced exception"
argument_list|,
name|ex
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
name|random
argument_list|)
expr_stmt|;
comment|// expected post conditions:
comment|// 1) at least one of the documents should be updated
comment|// 2) for all documents: reading from cache and uncached
comment|// should return the same document
name|Set
argument_list|<
name|String
argument_list|>
name|modifiedDocuments
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|id
range|:
operator|new
name|String
index|[]
block|{
name|id1
block|,
name|id2
block|,
name|id3
block|}
control|)
block|{
comment|// get cached value
name|NodeDocument
name|newdoc
init|=
name|ds
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|equals
argument_list|(
name|newdoc
operator|.
name|get
argument_list|(
literal|"_test"
argument_list|)
argument_list|)
condition|)
block|{
name|modifiedDocuments
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
comment|// compare with persisted value
name|assertEquals
argument_list|(
name|newdoc
operator|.
name|get
argument_list|(
literal|"_test"
argument_list|)
argument_list|,
name|ds
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id
argument_list|,
literal|0
argument_list|)
operator|.
name|get
argument_list|(
literal|"_test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"at least one document should have been updated"
argument_list|,
operator|!
name|modifiedDocuments
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|setTemporaryUpdateException
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// delete
try|try
block|{
name|String
name|random
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|setTemporaryUpdateException
argument_list|(
name|random
argument_list|)
expr_stmt|;
try|try
block|{
name|ds
operator|.
name|remove
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have failed with DocumentStoreException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"should fail with enforced exception"
argument_list|,
name|ex
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
name|random
argument_list|)
expr_stmt|;
comment|// make sure cache was invalidated
name|NodeDocument
name|newdoc
init|=
name|ds
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id1
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|newdoc
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|setTemporaryUpdateException
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
