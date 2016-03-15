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
operator|.
name|impl
package|;
end_package

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
name|annotation
operator|.
name|CheckForNull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Credentials
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|GuestCredentials
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
name|collect
operator|.
name|ImmutableList
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
name|ImmutableMap
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
name|ImmutableSet
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
name|AuthInfo
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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|external
operator|.
name|ExternalGroup
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
name|ExternalIdentity
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
name|ExternalIdentityProvider
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
name|ExternalIdentityRef
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
name|ExternalLoginModuleTestBase
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
name|ExternalUser
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
name|credentials
operator|.
name|CredentialsSupport
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
name|fail
import|;
end_import

begin_comment
comment|/**  * Test login against the {@link ExternalLoginModule} with a setup that includes  * a custom implementation of {@link CredentialsSupport} and an {@link ExternalIdentityProvider}  * that deals with these supported credentials.  */
end_comment

begin_class
specifier|public
class|class
name|CustomCredentialsSupportTest
extends|extends
name|ExternalLoginModuleTestBase
block|{
specifier|private
specifier|final
name|IDP
name|idp
init|=
operator|new
name|IDP
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|void
name|assertAttributes
parameter_list|(
annotation|@
name|Nonnull
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|expected
parameter_list|,
annotation|@
name|Nonnull
name|AuthInfo
name|info
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expected
operator|.
name|size
argument_list|()
argument_list|,
name|info
operator|.
name|getAttributeNames
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|aName
range|:
name|info
operator|.
name|getAttributeNames
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
name|expected
operator|.
name|get
argument_list|(
name|aName
argument_list|)
argument_list|,
name|info
operator|.
name|getAttribute
argument_list|(
name|aName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLogin
parameter_list|()
throws|throws
name|Exception
block|{
name|TestCredentials
name|creds
init|=
operator|new
name|TestCredentials
argument_list|(
literal|"testUser"
argument_list|)
decl_stmt|;
name|ContentSession
name|cs
init|=
name|login
argument_list|(
name|creds
argument_list|)
decl_stmt|;
try|try
block|{
name|AuthInfo
name|info
init|=
name|cs
operator|.
name|getAuthInfo
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"testUser"
argument_list|,
name|info
operator|.
name|getUserID
argument_list|()
argument_list|)
expr_stmt|;
name|assertAttributes
argument_list|(
name|idp
operator|.
name|getAttributes
argument_list|(
name|creds
argument_list|)
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLoginWithUnsupportedCredentials
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Credentials
argument_list|>
name|creds
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
literal|"testUser"
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|,
operator|new
name|GuestCredentials
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Credentials
name|c
range|:
name|creds
control|)
block|{
try|try
block|{
name|login
argument_list|(
name|c
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"login must fail for credentials "
operator|+
name|c
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LoginException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
block|}
annotation|@
name|Override
specifier|protected
name|ExternalIdentityProvider
name|createIDP
parameter_list|()
block|{
return|return
name|idp
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|destroyIDP
parameter_list|(
name|ExternalIdentityProvider
name|idp
parameter_list|)
block|{
comment|// ignore
block|}
specifier|private
specifier|static
specifier|final
class|class
name|TestCredentials
implements|implements
name|Credentials
block|{
specifier|private
specifier|final
name|String
name|uid
decl_stmt|;
specifier|private
name|TestCredentials
parameter_list|(
annotation|@
name|Nonnull
name|String
name|uid
parameter_list|)
block|{
name|this
operator|.
name|uid
operator|=
name|uid
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
specifier|final
class|class
name|IDP
implements|implements
name|ExternalIdentityProvider
implements|,
name|CredentialsSupport
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"creds_test"
return|;
block|}
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
name|ExternalIdentity
name|getIdentity
parameter_list|(
annotation|@
name|Nonnull
name|ExternalIdentityRef
name|ref
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
name|ExternalUser
name|getUser
parameter_list|(
annotation|@
name|Nonnull
name|String
name|userId
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
name|ExternalUser
name|authenticate
parameter_list|(
annotation|@
name|Nonnull
name|Credentials
name|credentials
parameter_list|)
block|{
if|if
condition|(
name|credentials
operator|instanceof
name|TestCredentials
condition|)
block|{
specifier|final
name|String
name|uid
init|=
operator|(
operator|(
name|TestCredentials
operator|)
name|credentials
operator|)
operator|.
name|uid
decl_stmt|;
return|return
operator|new
name|ExternalUser
argument_list|()
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|ExternalIdentityRef
name|getExternalId
parameter_list|()
block|{
return|return
operator|new
name|ExternalIdentityRef
argument_list|(
name|uid
argument_list|,
literal|"test"
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|uid
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getPrincipalName
parameter_list|()
block|{
return|return
literal|"principal"
operator|+
name|uid
return|;
block|}
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
name|String
name|getIntermediatePath
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|ExternalIdentityRef
argument_list|>
name|getDeclaredGroups
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|getProperties
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
block|}
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
name|ExternalGroup
name|getGroup
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ExternalUser
argument_list|>
name|listUsers
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ExternalGroup
argument_list|>
name|listGroups
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Class
argument_list|>
name|getCredentialClasses
parameter_list|()
block|{
return|return
name|ImmutableSet
operator|.
expr|<
name|Class
operator|>
name|of
argument_list|(
name|TestCredentials
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
name|String
name|getUserId
parameter_list|(
annotation|@
name|Nonnull
name|Credentials
name|credentials
parameter_list|)
block|{
if|if
condition|(
name|credentials
operator|instanceof
name|TestCredentials
condition|)
block|{
return|return
operator|(
operator|(
name|TestCredentials
operator|)
name|credentials
operator|)
operator|.
name|uid
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|getAttributes
parameter_list|(
annotation|@
name|Nonnull
name|Credentials
name|credentials
parameter_list|)
block|{
if|if
condition|(
name|credentials
operator|instanceof
name|TestCredentials
condition|)
block|{
return|return
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"a"
argument_list|,
literal|"a"
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|ImmutableMap
operator|.
name|of
argument_list|()
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

