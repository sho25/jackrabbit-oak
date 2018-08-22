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
name|api
operator|.
name|blob
operator|.
name|BlobAccessProvider
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
name|value
operator|.
name|jcr
operator|.
name|PartialValueFactory
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
name|user
operator|.
name|autosave
operator|.
name|AutoSaveEnabledManager
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
name|ThreeWayConflictHandler
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
name|principal
operator|.
name|PrincipalProvider
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
name|UserAuthenticationFactory
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
name|security
operator|.
name|user
operator|.
name|util
operator|.
name|PasswordUtil
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
name|WhiteboardUtils
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
name|ImportBehavior
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
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
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
name|osgi
operator|.
name|service
operator|.
name|component
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
name|osgi
operator|.
name|service
operator|.
name|component
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
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|annotations
operator|.
name|ReferenceCardinality
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|metatype
operator|.
name|annotations
operator|.
name|AttributeDefinition
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|metatype
operator|.
name|annotations
operator|.
name|Designate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|metatype
operator|.
name|annotations
operator|.
name|ObjectClassDefinition
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|metatype
operator|.
name|annotations
operator|.
name|Option
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
name|plugins
operator|.
name|value
operator|.
name|jcr
operator|.
name|PartialValueFactory
operator|.
name|DEFAULT_BLOB_ACCESS_PROVIDER
import|;
end_import

begin_comment
comment|/**  * Default implementation of the {@link UserConfiguration}.  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|service
operator|=
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
annotation|@
name|Designate
argument_list|(
name|ocd
operator|=
name|UserConfigurationImpl
operator|.
name|Configuration
operator|.
name|class
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
annotation|@
name|ObjectClassDefinition
argument_list|(
name|name
operator|=
literal|"Apache Jackrabbit Oak UserConfiguration"
argument_list|)
annotation_defn|@interface
name|Configuration
block|{
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"User Path"
argument_list|,
name|description
operator|=
literal|"Path underneath which user nodes are being created."
argument_list|)
name|String
name|usersPath
parameter_list|()
default|default
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Group Path"
argument_list|,
name|description
operator|=
literal|"Path underneath which group nodes are being created."
argument_list|)
name|String
name|groupsPath
parameter_list|()
default|default
name|UserConstants
operator|.
name|DEFAULT_GROUP_PATH
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"System User Relative Path"
argument_list|,
name|description
operator|=
literal|"Path relative to the user root path underneath which system user nodes are being "
operator|+
literal|"created. The default value is 'system'."
argument_list|)
name|String
name|systemRelativePath
parameter_list|()
default|default
name|UserConstants
operator|.
name|DEFAULT_SYSTEM_RELATIVE_PATH
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Default Depth"
argument_list|,
name|description
operator|=
literal|"Number of levels that are used by default to store authorizable nodes"
argument_list|)
name|int
name|defaultDepth
parameter_list|()
default|default
name|UserConstants
operator|.
name|DEFAULT_DEPTH
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Import Behavior"
argument_list|,
name|description
operator|=
literal|"Behavior for user/group related items upon XML import."
argument_list|,
name|options
operator|=
block|{
annotation|@
name|Option
argument_list|(
name|label
operator|=
name|ImportBehavior
operator|.
name|NAME_ABORT
argument_list|,
name|value
operator|=
name|ImportBehavior
operator|.
name|NAME_ABORT
argument_list|)
block|,
annotation|@
name|Option
argument_list|(
name|label
operator|=
name|ImportBehavior
operator|.
name|NAME_BESTEFFORT
argument_list|,
name|value
operator|=
name|ImportBehavior
operator|.
name|NAME_BESTEFFORT
argument_list|)
block|,
annotation|@
name|Option
argument_list|(
name|label
operator|=
name|ImportBehavior
operator|.
name|NAME_IGNORE
argument_list|,
name|value
operator|=
name|ImportBehavior
operator|.
name|NAME_IGNORE
argument_list|)
block|}
argument_list|)
name|String
name|importBehavior
parameter_list|()
default|default
name|ImportBehavior
operator|.
name|NAME_IGNORE
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Hash Algorithm"
argument_list|,
name|description
operator|=
literal|"Name of the algorithm used to generate the password hash."
argument_list|)
name|String
name|passwordHashAlgorithm
parameter_list|()
default|default
name|PasswordUtil
operator|.
name|DEFAULT_ALGORITHM
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Hash Iterations"
argument_list|,
name|description
operator|=
literal|"Number of iterations to generate the password hash."
argument_list|)
name|int
name|passwordHashIterations
parameter_list|()
default|default
name|PasswordUtil
operator|.
name|DEFAULT_ITERATIONS
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Hash Salt Size"
argument_list|,
name|description
operator|=
literal|"Salt size to generate the password hash."
argument_list|)
name|int
name|passwordSaltSize
parameter_list|()
default|default
name|PasswordUtil
operator|.
name|DEFAULT_SALT_SIZE
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Omit Admin Password"
argument_list|,
name|description
operator|=
literal|"Boolean flag to prevent the administrator account to be created with a password "
operator|+
literal|"upon repository initialization. Please note that changing this option after the initial "
operator|+
literal|"repository setup will have no effect."
argument_list|)
name|boolean
name|omitAdminPw
parameter_list|()
default|default
literal|false
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Autosave Support"
argument_list|,
name|description
operator|=
literal|"Configuration option to enable autosave behavior. Note: this config option is "
operator|+
literal|"present for backwards compatibility with Jackrabbit 2.x and should only be used for "
operator|+
literal|"broken code that doesn't properly verify the autosave behavior (see Jackrabbit API). "
operator|+
literal|"If this option is turned on autosave will be enabled by default; otherwise autosave is "
operator|+
literal|"not supported."
argument_list|)
name|boolean
name|supportAutoSave
parameter_list|()
default|default
literal|false
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Maximum Password Age"
argument_list|,
name|description
operator|=
literal|"Maximum age in days a password may have. Values greater 0 will implicitly enable "
operator|+
literal|"password expiry. A value of 0 indicates unlimited password age."
argument_list|)
name|int
name|passwordMaxAge
parameter_list|()
default|default
name|UserConstants
operator|.
name|DEFAULT_PASSWORD_MAX_AGE
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Change Password On First Login"
argument_list|,
name|description
operator|=
literal|"When enabled, forces users to change their password upon first login."
argument_list|)
name|boolean
name|initialPasswordChange
parameter_list|()
default|default
name|UserConstants
operator|.
name|DEFAULT_PASSWORD_INITIAL_CHANGE
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Maximum Password History Size"
argument_list|,
name|description
operator|=
literal|"Maximum number of passwords recorded for a user after changing her password (NOTE: "
operator|+
literal|"upper limit is 1000). When changing the password the new password must not be present in the "
operator|+
literal|"password history. A value of 0 indicates no password history is recorded."
argument_list|)
name|int
name|passwordHistorySize
parameter_list|()
default|default
name|UserConstants
operator|.
name|PASSWORD_HISTORY_DISABLED_SIZE
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Principal Cache Expiration"
argument_list|,
name|description
operator|=
literal|"Optional configuration defining the number of milliseconds "
operator|+
literal|"until the principal cache expires (NOTE: currently only respected for principal resolution "
operator|+
literal|"with the internal system session such as used for login). If not set or equal/lower than zero "
operator|+
literal|"no caches are created/evaluated."
argument_list|)
name|long
name|cacheExpiration
parameter_list|()
default|default
name|UserPrincipalProvider
operator|.
name|EXPIRATION_NO_CACHE
function_decl|;
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"RFC7613 Username Comparison Profile"
argument_list|,
name|description
operator|=
literal|"Enable the UsercaseMappedProfile defined in RFC7613 for username comparison."
argument_list|)
name|boolean
name|enableRFC7613UsercaseMappedProfile
parameter_list|()
default|default
literal|false
function_decl|;
block|}
specifier|private
specifier|static
specifier|final
name|UserAuthenticationFactory
name|DEFAULT_AUTH_FACTORY
init|=
operator|new
name|UserAuthenticationFactoryImpl
argument_list|()
decl_stmt|;
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
specifier|public
specifier|static
name|UserAuthenticationFactory
name|getDefaultAuthenticationFactory
parameter_list|()
block|{
return|return
name|DEFAULT_AUTH_FACTORY
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
annotation|@
name|Activate
comment|// reference to @Configuration class needed for correct DS xml generation
specifier|private
name|void
name|activate
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
parameter_list|)
block|{
name|setParameters
argument_list|(
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|properties
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Reference
argument_list|(
name|cardinality
operator|=
name|ReferenceCardinality
operator|.
name|OPTIONAL
argument_list|)
specifier|private
name|BlobAccessProvider
name|blobAccessProvider
decl_stmt|;
comment|//----------------------------------------------< SecurityConfiguration>---
annotation|@
name|NotNull
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
name|NotNull
annotation|@
name|Override
specifier|public
name|ConfigurationParameters
name|getParameters
parameter_list|()
block|{
name|ConfigurationParameters
name|params
init|=
name|super
operator|.
name|getParameters
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|params
operator|.
name|containsKey
argument_list|(
name|UserConstants
operator|.
name|PARAM_USER_AUTHENTICATION_FACTORY
argument_list|)
condition|)
block|{
return|return
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|params
argument_list|,
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|UserConstants
operator|.
name|PARAM_USER_AUTHENTICATION_FACTORY
argument_list|,
name|DEFAULT_AUTH_FACTORY
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|params
return|;
block|}
block|}
annotation|@
name|NotNull
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
name|NotNull
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
name|NotNull
name|String
name|workspaceName
parameter_list|,
annotation|@
name|NotNull
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|,
annotation|@
name|NotNull
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
name|UserValidatorProvider
argument_list|(
name|getParameters
argument_list|()
argument_list|,
name|getRootProvider
argument_list|()
argument_list|,
name|getTreeProvider
argument_list|()
argument_list|)
argument_list|,
operator|new
name|CacheValidatorProvider
argument_list|(
name|principals
argument_list|,
name|getTreeProvider
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|ThreeWayConflictHandler
argument_list|>
name|getConflictHandlers
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|RepMembersConflictHandler
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|NotNull
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
name|NotNull
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
name|NotNull
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
name|PartialValueFactory
name|vf
init|=
operator|new
name|PartialValueFactory
argument_list|(
name|namePathMapper
argument_list|,
name|getBlobAccessProvider
argument_list|()
argument_list|)
decl_stmt|;
name|UserManager
name|umgr
init|=
operator|new
name|UserManagerImpl
argument_list|(
name|root
argument_list|,
name|vf
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|getParameters
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_SUPPORT_AUTOSAVE
argument_list|,
literal|false
argument_list|)
condition|)
block|{
return|return
operator|new
name|AutoSaveEnabledManager
argument_list|(
name|umgr
argument_list|,
name|root
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|umgr
return|;
block|}
block|}
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|PrincipalProvider
name|getUserPrincipalProvider
parameter_list|(
annotation|@
name|NotNull
name|Root
name|root
parameter_list|,
annotation|@
name|NotNull
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
return|return
operator|new
name|UserPrincipalProvider
argument_list|(
name|root
argument_list|,
name|this
argument_list|,
name|namePathMapper
argument_list|)
return|;
block|}
comment|//-----------------------------------------------------------< internal>---
specifier|private
name|BlobAccessProvider
name|getBlobAccessProvider
parameter_list|()
block|{
name|BlobAccessProvider
name|provider
init|=
name|blobAccessProvider
decl_stmt|;
if|if
condition|(
name|provider
operator|==
literal|null
condition|)
block|{
name|SecurityProvider
name|securityProvider
init|=
name|getSecurityProvider
argument_list|()
decl_stmt|;
if|if
condition|(
name|securityProvider
operator|instanceof
name|WhiteboardAware
condition|)
block|{
name|Whiteboard
name|wb
init|=
operator|(
operator|(
name|WhiteboardAware
operator|)
name|securityProvider
operator|)
operator|.
name|getWhiteboard
argument_list|()
decl_stmt|;
if|if
condition|(
name|wb
operator|!=
literal|null
condition|)
block|{
name|provider
operator|=
name|WhiteboardUtils
operator|.
name|getService
argument_list|(
name|wb
argument_list|,
name|BlobAccessProvider
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|provider
operator|==
literal|null
condition|)
block|{
name|provider
operator|=
name|DEFAULT_BLOB_ACCESS_PROVIDER
expr_stmt|;
name|blobAccessProvider
operator|=
name|provider
expr_stmt|;
block|}
return|return
name|provider
return|;
block|}
block|}
end_class

end_unit

