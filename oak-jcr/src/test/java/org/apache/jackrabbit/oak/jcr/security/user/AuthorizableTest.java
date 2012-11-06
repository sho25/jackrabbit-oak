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
name|jcr
operator|.
name|security
operator|.
name|user
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
name|HashSet
import|;
end_import

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
name|Set
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
name|UnsupportedRepositoryOperationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Value
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
name|test
operator|.
name|NotExecutableException
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
comment|/**  * AuthorizableTest...  */
end_comment

begin_class
specifier|public
class|class
name|AuthorizableTest
extends|extends
name|AbstractUserTest
block|{
comment|/**      * Removing an authorizable that is still listed as member of a group.      * @throws javax.jcr.RepositoryException      * @throws org.apache.jackrabbit.test.NotExecutableException      */
specifier|public
name|void
name|testRemoveListedAuthorizable
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|String
name|newUserId
init|=
literal|null
decl_stmt|;
name|Group
name|newGroup
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Principal
name|uP
init|=
name|getTestPrincipal
argument_list|()
decl_stmt|;
name|User
name|newUser
init|=
name|userMgr
operator|.
name|createUser
argument_list|(
name|uP
operator|.
name|getName
argument_list|()
argument_list|,
name|uP
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|newUserId
operator|=
name|newUser
operator|.
name|getID
argument_list|()
expr_stmt|;
name|newGroup
operator|=
name|userMgr
operator|.
name|createGroup
argument_list|(
name|getTestPrincipal
argument_list|()
argument_list|)
expr_stmt|;
name|newGroup
operator|.
name|addMember
argument_list|(
name|newUser
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// remove the new user that is still listed as member.
name|newUser
operator|.
name|remove
argument_list|()
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|newUserId
operator|!=
literal|null
condition|)
block|{
name|Authorizable
name|u
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|newUserId
argument_list|)
decl_stmt|;
if|if
condition|(
name|u
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|newGroup
operator|!=
literal|null
condition|)
block|{
name|newGroup
operator|.
name|removeMember
argument_list|(
name|u
argument_list|)
expr_stmt|;
block|}
name|u
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|newGroup
operator|!=
literal|null
condition|)
block|{
name|newGroup
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testObjectMethods
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Authorizable
name|user
init|=
name|getTestUser
argument_list|(
name|superuser
argument_list|)
decl_stmt|;
name|Authorizable
name|user2
init|=
name|getTestUser
argument_list|(
name|superuser
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|user
argument_list|,
name|user2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|user
operator|.
name|hashCode
argument_list|()
argument_list|,
name|user2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Authorizable
argument_list|>
name|s
init|=
operator|new
name|HashSet
argument_list|<
name|Authorizable
argument_list|>
argument_list|()
decl_stmt|;
name|s
operator|.
name|add
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|s
operator|.
name|add
argument_list|(
name|user2
argument_list|)
argument_list|)
expr_stmt|;
name|Authorizable
name|user3
init|=
operator|new
name|Authorizable
argument_list|()
block|{
specifier|public
name|String
name|getID
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|user
operator|.
name|getID
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isGroup
parameter_list|()
block|{
return|return
name|user
operator|.
name|isGroup
argument_list|()
return|;
block|}
specifier|public
name|Principal
name|getPrincipal
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|user
operator|.
name|getPrincipal
argument_list|()
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|Group
argument_list|>
name|declaredMemberOf
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|user
operator|.
name|declaredMemberOf
argument_list|()
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|Group
argument_list|>
name|memberOf
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|user
operator|.
name|memberOf
argument_list|()
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|user
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|getPropertyNames
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|user
operator|.
name|getPropertyNames
argument_list|()
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|getPropertyNames
parameter_list|(
name|String
name|relPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|user
operator|.
name|getPropertyNames
argument_list|(
name|relPath
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|hasProperty
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|user
operator|.
name|hasProperty
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Value
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|user
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Value
index|[]
name|values
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|user
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Value
index|[]
name|getProperty
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|user
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|removeProperty
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|user
operator|.
name|removeProperty
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|String
name|getPath
parameter_list|()
throws|throws
name|UnsupportedRepositoryOperationException
throws|,
name|RepositoryException
block|{
return|return
name|user
operator|.
name|getPath
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|assertFalse
argument_list|(
name|user
operator|.
name|equals
argument_list|(
name|user3
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|s
operator|.
name|add
argument_list|(
name|user3
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

