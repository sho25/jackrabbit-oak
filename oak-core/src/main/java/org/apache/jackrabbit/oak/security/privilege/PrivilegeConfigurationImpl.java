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
name|privilege
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
name|authorization
operator|.
name|PrivilegeManager
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
name|CommitHook
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
name|RepositoryInitializer
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
name|privilege
operator|.
name|PrivilegeConfiguration
import|;
end_import

begin_comment
comment|/**  * Configuration for the privilege management component.  */
end_comment

begin_class
specifier|public
class|class
name|PrivilegeConfigurationImpl
extends|extends
name|ConfigurationBase
implements|implements
name|PrivilegeConfiguration
block|{
comment|//---------------------------------------------< PrivilegeConfiguration>---
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|PrivilegeManager
name|getPrivilegeManager
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
name|PrivilegeManagerImpl
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|)
return|;
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
name|RepositoryInitializer
name|getRepositoryInitializer
parameter_list|()
block|{
return|return
operator|new
name|PrivilegeInitializer
argument_list|()
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
name|CommitHook
argument_list|>
name|getCommitHooks
parameter_list|(
name|String
name|workspaceName
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|JcrAllCommitHook
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
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|PrivilegeValidatorProvider
argument_list|()
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
name|PrivilegeContext
operator|.
name|getInstance
argument_list|()
return|;
block|}
block|}
end_class

end_unit

