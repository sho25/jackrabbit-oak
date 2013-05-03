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
name|JcrConstants
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
name|plugins
operator|.
name|index
operator|.
name|IndexUtils
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
name|query
operator|.
name|QueryIndexProvider
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
name|NodeBuilder
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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_SYSTEM
import|;
end_import

begin_comment
comment|/**  * Implementation of the {@code WorkspaceInitializer} interface that creates  * a property index definitions for {@link #REP_PRINCIPAL_NAME rep:principalName}  * properties defined with ACE nodes.  */
end_comment

begin_class
class|class
name|AccessControlInitializer
implements|implements
name|WorkspaceInitializer
implements|,
name|AccessControlConstants
implements|,
name|PermissionConstants
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|NodeState
name|initialize
parameter_list|(
name|NodeState
name|workspaceRoot
parameter_list|,
name|String
name|workspaceName
parameter_list|,
name|QueryIndexProvider
name|indexProvider
parameter_list|,
name|CommitHook
name|commitHook
parameter_list|)
block|{
name|NodeBuilder
name|root
init|=
name|workspaceRoot
operator|.
name|builder
argument_list|()
decl_stmt|;
comment|// property index for rep:principalName stored in ACEs
name|NodeBuilder
name|index
init|=
name|IndexUtils
operator|.
name|getOrCreateOakIndex
argument_list|(
name|root
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|index
operator|.
name|hasChildNode
argument_list|(
literal|"acPrincipalName"
argument_list|)
condition|)
block|{
name|IndexUtils
operator|.
name|createIndexDefinition
argument_list|(
name|index
argument_list|,
literal|"acPrincipalName"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|of
argument_list|(
name|REP_PRINCIPAL_NAME
argument_list|)
argument_list|,
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|of
argument_list|(
name|NT_REP_DENY_ACE
argument_list|,
name|NT_REP_GRANT_ACE
argument_list|,
name|NT_REP_ACE
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// create the permission store and the root for this workspace.
name|NodeBuilder
name|permissionStore
init|=
name|root
operator|.
name|child
argument_list|(
name|JCR_SYSTEM
argument_list|)
operator|.
name|child
argument_list|(
name|REP_PERMISSION_STORE
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|permissionStore
operator|.
name|hasProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
condition|)
block|{
name|permissionStore
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_REP_PERMISSION_STORE
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|permissionStore
operator|.
name|hasChildNode
argument_list|(
name|workspaceName
argument_list|)
condition|)
block|{
name|permissionStore
operator|.
name|child
argument_list|(
name|workspaceName
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_REP_PERMISSION_STORE
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
return|return
name|root
operator|.
name|getNodeState
argument_list|()
return|;
block|}
block|}
end_class

end_unit

