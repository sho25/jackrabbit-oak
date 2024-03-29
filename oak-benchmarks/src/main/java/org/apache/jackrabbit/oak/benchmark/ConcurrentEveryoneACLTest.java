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
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

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
name|Property
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
name|api
operator|.
name|security
operator|.
name|JackrabbitAccessControlList
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
name|commons
operator|.
name|jackrabbit
operator|.
name|authorization
operator|.
name|AccessControlUtils
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
name|oak
operator|.
name|spi
operator|.
name|security
operator|.
name|principal
operator|.
name|EveryonePrincipal
import|;
end_import

begin_comment
comment|/**  * Test case that randomly reads from 10k unstructured nodes that are all access controlled with an everyone ACL.  */
end_comment

begin_class
specifier|public
class|class
name|ConcurrentEveryoneACLTest
extends|extends
name|AbstractTest
block|{
specifier|private
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|int
name|NODE_COUNT
init|=
literal|100
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|ROOT_NODE_NAME
init|=
literal|"test"
operator|+
name|TEST_ID
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|runAsAdmin
decl_stmt|;
specifier|private
specifier|final
name|int
name|itemsToRead
decl_stmt|;
specifier|public
name|ConcurrentEveryoneACLTest
parameter_list|(
name|boolean
name|runAsAdmin
parameter_list|,
name|int
name|itemsToRead
parameter_list|)
block|{
name|this
operator|.
name|runAsAdmin
operator|=
name|runAsAdmin
expr_stmt|;
name|this
operator|.
name|itemsToRead
operator|=
name|itemsToRead
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeSuite
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|loginWriter
argument_list|()
decl_stmt|;
name|AccessControlManager
name|acMgr
init|=
name|session
operator|.
name|getAccessControlManager
argument_list|()
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
specifier|final
name|Node
name|root
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|ROOT_NODE_NAME
argument_list|,
literal|"nt:unstructured"
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
name|NODE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|node
init|=
name|root
operator|.
name|addNode
argument_list|(
literal|"node"
operator|+
name|i
argument_list|,
literal|"nt:unstructured"
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
name|NODE_COUNT
condition|;
name|j
operator|++
control|)
block|{
name|Node
name|newNode
init|=
name|node
operator|.
name|addNode
argument_list|(
literal|"node"
operator|+
name|j
argument_list|,
literal|"nt:unstructured"
argument_list|)
decl_stmt|;
name|JackrabbitAccessControlList
name|acl
init|=
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|session
argument_list|,
name|newNode
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|acl
operator|.
name|addEntry
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|privileges
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|newNode
operator|.
name|getPath
argument_list|()
argument_list|,
name|acl
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
comment|// deny everyone on root node
name|JackrabbitAccessControlList
name|acl
init|=
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|session
argument_list|,
name|root
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|acl
operator|.
name|addEntry
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|privileges
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|root
operator|.
name|getPath
argument_list|()
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
specifier|final
name|int
index|[]
name|numACEs
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
name|ItemVisitor
name|v
init|=
operator|new
name|TraversingItemVisitor
operator|.
name|Default
argument_list|()
block|{
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
name|i
parameter_list|)
throws|throws
name|RepositoryException
block|{
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
name|numACEs
index|[
literal|0
index|]
operator|++
expr_stmt|;
block|}
name|super
operator|.
name|entering
argument_list|(
name|node
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|entering
parameter_list|(
name|Property
name|prop
parameter_list|,
name|int
name|i
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|super
operator|.
name|entering
argument_list|(
name|prop
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|v
operator|.
name|visit
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Num ACEs: "
operator|+
name|numACEs
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterSuite
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|loginWriter
argument_list|()
decl_stmt|;
name|Node
name|root
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
name|ROOT_NODE_NAME
argument_list|)
decl_stmt|;
name|root
operator|.
name|remove
argument_list|()
expr_stmt|;
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|runTest
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
literal|null
decl_stmt|;
try|try
block|{
name|session
operator|=
name|runAsAdmin
condition|?
name|loginWriter
argument_list|()
else|:
name|loginAnonymous
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
name|itemsToRead
condition|;
name|i
operator|++
control|)
block|{
name|session
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|int
name|a
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|NODE_COUNT
argument_list|)
decl_stmt|;
name|int
name|b
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|NODE_COUNT
argument_list|)
decl_stmt|;
name|String
name|path
init|=
literal|"/"
operator|+
name|ROOT_NODE_NAME
operator|+
literal|"/node"
operator|+
name|a
operator|+
literal|"/node"
operator|+
name|b
operator|+
literal|"/jcr:primaryType"
decl_stmt|;
name|session
operator|.
name|getProperty
argument_list|(
name|path
argument_list|)
operator|.
name|getString
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|session
operator|!=
literal|null
condition|)
block|{
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

