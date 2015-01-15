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
name|authorization
operator|.
name|cug
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|security
operator|.
name|PrivilegedActionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import

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
name|ConfigurationPolicy
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
name|PropertyOption
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
name|Reference
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
name|CommitFailedException
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
name|ContentRepository
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
name|tree
operator|.
name|RootFactory
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
name|SystemSubject
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
name|cug
operator|.
name|CugExclude
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
name|AggregatedPermissionProvider
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
name|ControlFlag
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
literal|"CUG Configuration"
argument_list|,
name|description
operator|=
literal|"Component to enable Allows to exclude principal(s) with the configured name(s) from CUG evaluation."
argument_list|,
name|policy
operator|=
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
name|CugConstants
operator|.
name|PARAM_CUG_SUPPORTED_PATHS
argument_list|,
name|label
operator|=
literal|"Supported Paths"
argument_list|,
name|description
operator|=
literal|"Paths under which CUGs can be created and will be evaluated."
argument_list|,
name|cardinality
operator|=
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
block|,
annotation|@
name|Property
argument_list|(
name|name
operator|=
name|CugConstants
operator|.
name|PARAM_CUG_ENABLED
argument_list|,
name|label
operator|=
literal|"CUG Enabled"
argument_list|,
name|description
operator|=
literal|"Flag to enable the evaluation of the configured CUG policies."
argument_list|,
name|boolValue
operator|=
literal|false
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
literal|200
argument_list|)
block|,
annotation|@
name|Property
argument_list|(
name|name
operator|=
name|AggregatedPermissionProvider
operator|.
name|PARAM_CONTROL_FLAG
argument_list|,
name|label
operator|=
literal|"Control Flag"
argument_list|,
name|description
operator|=
literal|"Control flag defining if the permission provider is SUFFICIENT or REQUISITE."
argument_list|,
name|options
operator|=
block|{
annotation|@
name|PropertyOption
argument_list|(
name|name
operator|=
name|ControlFlag
operator|.
name|SUFFICIENT_NAME
argument_list|,
name|value
operator|=
name|ControlFlag
operator|.
name|SUFFICIENT_NAME
argument_list|)
block|,
annotation|@
name|PropertyOption
argument_list|(
name|name
operator|=
name|ControlFlag
operator|.
name|REQUISITE_NAME
argument_list|,
name|value
operator|=
name|ControlFlag
operator|.
name|REQUISITE_NAME
argument_list|)
block|}
argument_list|,
name|value
operator|=
name|ControlFlag
operator|.
name|REQUISITE_NAME
argument_list|)
block|}
argument_list|)
specifier|public
class|class
name|CugConfiguration
extends|extends
name|ConfigurationBase
implements|implements
name|AuthorizationConfiguration
implements|,
name|CugConstants
block|{
annotation|@
name|Reference
specifier|private
name|ContentRepository
name|repository
decl_stmt|;
comment|/**      * Reference to services implementing {@link org.apache.jackrabbit.oak.spi.security.authorization.cug.CugExclude}.      */
annotation|@
name|Reference
specifier|private
name|CugExclude
name|exclude
init|=
operator|new
name|CugExclude
operator|.
name|Default
argument_list|()
decl_stmt|;
specifier|public
name|CugConfiguration
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|CugConfiguration
parameter_list|(
annotation|@
name|Nonnull
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
name|Override
specifier|public
name|AccessControlManager
name|getAccessControlManager
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
name|CugAccessControlManager
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
name|Override
specifier|public
name|PermissionProvider
name|getPermissionProvider
parameter_list|(
name|Root
name|root
parameter_list|,
name|String
name|workspaceName
parameter_list|,
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
name|ConfigurationParameters
name|params
init|=
name|getParameters
argument_list|()
decl_stmt|;
name|boolean
name|enabled
init|=
name|params
operator|.
name|getConfigValue
argument_list|(
name|CugConstants
operator|.
name|PARAM_CUG_ENABLED
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|String
index|[]
name|supportedPaths
init|=
name|params
operator|.
name|getConfigValue
argument_list|(
name|CugConstants
operator|.
name|PARAM_CUG_SUPPORTED_PATHS
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|enabled
operator|||
name|supportedPaths
operator|.
name|length
operator|==
literal|0
operator|||
name|getExclude
argument_list|()
operator|.
name|isExcluded
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
name|ControlFlag
name|flag
init|=
name|ControlFlag
operator|.
name|valueOf
argument_list|(
name|params
operator|.
name|getConfigValue
argument_list|(
name|AggregatedPermissionProvider
operator|.
name|PARAM_CONTROL_FLAG
argument_list|,
name|ControlFlag
operator|.
name|REQUISITE_NAME
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|CugPermissionProvider
argument_list|(
name|root
argument_list|,
name|principals
argument_list|,
name|supportedPaths
argument_list|,
name|flag
argument_list|,
name|getContext
argument_list|()
argument_list|)
return|;
block|}
block|}
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
return|return
operator|new
name|RepositoryInitializer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
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
name|RootFactory
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
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|CugUtil
operator|.
name|registerCugNodeTypes
argument_list|(
name|root
argument_list|)
condition|)
block|{
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
block|}
return|;
block|}
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
name|CugImporter
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Context
name|getContext
parameter_list|()
block|{
return|return
name|CugContext
operator|.
name|INSTANCE
return|;
block|}
comment|//----------------------------------------------------< SCR Integration>---
annotation|@
name|Activate
specifier|protected
name|void
name|activate
parameter_list|()
throws|throws
name|IOException
throws|,
name|CommitFailedException
throws|,
name|PrivilegedActionException
throws|,
name|RepositoryException
block|{
name|ContentSession
name|systemSession
init|=
literal|null
decl_stmt|;
try|try
block|{
name|systemSession
operator|=
name|Subject
operator|.
name|doAs
argument_list|(
name|SystemSubject
operator|.
name|INSTANCE
argument_list|,
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|ContentSession
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ContentSession
name|run
parameter_list|()
throws|throws
name|LoginException
throws|,
name|RepositoryException
block|{
return|return
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|Root
name|root
init|=
name|systemSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
if|if
condition|(
name|CugUtil
operator|.
name|registerCugNodeTypes
argument_list|(
name|root
argument_list|)
condition|)
block|{
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|systemSession
operator|!=
literal|null
condition|)
block|{
name|systemSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|//--------------------------------------------------------------------------
specifier|private
name|CugExclude
name|getExclude
parameter_list|()
block|{
return|return
operator|(
name|exclude
operator|==
literal|null
operator|)
condition|?
operator|new
name|CugExclude
operator|.
name|Default
argument_list|()
else|:
name|exclude
return|;
block|}
block|}
end_class

end_unit

