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
name|Iterator
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
name|RepositoryException
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
name|ImmutableList
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
name|oak
operator|.
name|AbstractSecurityTest
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
name|PrincipalImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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
name|mockito
operator|.
name|Mockito
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
name|assertFalse
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
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
specifier|public
class|class
name|AbstractGroupPrincipalTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|Group
name|testGroup
decl_stmt|;
specifier|private
name|AbstractGroupPrincipal
name|agp
decl_stmt|;
specifier|private
name|AbstractGroupPrincipal
name|everyoneAgp
decl_stmt|;
specifier|private
name|AbstractGroupPrincipal
name|throwing
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
name|testGroup
operator|=
name|getUserManager
argument_list|(
name|root
argument_list|)
operator|.
name|createGroup
argument_list|(
literal|"AbstractGroupPrincipalTest"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|agp
operator|=
operator|new
name|AGP
argument_list|()
expr_stmt|;
name|everyoneAgp
operator|=
operator|new
name|AGP
argument_list|()
expr_stmt|;
operator|(
operator|(
name|AGP
operator|)
name|everyoneAgp
operator|)
operator|.
name|isEveryone
operator|=
literal|true
expr_stmt|;
name|throwing
operator|=
operator|new
name|ThrowingAGP
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
if|if
condition|(
name|testGroup
operator|!=
literal|null
condition|)
block|{
name|testGroup
operator|.
name|remove
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsMemberOf
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Principal
name|p
init|=
name|getTestUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|agp
operator|.
name|isMember
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|agp
operator|.
name|isMember
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|agp
operator|.
name|isMember
argument_list|(
operator|new
name|Principal
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|p
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsMemberMissingAuthorizable
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|List
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
literal|"name"
argument_list|)
argument_list|,
operator|new
name|Principal
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"name"
return|;
block|}
block|}
argument_list|)
decl_stmt|;
for|for
control|(
name|Principal
name|p
range|:
name|principals
control|)
block|{
name|assertFalse
argument_list|(
name|agp
operator|.
name|isMember
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsMemberOfEveryone
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Principal
name|p
init|=
name|getTestUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|everyoneAgp
operator|.
name|isMember
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|everyoneAgp
operator|.
name|isMember
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|everyoneAgp
operator|.
name|isMember
argument_list|(
operator|new
name|Principal
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|p
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsMemberOfEveryoneMissingAuthorizable
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|List
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
literal|"name"
argument_list|)
argument_list|,
operator|new
name|Principal
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"name"
return|;
block|}
block|}
argument_list|)
decl_stmt|;
for|for
control|(
name|Principal
name|p
range|:
name|principals
control|)
block|{
name|assertTrue
argument_list|(
name|everyoneAgp
operator|.
name|isMember
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsMemberOfInternalError
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Principal
name|p
init|=
name|getTestUser
argument_list|()
operator|.
name|getPrincipal
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|throwing
operator|.
name|isMember
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testMembersInternalError
parameter_list|()
throws|throws
name|Exception
block|{
name|throwing
operator|.
name|members
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEveryoneIsMemberOfEveryone
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|AbstractGroupPrincipal
name|member
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|AbstractGroupPrincipal
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|member
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|everyoneAgp
operator|.
name|isMember
argument_list|(
name|member
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
class|class
name|AGP
extends|extends
name|AbstractGroupPrincipal
block|{
specifier|private
name|Authorizable
name|member
decl_stmt|;
specifier|private
name|boolean
name|isEveryone
decl_stmt|;
name|AGP
parameter_list|()
throws|throws
name|Exception
block|{
name|super
argument_list|(
name|testGroup
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|root
operator|.
name|getTree
argument_list|(
name|testGroup
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
name|AbstractGroupPrincipalTest
operator|.
name|this
operator|.
name|getNamePathMapper
argument_list|()
argument_list|)
expr_stmt|;
name|member
operator|=
name|getTestUser
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
name|UserManager
name|getUserManager
parameter_list|()
block|{
return|return
name|AbstractGroupPrincipalTest
operator|.
name|this
operator|.
name|getUserManager
argument_list|(
name|root
argument_list|)
return|;
block|}
annotation|@
name|Override
name|boolean
name|isEveryone
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|isEveryone
return|;
block|}
annotation|@
name|Override
name|boolean
name|isMember
parameter_list|(
annotation|@
name|NotNull
name|Authorizable
name|authorizable
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|member
operator|.
name|getID
argument_list|()
operator|.
name|equals
argument_list|(
name|authorizable
operator|.
name|getID
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|getMembers
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|member
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
specifier|private
class|class
name|ThrowingAGP
extends|extends
name|AGP
block|{
name|ThrowingAGP
parameter_list|()
throws|throws
name|Exception
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
name|UserManager
name|getUserManager
parameter_list|()
block|{
name|UserManager
name|userManager
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|UserManager
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|doThrow
argument_list|(
name|RepositoryException
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|userManager
return|;
block|}
annotation|@
name|Override
name|boolean
name|isEveryone
parameter_list|()
throws|throws
name|RepositoryException
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|()
throw|;
block|}
annotation|@
name|Override
name|boolean
name|isMember
parameter_list|(
annotation|@
name|NotNull
name|Authorizable
name|authorizable
parameter_list|)
throws|throws
name|RepositoryException
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|()
throw|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|getMembers
parameter_list|()
throws|throws
name|RepositoryException
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

