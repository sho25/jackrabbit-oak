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
name|authentication
operator|.
name|external
operator|.
name|impl
operator|.
name|principal
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
name|security
operator|.
name|acl
operator|.
name|Group
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|annotation
operator|.
name|Nullable
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
name|Function
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
name|Predicates
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
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
name|api
operator|.
name|security
operator|.
name|principal
operator|.
name|PrincipalManager
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
name|external
operator|.
name|SyncHandler
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
name|external
operator|.
name|impl
operator|.
name|DefaultSyncConfigImpl
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
name|external
operator|.
name|impl
operator|.
name|ExternalIdentityConstants
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
name|external
operator|.
name|impl
operator|.
name|ExternalLoginModuleFactory
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
name|external
operator|.
name|impl
operator|.
name|SyncHandlerMapping
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
name|EmptyPrincipalProvider
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
name|principal
operator|.
name|PrincipalManagerImpl
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

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|BundleContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|ServiceReference
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|util
operator|.
name|tracker
operator|.
name|ServiceTracker
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

begin_comment
comment|/**  * Implementation of the {@code PrincipalConfiguration} interface that provides  * principal management for {@link Group principals} associated with  * {@link org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalIdentity external identities}  * managed outside of the scope of the repository by an  * {@link org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalIdentityProvider}.  *  * @since Oak 1.5.3  * @see<a href="https://issues.apache.org/jira/browse/OAK-4101">OAK-4101</a>  */
end_comment

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
literal|"Apache Jackrabbit Oak External PrincipalConfiguration"
argument_list|,
name|immediate
operator|=
literal|true
argument_list|)
annotation|@
name|Service
argument_list|(
block|{
name|PrincipalConfiguration
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
name|ExternalIdentityConstants
operator|.
name|PARAM_PROTECT_EXTERNAL_IDS
argument_list|,
name|label
operator|=
literal|"External Identity Protection"
argument_list|,
name|description
operator|=
literal|"If disabled rep:externalId properties won't be properly protected (backwards compatible behavior). NOTE: for security reasons it is strongly recommend to keep the protection enabled!"
argument_list|,
name|boolValue
operator|=
name|ExternalIdentityConstants
operator|.
name|DEFAULT_PROTECT_EXTERNAL_IDS
argument_list|)
block|}
argument_list|)
specifier|public
class|class
name|ExternalPrincipalConfiguration
extends|extends
name|ConfigurationBase
implements|implements
name|PrincipalConfiguration
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
name|ExternalPrincipalConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|SyncConfigTracker
name|syncConfigTracker
decl_stmt|;
specifier|private
name|SyncHandlerMappingTracker
name|syncHandlerMappingTracker
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|public
name|ExternalPrincipalConfiguration
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|ExternalPrincipalConfiguration
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
comment|//---------------------------------------------< PrincipalConfiguration>---
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|PrincipalManager
name|getPrincipalManager
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
name|PrincipalManagerImpl
argument_list|(
name|getPrincipalProvider
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|PrincipalProvider
name|getPrincipalProvider
parameter_list|(
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
if|if
condition|(
name|dynamicMembershipEnabled
argument_list|()
condition|)
block|{
name|UserConfiguration
name|uc
init|=
name|getSecurityProvider
argument_list|()
operator|.
name|getConfiguration
argument_list|(
name|UserConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
operator|new
name|ExternalGroupPrincipalProvider
argument_list|(
name|root
argument_list|,
name|uc
argument_list|,
name|namePathMapper
argument_list|,
name|syncConfigTracker
operator|.
name|getAutoMembership
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|EmptyPrincipalProvider
operator|.
name|INSTANCE
return|;
block|}
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
name|ExternalIdentityRepositoryInitializer
argument_list|(
name|protectedExternalIds
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
name|ExternalIdentityValidatorProvider
argument_list|(
name|principals
argument_list|,
name|protectedExternalIds
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
name|ImmutableList
operator|.
expr|<
name|ProtectedItemImporter
operator|>
name|of
argument_list|(
operator|new
name|ExternalIdentityImporter
argument_list|()
argument_list|)
return|;
block|}
comment|//----------------------------------------------------< SCR integration>---
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
annotation|@
name|Activate
specifier|private
name|void
name|activate
parameter_list|(
name|BundleContext
name|bundleContext
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
name|syncHandlerMappingTracker
operator|=
operator|new
name|SyncHandlerMappingTracker
argument_list|(
name|bundleContext
argument_list|)
expr_stmt|;
name|syncHandlerMappingTracker
operator|.
name|open
argument_list|()
expr_stmt|;
name|syncConfigTracker
operator|=
operator|new
name|SyncConfigTracker
argument_list|(
name|bundleContext
argument_list|,
name|syncHandlerMappingTracker
argument_list|)
expr_stmt|;
name|syncConfigTracker
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
annotation|@
name|Deactivate
specifier|private
name|void
name|deactivate
parameter_list|()
block|{
if|if
condition|(
name|syncConfigTracker
operator|!=
literal|null
condition|)
block|{
name|syncConfigTracker
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|syncHandlerMappingTracker
operator|!=
literal|null
condition|)
block|{
name|syncHandlerMappingTracker
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|boolean
name|dynamicMembershipEnabled
parameter_list|()
block|{
return|return
name|syncConfigTracker
operator|!=
literal|null
operator|&&
name|syncConfigTracker
operator|.
name|isEnabled
return|;
block|}
specifier|private
name|boolean
name|protectedExternalIds
parameter_list|()
block|{
return|return
name|getParameters
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|ExternalIdentityConstants
operator|.
name|PARAM_PROTECT_EXTERNAL_IDS
argument_list|,
name|ExternalIdentityConstants
operator|.
name|DEFAULT_PROTECT_EXTERNAL_IDS
argument_list|)
return|;
block|}
comment|/**      * {@code ServiceTracker} to detect any {@link SyncHandler} that has      * dynamic membership enabled.      */
specifier|private
specifier|static
specifier|final
class|class
name|SyncConfigTracker
extends|extends
name|ServiceTracker
block|{
specifier|private
specifier|final
name|SyncHandlerMappingTracker
name|mappingTracker
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|ServiceReference
argument_list|>
name|enablingRefs
init|=
operator|new
name|HashSet
argument_list|<
name|ServiceReference
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|isEnabled
init|=
literal|false
decl_stmt|;
specifier|public
name|SyncConfigTracker
parameter_list|(
annotation|@
name|Nonnull
name|BundleContext
name|context
parameter_list|,
annotation|@
name|Nonnull
name|SyncHandlerMappingTracker
name|mappingTracker
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|SyncHandler
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|mappingTracker
operator|=
name|mappingTracker
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|addingService
parameter_list|(
name|ServiceReference
name|reference
parameter_list|)
block|{
if|if
condition|(
name|hasDynamicMembership
argument_list|(
name|reference
argument_list|)
condition|)
block|{
name|enablingRefs
operator|.
name|add
argument_list|(
name|reference
argument_list|)
expr_stmt|;
name|isEnabled
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|super
operator|.
name|addingService
argument_list|(
name|reference
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|modifiedService
parameter_list|(
name|ServiceReference
name|reference
parameter_list|,
name|Object
name|service
parameter_list|)
block|{
if|if
condition|(
name|hasDynamicMembership
argument_list|(
name|reference
argument_list|)
condition|)
block|{
name|enablingRefs
operator|.
name|add
argument_list|(
name|reference
argument_list|)
expr_stmt|;
name|isEnabled
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|enablingRefs
operator|.
name|remove
argument_list|(
name|reference
argument_list|)
expr_stmt|;
name|isEnabled
operator|=
operator|!
name|enablingRefs
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|modifiedService
argument_list|(
name|reference
argument_list|,
name|service
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removedService
parameter_list|(
name|ServiceReference
name|reference
parameter_list|,
name|Object
name|service
parameter_list|)
block|{
name|enablingRefs
operator|.
name|remove
argument_list|(
name|reference
argument_list|)
expr_stmt|;
name|isEnabled
operator|=
operator|!
name|enablingRefs
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|super
operator|.
name|removedService
argument_list|(
name|reference
argument_list|,
name|service
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|boolean
name|hasDynamicMembership
parameter_list|(
name|ServiceReference
name|reference
parameter_list|)
block|{
return|return
name|PropertiesUtil
operator|.
name|toBoolean
argument_list|(
name|reference
operator|.
name|getProperty
argument_list|(
name|DefaultSyncConfigImpl
operator|.
name|PARAM_USER_DYNAMIC_MEMBERSHIP
argument_list|)
argument_list|,
name|DefaultSyncConfigImpl
operator|.
name|PARAM_USER_DYNAMIC_MEMBERSHIP_DEFAULT
argument_list|)
return|;
block|}
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|getAutoMembership
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|autoMembership
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ServiceReference
name|ref
range|:
name|enablingRefs
control|)
block|{
name|String
name|syncHandlerName
init|=
name|PropertiesUtil
operator|.
name|toString
argument_list|(
name|ref
operator|.
name|getProperty
argument_list|(
name|DefaultSyncConfigImpl
operator|.
name|PARAM_NAME
argument_list|)
argument_list|,
name|DefaultSyncConfigImpl
operator|.
name|PARAM_NAME_DEFAULT
argument_list|)
decl_stmt|;
name|String
index|[]
name|membership
init|=
name|PropertiesUtil
operator|.
name|toStringArray
argument_list|(
name|ref
operator|.
name|getProperty
argument_list|(
name|DefaultSyncConfigImpl
operator|.
name|PARAM_USER_AUTO_MEMBERSHIP
argument_list|)
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|idpName
range|:
name|mappingTracker
operator|.
name|getIdpNames
argument_list|(
name|syncHandlerName
argument_list|)
control|)
block|{
name|String
index|[]
name|previous
init|=
name|autoMembership
operator|.
name|put
argument_list|(
name|idpName
argument_list|,
name|membership
argument_list|)
decl_stmt|;
if|if
condition|(
name|previous
operator|!=
literal|null
condition|)
block|{
name|String
name|msg
init|=
operator|(
name|Arrays
operator|.
name|equals
argument_list|(
name|previous
argument_list|,
name|membership
argument_list|)
operator|)
condition|?
literal|"Duplicate"
else|:
literal|"Colliding"
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
name|msg
operator|+
literal|" auto-membership configuration for IDP '{}'; replacing previous values {} by {} defined by SyncHandler '{}'"
argument_list|,
name|idpName
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
name|previous
argument_list|)
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
name|membership
argument_list|)
argument_list|,
name|syncHandlerName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|autoMembership
return|;
block|}
block|}
comment|/**      * {@code ServiceTracker} to detect any {@link SyncHandler} that has      * dynamic membership enabled.      */
specifier|private
specifier|static
specifier|final
class|class
name|SyncHandlerMappingTracker
extends|extends
name|ServiceTracker
block|{
specifier|private
name|Map
argument_list|<
name|ServiceReference
argument_list|,
name|String
index|[]
argument_list|>
name|referenceMap
init|=
operator|new
name|HashMap
argument_list|<
name|ServiceReference
argument_list|,
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|SyncHandlerMappingTracker
parameter_list|(
annotation|@
name|Nonnull
name|BundleContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|SyncHandlerMapping
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|addingService
parameter_list|(
name|ServiceReference
name|reference
parameter_list|)
block|{
name|addMapping
argument_list|(
name|reference
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|addingService
argument_list|(
name|reference
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|modifiedService
parameter_list|(
name|ServiceReference
name|reference
parameter_list|,
name|Object
name|service
parameter_list|)
block|{
name|addMapping
argument_list|(
name|reference
argument_list|)
expr_stmt|;
name|super
operator|.
name|modifiedService
argument_list|(
name|reference
argument_list|,
name|service
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removedService
parameter_list|(
name|ServiceReference
name|reference
parameter_list|,
name|Object
name|service
parameter_list|)
block|{
name|referenceMap
operator|.
name|remove
argument_list|(
name|reference
argument_list|)
expr_stmt|;
name|super
operator|.
name|removedService
argument_list|(
name|reference
argument_list|,
name|service
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addMapping
parameter_list|(
name|ServiceReference
name|reference
parameter_list|)
block|{
name|String
name|idpName
init|=
name|PropertiesUtil
operator|.
name|toString
argument_list|(
name|reference
operator|.
name|getProperty
argument_list|(
name|ExternalLoginModuleFactory
operator|.
name|PARAM_IDP_NAME
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|syncHandlerName
init|=
name|PropertiesUtil
operator|.
name|toString
argument_list|(
name|reference
operator|.
name|getProperty
argument_list|(
name|ExternalLoginModuleFactory
operator|.
name|PARAM_SYNC_HANDLER_NAME
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|idpName
operator|!=
literal|null
operator|&&
name|syncHandlerName
operator|!=
literal|null
condition|)
block|{
name|referenceMap
operator|.
name|put
argument_list|(
name|reference
argument_list|,
operator|new
name|String
index|[]
block|{
name|syncHandlerName
block|,
name|idpName
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Ignoring SyncHandlerMapping with incomplete mapping of IDP '{}' and SyncHandler '{}'"
argument_list|,
name|idpName
argument_list|,
name|syncHandlerName
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Iterable
argument_list|<
name|String
argument_list|>
name|getIdpNames
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|syncHandlerName
parameter_list|)
block|{
return|return
name|Iterables
operator|.
name|filter
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|referenceMap
operator|.
name|values
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|String
index|[]
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
annotation|@
name|Nullable
name|String
index|[]
name|input
parameter_list|)
block|{
if|if
condition|(
name|input
operator|!=
literal|null
operator|&&
name|input
operator|.
name|length
operator|==
literal|2
condition|)
block|{
if|if
condition|(
name|syncHandlerName
operator|.
name|equals
argument_list|(
name|input
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
return|return
name|input
index|[
literal|1
index|]
return|;
block|}
comment|// else: different sync-handler
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unexpected value of reference map. Expected String[] with length = 2"
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
argument_list|,
name|Predicates
operator|.
name|notNull
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

