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
name|Repository
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
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|Configuration
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
name|authentication
operator|.
name|token
operator|.
name|TokenCredentials
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
name|Oak
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
name|JcrCreator
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
name|OakRepositoryFixture
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
name|RepositoryFixture
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
name|jcr
operator|.
name|Jcr
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
name|security
operator|.
name|internal
operator|.
name|SecurityProviderBuilder
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
name|ConfigurationParameters
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
name|ConfigurationUtil
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
name|token
operator|.
name|TokenConfiguration
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
name|user
operator|.
name|UserConfiguration
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
name|user
operator|.
name|UserConstants
import|;
end_import

begin_comment
comment|/**  * AbstractLoginTest... TODO  */
end_comment

begin_class
specifier|abstract
class|class
name|AbstractLoginTest
extends|extends
name|AbstractTest
block|{
specifier|public
specifier|final
specifier|static
name|int
name|COUNT
init|=
literal|1000
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|USER
init|=
literal|"user"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|DEFAULT_ITERATIONS
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|long
name|NO_CACHE
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|String
name|runAsUser
decl_stmt|;
specifier|private
name|boolean
name|runWithToken
decl_stmt|;
specifier|private
name|int
name|noIterations
decl_stmt|;
specifier|private
name|long
name|expiration
decl_stmt|;
specifier|public
name|AbstractLoginTest
parameter_list|()
block|{
name|this
argument_list|(
literal|"admin"
argument_list|,
literal|false
argument_list|,
name|DEFAULT_ITERATIONS
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AbstractLoginTest
parameter_list|(
name|String
name|runAsUser
parameter_list|,
name|boolean
name|runWithToken
parameter_list|,
name|int
name|noIterations
parameter_list|)
block|{
name|this
argument_list|(
name|runAsUser
argument_list|,
name|runWithToken
argument_list|,
name|noIterations
argument_list|,
name|NO_CACHE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AbstractLoginTest
parameter_list|(
name|String
name|runAsUser
parameter_list|,
name|boolean
name|runWithToken
parameter_list|,
name|int
name|noIterations
parameter_list|,
name|long
name|expiration
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|runAsUser
operator|=
name|runAsUser
expr_stmt|;
name|this
operator|.
name|runWithToken
operator|=
name|runWithToken
expr_stmt|;
name|this
operator|.
name|noIterations
operator|=
name|noIterations
expr_stmt|;
name|this
operator|.
name|expiration
operator|=
name|expiration
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setUp
parameter_list|(
name|Repository
name|repository
parameter_list|,
name|Credentials
name|credentials
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|(
name|repository
argument_list|,
name|buildCredentials
argument_list|(
name|repository
argument_list|,
name|credentials
argument_list|)
argument_list|)
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
name|s
init|=
name|loginAdministrative
argument_list|()
decl_stmt|;
try|try
block|{
name|AccessControlUtils
operator|.
name|addAccessControlEntry
argument_list|(
name|s
argument_list|,
literal|"/"
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
if|if
condition|(
name|USER
operator|.
name|equals
argument_list|(
name|runAsUser
argument_list|)
condition|)
block|{
operator|(
operator|(
name|JackrabbitSession
operator|)
name|s
operator|)
operator|.
name|getUserManager
argument_list|()
operator|.
name|createUser
argument_list|(
name|USER
argument_list|,
name|USER
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|s
operator|.
name|save
argument_list|()
expr_stmt|;
name|s
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
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
name|s
init|=
name|loginAdministrative
argument_list|()
decl_stmt|;
try|try
block|{
name|Authorizable
name|authorizable
init|=
operator|(
operator|(
name|JackrabbitSession
operator|)
name|s
operator|)
operator|.
name|getUserManager
argument_list|()
operator|.
name|getAuthorizable
argument_list|(
name|USER
argument_list|)
decl_stmt|;
if|if
condition|(
name|authorizable
operator|!=
literal|null
condition|)
block|{
name|authorizable
operator|.
name|remove
argument_list|()
expr_stmt|;
name|s
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|s
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|boolean
name|customConfigurationParameters
parameter_list|()
block|{
return|return
name|noIterations
operator|!=
operator|-
literal|1
operator|||
name|expiration
operator|>
literal|0
return|;
block|}
specifier|protected
name|ConfigurationParameters
name|prepare
parameter_list|(
name|ConfigurationParameters
name|conf
parameter_list|)
block|{
return|return
name|conf
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Repository
index|[]
name|createRepository
parameter_list|(
name|RepositoryFixture
name|fixture
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|customConfigurationParameters
argument_list|()
condition|)
block|{
if|if
condition|(
name|fixture
operator|instanceof
name|OakRepositoryFixture
condition|)
block|{
return|return
operator|(
operator|(
name|OakRepositoryFixture
operator|)
name|fixture
operator|)
operator|.
name|setUpCluster
argument_list|(
literal|1
argument_list|,
operator|new
name|JcrCreator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Jcr
name|customize
parameter_list|(
name|Oak
name|oak
parameter_list|)
block|{
name|ConfigurationParameters
name|conf
decl_stmt|;
name|ConfigurationParameters
name|iterations
init|=
name|ConfigurationParameters
operator|.
name|EMPTY
decl_stmt|;
if|if
condition|(
name|noIterations
operator|!=
name|DEFAULT_ITERATIONS
condition|)
block|{
name|iterations
operator|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|UserConstants
operator|.
name|PARAM_PASSWORD_HASH_ITERATIONS
argument_list|,
name|noIterations
argument_list|)
expr_stmt|;
block|}
name|ConfigurationParameters
name|cache
init|=
name|ConfigurationParameters
operator|.
name|EMPTY
decl_stmt|;
if|if
condition|(
name|expiration
operator|>
literal|0
condition|)
block|{
name|cache
operator|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
literal|"cacheExpiration"
argument_list|,
name|expiration
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|runWithToken
condition|)
block|{
name|conf
operator|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|TokenConfiguration
operator|.
name|NAME
argument_list|,
name|iterations
argument_list|,
name|UserConfiguration
operator|.
name|NAME
argument_list|,
name|cache
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|conf
operator|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|UserConfiguration
operator|.
name|NAME
argument_list|,
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|iterations
argument_list|,
name|cache
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|conf
operator|=
name|prepare
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|SecurityProvider
name|sp
init|=
name|SecurityProviderBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|with
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
operator|new
name|Jcr
argument_list|(
name|oak
argument_list|)
operator|.
name|with
argument_list|(
name|sp
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not yet supported -> Change repository.xml to configure no of iterations."
argument_list|)
throw|;
block|}
block|}
return|return
name|super
operator|.
name|createRepository
argument_list|(
name|fixture
argument_list|)
return|;
block|}
specifier|private
name|Credentials
name|buildCredentials
parameter_list|(
name|Repository
name|repository
parameter_list|,
name|Credentials
name|credentials
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Credentials
name|creds
decl_stmt|;
if|if
condition|(
literal|"admin"
operator|.
name|equals
argument_list|(
name|runAsUser
argument_list|)
condition|)
block|{
name|creds
operator|=
name|credentials
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"anonymous"
operator|.
name|equals
argument_list|(
name|runAsUser
argument_list|)
condition|)
block|{
name|creds
operator|=
operator|new
name|GuestCredentials
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|creds
operator|=
operator|new
name|SimpleCredentials
argument_list|(
name|USER
argument_list|,
name|USER
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|runWithToken
condition|)
block|{
name|Configuration
operator|.
name|setConfiguration
argument_list|(
name|ConfigurationUtil
operator|.
name|getJackrabbit2Configuration
argument_list|(
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|creds
operator|instanceof
name|SimpleCredentials
condition|)
block|{
name|SimpleCredentials
name|sc
init|=
operator|(
name|SimpleCredentials
operator|)
name|creds
decl_stmt|;
name|sc
operator|.
name|setAttribute
argument_list|(
literal|".token"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|repository
operator|.
name|login
argument_list|(
name|sc
argument_list|)
operator|.
name|logout
argument_list|()
expr_stmt|;
name|creds
operator|=
operator|new
name|TokenCredentials
argument_list|(
name|sc
operator|.
name|getAttribute
argument_list|(
literal|".token"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|return
name|creds
return|;
block|}
block|}
end_class

end_unit

