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
name|benchmark
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ItemVisitor
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
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
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlList
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlPolicy
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlPolicyIterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|Privilege
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|util
operator|.
name|TraversingItemVisitor
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
name|core
operator|.
name|security
operator|.
name|principal
operator|.
name|EveryonePrincipal
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
name|security
operator|.
name|authorization
operator|.
name|accesscontrol
operator|.
name|AccessControlConstants
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
name|util
operator|.
name|Text
import|;
end_import

begin_comment
comment|/**  * Concurrently reads random items from the deep tree where every 10th node is  * access controlled.  */
end_comment

begin_class
specifier|public
class|class
name|ConcurrentReadAccessControlledTreeTest
extends|extends
name|AbstractDeepTreeTest
block|{
specifier|public
name|ConcurrentReadAccessControlledTreeTest
parameter_list|(
name|boolean
name|runAsAdmin
parameter_list|,
name|int
name|itemsToRead
parameter_list|,
name|int
name|bgReaders
parameter_list|,
name|boolean
name|doReport
parameter_list|)
block|{
name|super
argument_list|(
name|runAsAdmin
argument_list|,
name|itemsToRead
argument_list|,
name|bgReaders
argument_list|,
name|doReport
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|beforeSuite
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|beforeSuite
argument_list|()
expr_stmt|;
name|ItemVisitor
name|visitor
init|=
operator|new
name|TraversingItemVisitor
operator|.
name|Default
argument_list|()
block|{
name|int
name|counter
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|entering
parameter_list|(
name|Node
name|node
parameter_list|,
name|int
name|level
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
operator|++
name|counter
operator|==
literal|10
condition|)
block|{
name|addPolicy
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|counter
operator|=
literal|0
expr_stmt|;
block|}
name|super
operator|.
name|entering
argument_list|(
name|node
argument_list|,
name|level
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addPolicy
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|AccessControlManager
name|acMgr
init|=
name|node
operator|.
name|getSession
argument_list|()
operator|.
name|getAccessControlManager
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|node
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|int
name|level
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|isNodeType
argument_list|(
name|AccessControlConstants
operator|.
name|NT_REP_POLICY
argument_list|)
condition|)
block|{
name|level
operator|=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|node
operator|.
name|isNodeType
argument_list|(
name|AccessControlConstants
operator|.
name|NT_REP_ACE
argument_list|)
condition|)
block|{
name|level
operator|=
literal|2
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|node
operator|.
name|isNodeType
argument_list|(
name|AccessControlConstants
operator|.
name|NT_REP_RESTRICTIONS
argument_list|)
condition|)
block|{
name|level
operator|=
literal|3
expr_stmt|;
block|}
if|if
condition|(
name|level
operator|>
literal|0
condition|)
block|{
name|path
operator|=
name|Text
operator|.
name|getRelativeParent
argument_list|(
name|path
argument_list|,
name|level
argument_list|)
expr_stmt|;
block|}
name|AccessControlPolicyIterator
name|acIterator
init|=
name|acMgr
operator|.
name|getApplicablePolicies
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|acIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|AccessControlPolicy
name|policy
init|=
name|acIterator
operator|.
name|nextAccessControlPolicy
argument_list|()
decl_stmt|;
if|if
condition|(
name|policy
operator|instanceof
name|AccessControlList
condition|)
block|{
name|AccessControlList
name|acl
init|=
operator|(
name|AccessControlList
operator|)
name|policy
decl_stmt|;
name|Privilege
index|[]
name|privileges
init|=
operator|new
name|Privilege
index|[]
block|{
name|acMgr
operator|.
name|privilegeFromName
argument_list|(
name|Privilege
operator|.
name|JCR_READ
argument_list|)
block|,
name|acMgr
operator|.
name|privilegeFromName
argument_list|(
name|Privilege
operator|.
name|JCR_READ_ACCESS_CONTROL
argument_list|)
block|}
decl_stmt|;
if|if
condition|(
name|acl
operator|.
name|addAccessControlEntry
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|privileges
argument_list|)
condition|)
block|{
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|path
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|node
operator|.
name|getSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
decl_stmt|;
name|visitor
operator|.
name|visit
argument_list|(
name|testRoot
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
name|bgReaders
condition|;
name|i
operator|++
control|)
block|{
name|addBackgroundJob
argument_list|(
operator|new
name|RandomRead
argument_list|(
name|getTestSession
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|runTest
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|testSession
init|=
name|getTestSession
argument_list|()
decl_stmt|;
name|RandomRead
name|randomRead
init|=
operator|new
name|RandomRead
argument_list|(
name|testSession
argument_list|)
decl_stmt|;
name|randomRead
operator|.
name|run
argument_list|()
expr_stmt|;
name|testSession
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
specifier|private
class|class
name|RandomRead
implements|implements
name|Runnable
block|{
specifier|private
specifier|final
name|Session
name|testSession
decl_stmt|;
specifier|private
name|RandomRead
parameter_list|(
name|Session
name|testSession
parameter_list|)
block|{
name|this
operator|.
name|testSession
operator|=
name|testSession
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|randomRead
argument_list|(
name|testSession
argument_list|,
name|allPaths
argument_list|,
name|itemsToRead
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
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
block|}
block|}
end_class

end_unit

