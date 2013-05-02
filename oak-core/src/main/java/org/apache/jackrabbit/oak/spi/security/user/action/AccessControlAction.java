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
name|user
operator|.
name|action
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
name|ArrayList
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
name|jcr
operator|.
name|security
operator|.
name|AccessControlPolicy
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
name|AccessControlPolicyIterator
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
name|Privilege
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
name|JackrabbitAccessControlList
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
name|Authorizable
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
name|Group
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
name|User
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
name|util
operator|.
name|UserUtility
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
name|util
operator|.
name|Text
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
comment|/**  * The {@code AccessControlAction} allows to setup permissions upon creation  * of a new authorizable; namely the privileges the new authorizable should be  * granted on it's own 'home directory' being represented by the new node  * associated with that new authorizable.  *  *<p>The following to configuration parameters are available with this implementation:  *<ul>  *<li><strong>groupPrivilegeNames</strong>: the value is expected to be a  *    comma separated list of privileges that will be granted to the new group on  *    the group node</li>  *<li><strong>userPrivilegeNames</strong>: the value is expected to be a  *    comma separated list of privileges that will be granted to the new user on  *    the user node.</li>  *</ul>  *</p>  *<p>Example configuration:  *<pre>  *    groupPrivilegeNames : "jcr:read"  *    userPrivilegeNames  : "jcr:read, rep:write"  *</pre>  *</p>  *<p>This configuration could for example lead to the following content  * structure upon user or group creation. Note however that the resulting  * structure depends on the actual access control management being in place:  *  *<pre>  *     UserManager umgr = ((JackrabbitSession) session).getUserManager();  *     User user = umgr.createUser("testUser", "t");  *  *     + t                           rep:AuthorizableFolder  *       + te                        rep:AuthorizableFolder  *         + testUser                rep:User, mix:AccessControllable  *           + rep:policy            rep:ACL  *             + allow               rep:GrantACE  *               - rep:principalName = "testUser"  *               - rep:privileges    = ["jcr:read","rep:write"]  *           - rep:password  *           - rep:principalName     = "testUser"  *</pre>  *  *<pre>  *     UserManager umgr = ((JackrabbitSession) session).getUserManager();  *     Group group = umgr.createGroup("testGroup");  *  *     + t                           rep:AuthorizableFolder  *       + te                        rep:AuthorizableFolder  *         + testGroup               rep:Group, mix:AccessControllable  *           + rep:policy            rep:ACL  *             + allow               rep:GrantACE  *               - rep:principalName = "testGroup"  *               - rep:privileges    = ["jcr:read"]  *           - rep:principalName     = "testGroup"  *</pre>  *</p>  */
end_comment

begin_class
specifier|public
class|class
name|AccessControlAction
extends|extends
name|AbstractAuthorizableAction
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
name|AccessControlAction
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|USER_PRIVILEGE_NAMES
init|=
literal|"userPrivilegeNames"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|GROUP_PRIVILEGE_NAMES
init|=
literal|"groupPrivilegeNames"
decl_stmt|;
specifier|private
name|SecurityProvider
name|securityProvider
decl_stmt|;
specifier|private
name|String
index|[]
name|groupPrivilegeNames
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
specifier|private
name|String
index|[]
name|userPrivilegeNames
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
comment|//-----------------------------------------< AbstractAuthorizableAction>---
annotation|@
name|Override
specifier|protected
name|void
name|init
parameter_list|(
name|SecurityProvider
name|securityProvider
parameter_list|,
name|ConfigurationParameters
name|config
parameter_list|)
block|{
name|setSecurityProvider
argument_list|(
name|securityProvider
argument_list|)
expr_stmt|;
name|setUserPrivilegeNames
argument_list|(
name|config
operator|.
name|getConfigValue
argument_list|(
name|USER_PRIVILEGE_NAMES
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|setGroupPrivilegeNames
argument_list|(
name|config
operator|.
name|getConfigValue
argument_list|(
name|GROUP_PRIVILEGE_NAMES
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//-------------------------------------------------< AuthorizableAction>---
annotation|@
name|Override
specifier|public
name|void
name|onCreate
parameter_list|(
name|Group
name|group
parameter_list|,
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|setAC
argument_list|(
name|group
argument_list|,
name|root
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onCreate
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|password
parameter_list|,
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|setAC
argument_list|(
name|user
argument_list|,
name|root
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------------< Configuration>---
specifier|public
name|void
name|setSecurityProvider
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
comment|/**      * Sets the privileges a new group will be granted on the group's home directory.      *      * @param privilegeNames A comma separated list of privilege names.      */
specifier|public
name|void
name|setGroupPrivilegeNames
parameter_list|(
name|String
name|privilegeNames
parameter_list|)
block|{
if|if
condition|(
name|privilegeNames
operator|!=
literal|null
operator|&&
name|privilegeNames
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|groupPrivilegeNames
operator|=
name|split
argument_list|(
name|privilegeNames
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Sets the privileges a new user will be granted on the user's home directory.      *      * @param privilegeNames  A comma separated list of privilege names.      */
specifier|public
name|void
name|setUserPrivilegeNames
parameter_list|(
name|String
name|privilegeNames
parameter_list|)
block|{
if|if
condition|(
name|privilegeNames
operator|!=
literal|null
operator|&&
name|privilegeNames
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|userPrivilegeNames
operator|=
name|split
argument_list|(
name|privilegeNames
argument_list|)
expr_stmt|;
block|}
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|void
name|setAC
parameter_list|(
name|Authorizable
name|authorizable
parameter_list|,
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|securityProvider
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Not initialized"
argument_list|)
throw|;
block|}
if|if
condition|(
name|isSystemUser
argument_list|(
name|authorizable
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"System user: "
operator|+
name|authorizable
operator|.
name|getID
argument_list|()
operator|+
literal|"; omit ac setup."
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|groupPrivilegeNames
operator|.
name|length
operator|==
literal|0
operator|&&
name|userPrivilegeNames
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"No privileges configured for groups and users; omit ac setup."
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|path
init|=
name|authorizable
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|AccessControlManager
name|acMgr
init|=
name|securityProvider
operator|.
name|getAccessControlConfiguration
argument_list|()
operator|.
name|getAccessControlManager
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|)
decl_stmt|;
name|JackrabbitAccessControlList
name|acl
init|=
literal|null
decl_stmt|;
for|for
control|(
name|AccessControlPolicyIterator
name|it
init|=
name|acMgr
operator|.
name|getApplicablePolicies
argument_list|(
name|path
argument_list|)
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|AccessControlPolicy
name|plc
init|=
name|it
operator|.
name|nextAccessControlPolicy
argument_list|()
decl_stmt|;
if|if
condition|(
name|plc
operator|instanceof
name|JackrabbitAccessControlList
condition|)
block|{
name|acl
operator|=
operator|(
name|JackrabbitAccessControlList
operator|)
name|plc
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|acl
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Cannot process AccessControlAction: no applicable ACL at "
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// setup acl according to configuration.
name|Principal
name|principal
init|=
name|authorizable
operator|.
name|getPrincipal
argument_list|()
decl_stmt|;
name|boolean
name|modified
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|authorizable
operator|.
name|isGroup
argument_list|()
condition|)
block|{
comment|// new authorizable is a Group
if|if
condition|(
name|groupPrivilegeNames
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|modified
operator|=
name|acl
operator|.
name|addAccessControlEntry
argument_list|(
name|principal
argument_list|,
name|getPrivileges
argument_list|(
name|groupPrivilegeNames
argument_list|,
name|acMgr
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// new authorizable is a User
if|if
condition|(
name|userPrivilegeNames
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|modified
operator|=
name|acl
operator|.
name|addAccessControlEntry
argument_list|(
name|principal
argument_list|,
name|getPrivileges
argument_list|(
name|userPrivilegeNames
argument_list|,
name|acMgr
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|modified
condition|)
block|{
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|path
argument_list|,
name|acl
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|boolean
name|isSystemUser
parameter_list|(
name|Authorizable
name|authorizable
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|authorizable
operator|.
name|isGroup
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ConfigurationParameters
name|userConfig
init|=
name|securityProvider
operator|.
name|getUserConfiguration
argument_list|()
operator|.
name|getConfigurationParameters
argument_list|()
decl_stmt|;
name|String
name|userId
init|=
name|authorizable
operator|.
name|getID
argument_list|()
decl_stmt|;
return|return
name|UserUtility
operator|.
name|getAdminId
argument_list|(
name|userConfig
argument_list|)
operator|.
name|equals
argument_list|(
name|userId
argument_list|)
operator|||
name|UserUtility
operator|.
name|getAnonymousId
argument_list|(
name|userConfig
argument_list|)
operator|.
name|equals
argument_list|(
name|userId
argument_list|)
return|;
block|}
comment|/**      * Retrieve privileges for the specified privilege names.      *      * @param privNames The privilege names.      * @param acMgr The access control manager.      * @return Array of {@code Privilege}      * @throws javax.jcr.RepositoryException If a privilege name cannot be      * resolved to a valid privilege.      */
specifier|private
specifier|static
name|Privilege
index|[]
name|getPrivileges
parameter_list|(
name|String
index|[]
name|privNames
parameter_list|,
name|AccessControlManager
name|acMgr
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|privNames
operator|==
literal|null
operator|||
name|privNames
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|Privilege
index|[
literal|0
index|]
return|;
block|}
name|Privilege
index|[]
name|privileges
init|=
operator|new
name|Privilege
index|[
name|privNames
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|privNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|privileges
index|[
name|i
index|]
operator|=
name|acMgr
operator|.
name|privilegeFromName
argument_list|(
name|privNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|privileges
return|;
block|}
comment|/**      * Split the specified configuration parameter into privilege names.      *      * @param configParam The configuration parameter defining a comma separated      * list of privilege names.      * @return An array of privilege names.      */
specifier|private
specifier|static
name|String
index|[]
name|split
parameter_list|(
name|String
name|configParam
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|nameList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|pn
range|:
name|Text
operator|.
name|explode
argument_list|(
name|configParam
argument_list|,
literal|','
argument_list|,
literal|false
argument_list|)
control|)
block|{
name|String
name|privName
init|=
name|pn
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|privName
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|nameList
operator|.
name|add
argument_list|(
name|privName
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|nameList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|nameList
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
block|}
end_class

end_unit

