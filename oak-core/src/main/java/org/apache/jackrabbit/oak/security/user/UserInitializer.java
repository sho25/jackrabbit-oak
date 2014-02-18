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
name|jcr
operator|.
name|RepositoryException
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
name|base
operator|.
name|Strings
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
name|SystemRoot
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
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
name|NodeBuilder
name|builder
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
name|NodeState
name|base
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|MemoryNodeStore
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
operator|new
name|SystemRoot
argument_list|(
name|store
argument_list|,
name|commitHook
argument_list|,
name|workspaceName
argument_list|,
name|securityProvider
argument_list|,
name|indexProvider
argument_list|)
decl_stmt|;
name|UserConfiguration
name|userConfiguration
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
name|NT_REP_AUTHORIZABLE
block|}
argument_list|)
expr_stmt|;
block|}
name|ConfigurationParameters
name|params
init|=
name|userConfiguration
operator|.
name|getParameters
argument_list|()
decl_stmt|;
name|String
name|adminId
init|=
name|params
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
name|boolean
name|omitPw
init|=
name|params
operator|.
name|getConfigValue
argument_list|(
name|PARAM_OMIT_ADMIN_PW
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|userManager
operator|.
name|createUser
argument_list|(
name|adminId
argument_list|,
operator|(
name|omitPw
operator|)
condition|?
literal|null
else|:
name|adminId
argument_list|)
expr_stmt|;
block|}
name|String
name|anonymousId
init|=
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|params
operator|.
name|getConfigValue
argument_list|(
name|PARAM_ANONYMOUS_ID
argument_list|,
name|DEFAULT_ANONYMOUS_ID
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|anonymousId
operator|!=
literal|null
operator|&&
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
end_class

end_unit

