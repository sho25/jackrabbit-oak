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
name|Collections
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
name|security
operator|.
name|auth
operator|.
name|Subject
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
name|Impersonation
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
name|Ignore
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
comment|/**  * ImpersonationTest...  */
end_comment

begin_class
annotation|@
name|Ignore
comment|// FIXME: enable again
specifier|public
class|class
name|ImpersonationTest
extends|extends
name|AbstractUserTest
block|{
specifier|private
name|User
name|user2
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|user2
operator|=
name|userMgr
operator|.
name|createUser
argument_list|(
literal|"user2"
argument_list|,
literal|"pw"
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|user2
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImpersonation
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|Principal
name|user2Principal
init|=
name|user2
operator|.
name|getPrincipal
argument_list|()
decl_stmt|;
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|(
literal|true
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|user2Principal
argument_list|)
argument_list|,
name|Collections
operator|.
expr|<
name|Object
operator|>
name|emptySet
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|Object
operator|>
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|Impersonation
name|impers
init|=
name|user
operator|.
name|getImpersonation
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|impers
operator|.
name|allows
argument_list|(
name|subject
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|impers
operator|.
name|grantImpersonation
argument_list|(
name|user2Principal
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|impers
operator|.
name|grantImpersonation
argument_list|(
name|user2Principal
argument_list|)
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|impers
operator|.
name|allows
argument_list|(
name|subject
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|impers
operator|.
name|revokeImpersonation
argument_list|(
name|user2Principal
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|impers
operator|.
name|revokeImpersonation
argument_list|(
name|user2Principal
argument_list|)
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|impers
operator|.
name|allows
argument_list|(
name|subject
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAdminPrincipalAsImpersonator
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|String
name|adminId
init|=
name|superuser
operator|.
name|getUserID
argument_list|()
decl_stmt|;
name|Authorizable
name|admin
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|adminId
argument_list|)
decl_stmt|;
if|if
condition|(
name|admin
operator|==
literal|null
operator|||
name|admin
operator|.
name|isGroup
argument_list|()
operator|||
operator|!
operator|(
operator|(
name|User
operator|)
name|admin
operator|)
operator|.
name|isAdmin
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NotExecutableException
argument_list|(
name|adminId
operator|+
literal|" is not administators ID"
argument_list|)
throw|;
block|}
name|Principal
name|adminPrincipal
init|=
name|admin
operator|.
name|getPrincipal
argument_list|()
decl_stmt|;
comment|// admin cannot be add/remove to set of impersonators of 'u' but is
comment|// always allowed to impersonate that user.
name|Impersonation
name|impersonation
init|=
name|user
operator|.
name|getImpersonation
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|impersonation
operator|.
name|grantImpersonation
argument_list|(
name|adminPrincipal
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|impersonation
operator|.
name|revokeImpersonation
argument_list|(
name|adminPrincipal
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|impersonation
operator|.
name|allows
argument_list|(
name|buildSubject
argument_list|(
name|adminPrincipal
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// same if the impersonation object of the admin itself is used.
name|Impersonation
name|adminImpersonation
init|=
operator|(
operator|(
name|User
operator|)
name|admin
operator|)
operator|.
name|getImpersonation
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|adminImpersonation
operator|.
name|grantImpersonation
argument_list|(
name|adminPrincipal
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|adminImpersonation
operator|.
name|revokeImpersonation
argument_list|(
name|adminPrincipal
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|impersonation
operator|.
name|allows
argument_list|(
name|buildSubject
argument_list|(
name|adminPrincipal
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

