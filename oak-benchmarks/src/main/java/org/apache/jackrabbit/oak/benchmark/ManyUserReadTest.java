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
name|Node
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
name|SimpleCredentials
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
name|Group
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
name|User
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
name|principal
operator|.
name|EveryonePrincipal
import|;
end_import

begin_comment
comment|/**  * UserTest... TODO  */
end_comment

begin_class
specifier|public
class|class
name|ManyUserReadTest
extends|extends
name|ReadDeepTreeTest
block|{
specifier|private
specifier|final
name|int
name|numberOfUsers
init|=
literal|1000
decl_stmt|;
specifier|private
specifier|final
name|int
name|numberOfMembers
init|=
literal|10
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|randomUser
decl_stmt|;
specifier|protected
name|ManyUserReadTest
parameter_list|(
name|boolean
name|runAsAdmin
parameter_list|,
name|int
name|itemsToRead
parameter_list|,
name|boolean
name|doReport
parameter_list|,
name|boolean
name|randomUser
parameter_list|)
block|{
name|super
argument_list|(
name|runAsAdmin
argument_list|,
name|itemsToRead
argument_list|,
name|doReport
argument_list|,
operator|!
name|randomUser
argument_list|)
expr_stmt|;
name|this
operator|.
name|randomUser
operator|=
name|randomUser
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
name|super
operator|.
name|createDeepTree
argument_list|()
expr_stmt|;
name|UserManager
name|userManager
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
name|numberOfUsers
condition|;
name|i
operator|++
control|)
block|{
name|User
name|user
init|=
name|userManager
operator|.
name|createUser
argument_list|(
literal|"user"
operator|+
name|i
argument_list|,
literal|"user"
operator|+
name|i
argument_list|)
decl_stmt|;
name|AccessControlUtils
operator|.
name|addAccessControlEntry
argument_list|(
name|adminSession
argument_list|,
name|user
operator|.
name|getPath
argument_list|()
argument_list|,
name|user
operator|.
name|getPrincipal
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
name|Privilege
operator|.
name|JCR_ALL
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Node
name|userNode
init|=
name|adminSession
operator|.
name|getNode
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|Node
name|n
init|=
name|userNode
operator|.
name|addNode
argument_list|(
literal|"public"
argument_list|)
decl_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|String
name|path
init|=
name|n
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|AccessControlUtils
operator|.
name|addAccessControlEntry
argument_list|(
name|adminSession
argument_list|,
name|path
argument_list|,
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
name|Privilege
operator|.
name|JCR_READ
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|allPaths
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|allPaths
operator|.
name|add
argument_list|(
name|path
operator|+
literal|"/prop"
argument_list|)
expr_stmt|;
name|Group
name|g
init|=
name|userManager
operator|.
name|createGroup
argument_list|(
literal|"group"
operator|+
name|i
argument_list|)
decl_stmt|;
name|AccessControlUtils
operator|.
name|addAccessControlEntry
argument_list|(
name|adminSession
argument_list|,
name|g
operator|.
name|getPath
argument_list|()
argument_list|,
name|g
operator|.
name|getPrincipal
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
name|Privilege
operator|.
name|JCR_READ
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|n
operator|=
name|userNode
operator|.
name|addNode
argument_list|(
literal|"semi"
argument_list|)
expr_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|path
operator|=
name|n
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|AccessControlUtils
operator|.
name|addAccessControlEntry
argument_list|(
name|adminSession
argument_list|,
name|path
argument_list|,
name|g
operator|.
name|getPrincipal
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
name|Privilege
operator|.
name|JCR_READ
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|allPaths
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|allPaths
operator|.
name|add
argument_list|(
name|path
operator|+
literal|"/prop"
argument_list|)
expr_stmt|;
name|userNode
operator|.
name|addNode
argument_list|(
literal|"private"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|adminSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Setup "
operator|+
name|numberOfUsers
operator|+
literal|" users"
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
name|numberOfUsers
condition|;
name|i
operator|++
control|)
block|{
name|Group
name|g
init|=
operator|(
name|Group
operator|)
name|userManager
operator|.
name|getAuthorizable
argument_list|(
literal|"group"
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
name|numberOfMembers
condition|;
name|j
operator|++
control|)
block|{
name|g
operator|.
name|addMember
argument_list|(
name|userManager
operator|.
name|getAuthorizable
argument_list|(
literal|"user"
operator|+
name|getIndex
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|adminSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Setup group membership ("
operator|+
name|numberOfMembers
operator|+
literal|" members per group)"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"All Paths : "
operator|+
name|allPaths
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|AccessControlUtils
operator|.
name|denyAllToEveryone
argument_list|(
name|adminSession
argument_list|,
literal|"/rep:security/rep:authorizables"
argument_list|)
expr_stmt|;
name|adminSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getImportFileName
parameter_list|()
block|{
return|return
literal|"deepTree_everyone.xml"
return|;
block|}
specifier|protected
name|Session
name|getTestSession
parameter_list|()
block|{
if|if
condition|(
name|runAsAdmin
condition|)
block|{
return|return
name|loginWriter
argument_list|()
return|;
block|}
else|else
block|{
name|String
name|userId
init|=
operator|(
name|randomUser
operator|)
condition|?
literal|"user"
operator|+
name|getIndex
argument_list|()
else|:
literal|"user1"
decl_stmt|;
name|SimpleCredentials
name|sc
init|=
operator|new
name|SimpleCredentials
argument_list|(
name|userId
argument_list|,
name|userId
operator|.
name|toCharArray
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|login
argument_list|(
name|sc
argument_list|)
return|;
block|}
block|}
specifier|private
name|int
name|getIndex
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
name|numberOfUsers
operator|*
name|Math
operator|.
name|random
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

