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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|external
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
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
name|NoSuchWorkspaceException
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
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginException
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
name|base
operator|.
name|Function
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
name|base
operator|.
name|Predicates
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
name|Iterators
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
name|api
operator|.
name|ContentSession
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
name|api
operator|.
name|PropertyState
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
name|api
operator|.
name|Root
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
name|api
operator|.
name|Tree
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
name|api
operator|.
name|Type
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
name|SecurityProvider
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
name|authentication
operator|.
name|SystemSubject
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
name|authentication
operator|.
name|external
operator|.
name|basic
operator|.
name|DefaultSyncConfig
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
name|authentication
operator|.
name|external
operator|.
name|impl
operator|.
name|ExternalIdentityConstants
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
name|authentication
operator|.
name|external
operator|.
name|impl
operator|.
name|principal
operator|.
name|ExternalPrincipalConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sling
operator|.
name|testing
operator|.
name|mock
operator|.
name|osgi
operator|.
name|junit
operator|.
name|OsgiContext
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
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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

begin_comment
comment|/**  * Abstract base test for external-authentication tests.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractExternalAuthTest
extends|extends
name|AbstractSecurityTest
block|{
annotation|@
name|Rule
specifier|public
specifier|final
name|OsgiContext
name|context
init|=
operator|new
name|OsgiContext
argument_list|()
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|USER_ID
init|=
name|TestIdentityProvider
operator|.
name|ID_TEST_USER
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|TEST_CONSTANT_PROPERTY_NAME
init|=
literal|"profile/constantProperty"
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|TEST_CONSTANT_PROPERTY_VALUE
init|=
literal|"constant-value"
decl_stmt|;
specifier|protected
name|ExternalIdentityProvider
name|idp
decl_stmt|;
specifier|protected
name|DefaultSyncConfig
name|syncConfig
decl_stmt|;
specifier|protected
name|ExternalPrincipalConfiguration
name|externalPrincipalConfiguration
init|=
operator|new
name|ExternalPrincipalConfiguration
argument_list|()
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|ids
decl_stmt|;
specifier|private
name|ContentSession
name|systemSession
decl_stmt|;
specifier|private
name|Root
name|systemRoot
decl_stmt|;
annotation|@
name|Before
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
name|getTestUser
argument_list|()
expr_stmt|;
name|ids
operator|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|getAllAuthorizableIds
argument_list|(
name|getUserManager
argument_list|(
name|root
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|idp
operator|=
name|createIDP
argument_list|()
expr_stmt|;
name|syncConfig
operator|=
name|createSyncConfig
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|destroyIDP
argument_list|()
expr_stmt|;
name|idp
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|systemSession
operator|!=
literal|null
condition|)
block|{
name|systemSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// discard any pending changes
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|UserManager
name|userManager
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|iter
init|=
name|getAllAuthorizableIds
argument_list|(
name|userManager
argument_list|)
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|id
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|ids
operator|.
name|remove
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|Authorizable
name|a
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|!=
literal|null
condition|)
block|{
name|a
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|Iterator
argument_list|<
name|String
argument_list|>
name|getAllAuthorizableIds
parameter_list|(
annotation|@
name|NotNull
name|UserManager
name|userManager
parameter_list|)
throws|throws
name|Exception
block|{
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|iter
init|=
name|userManager
operator|.
name|findAuthorizables
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|Iterators
operator|.
name|filter
argument_list|(
name|Iterators
operator|.
name|transform
argument_list|(
name|iter
argument_list|,
operator|new
name|Function
argument_list|<
name|Authorizable
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|Authorizable
name|input
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|input
operator|!=
literal|null
condition|)
block|{
return|return
name|input
operator|.
name|getID
argument_list|()
return|;
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
comment|// failed to retrieve ID
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
argument_list|,
name|Predicates
operator|.
name|notNull
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|SecurityProvider
name|getSecurityProvider
parameter_list|()
block|{
if|if
condition|(
name|securityProvider
operator|==
literal|null
condition|)
block|{
name|securityProvider
operator|=
name|TestSecurityProvider
operator|.
name|newTestSecurityProvider
argument_list|(
name|getSecurityConfigParameters
argument_list|()
argument_list|,
name|externalPrincipalConfiguration
argument_list|)
expr_stmt|;
comment|// register PrincipalConfiguration with OSGi context
name|context
operator|.
name|registerInjectActivateService
argument_list|(
name|externalPrincipalConfiguration
argument_list|)
expr_stmt|;
block|}
return|return
name|securityProvider
return|;
block|}
specifier|protected
name|ExternalIdentityProvider
name|createIDP
parameter_list|()
block|{
return|return
operator|new
name|TestIdentityProvider
argument_list|()
return|;
block|}
specifier|protected
name|void
name|destroyIDP
parameter_list|()
block|{
comment|// nothing to do
block|}
specifier|protected
name|void
name|addIDPUser
parameter_list|(
name|String
name|id
parameter_list|)
block|{
operator|(
operator|(
name|TestIdentityProvider
operator|)
name|idp
operator|)
operator|.
name|addUser
argument_list|(
operator|new
name|TestIdentityProvider
operator|.
name|TestUser
argument_list|(
name|id
argument_list|,
name|idp
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|DefaultSyncConfig
name|createSyncConfig
parameter_list|()
block|{
name|DefaultSyncConfig
name|syncConfig
init|=
operator|new
name|DefaultSyncConfig
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mapping
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|mapping
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|put
argument_list|(
literal|"email"
argument_list|,
literal|"email"
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|put
argument_list|(
literal|"profile/name"
argument_list|,
literal|"profile/name"
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|put
argument_list|(
literal|"profile/age"
argument_list|,
literal|"profile/age"
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|put
argument_list|(
name|TEST_CONSTANT_PROPERTY_NAME
argument_list|,
literal|"\""
operator|+
name|TEST_CONSTANT_PROPERTY_VALUE
operator|+
literal|"\""
argument_list|)
expr_stmt|;
name|syncConfig
operator|.
name|user
argument_list|()
operator|.
name|setPropertyMapping
argument_list|(
name|mapping
argument_list|)
expr_stmt|;
name|syncConfig
operator|.
name|user
argument_list|()
operator|.
name|setMembershipNestingDepth
argument_list|(
literal|1
argument_list|)
expr_stmt|;
return|return
name|syncConfig
return|;
block|}
specifier|protected
name|Root
name|getSystemRoot
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|systemRoot
operator|==
literal|null
condition|)
block|{
name|systemSession
operator|=
name|Subject
operator|.
name|doAs
argument_list|(
name|SystemSubject
operator|.
name|INSTANCE
argument_list|,
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|ContentSession
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ContentSession
name|run
parameter_list|()
throws|throws
name|LoginException
throws|,
name|NoSuchWorkspaceException
block|{
return|return
name|getContentRepository
argument_list|()
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|systemRoot
operator|=
name|systemSession
operator|.
name|getLatestRoot
argument_list|()
expr_stmt|;
block|}
return|return
name|systemRoot
return|;
block|}
specifier|protected
specifier|static
name|void
name|waitUntilExpired
parameter_list|(
annotation|@
name|NotNull
name|User
name|user
parameter_list|,
annotation|@
name|NotNull
name|Root
name|root
parameter_list|,
name|long
name|expTime
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Tree
name|t
init|=
name|root
operator|.
name|getTree
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|PropertyState
name|ps
init|=
name|t
operator|.
name|getProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_LAST_SYNCED
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps
operator|==
literal|null
operator|||
name|ps
operator|.
name|count
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|long
name|lastSynced
init|=
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
decl_stmt|;
name|long
name|now
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
operator|.
name|getTimeInMillis
argument_list|()
decl_stmt|;
while|while
condition|(
name|now
operator|-
name|lastSynced
operator|<=
name|expTime
condition|)
block|{
name|now
operator|=
name|Calendar
operator|.
name|getInstance
argument_list|()
operator|.
name|getTimeInMillis
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

