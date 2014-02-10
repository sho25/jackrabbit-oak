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
name|authorization
operator|.
name|permission
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|core
operator|.
name|ImmutableRoot
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
name|plugins
operator|.
name|tree
operator|.
name|ImmutableTree
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
name|CommitInfo
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
name|Validator
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
name|permission
operator|.
name|PermissionConstants
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
name|permission
operator|.
name|PermissionProvider
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
name|permission
operator|.
name|Permissions
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
name|state
operator|.
name|NodeState
import|;
end_import

begin_comment
comment|/**  * {@code ValidatorProvider} implementation for permission evaluation associated  * with write operations.  */
end_comment

begin_class
specifier|public
class|class
name|PermissionValidatorProvider
extends|extends
name|ValidatorProvider
block|{
specifier|private
specifier|final
name|SecurityProvider
name|securityProvider
decl_stmt|;
specifier|private
specifier|final
name|AuthorizationConfiguration
name|acConfig
decl_stmt|;
specifier|private
specifier|final
name|long
name|jr2Permissions
decl_stmt|;
specifier|private
specifier|final
name|String
name|workspaceName
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
decl_stmt|;
specifier|private
specifier|final
name|MoveTracker
name|moveTracker
decl_stmt|;
specifier|private
name|Context
name|acCtx
decl_stmt|;
specifier|private
name|Context
name|userCtx
decl_stmt|;
specifier|public
name|PermissionValidatorProvider
parameter_list|(
name|SecurityProvider
name|securityProvider
parameter_list|,
name|String
name|workspaceName
parameter_list|,
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|,
name|MoveTracker
name|moveTracker
parameter_list|)
block|{
name|this
operator|.
name|securityProvider
operator|=
name|securityProvider
expr_stmt|;
name|this
operator|.
name|acConfig
operator|=
name|securityProvider
operator|.
name|getConfiguration
argument_list|(
name|AuthorizationConfiguration
operator|.
name|class
argument_list|)
expr_stmt|;
name|ConfigurationParameters
name|params
init|=
name|acConfig
operator|.
name|getParameters
argument_list|()
decl_stmt|;
name|String
name|compatValue
init|=
name|params
operator|.
name|getConfigValue
argument_list|(
name|PermissionConstants
operator|.
name|PARAM_PERMISSIONS_JR2
argument_list|,
literal|null
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|jr2Permissions
operator|=
name|Permissions
operator|.
name|getPermissions
argument_list|(
name|compatValue
argument_list|)
expr_stmt|;
name|this
operator|.
name|workspaceName
operator|=
name|workspaceName
expr_stmt|;
name|this
operator|.
name|principals
operator|=
name|principals
expr_stmt|;
name|this
operator|.
name|moveTracker
operator|=
name|moveTracker
expr_stmt|;
block|}
comment|//--------------------------------------------------< ValidatorProvider>---
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Validator
name|getRootValidator
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
block|{
name|PermissionProvider
name|pp
init|=
name|acConfig
operator|.
name|getPermissionProvider
argument_list|(
operator|new
name|ImmutableRoot
argument_list|(
name|before
argument_list|)
argument_list|,
name|workspaceName
argument_list|,
name|principals
argument_list|)
decl_stmt|;
name|ImmutableTree
name|rootBefore
init|=
operator|new
name|ImmutableTree
argument_list|(
name|before
argument_list|)
decl_stmt|;
name|ImmutableTree
name|rootAfter
init|=
operator|new
name|ImmutableTree
argument_list|(
name|after
argument_list|)
decl_stmt|;
if|if
condition|(
name|moveTracker
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|new
name|PermissionValidator
argument_list|(
name|rootBefore
argument_list|,
name|rootAfter
argument_list|,
name|pp
argument_list|,
name|this
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|MoveAwarePermissionValidator
argument_list|(
name|rootBefore
argument_list|,
name|rootAfter
argument_list|,
name|pp
argument_list|,
name|this
argument_list|,
name|moveTracker
argument_list|)
return|;
block|}
block|}
comment|//--------------------------------------------------------------------------
name|Context
name|getAccessControlContext
parameter_list|()
block|{
if|if
condition|(
name|acCtx
operator|==
literal|null
condition|)
block|{
name|acCtx
operator|=
name|acConfig
operator|.
name|getContext
argument_list|()
expr_stmt|;
block|}
return|return
name|acCtx
return|;
block|}
name|Context
name|getUserContext
parameter_list|()
block|{
if|if
condition|(
name|userCtx
operator|==
literal|null
condition|)
block|{
name|UserConfiguration
name|uc
init|=
name|securityProvider
operator|.
name|getConfiguration
argument_list|(
name|UserConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
name|userCtx
operator|=
name|uc
operator|.
name|getContext
argument_list|()
expr_stmt|;
block|}
return|return
name|userCtx
return|;
block|}
name|boolean
name|requiresJr2Permissions
parameter_list|(
name|long
name|permission
parameter_list|)
block|{
return|return
name|Permissions
operator|.
name|includes
argument_list|(
name|jr2Permissions
argument_list|,
name|permission
argument_list|)
return|;
block|}
block|}
end_class

end_unit

