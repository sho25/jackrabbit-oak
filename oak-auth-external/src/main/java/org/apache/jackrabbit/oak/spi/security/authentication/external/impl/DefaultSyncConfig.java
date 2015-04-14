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
name|Property
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

begin_comment
comment|/**  * {@code DefaultSyncConfig} defines how users and groups from an external source are synced into the repository using  * the {@link DefaultSyncHandler}.  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|label
operator|=
literal|"Apache Jackrabbit Oak Default Sync Handler"
argument_list|,
name|name
operator|=
literal|"org.apache.jackrabbit.oak.spi.security.authentication.external.impl.DefaultSyncHandler"
argument_list|,
name|configurationFactory
operator|=
literal|true
argument_list|,
name|metatype
operator|=
literal|true
argument_list|,
name|ds
operator|=
literal|false
argument_list|)
specifier|public
class|class
name|DefaultSyncConfig
block|{
comment|/**      * @see #getName()      */
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_NAME_DEFAULT
init|=
literal|"default"
decl_stmt|;
comment|/**      * @see #getName()      */
annotation|@
name|Property
argument_list|(
name|label
operator|=
literal|"Sync Handler Name"
argument_list|,
name|description
operator|=
literal|"Name of this sync configuration. This is used to reference this handler by the login modules."
argument_list|,
name|value
operator|=
name|PARAM_NAME_DEFAULT
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_NAME
init|=
literal|"handler.name"
decl_stmt|;
comment|/**      * @see DefaultSyncConfig.User#getExpirationTime()      */
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_USER_EXPIRATION_TIME_DEFAULT
init|=
literal|"1h"
decl_stmt|;
comment|/**      * @see DefaultSyncConfig.User#getExpirationTime()      */
annotation|@
name|Property
argument_list|(
name|label
operator|=
literal|"User Expiration Time"
argument_list|,
name|description
operator|=
literal|"Duration until a synced user gets expired (eg. '1h 30m' or '1d')."
argument_list|,
name|value
operator|=
name|PARAM_USER_EXPIRATION_TIME_DEFAULT
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_USER_EXPIRATION_TIME
init|=
literal|"user.expirationTime"
decl_stmt|;
comment|/**      * @see DefaultSyncConfig.User#getAutoMembership()      */
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|PARAM_USER_AUTO_MEMBERSHIP_DEFAULT
init|=
block|{}
decl_stmt|;
comment|/**      * @see DefaultSyncConfig.User#getAutoMembership()      */
annotation|@
name|Property
argument_list|(
name|label
operator|=
literal|"User auto membership"
argument_list|,
name|description
operator|=
literal|"List of groups that a synced user is added to automatically"
argument_list|,
name|value
operator|=
block|{}
argument_list|,
name|cardinality
operator|=
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_USER_AUTO_MEMBERSHIP
init|=
literal|"user.autoMembership"
decl_stmt|;
comment|/**      * @see DefaultSyncConfig.User#getPropertyMapping()      */
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|PARAM_USER_PROPERTY_MAPPING_DEFAULT
init|=
block|{
literal|"rep:fullname=cn"
block|}
decl_stmt|;
comment|/**      * @see DefaultSyncConfig.User#getPropertyMapping()      */
annotation|@
name|Property
argument_list|(
name|label
operator|=
literal|"User property mapping"
argument_list|,
name|description
operator|=
literal|"List mapping definition of local properties from external ones. eg: 'profile/email=mail'."
operator|+
literal|"Use double quotes for fixed values. eg: 'profile/nt:primaryType=\"nt:unstructured\""
argument_list|,
name|value
operator|=
block|{
literal|"rep:fullname=cn"
block|}
argument_list|,
name|cardinality
operator|=
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_USER_PROPERTY_MAPPING
init|=
literal|"user.propertyMapping"
decl_stmt|;
comment|/**      * @see DefaultSyncConfig.User#getPathPrefix()      */
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_USER_PATH_PREFIX_DEFAULT
init|=
literal|""
decl_stmt|;
comment|/**      * @see DefaultSyncConfig.User#getPathPrefix()      */
annotation|@
name|Property
argument_list|(
name|label
operator|=
literal|"User Path Prefix"
argument_list|,
name|description
operator|=
literal|"The path prefix used when creating new users."
argument_list|,
name|value
operator|=
name|PARAM_USER_PATH_PREFIX_DEFAULT
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_USER_PATH_PREFIX
init|=
literal|"user.pathPrefix"
decl_stmt|;
comment|/**      * @see DefaultSyncConfig.User#getMembershipExpirationTime()      */
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_USER_MEMBERSHIP_EXPIRATION_TIME_DEFAULT
init|=
literal|"1h"
decl_stmt|;
comment|/**      * @see DefaultSyncConfig.User#getMembershipExpirationTime()      */
annotation|@
name|Property
argument_list|(
name|label
operator|=
literal|"User Membership Expiration"
argument_list|,
name|description
operator|=
literal|"Time after which membership expires (eg. '1h 30m' or '1d')."
argument_list|,
name|value
operator|=
name|PARAM_USER_MEMBERSHIP_EXPIRATION_TIME_DEFAULT
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_USER_MEMBERSHIP_EXPIRATION_TIME
init|=
literal|"user.membershipExpTime"
decl_stmt|;
comment|/**      * @see User#getMembershipNestingDepth()      */
specifier|public
specifier|static
specifier|final
name|int
name|PARAM_USER_MEMBERSHIP_NESTING_DEPTH_DEFAULT
init|=
literal|0
decl_stmt|;
comment|/**      * @see User#getMembershipNestingDepth()      */
annotation|@
name|Property
argument_list|(
name|label
operator|=
literal|"User membership nesting depth"
argument_list|,
name|description
operator|=
literal|"Returns the maximum depth of group nesting when membership relations are synced. "
operator|+
literal|"A value of 0 effectively disables group membership lookup. A value of 1 only adds the direct "
operator|+
literal|"groups of a user. This value has no effect when syncing individual groups only when syncing a "
operator|+
literal|"users membership ancestry."
argument_list|,
name|intValue
operator|=
name|PARAM_USER_MEMBERSHIP_NESTING_DEPTH_DEFAULT
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_USER_MEMBERSHIP_NESTING_DEPTH
init|=
literal|"user.membershipNestingDepth"
decl_stmt|;
comment|/**      * @see DefaultSyncConfig.Group#getExpirationTime()      */
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_GROUP_EXPIRATION_TIME_DEFAULT
init|=
literal|"1d"
decl_stmt|;
comment|/**      * @see DefaultSyncConfig.Group#getExpirationTime()      */
annotation|@
name|Property
argument_list|(
name|label
operator|=
literal|"Group Expiration Time"
argument_list|,
name|description
operator|=
literal|"Duration until a synced group expires (eg. '1h 30m' or '1d')."
argument_list|,
name|value
operator|=
name|PARAM_GROUP_EXPIRATION_TIME_DEFAULT
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_GROUP_EXPIRATION_TIME
init|=
literal|"group.expirationTime"
decl_stmt|;
comment|/**      * @see DefaultSyncConfig.Group#getAutoMembership()      */
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|PARAM_GROUP_AUTO_MEMBERSHIP_DEFAULT
init|=
block|{}
decl_stmt|;
comment|/**      * @see DefaultSyncConfig.Group#getAutoMembership()      */
annotation|@
name|Property
argument_list|(
name|label
operator|=
literal|"Group auto membership"
argument_list|,
name|description
operator|=
literal|"List of groups that a synced group is added to automatically"
argument_list|,
name|value
operator|=
block|{}
argument_list|,
name|cardinality
operator|=
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_GROUP_AUTO_MEMBERSHIP
init|=
literal|"group.autoMembership"
decl_stmt|;
comment|/**      * @see DefaultSyncConfig.Group#getPropertyMapping()      */
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|PARAM_GROUP_PROPERTY_MAPPING_DEFAULT
init|=
block|{}
decl_stmt|;
comment|/**      * @see DefaultSyncConfig.Group#getPropertyMapping()      */
annotation|@
name|Property
argument_list|(
name|label
operator|=
literal|"Group property mapping"
argument_list|,
name|description
operator|=
literal|"List mapping definition of local properties from external ones."
argument_list|,
name|value
operator|=
block|{}
argument_list|,
name|cardinality
operator|=
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_GROUP_PROPERTY_MAPPING
init|=
literal|"group.propertyMapping"
decl_stmt|;
comment|/**      * @see DefaultSyncConfig.Group#getPathPrefix()      */
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_GROUP_PATH_PREFIX_DEFAULT
init|=
literal|""
decl_stmt|;
comment|/**      * @see DefaultSyncConfig.Group#getPathPrefix()      */
annotation|@
name|Property
argument_list|(
name|label
operator|=
literal|"Group Path Prefix"
argument_list|,
name|description
operator|=
literal|"The path prefix used when creating new groups."
argument_list|,
name|value
operator|=
name|PARAM_GROUP_PATH_PREFIX_DEFAULT
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_GROUP_PATH_PREFIX
init|=
literal|"group.pathPrefix"
decl_stmt|;
comment|/**      * Base config class for users and groups      */
specifier|public
specifier|abstract
specifier|static
class|class
name|Authorizable
block|{
specifier|private
name|long
name|expirationTime
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|autoMembership
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|propertyMapping
decl_stmt|;
specifier|private
name|String
name|pathPrefix
decl_stmt|;
comment|/**          * Returns the duration in milliseconds until a synced authorizable gets expired. An expired authorizable will          * be re-synced.          * @return the expiration time in milliseconds.          */
specifier|public
name|long
name|getExpirationTime
parameter_list|()
block|{
return|return
name|expirationTime
return|;
block|}
comment|/**          * Sets the expiration time.          * @param expirationTime time in milliseconds.          * @return {@code this}          * @see #getExpirationTime()          */
annotation|@
name|Nonnull
specifier|public
name|Authorizable
name|setExpirationTime
parameter_list|(
name|long
name|expirationTime
parameter_list|)
block|{
name|this
operator|.
name|expirationTime
operator|=
name|expirationTime
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Defines the set of group names that are automatically added to synced authorizable.          * @return set of group names.          */
annotation|@
name|Nonnull
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getAutoMembership
parameter_list|()
block|{
return|return
name|autoMembership
operator|==
literal|null
condition|?
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
else|:
name|autoMembership
return|;
block|}
comment|/**          * Sets the auto membership          * @param autoMembership the membership          * @return {@code this}          * @see #getAutoMembership()          */
annotation|@
name|Nonnull
specifier|public
name|Authorizable
name|setAutoMembership
parameter_list|(
name|String
modifier|...
name|autoMembership
parameter_list|)
block|{
name|this
operator|.
name|autoMembership
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|groupName
range|:
name|autoMembership
control|)
block|{
if|if
condition|(
operator|!
name|groupName
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|this
operator|.
name|autoMembership
operator|.
name|add
argument_list|(
name|groupName
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|this
return|;
block|}
comment|/**          * Defines the mapping of internal property names from external values. Only the external properties defined as          * keys of this map are synced with the mapped internal properties. note that the property names can be relative          * paths. the intermediate nodes will be created accordingly.          *          * Example:          *<xmp>          *     {          *         "rep:fullname": "cn",          *         "country", "c",          *         "profile/email": "mail",          *         "profile/givenName": "cn"          *     }          *</xmp>          *          * The implicit properties like userid, groupname, password must not be mapped.          *          * @return the property mapping where the keys are the local property names and the values the external ones.          */
annotation|@
name|Nonnull
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getPropertyMapping
parameter_list|()
block|{
return|return
name|propertyMapping
operator|==
literal|null
condition|?
name|Collections
operator|.
expr|<
name|String
operator|,
name|String
operator|>
name|emptyMap
argument_list|()
operator|:
name|propertyMapping
return|;
block|}
comment|/**          * Sets the property mapping.          * @param propertyMapping the mapping          * @return {@code this}          * @see #getPropertyMapping()          */
annotation|@
name|Nonnull
specifier|public
name|Authorizable
name|setPropertyMapping
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|propertyMapping
parameter_list|)
block|{
name|this
operator|.
name|propertyMapping
operator|=
name|propertyMapping
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Defines the authorizables intermediate path prefix that is used when creating new authorizables. This prefix          * is always prepended to the path provided by the {@link org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalIdentity}.          * @return the intermediate path prefix.          */
annotation|@
name|Nonnull
specifier|public
name|String
name|getPathPrefix
parameter_list|()
block|{
return|return
name|pathPrefix
operator|==
literal|null
condition|?
literal|""
else|:
name|pathPrefix
return|;
block|}
comment|/**          * Sets the path prefix.          * @param pathPrefix the path prefix.          * @return {@code this}          * @see #getPathPrefix()          */
annotation|@
name|Nonnull
specifier|public
name|Authorizable
name|setPathPrefix
parameter_list|(
name|String
name|pathPrefix
parameter_list|)
block|{
name|this
operator|.
name|pathPrefix
operator|=
name|pathPrefix
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
comment|/**      * User specific config.      */
specifier|public
specifier|static
class|class
name|User
extends|extends
name|Authorizable
block|{
specifier|private
name|long
name|membershipExpirationTime
decl_stmt|;
specifier|private
name|long
name|membershipNestingDepth
decl_stmt|;
comment|/**          * Returns the duration in milliseconds until the group membership of a user is expired. If the          * membership information is expired it is re-synced according to the maximum nesting depth.          * Note that the membership is the groups an authorizable is member of, not the list of members of a group.          * Also note, that the group membership expiration time can be higher than the user expiration time itself and          * that value has no effect when syncing individual groups only when syncing a users membership ancestry.          *          * @return the expiration time in milliseconds.          */
specifier|public
name|long
name|getMembershipExpirationTime
parameter_list|()
block|{
return|return
name|membershipExpirationTime
return|;
block|}
comment|/**          * Sets the membership expiration time          * @param membershipExpirationTime the time in milliseconds.          * @return {@code this}          * @see #getMembershipExpirationTime()          */
annotation|@
name|Nonnull
specifier|public
name|User
name|setMembershipExpirationTime
parameter_list|(
name|long
name|membershipExpirationTime
parameter_list|)
block|{
name|this
operator|.
name|membershipExpirationTime
operator|=
name|membershipExpirationTime
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Returns the maximum depth of group nesting when membership relations are synced. A value of 0 effectively          * disables group membership lookup. A value of 1 only adds the direct groups of a user. This value has no effect          * when syncing individual groups only when syncing a users membership ancestry.          * @return the group nesting depth          */
specifier|public
name|long
name|getMembershipNestingDepth
parameter_list|()
block|{
return|return
name|membershipNestingDepth
return|;
block|}
comment|/**          * Sets the group nesting depth.          * @param membershipNestingDepth the depth.          * @return {@code this}          * @see #getMembershipNestingDepth()          */
annotation|@
name|Nonnull
specifier|public
name|User
name|setMembershipNestingDepth
parameter_list|(
name|long
name|membershipNestingDepth
parameter_list|)
block|{
name|this
operator|.
name|membershipNestingDepth
operator|=
name|membershipNestingDepth
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
comment|/**      * Group specific config      */
specifier|public
specifier|static
class|class
name|Group
extends|extends
name|Authorizable
block|{      }
comment|/**      * Creates a new LDAP provider configuration based on the properties store in the given parameters.      * @param params the configuration parameters.      * @return the config      */
specifier|public
specifier|static
name|DefaultSyncConfig
name|of
parameter_list|(
name|ConfigurationParameters
name|params
parameter_list|)
block|{
name|DefaultSyncConfig
name|cfg
init|=
operator|new
name|DefaultSyncConfig
argument_list|()
operator|.
name|setName
argument_list|(
name|params
operator|.
name|getConfigValue
argument_list|(
name|PARAM_NAME
argument_list|,
name|PARAM_NAME_DEFAULT
argument_list|)
argument_list|)
decl_stmt|;
name|cfg
operator|.
name|user
argument_list|()
operator|.
name|setMembershipExpirationTime
argument_list|(
name|ConfigurationParameters
operator|.
name|Milliseconds
operator|.
name|of
argument_list|(
name|params
operator|.
name|getConfigValue
argument_list|(
name|PARAM_USER_MEMBERSHIP_EXPIRATION_TIME
argument_list|,
name|PARAM_USER_MEMBERSHIP_EXPIRATION_TIME_DEFAULT
argument_list|)
argument_list|)
operator|.
name|value
argument_list|)
operator|.
name|setMembershipNestingDepth
argument_list|(
name|params
operator|.
name|getConfigValue
argument_list|(
name|PARAM_USER_MEMBERSHIP_NESTING_DEPTH
argument_list|,
name|PARAM_USER_MEMBERSHIP_NESTING_DEPTH_DEFAULT
argument_list|)
argument_list|)
operator|.
name|setExpirationTime
argument_list|(
name|ConfigurationParameters
operator|.
name|Milliseconds
operator|.
name|of
argument_list|(
name|params
operator|.
name|getConfigValue
argument_list|(
name|PARAM_USER_EXPIRATION_TIME
argument_list|,
name|PARAM_USER_EXPIRATION_TIME_DEFAULT
argument_list|)
argument_list|)
operator|.
name|value
argument_list|)
operator|.
name|setPathPrefix
argument_list|(
name|params
operator|.
name|getConfigValue
argument_list|(
name|PARAM_USER_PATH_PREFIX
argument_list|,
name|PARAM_USER_PATH_PREFIX_DEFAULT
argument_list|)
argument_list|)
operator|.
name|setAutoMembership
argument_list|(
name|params
operator|.
name|getConfigValue
argument_list|(
name|PARAM_USER_AUTO_MEMBERSHIP
argument_list|,
name|PARAM_USER_AUTO_MEMBERSHIP_DEFAULT
argument_list|)
argument_list|)
operator|.
name|setPropertyMapping
argument_list|(
name|createMapping
argument_list|(
name|params
operator|.
name|getConfigValue
argument_list|(
name|PARAM_USER_PROPERTY_MAPPING
argument_list|,
name|PARAM_USER_PROPERTY_MAPPING_DEFAULT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|group
argument_list|()
operator|.
name|setExpirationTime
argument_list|(
name|ConfigurationParameters
operator|.
name|Milliseconds
operator|.
name|of
argument_list|(
name|params
operator|.
name|getConfigValue
argument_list|(
name|PARAM_GROUP_EXPIRATION_TIME
argument_list|,
name|PARAM_GROUP_EXPIRATION_TIME_DEFAULT
argument_list|)
argument_list|)
operator|.
name|value
argument_list|)
operator|.
name|setPathPrefix
argument_list|(
name|params
operator|.
name|getConfigValue
argument_list|(
name|PARAM_GROUP_PATH_PREFIX
argument_list|,
name|PARAM_GROUP_PATH_PREFIX_DEFAULT
argument_list|)
argument_list|)
operator|.
name|setAutoMembership
argument_list|(
name|params
operator|.
name|getConfigValue
argument_list|(
name|PARAM_GROUP_AUTO_MEMBERSHIP
argument_list|,
name|PARAM_GROUP_AUTO_MEMBERSHIP_DEFAULT
argument_list|)
argument_list|)
operator|.
name|setPropertyMapping
argument_list|(
name|createMapping
argument_list|(
name|params
operator|.
name|getConfigValue
argument_list|(
name|PARAM_GROUP_PROPERTY_MAPPING
argument_list|,
name|PARAM_GROUP_PROPERTY_MAPPING_DEFAULT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|cfg
return|;
block|}
comment|/**      * Creates a new property mapping map from a list of patterns.      * @param patterns the patterns      * @return the mapping map      */
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|createMapping
parameter_list|(
name|String
index|[]
name|patterns
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mapping
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|pattern
range|:
name|patterns
control|)
block|{
name|int
name|idx
init|=
name|pattern
operator|.
name|indexOf
argument_list|(
literal|'='
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
name|String
name|relPath
init|=
name|pattern
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|pattern
operator|.
name|substring
argument_list|(
name|idx
operator|+
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|mapping
operator|.
name|put
argument_list|(
name|relPath
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|mapping
return|;
block|}
specifier|private
name|String
name|name
init|=
name|PARAM_NAME_DEFAULT
decl_stmt|;
specifier|private
specifier|final
name|User
name|user
init|=
operator|new
name|User
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Group
name|group
init|=
operator|new
name|Group
argument_list|()
decl_stmt|;
comment|/**      * Configures the name of this configuration      * @return the name      */
annotation|@
name|Nonnull
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**      * Sets the name      * @param name the name      * @return {@code this}      * @see #getName()      */
specifier|public
name|DefaultSyncConfig
name|setName
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Returns the sync configuration for users.      * @return the user sync configuration.      */
annotation|@
name|Nonnull
specifier|public
name|User
name|user
parameter_list|()
block|{
return|return
name|user
return|;
block|}
comment|/**      * Returns the sync configuration for groups.      * @return the group sync configuration.      */
annotation|@
name|Nonnull
specifier|public
name|Group
name|group
parameter_list|()
block|{
return|return
name|group
return|;
block|}
block|}
end_class

end_unit

