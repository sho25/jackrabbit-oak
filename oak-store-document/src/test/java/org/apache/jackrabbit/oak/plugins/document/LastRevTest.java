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
name|util
operator|.
name|Utils
operator|.
name|getIdFromPath
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

begin_comment
comment|/**  * Checks if _lastRev entries are correctly set and updated.  */
end_comment

begin_class
specifier|public
class|class
name|LastRevTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|lastRev
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentNodeStore
name|store
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
name|DocumentStore
name|docStore
init|=
name|store
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
name|NodeBuilder
name|root
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|NodeBuilder
name|child
init|=
name|root
operator|.
name|child
argument_list|(
literal|"child-"
operator|+
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|10
condition|;
name|j
operator|++
control|)
block|{
name|child
operator|.
name|child
argument_list|(
literal|"test-"
operator|+
name|j
argument_list|)
expr_stmt|;
block|}
block|}
name|store
operator|.
name|merge
argument_list|(
name|root
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
name|store
operator|.
name|runBackgroundOperations
argument_list|()
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|String
name|parentPath
init|=
literal|"/child-"
operator|+
name|i
decl_stmt|;
name|assertLastRevSize
argument_list|(
name|docStore
argument_list|,
name|parentPath
argument_list|,
literal|0
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
literal|10
condition|;
name|j
operator|++
control|)
block|{
name|String
name|path
init|=
name|parentPath
operator|+
literal|"/test-"
operator|+
name|j
decl_stmt|;
name|assertLastRevSize
argument_list|(
name|docStore
argument_list|,
name|path
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
name|store
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|assertLastRevSize
parameter_list|(
name|DocumentStore
name|store
parameter_list|,
name|String
name|path
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|NodeDocument
name|doc
init|=
name|store
operator|.
name|find
argument_list|(
name|NODES
argument_list|,
name|getIdFromPath
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"_lastRev: "
operator|+
name|doc
operator|.
name|getLastRev
argument_list|()
argument_list|,
name|size
argument_list|,
name|doc
operator|.
name|getLastRev
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

