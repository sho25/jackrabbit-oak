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
name|security
operator|.
name|Principal
import|;
end_import

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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|api
operator|.
name|JackrabbitSession
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
name|user
operator|.
name|Authorizable
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
name|user
operator|.
name|UserManager
import|;
end_import

begin_comment
comment|/**  * Concurrently calls Session#hasPermission on the deep tree where every 100th node  * is access controlled and each policy node contains 100 ACEs for different  * principals. The hasPermission methods is calles as follows:  *  * - the path argument a random path out of the deep tree  * - the actions are randomly selected from the combinations listed in {@link #ACTIONS}  */
end_comment

begin_class
specifier|public
class|class
name|ConcurrentHasPermissionTest2
extends|extends
name|ConcurrentHasPermissionTest
block|{
name|int
name|counter
init|=
literal|0
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
operator|new
name|ArrayList
argument_list|<
name|Principal
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|ConcurrentHasPermissionTest2
parameter_list|(
name|boolean
name|runAsAdmin
parameter_list|,
name|int
name|itemsToRead
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
name|doReport
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|createDeepTree
parameter_list|()
throws|throws
name|Exception
block|{
name|UserManager
name|uMgr
init|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|adminSession
operator|)
operator|.
name|getUserManager
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
name|Authorizable
name|a
init|=
name|uMgr
operator|.
name|getAuthorizable
argument_list|(
literal|"group"
operator|+
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|==
literal|null
condition|)
block|{
name|a
operator|=
name|uMgr
operator|.
name|createGroup
argument_list|(
literal|"group"
operator|+
name|i
argument_list|)
expr_stmt|;
name|principals
operator|.
name|add
argument_list|(
name|a
operator|.
name|getPrincipal
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|super
operator|.
name|createDeepTree
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|visitingNode
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
name|super
operator|.
name|visitingNode
argument_list|(
name|node
argument_list|,
name|i
argument_list|)
expr_stmt|;
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
operator|!
name|node
operator|.
name|getPath
argument_list|()
operator|.
name|contains
argument_list|(
literal|"rep:policy"
argument_list|)
condition|)
block|{
if|if
condition|(
operator|++
name|counter
operator|==
literal|100
condition|)
block|{
name|addPolicy
argument_list|(
name|acMgr
argument_list|,
name|node
argument_list|,
name|privileges
argument_list|,
name|principals
argument_list|)
expr_stmt|;
name|counter
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

