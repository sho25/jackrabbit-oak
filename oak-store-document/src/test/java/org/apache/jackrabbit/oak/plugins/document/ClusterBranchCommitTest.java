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
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|ClusterBranchCommitTest
block|{
annotation|@
name|Rule
specifier|public
specifier|final
name|DocumentMKBuilderProvider
name|builderProvider
init|=
operator|new
name|DocumentMKBuilderProvider
argument_list|()
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|ns1
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|ns2
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|DocumentStore
name|store
init|=
operator|new
name|MemoryDocumentStore
argument_list|()
decl_stmt|;
name|ns1
operator|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|setDocumentStore
argument_list|(
name|store
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|setClusterId
argument_list|(
literal|1
argument_list|)
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
name|ns2
operator|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|setDocumentStore
argument_list|(
name|store
argument_list|)
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|setClusterId
argument_list|(
literal|2
argument_list|)
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|branchCommit
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|builder
init|=
name|ns1
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
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|ns1
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|ns1
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|ns2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|int
name|bc
init|=
name|numBranchCommits
argument_list|(
name|ns2
argument_list|)
decl_stmt|;
name|builder
operator|=
name|ns2
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|bc
operator|==
name|numBranchCommits
argument_list|(
name|ns2
argument_list|)
condition|)
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"node-"
operator|+
name|i
operator|++
argument_list|)
expr_stmt|;
block|}
name|NodeDocument
name|root
init|=
name|Utils
operator|.
name|getRootDocument
argument_list|(
name|ns2
operator|.
name|getDocumentStore
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|tag
range|:
name|root
operator|.
name|getLocalRevisions
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|Utils
operator|.
name|isCommitted
argument_list|(
name|tag
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|Revision
operator|.
name|fromString
argument_list|(
name|tag
argument_list|)
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tag
operator|.
name|startsWith
argument_list|(
literal|"br"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|int
name|numBranchCommits
parameter_list|(
name|DocumentNodeStore
name|ns
parameter_list|)
block|{
name|int
name|num
init|=
literal|0
decl_stmt|;
name|NodeDocument
name|root
init|=
name|Utils
operator|.
name|getRootDocument
argument_list|(
name|ns
operator|.
name|getDocumentStore
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|tag
range|:
name|root
operator|.
name|getLocalRevisions
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|num
operator|+=
name|Utils
operator|.
name|isCommitted
argument_list|(
name|tag
argument_list|)
condition|?
literal|0
else|:
literal|1
expr_stmt|;
block|}
return|return
name|num
return|;
block|}
specifier|private
specifier|static
name|NodeState
name|merge
parameter_list|(
name|DocumentNodeStore
name|ns
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
name|ns
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
return|;
block|}
block|}
end_class

end_unit
