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
name|List
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
name|namepath
operator|.
name|NamePathMapper
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
name|lifecycle
operator|.
name|WorkspaceInitializer
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
name|Context
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
name|xml
operator|.
name|ProtectedItemImporter
import|;
end_import

begin_comment
comment|/**  * Default implementation of the {@link UserConfiguration}.  */
end_comment

begin_class
annotation|@
name|Component
argument_list|()
annotation|@
name|Service
argument_list|(
block|{
name|UserConfiguration
operator|.
name|class
block|,
name|SecurityConfiguration
operator|.
name|class
block|}
argument_list|)
specifier|public
class|class
name|UserConfigurationImpl
extends|extends
name|ConfigurationBase
implements|implements
name|UserConfiguration
implements|,
name|SecurityConfiguration
block|{
specifier|public
name|UserConfigurationImpl
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|UserConfigurationImpl
parameter_list|(
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
name|super
argument_list|(
name|securityProvider
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
name|WorkspaceInitializer
name|getWorkspaceInitializer
parameter_list|()
block|{
return|return
operator|new
name|UserInitializer
argument_list|(
name|getSecurityProvider
argument_list|()
argument_list|)
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
name|String
name|workspaceName
parameter_list|,
name|Subject
name|subject
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|UserValidatorProvider
argument_list|(
name|getParameters
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|ProtectedItemImporter
argument_list|>
name|getProtectedItemImporters
parameter_list|()
block|{
return|return
name|Collections
operator|.
expr|<
name|ProtectedItemImporter
operator|>
name|singletonList
argument_list|(
operator|new
name|UserImporter
argument_list|(
name|getParameters
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Context
name|getContext
parameter_list|()
block|{
return|return
name|UserContext
operator|.
name|getInstance
argument_list|()
return|;
block|}
comment|//--------------------------------------------------< UserConfiguration>---
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|UserManager
name|getUserManager
parameter_list|(
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
return|return
operator|new
name|UserManagerImpl
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

