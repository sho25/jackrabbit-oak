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
name|exercise
operator|.
name|security
operator|.
name|authorization
operator|.
name|models
operator|.
name|simplifiedroles
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

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
name|security
operator|.
name|AccessControlManager
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
name|Deactivate
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
name|Modified
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
name|commons
operator|.
name|PropertiesUtil
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
name|plugins
operator|.
name|memory
operator|.
name|MemoryNodeStore
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
name|name
operator|.
name|NamespaceEditorProvider
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
name|nodetype
operator|.
name|ReadOnlyNodeTypeManager
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
name|nodetype
operator|.
name|TypeEditorProvider
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
name|nodetype
operator|.
name|write
operator|.
name|NodeTypeRegistry
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
name|TreeLocation
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
name|CompositeEditorProvider
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
name|EditorHook
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
name|CompositeConfiguration
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
name|accesscontrol
operator|.
name|AccessControlConstants
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
name|EmptyPermissionProvider
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
name|restriction
operator|.
name|RestrictionProvider
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
name|AdminPrincipal
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
name|SystemPrincipal
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
name|ApplyDiff
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
name|NodeStore
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|oak
operator|.
name|spi
operator|.
name|security
operator|.
name|RegistrationConstants
operator|.
name|OAK_SECURITY_NAME
import|;
end_import

begin_class
annotation|@
name|Component
argument_list|(
name|metatype
operator|=
literal|true
argument_list|,
name|policy
operator|=
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
name|ConfigurationPolicy
operator|.
name|REQUIRE
argument_list|)
annotation|@
name|Service
argument_list|(
block|{
name|AuthorizationConfiguration
operator|.
name|class
block|,
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
literal|"supportedPath"
argument_list|,
name|label
operator|=
literal|"Supported Path"
argument_list|)
block|,
annotation|@
name|Property
argument_list|(
name|name
operator|=
name|CompositeConfiguration
operator|.
name|PARAM_RANKING
argument_list|,
name|label
operator|=
literal|"Ranking"
argument_list|,
name|description
operator|=
literal|"Ranking of this configuration in a setup with multiple authorization configurations."
argument_list|,
name|intValue
operator|=
literal|10
argument_list|)
block|,
annotation|@
name|Property
argument_list|(
name|name
operator|=
name|OAK_SECURITY_NAME
argument_list|,
name|propertyPrivate
operator|=
literal|true
argument_list|,
name|value
operator|=
literal|"org.apache.jackrabbit.oak.exercise.security.authorization.models.simplifiedroles.ThreeRolesAuthorizationConfiguration"
argument_list|)
block|}
argument_list|)
specifier|public
class|class
name|ThreeRolesAuthorizationConfiguration
extends|extends
name|ConfigurationBase
implements|implements
name|AuthorizationConfiguration
implements|,
name|ThreeRolesConstants
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ThreeRolesAuthorizationConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|String
name|supportedPath
decl_stmt|;
annotation|@
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
name|supportedPath
operator|=
name|PropertiesUtil
operator|.
name|toString
argument_list|(
name|properties
operator|.
name|get
argument_list|(
literal|"supportedPath"
argument_list|)
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Modified
specifier|private
name|void
name|modified
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
name|supportedPath
operator|=
name|PropertiesUtil
operator|.
name|toString
argument_list|(
name|properties
operator|.
name|get
argument_list|(
literal|"supportedPath"
argument_list|)
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deactivate
specifier|private
name|void
name|deactivate
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
name|supportedPath
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|AccessControlManager
name|getAccessControlManager
parameter_list|(
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|,
annotation|@
name|Nonnull
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
return|return
operator|new
name|ThreeRolesAccessControlManager
argument_list|(
name|root
argument_list|,
name|supportedPath
argument_list|,
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
name|RestrictionProvider
name|getRestrictionProvider
parameter_list|()
block|{
return|return
name|RestrictionProvider
operator|.
name|EMPTY
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|PermissionProvider
name|getPermissionProvider
parameter_list|(
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|,
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
parameter_list|)
block|{
if|if
condition|(
name|supportedPath
operator|==
literal|null
operator|||
name|isAdminOrSystem
argument_list|(
name|principals
argument_list|)
condition|)
block|{
return|return
name|EmptyPermissionProvider
operator|.
name|getInstance
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|new
name|ThreeRolesPermissionProvider
argument_list|(
name|root
argument_list|,
name|principals
argument_list|,
name|supportedPath
argument_list|,
name|getContext
argument_list|()
argument_list|,
name|getRootProvider
argument_list|()
argument_list|)
return|;
block|}
block|}
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
name|AuthorizationConfiguration
operator|.
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
name|String
name|cnd
init|=
literal|"<rep='internal'>\n"
operator|+
literal|"["
operator|+
name|MIX_REP_THREE_ROLES_POLICY
operator|+
literal|"] \n   mixin\n "
operator|+
literal|"  +"
operator|+
name|REP_3_ROLES_POLICY
operator|+
literal|" ("
operator|+
name|NT_REP_THREE_ROLES_POLICY
operator|+
literal|") protected IGNORE\n\n"
operator|+
literal|"["
operator|+
name|NT_REP_THREE_ROLES_POLICY
operator|+
literal|"]> "
operator|+
name|AccessControlConstants
operator|.
name|NT_REP_POLICY
operator|+
literal|"\n"
operator|+
literal|"  - "
operator|+
name|REP_READERS
operator|+
literal|" (STRING) multiple protected IGNORE\n"
operator|+
literal|"  - "
operator|+
name|REP_EDITORS
operator|+
literal|" (STRING) multiple protected IGNORE\n"
operator|+
literal|"  - "
operator|+
name|REP_OWNERS
operator|+
literal|" (STRING) multiple protected IGNORE"
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|cnd
argument_list|)
expr_stmt|;
return|return
name|builder
lambda|->
block|{
name|NodeState
name|base
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|NodeStore
name|store
init|=
operator|new
name|MemoryNodeStore
argument_list|(
name|base
argument_list|)
decl_stmt|;
name|Root
name|root
init|=
name|getRootProvider
argument_list|()
operator|.
name|createSystemRoot
argument_list|(
name|store
argument_list|,
operator|new
name|EditorHook
argument_list|(
operator|new
name|CompositeEditorProvider
argument_list|(
operator|new
name|NamespaceEditorProvider
argument_list|()
argument_list|,
operator|new
name|TypeEditorProvider
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|ReadOnlyNodeTypeManager
operator|.
name|getInstance
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
operator|.
name|hasNodeType
argument_list|(
name|MIX_REP_THREE_ROLES_POLICY
argument_list|)
condition|)
block|{
name|NodeTypeRegistry
operator|.
name|register
argument_list|(
name|root
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
name|cnd
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
literal|"oak exercise"
argument_list|)
expr_stmt|;
name|NodeState
name|target
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|target
operator|.
name|compareAgainstBaseState
argument_list|(
name|base
argument_list|,
operator|new
name|ApplyDiff
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|ValidatorProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
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
comment|// TODO: write a validator that meets the following requirements:
comment|// 1. check that the item names defined with the node types are only used within the scope of the 2 node types
comment|// 2. check that the policies are never nested.
comment|// NOTE: having this validator allows to rely on item names in
comment|// Context implementation and simplify the permission evaluation
comment|// i.e. preventing expensive effective node type calculation/verification
comment|// and checks for nested policies in time critical evaluation steps.
return|return
literal|null
return|;
block|}
block|}
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
comment|// TODO
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
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
comment|/**          * Simplified implementation of the Context interface that just tests          * item names without every evaluating node type information.          * See {@link #getValidators(String, Set, MoveTracker)} above.          */
return|return
operator|new
name|Context
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|definesProperty
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|parent
parameter_list|,
annotation|@
name|Nonnull
name|PropertyState
name|property
parameter_list|)
block|{
return|return
name|definesTree
argument_list|(
name|parent
argument_list|)
operator|&&
name|NAMES
operator|.
name|contains
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesContextRoot
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|definesTree
argument_list|(
name|tree
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesTree
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|REP_3_ROLES_POLICY
operator|.
name|equals
argument_list|(
name|tree
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesLocation
parameter_list|(
annotation|@
name|Nonnull
name|TreeLocation
name|location
parameter_list|)
block|{
name|String
name|name
init|=
name|location
operator|.
name|getName
argument_list|()
decl_stmt|;
return|return
name|NAMES
operator|.
name|contains
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesInternal
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isAdminOrSystem
parameter_list|(
annotation|@
name|Nonnull
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
if|if
condition|(
name|principals
operator|.
name|contains
argument_list|(
name|SystemPrincipal
operator|.
name|INSTANCE
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
for|for
control|(
name|Principal
name|principal
range|:
name|principals
control|)
block|{
if|if
condition|(
name|principal
operator|instanceof
name|AdminPrincipal
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setParameters
parameter_list|(
annotation|@
name|Nonnull
name|ConfigurationParameters
name|config
parameter_list|)
block|{
name|super
operator|.
name|setParameters
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|supportedPath
operator|=
name|config
operator|.
name|getConfigValue
argument_list|(
literal|"supportedPath"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

