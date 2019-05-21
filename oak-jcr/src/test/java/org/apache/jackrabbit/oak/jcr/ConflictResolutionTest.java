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
name|jcr
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|containsString
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
name|assertThat
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
name|javax
operator|.
name|jcr
operator|.
name|InvalidItemStateException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
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
name|junit
operator|.
name|LogCustomizer
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
name|fixture
operator|.
name|NodeStoreFixture
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
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|classic
operator|.
name|Level
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

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|ConflictResolutionTest
extends|extends
name|AbstractRepositoryTest
block|{
comment|// TODO add tests for all ConflictType types to observe generated logs
specifier|private
specifier|final
name|LogCustomizer
name|logMergingNodeStateDiff
init|=
name|LogCustomizer
operator|.
name|forLogger
argument_list|(
literal|"org.apache.jackrabbit.oak.plugins.commit.MergingNodeStateDiff"
argument_list|)
operator|.
name|enable
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|LogCustomizer
name|logConflictValidator
init|=
name|LogCustomizer
operator|.
name|forLogger
argument_list|(
literal|"org.apache.jackrabbit.oak.plugins.commit.ConflictValidator"
argument_list|)
operator|.
name|enable
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
specifier|public
name|ConflictResolutionTest
parameter_list|(
name|NodeStoreFixture
name|fixture
parameter_list|)
block|{
name|super
argument_list|(
name|fixture
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
block|{
name|super
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|deleteChangedNode
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// first INFO level
name|deleteChangedNodeOps
argument_list|(
literal|"node0"
argument_list|)
expr_stmt|;
comment|// DEBUG level
name|Set
argument_list|<
name|String
argument_list|>
name|mnsdLogs
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|cvLogs
decl_stmt|;
try|try
block|{
name|logMergingNodeStateDiff
operator|.
name|starting
argument_list|()
expr_stmt|;
name|logConflictValidator
operator|.
name|starting
argument_list|()
expr_stmt|;
name|deleteChangedNodeOps
argument_list|(
literal|"node1"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|mnsdLogs
operator|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|logMergingNodeStateDiff
operator|.
name|getLogs
argument_list|()
argument_list|)
expr_stmt|;
name|cvLogs
operator|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|logConflictValidator
operator|.
name|getLogs
argument_list|()
argument_list|)
expr_stmt|;
name|logMergingNodeStateDiff
operator|.
name|finished
argument_list|()
expr_stmt|;
name|logConflictValidator
operator|.
name|finished
argument_list|()
expr_stmt|;
block|}
comment|// MergingNodeStateDif debug: NodeConflictHandler<DELETE_CHANGED_NODE>
comment|// resolved conflict of type DELETE_CHANGED_NODE with resolution THEIRS
comment|// on node jcr:content, conflict trace ^"/metadata/updated":"myself"
name|assertTrue
argument_list|(
name|mnsdLogs
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"MergingNodeStateDiff log message must contain a reference to the handler"
argument_list|,
name|mnsdLogs
operator|.
name|toString
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"NodeConflictHandler<DELETE_CHANGED_NODE>"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"MergingNodeStateDiff log message must contain a reference to the resolution"
argument_list|,
name|mnsdLogs
operator|.
name|toString
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"DELETE_CHANGED_NODE with resolution THEIRS"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"MergingNodeStateDiff log message must contain a reference to the modified property"
argument_list|,
name|mnsdLogs
operator|.
name|toString
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"^\"/metadata/updated\":\"myself\"]"
argument_list|)
argument_list|)
expr_stmt|;
comment|// ConflictValidator debug: Commit failed due to unresolved
comment|// conflicts in /node1 = {deleteChangedNode = {jcr:content}}
name|assertTrue
argument_list|(
name|cvLogs
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"ConflictValidator log message must contain a reference to the path"
argument_list|,
name|cvLogs
operator|.
name|toString
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"/node1 = {deleteChangedNode = {jcr:content}}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|deleteChangedNodeOps
parameter_list|(
name|String
name|node
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|getAdminSession
argument_list|()
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|node
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"jcr:content"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"metadata"
argument_list|)
expr_stmt|;
name|getAdminSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
name|Session
name|session1
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
name|Session
name|session2
init|=
name|createAdminSession
argument_list|()
decl_stmt|;
try|try
block|{
name|session1
operator|.
name|getNode
argument_list|(
literal|"/"
operator|+
name|node
operator|+
literal|"/jcr:content"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|session2
operator|.
name|getNode
argument_list|(
literal|"/"
operator|+
name|node
operator|+
literal|"/jcr:content/metadata"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"updated"
argument_list|,
literal|"myself"
argument_list|)
expr_stmt|;
name|session2
operator|.
name|save
argument_list|()
expr_stmt|;
try|try
block|{
name|session1
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected InvalidItemStateException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidItemStateException
name|expected
parameter_list|)
block|{
name|assertThat
argument_list|(
literal|"Expecting 'Unresolved conflicts in /"
operator|+
name|node
operator|+
literal|"'"
argument_list|,
name|expected
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"OakState0001: Unresolved conflicts in /"
operator|+
name|node
operator|+
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|session1
operator|.
name|logout
argument_list|()
expr_stmt|;
name|session2
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

