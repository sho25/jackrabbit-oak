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
name|HashMap
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
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
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
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Value
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|ConstraintViolationException
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
name|spi
operator|.
name|security
operator|.
name|principal
operator|.
name|PrincipalImpl
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
name|AccessControlConfiguration
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
name|NodeInfo
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
name|PropInfo
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
name|ProtectedNodeImporter
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
name|ReferenceChangeTracker
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
name|TextValue
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
comment|/**  * AccessControlImporter... TODO  */
end_comment

begin_class
class|class
name|AccessControlImporter
implements|implements
name|ProtectedNodeImporter
implements|,
name|AccessControlConstants
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
name|AccessControlImporter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|CHILD_STATUS_UNDEFINED
init|=
literal|0
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|CHILD_STATUS_ACE
init|=
literal|1
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|CHILD_STATUS_RESTRICTION
init|=
literal|2
decl_stmt|;
specifier|private
specifier|final
name|SecurityProvider
name|securityProvider
decl_stmt|;
specifier|private
name|AccessControlManager
name|acMgr
decl_stmt|;
specifier|private
name|PrincipalManager
name|principalManager
decl_stmt|;
specifier|private
name|ReadOnlyNodeTypeManager
name|ntMgr
decl_stmt|;
specifier|private
name|boolean
name|initialized
init|=
literal|false
decl_stmt|;
specifier|private
name|int
name|childStatus
decl_stmt|;
specifier|private
name|JackrabbitAccessControlList
name|acl
decl_stmt|;
specifier|private
name|MutableEntry
name|entry
decl_stmt|;
name|AccessControlImporter
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
comment|//----------------------------------------------< ProtectedItemImporter>---
annotation|@
name|Override
specifier|public
name|boolean
name|init
parameter_list|(
name|Session
name|session
parameter_list|,
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|,
name|boolean
name|isWorkspaceImport
parameter_list|,
name|int
name|uuidBehavior
parameter_list|,
name|ReferenceChangeTracker
name|referenceTracker
parameter_list|)
block|{
if|if
condition|(
name|initialized
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Already initialized"
argument_list|)
throw|;
block|}
try|try
block|{
name|AccessControlConfiguration
name|config
init|=
name|securityProvider
operator|.
name|getAccessControlConfiguration
argument_list|()
decl_stmt|;
if|if
condition|(
name|isWorkspaceImport
condition|)
block|{
name|acMgr
operator|=
name|config
operator|.
name|getAccessControlManager
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|acMgr
operator|=
name|session
operator|.
name|getAccessControlManager
argument_list|()
expr_stmt|;
block|}
name|principalManager
operator|=
name|securityProvider
operator|.
name|getPrincipalConfiguration
argument_list|()
operator|.
name|getPrincipalManager
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
name|ntMgr
operator|=
name|ReadOnlyNodeTypeManager
operator|.
name|getInstance
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
name|initialized
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error while initializing access control importer"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|initialized
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|processReferences
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// nothing to do.
block|}
comment|//----------------------------------------------< ProtectedNodeImporter>---
annotation|@
name|Override
specifier|public
name|boolean
name|start
parameter_list|(
name|Tree
name|protectedParent
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkInitialized
argument_list|()
expr_stmt|;
comment|// the acl node must have been added during the regular import before
comment|// this importer is only successfully started if an valid ACL was created.
name|acl
operator|=
name|getACL
argument_list|(
name|protectedParent
argument_list|)
expr_stmt|;
return|return
name|acl
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|end
parameter_list|(
name|Tree
name|protectedParent
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|acl
operator|!=
literal|null
condition|)
block|{
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|acl
operator|.
name|getPath
argument_list|()
argument_list|,
name|acl
argument_list|)
expr_stmt|;
name|acl
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"End reached without ACL to write back."
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|startChildInfo
parameter_list|(
name|NodeInfo
name|childInfo
parameter_list|,
name|List
argument_list|<
name|PropInfo
argument_list|>
name|propInfos
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkInitialized
argument_list|()
expr_stmt|;
name|String
name|ntName
init|=
name|childInfo
operator|.
name|getPrimaryTypeName
argument_list|()
decl_stmt|;
if|if
condition|(
name|NT_REP_GRANT_ACE
operator|.
name|equals
argument_list|(
name|ntName
argument_list|)
operator|||
name|NT_REP_DENY_ACE
operator|.
name|equals
argument_list|(
name|ntName
argument_list|)
condition|)
block|{
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Invalid child node sequence: ACEs may not be nested."
argument_list|)
throw|;
block|}
name|entry
operator|=
operator|new
name|MutableEntry
argument_list|(
name|NT_REP_GRANT_ACE
operator|.
name|equals
argument_list|(
name|ntName
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|PropInfo
name|prop
range|:
name|propInfos
control|)
block|{
name|String
name|name
init|=
name|prop
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|REP_PRINCIPAL_NAME
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|entry
operator|.
name|setPrincipal
argument_list|(
name|prop
operator|.
name|getTextValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|REP_PRIVILEGES
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|entry
operator|.
name|setPrivilegeNames
argument_list|(
name|prop
operator|.
name|getTextValues
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|entry
operator|.
name|addRestriction
argument_list|(
name|prop
argument_list|)
expr_stmt|;
block|}
block|}
name|childStatus
operator|=
name|CHILD_STATUS_ACE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|NT_REP_RESTRICTIONS
operator|.
name|equals
argument_list|(
name|ntName
argument_list|)
condition|)
block|{
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Invalid child node sequence: Restriction must be associated with an ACE"
argument_list|)
throw|;
block|}
name|entry
operator|.
name|addRestrictions
argument_list|(
name|propInfos
argument_list|)
expr_stmt|;
name|childStatus
operator|=
name|CHILD_STATUS_RESTRICTION
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Invalid child node with type "
operator|+
name|ntName
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endChildInfo
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|checkInitialized
argument_list|()
expr_stmt|;
switch|switch
condition|(
name|childStatus
condition|)
block|{
case|case
name|CHILD_STATUS_ACE
case|:
comment|// write the ace to the policy
name|entry
operator|.
name|applyTo
argument_list|(
name|acl
argument_list|)
expr_stmt|;
name|entry
operator|=
literal|null
expr_stmt|;
name|childStatus
operator|=
name|CHILD_STATUS_UNDEFINED
expr_stmt|;
break|break;
case|case
name|CHILD_STATUS_RESTRICTION
case|:
comment|// back to ace status
name|childStatus
operator|=
name|CHILD_STATUS_ACE
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"Invalid child node sequence."
argument_list|)
throw|;
block|}
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|void
name|checkInitialized
parameter_list|()
block|{
if|if
condition|(
operator|!
name|initialized
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
block|}
annotation|@
name|CheckForNull
specifier|private
name|JackrabbitAccessControlList
name|getACL
parameter_list|(
name|Tree
name|tree
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|nodeName
init|=
name|tree
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Tree
name|parent
init|=
name|tree
operator|.
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|AccessControlConstants
operator|.
name|REP_POLICY
operator|.
name|equals
argument_list|(
name|nodeName
argument_list|)
operator|&&
name|ntMgr
operator|.
name|isNodeType
argument_list|(
name|tree
argument_list|,
name|AccessControlConstants
operator|.
name|NT_REP_ACL
argument_list|)
condition|)
block|{
return|return
name|getACL
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|AccessControlConstants
operator|.
name|REP_REPO_POLICY
operator|.
name|equals
argument_list|(
name|nodeName
argument_list|)
operator|&&
name|ntMgr
operator|.
name|isNodeType
argument_list|(
name|tree
argument_list|,
name|AccessControlConstants
operator|.
name|NT_REP_ACL
argument_list|)
operator|&&
name|parent
operator|.
name|isRoot
argument_list|()
condition|)
block|{
return|return
name|getACL
argument_list|(
operator|(
name|String
operator|)
literal|null
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|CheckForNull
specifier|private
name|JackrabbitAccessControlList
name|getACL
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|JackrabbitAccessControlList
name|acl
init|=
literal|null
decl_stmt|;
for|for
control|(
name|AccessControlPolicy
name|p
range|:
name|acMgr
operator|.
name|getPolicies
argument_list|(
name|path
argument_list|)
control|)
block|{
if|if
condition|(
name|p
operator|instanceof
name|JackrabbitAccessControlList
condition|)
block|{
name|acl
operator|=
operator|(
name|JackrabbitAccessControlList
operator|)
name|p
expr_stmt|;
break|break;
block|}
block|}
return|return
name|acl
return|;
block|}
comment|//--------------------------------------------------------------------------
specifier|private
specifier|final
class|class
name|MutableEntry
block|{
specifier|final
name|boolean
name|isAllow
decl_stmt|;
name|Principal
name|principal
decl_stmt|;
name|List
argument_list|<
name|Privilege
argument_list|>
name|privileges
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Value
argument_list|>
name|restrictions
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Value
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|MutableEntry
parameter_list|(
name|boolean
name|isAllow
parameter_list|)
block|{
name|this
operator|.
name|isAllow
operator|=
name|isAllow
expr_stmt|;
block|}
specifier|private
name|void
name|setPrincipal
parameter_list|(
name|TextValue
name|txtValue
parameter_list|)
block|{
name|String
name|principalName
init|=
name|txtValue
operator|.
name|getString
argument_list|()
decl_stmt|;
name|principal
operator|=
name|principalManager
operator|.
name|getPrincipal
argument_list|(
name|principalName
argument_list|)
expr_stmt|;
comment|// TODO: review handling of unknown principals
if|if
condition|(
name|principal
operator|==
literal|null
condition|)
block|{
name|principal
operator|=
operator|new
name|PrincipalImpl
argument_list|(
name|principalName
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|setPrivilegeNames
parameter_list|(
name|TextValue
index|[]
name|txtValues
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|privileges
operator|=
operator|new
name|ArrayList
argument_list|<
name|Privilege
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|TextValue
name|value
range|:
name|txtValues
control|)
block|{
name|Value
name|privilegeName
init|=
name|value
operator|.
name|getValue
argument_list|(
name|PropertyType
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|privileges
operator|.
name|add
argument_list|(
name|acMgr
operator|.
name|privilegeFromName
argument_list|(
name|privilegeName
operator|.
name|getString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|addRestriction
parameter_list|(
name|PropInfo
name|propInfo
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|String
name|restrictionName
init|=
name|propInfo
operator|.
name|getName
argument_list|()
decl_stmt|;
name|int
name|targetType
init|=
name|acl
operator|.
name|getRestrictionType
argument_list|(
name|restrictionName
argument_list|)
decl_stmt|;
name|restrictions
operator|.
name|put
argument_list|(
name|propInfo
operator|.
name|getName
argument_list|()
argument_list|,
name|propInfo
operator|.
name|getValue
argument_list|(
name|targetType
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addRestrictions
parameter_list|(
name|List
argument_list|<
name|PropInfo
argument_list|>
name|propInfos
parameter_list|)
throws|throws
name|RepositoryException
block|{
for|for
control|(
name|PropInfo
name|prop
range|:
name|propInfos
control|)
block|{
name|addRestriction
argument_list|(
name|prop
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|applyTo
parameter_list|(
name|JackrabbitAccessControlList
name|acl
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkNotNull
argument_list|(
name|acl
argument_list|)
expr_stmt|;
name|acl
operator|.
name|addEntry
argument_list|(
name|principal
argument_list|,
name|privileges
operator|.
name|toArray
argument_list|(
operator|new
name|Privilege
index|[
name|privileges
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|isAllow
argument_list|,
name|restrictions
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

