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
name|authentication
operator|.
name|token
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
name|Nonnull
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
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Activate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Service
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
name|spi
operator|.
name|commit
operator|.
name|MoveTracker
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
name|commit
operator|.
name|ValidatorProvider
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
name|ConfigurationBase
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
name|SecurityConfiguration
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
name|authentication
operator|.
name|token
operator|.
name|TokenProvider
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
name|util
operator|.
name|PasswordUtil
import|;
end_import

begin_comment
comment|/**  * Default implementation for the {@code TokenConfiguration} interface.  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|metatype
operator|=
literal|true
argument_list|,
name|label
operator|=
literal|"Apache Jackrabbit Oak TokenConfiguration"
argument_list|)
annotation|@
name|Service
argument_list|(
block|{
name|TokenConfiguration
operator|.
name|class
block|,
name|SecurityConfiguration
operator|.
name|class
block|}
argument_list|)
annotation|@
name|Properties
argument_list|(
block|{
annotation|@
name|Property
argument_list|(
name|name
operator|=
name|TokenProvider
operator|.
name|PARAM_TOKEN_EXPIRATION
argument_list|,
name|label
operator|=
literal|"Token Expiration"
argument_list|,
name|description
operator|=
literal|"Expiration time of login tokens in ms."
argument_list|)
block|,
annotation|@
name|Property
argument_list|(
name|name
operator|=
name|TokenProvider
operator|.
name|PARAM_TOKEN_LENGTH
argument_list|,
name|label
operator|=
literal|"Token Length"
argument_list|,
name|description
operator|=
literal|"Length of the generated token."
argument_list|)
block|,
annotation|@
name|Property
argument_list|(
name|name
operator|=
name|UserConstants
operator|.
name|PARAM_PASSWORD_HASH_ALGORITHM
argument_list|,
name|label
operator|=
literal|"Hash Algorithm"
argument_list|,
name|description
operator|=
literal|"Name of the algorithm to hash the token."
argument_list|,
name|value
operator|=
name|PasswordUtil
operator|.
name|DEFAULT_ALGORITHM
argument_list|)
block|,
annotation|@
name|Property
argument_list|(
name|name
operator|=
name|UserConstants
operator|.
name|PARAM_PASSWORD_HASH_ITERATIONS
argument_list|,
name|label
operator|=
literal|"Hash Iterations"
argument_list|,
name|description
operator|=
literal|"Number of iterations used to hash the token."
argument_list|,
name|intValue
operator|=
name|PasswordUtil
operator|.
name|DEFAULT_ITERATIONS
argument_list|)
block|,
annotation|@
name|Property
argument_list|(
name|name
operator|=
name|UserConstants
operator|.
name|PARAM_PASSWORD_SALT_SIZE
argument_list|,
name|label
operator|=
literal|"Hash Salt Size"
argument_list|,
name|description
operator|=
literal|"Size of the salt used to generate the hash."
argument_list|,
name|intValue
operator|=
name|PasswordUtil
operator|.
name|DEFAULT_SALT_SIZE
argument_list|)
block|}
argument_list|)
specifier|public
class|class
name|TokenConfigurationImpl
extends|extends
name|ConfigurationBase
implements|implements
name|TokenConfiguration
block|{
specifier|public
name|TokenConfigurationImpl
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|TokenConfigurationImpl
parameter_list|(
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
name|super
argument_list|(
name|securityProvider
argument_list|,
name|securityProvider
operator|.
name|getParameters
argument_list|(
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
annotation|@
name|Activate
specifier|private
name|void
name|activate
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
parameter_list|)
block|{
name|setParameters
argument_list|(
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|properties
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//----------------------------------------------< SecurityConfiguration>---
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
name|NAME
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|ValidatorProvider
argument_list|>
name|getValidators
parameter_list|(
annotation|@
name|Nonnull
name|String
name|workspaceName
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|,
annotation|@
name|Nonnull
name|MoveTracker
name|moveTracker
parameter_list|)
block|{
name|ValidatorProvider
name|vp
init|=
operator|new
name|TokenValidatorProvider
argument_list|(
name|getSecurityProvider
argument_list|()
operator|.
name|getParameters
argument_list|(
name|UserConfiguration
operator|.
name|NAME
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|vp
argument_list|)
return|;
block|}
comment|//-------------------------------------------------< TokenConfiguration>---
comment|/**      * Returns a new instance of {@link org.apache.jackrabbit.oak.spi.security.authentication.token.TokenProvider}.      *      * @param root The target root.      * @return A new instance of {@link org.apache.jackrabbit.oak.spi.security.authentication.token.TokenProvider}.      */
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|TokenProvider
name|getTokenProvider
parameter_list|(
name|Root
name|root
parameter_list|)
block|{
name|UserConfiguration
name|uc
init|=
name|getSecurityProvider
argument_list|()
operator|.
name|getConfiguration
argument_list|(
name|UserConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenProviderImpl
argument_list|(
name|root
argument_list|,
name|getParameters
argument_list|()
argument_list|,
name|uc
argument_list|)
return|;
block|}
block|}
end_class

end_unit

