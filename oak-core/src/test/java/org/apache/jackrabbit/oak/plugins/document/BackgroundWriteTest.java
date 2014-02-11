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
name|plugins
operator|.
name|document
operator|.
name|memory
operator|.
name|MemoryDocumentStore
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Tests related to background write operation in DocumentNodeStore.  */
end_comment

begin_class
specifier|public
class|class
name|BackgroundWriteTest
block|{
annotation|@
name|Test
comment|// OAK-1190
specifier|public
name|void
name|limitMultiUpdate
parameter_list|()
block|{
name|DocumentMK
name|mk
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setDocumentStore
argument_list|(
operator|new
name|TestStore
argument_list|()
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|open
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|paths
operator|.
name|size
argument_list|()
operator|<
name|UnsavedModifications
operator|.
name|BACKGROUND_MULTI_UPDATE_LIMIT
operator|*
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|String
name|child
init|=
literal|"node-"
operator|+
name|i
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"+\""
argument_list|)
operator|.
name|append
argument_list|(
name|child
argument_list|)
operator|.
name|append
argument_list|(
literal|"\":{}"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|1000
condition|;
name|j
operator|++
control|)
block|{
name|String
name|p
init|=
name|child
operator|+
literal|"/node-"
operator|+
name|j
decl_stmt|;
name|paths
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"+\""
argument_list|)
operator|.
name|append
argument_list|(
name|p
argument_list|)
operator|.
name|append
argument_list|(
literal|"\":{}"
argument_list|)
expr_stmt|;
block|}
block|}
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|Revision
name|r
init|=
name|mk
operator|.
name|getNodeStore
argument_list|()
operator|.
name|newRevision
argument_list|()
decl_stmt|;
name|UnsavedModifications
name|pending
init|=
name|mk
operator|.
name|getNodeStore
argument_list|()
operator|.
name|getPendingModifications
argument_list|()
decl_stmt|;
name|paths
operator|.
name|add
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|p
range|:
name|paths
control|)
block|{
name|pending
operator|.
name|put
argument_list|(
name|p
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
name|mk
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|TestStore
extends|extends
name|MemoryDocumentStore
block|{
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|void
name|update
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|keys
parameter_list|,
name|UpdateOp
name|updateOp
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|keys
operator|.
name|size
argument_list|()
operator|<=
name|UnsavedModifications
operator|.
name|BACKGROUND_MULTI_UPDATE_LIMIT
argument_list|)
expr_stmt|;
name|super
operator|.
name|update
argument_list|(
name|collection
argument_list|,
name|keys
argument_list|,
name|updateOp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

