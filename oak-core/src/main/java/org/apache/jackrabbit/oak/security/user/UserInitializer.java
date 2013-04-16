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
name|core
operator|.
name|RootImpl
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
name|index
operator|.
name|IndexConstants
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
name|EmptyHook
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
name|security
operator|.
name|OpenSecurityProvider
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
name|NodeStoreBranch
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
name|util
operator|.
name|NodeUtil
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_comment
comment|/**  * Creates initial set of users to be present in a given workspace. This  * implementation uses the {@code UserManager} such as defined by the  * user configuration.  *<p/>  * Currently the following users are created:  *<p/>  *<ul>  *<li>An administrator user using {@link UserConstants#PARAM_ADMIN_ID}  * or {@link UserConstants#DEFAULT_ADMIN_ID} if the config option is missing.</li>  *<li>An administrator user using {@link UserConstants#PARAM_ANONYMOUS_ID}  * or {@link UserConstants#DEFAULT_ANONYMOUS_ID} if the config option is  * missing.</li>  *</ul>  *<p/>  * In addition this initializer sets up index definitions for the following  * user related properties:  *<p/>  *<ul>  *<li>{@link UserConstants#REP_AUTHORIZABLE_ID}</li>  *<li>{@link UserConstants#REP_PRINCIPAL_NAME}</li>  *<li>{@link UserConstants#REP_MEMBERS}</li>  *</ul>  */
end_comment

begin_class
specifier|public
class|class
name|UserInitializer
implements|implements
name|WorkspaceInitializer
implements|,
name|UserConstants
block|{
comment|/**      * logger instance      */
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
name|UserInitializer
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|SecurityProvider
name|securityProvider
decl_stmt|;
name|UserInitializer
parameter_list|(
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
name|this
operator|.
name|securityProvider
operator|=
name|securityProvider
expr_stmt|;
block|}
comment|//-----------------------------------------------< WorkspaceInitializer>---
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
name|MemoryNodeStore
name|store
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|NodeStoreBranch
name|branch
init|=
name|store
operator|.
name|branch
argument_list|()
decl_stmt|;
name|branch
operator|.
name|setRoot
argument_list|(
name|workspaceRoot
argument_list|)
expr_stmt|;
try|try
block|{
name|branch
operator|.
name|merge
argument_list|(
name|EmptyHook
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
comment|// TODO reconsider
name|Root
name|root
init|=
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|,
name|commitHook
argument_list|,
name|workspaceName
argument_list|,
name|SystemSubject
operator|.
name|INSTANCE
argument_list|,
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|,
name|indexProvider
argument_list|)
decl_stmt|;
name|UserConfiguration
name|userConfiguration
init|=
name|securityProvider
operator|.
name|getUserConfiguration
argument_list|()
decl_stmt|;
name|UserManager
name|userManager
init|=
name|userConfiguration
operator|.
name|getUserManager
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|String
name|errorMsg
init|=
literal|"Failed to initialize user content."
decl_stmt|;
try|try
block|{
name|NodeUtil
name|rootTree
init|=
name|checkNotNull
argument_list|(
operator|new
name|NodeUtil
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|NodeUtil
name|index
init|=
name|rootTree
operator|.
name|getOrAddChild
argument_list|(
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|index
operator|.
name|hasChild
argument_list|(
literal|"authorizableId"
argument_list|)
condition|)
block|{
name|IndexUtils
operator|.
name|createIndexDefinition
argument_list|(
name|index
argument_list|,
literal|"authorizableId"
argument_list|,
literal|true
argument_list|,
operator|new
name|String
index|[]
block|{
name|REP_AUTHORIZABLE_ID
block|}
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|index
operator|.
name|hasChild
argument_list|(
literal|"principalName"
argument_list|)
condition|)
block|{
name|IndexUtils
operator|.
name|createIndexDefinition
argument_list|(
name|index
argument_list|,
literal|"principalName"
argument_list|,
literal|true
argument_list|,
operator|new
name|String
index|[]
block|{
name|REP_PRINCIPAL_NAME
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
name|NT_REP_GROUP
block|,
name|NT_REP_USER
block|,
name|NT_REP_AUTHORIZABLE
block|}
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|index
operator|.
name|hasChild
argument_list|(
literal|"members"
argument_list|)
condition|)
block|{
name|IndexUtils
operator|.
name|createIndexDefinition
argument_list|(
name|index
argument_list|,
literal|"members"
argument_list|,
literal|false
argument_list|,
operator|new
name|String
index|[]
block|{
name|UserConstants
operator|.
name|REP_MEMBERS
block|}
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|String
name|adminId
init|=
name|userConfiguration
operator|.
name|getConfigurationParameters
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|PARAM_ADMIN_ID
argument_list|,
name|DEFAULT_ADMIN_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|adminId
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// TODO: init admin with null password and force application to set it.
name|userManager
operator|.
name|createUser
argument_list|(
name|adminId
argument_list|,
name|adminId
argument_list|)
expr_stmt|;
block|}
name|String
name|anonymousId
init|=
name|userConfiguration
operator|.
name|getConfigurationParameters
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|PARAM_ANONYMOUS_ID
argument_list|,
name|DEFAULT_ANONYMOUS_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|anonymousId
argument_list|)
operator|==
literal|null
condition|)
block|{
name|userManager
operator|.
name|createUser
argument_list|(
name|anonymousId
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
condition|)
block|{
name|root
operator|.
name|commit
argument_list|()
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
name|errorMsg
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|errorMsg
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|store
operator|.
name|getRoot
argument_list|()
return|;
block|}
block|}
end_class

end_unit

