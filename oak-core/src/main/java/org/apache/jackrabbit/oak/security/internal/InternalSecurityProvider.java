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
name|internal
package|;
end_package

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
name|principal
operator|.
name|PrincipalConfiguration
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
name|privilege
operator|.
name|PrivilegeConfiguration
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
name|whiteboard
operator|.
name|Whiteboard
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
name|whiteboard
operator|.
name|WhiteboardAware
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
import|;
end_import

begin_class
class|class
name|InternalSecurityProvider
implements|implements
name|SecurityProvider
implements|,
name|WhiteboardAware
block|{
specifier|private
name|AuthenticationConfiguration
name|authenticationConfiguration
decl_stmt|;
specifier|private
name|AuthorizationConfiguration
name|authorizationConfiguration
decl_stmt|;
specifier|private
name|UserConfiguration
name|userConfiguration
decl_stmt|;
specifier|private
name|PrivilegeConfiguration
name|privilegeConfiguration
decl_stmt|;
specifier|private
name|PrincipalConfiguration
name|principalConfiguration
decl_stmt|;
specifier|private
name|TokenConfiguration
name|tokenConfiguration
decl_stmt|;
specifier|private
name|Whiteboard
name|whiteboard
decl_stmt|;
annotation|@
name|NotNull
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
name|SecurityConfiguration
name|securityConfiguration
init|=
name|getSecurityConfigurationByName
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|securityConfiguration
operator|==
literal|null
condition|)
block|{
return|return
name|ConfigurationParameters
operator|.
name|EMPTY
return|;
block|}
return|return
name|securityConfiguration
operator|.
name|getParameters
argument_list|()
return|;
block|}
specifier|private
name|SecurityConfiguration
name|getSecurityConfigurationByName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|AuthenticationConfiguration
operator|.
name|NAME
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|authenticationConfiguration
return|;
block|}
if|if
condition|(
name|AuthorizationConfiguration
operator|.
name|NAME
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|authorizationConfiguration
return|;
block|}
if|if
condition|(
name|UserConfiguration
operator|.
name|NAME
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|userConfiguration
return|;
block|}
if|if
condition|(
name|PrivilegeConfiguration
operator|.
name|NAME
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|privilegeConfiguration
return|;
block|}
if|if
condition|(
name|PrincipalConfiguration
operator|.
name|NAME
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|principalConfiguration
return|;
block|}
if|if
condition|(
name|TokenConfiguration
operator|.
name|NAME
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|tokenConfiguration
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|NotNull
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
name|newHashSet
argument_list|(
name|authenticationConfiguration
argument_list|,
name|authorizationConfiguration
argument_list|,
name|userConfiguration
argument_list|,
name|privilegeConfiguration
argument_list|,
name|principalConfiguration
argument_list|,
name|tokenConfiguration
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|getConfiguration
parameter_list|(
annotation|@
name|NotNull
name|Class
argument_list|<
name|T
argument_list|>
name|configurationClass
parameter_list|)
block|{
if|if
condition|(
name|configurationClass
operator|==
name|AuthenticationConfiguration
operator|.
name|class
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|authenticationConfiguration
return|;
block|}
if|if
condition|(
name|configurationClass
operator|==
name|AuthorizationConfiguration
operator|.
name|class
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|authorizationConfiguration
return|;
block|}
if|if
condition|(
name|configurationClass
operator|==
name|UserConfiguration
operator|.
name|class
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|userConfiguration
return|;
block|}
if|if
condition|(
name|configurationClass
operator|==
name|PrivilegeConfiguration
operator|.
name|class
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|privilegeConfiguration
return|;
block|}
if|if
condition|(
name|configurationClass
operator|==
name|PrincipalConfiguration
operator|.
name|class
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|principalConfiguration
return|;
block|}
if|if
condition|(
name|configurationClass
operator|==
name|TokenConfiguration
operator|.
name|class
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|tokenConfiguration
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported security configuration class "
operator|+
name|configurationClass
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setWhiteboard
parameter_list|(
annotation|@
name|NotNull
name|Whiteboard
name|whiteboard
parameter_list|)
block|{
name|this
operator|.
name|whiteboard
operator|=
name|whiteboard
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Whiteboard
name|getWhiteboard
parameter_list|()
block|{
return|return
name|whiteboard
return|;
block|}
specifier|public
name|void
name|setAuthenticationConfiguration
parameter_list|(
name|AuthenticationConfiguration
name|authenticationConfiguration
parameter_list|)
block|{
name|this
operator|.
name|authenticationConfiguration
operator|=
name|authenticationConfiguration
expr_stmt|;
block|}
specifier|public
name|void
name|setAuthorizationConfiguration
parameter_list|(
name|AuthorizationConfiguration
name|authorizationConfiguration
parameter_list|)
block|{
name|this
operator|.
name|authorizationConfiguration
operator|=
name|authorizationConfiguration
expr_stmt|;
block|}
specifier|public
name|void
name|setUserConfiguration
parameter_list|(
name|UserConfiguration
name|userConfiguration
parameter_list|)
block|{
name|this
operator|.
name|userConfiguration
operator|=
name|userConfiguration
expr_stmt|;
block|}
specifier|public
name|void
name|setPrivilegeConfiguration
parameter_list|(
name|PrivilegeConfiguration
name|privilegeConfiguration
parameter_list|)
block|{
name|this
operator|.
name|privilegeConfiguration
operator|=
name|privilegeConfiguration
expr_stmt|;
block|}
specifier|public
name|void
name|setPrincipalConfiguration
parameter_list|(
name|PrincipalConfiguration
name|principalConfiguration
parameter_list|)
block|{
name|this
operator|.
name|principalConfiguration
operator|=
name|principalConfiguration
expr_stmt|;
block|}
specifier|public
name|void
name|setTokenConfiguration
parameter_list|(
name|TokenConfiguration
name|tokenConfiguration
parameter_list|)
block|{
name|this
operator|.
name|tokenConfiguration
operator|=
name|tokenConfiguration
expr_stmt|;
block|}
block|}
end_class

end_unit

