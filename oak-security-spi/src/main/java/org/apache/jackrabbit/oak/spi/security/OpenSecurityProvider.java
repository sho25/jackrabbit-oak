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
package|;
end_package

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
name|annotation
operator|.
name|Nullable
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
name|oak
operator|.
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|AuthenticationConfiguration
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
name|OpenAuthenticationConfiguration
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
name|AuthorizationConfiguration
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
name|OpenAuthorizationConfiguration
import|;
end_import

begin_comment
comment|/**  * Rudimentary {@code SecurityProvider} implementation that allow every subject  * to authenticate and grants it full access everywhere. Note, that this  * implementation does not provide support for other security related features  * such as e.g. user or access control management.  *  * @see org.apache.jackrabbit.oak.spi.security.authentication.OpenAuthenticationConfiguration  * @see org.apache.jackrabbit.oak.spi.security.authorization.OpenAuthorizationConfiguration  */
end_comment

begin_class
specifier|public
class|class
name|OpenSecurityProvider
implements|implements
name|SecurityProvider
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|ConfigurationParameters
name|getParameters
parameter_list|(
annotation|@
name|Nullable
name|String
name|name
parameter_list|)
block|{
return|return
name|ConfigurationParameters
operator|.
name|EMPTY
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|SecurityConfiguration
argument_list|>
name|getConfigurations
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|OpenAuthenticationConfiguration
argument_list|()
argument_list|,
operator|new
name|OpenAuthorizationConfiguration
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|getConfiguration
parameter_list|(
annotation|@
name|Nonnull
name|Class
argument_list|<
name|T
argument_list|>
name|configClass
parameter_list|)
block|{
if|if
condition|(
name|AuthenticationConfiguration
operator|.
name|class
operator|==
name|configClass
condition|)
block|{
return|return
operator|(
name|T
operator|)
operator|new
name|OpenAuthenticationConfiguration
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|AuthorizationConfiguration
operator|.
name|class
operator|==
name|configClass
condition|)
block|{
return|return
operator|(
name|T
operator|)
operator|new
name|OpenAuthorizationConfiguration
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported security configuration class "
operator|+
name|configClass
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit
