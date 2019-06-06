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
operator|.
name|permission
package|;
end_package

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
name|base
operator|.
name|Strings
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
name|Maps
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
name|nodetype
operator|.
name|TypePredicate
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
name|TreeProvider
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
name|accesscontrol
operator|.
name|ValidationEntry
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
name|security
operator|.
name|authorization
operator|.
name|restriction
operator|.
name|Restriction
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
name|privilege
operator|.
name|JcrAllUtil
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
name|privilege
operator|.
name|PrivilegeBits
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
name|privilege
operator|.
name|PrivilegeBitsProvider
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
name|util
operator|.
name|Text
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
name|collect
operator|.
name|Iterables
operator|.
name|addAll
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
name|collect
operator|.
name|Sets
operator|.
name|newLinkedHashSet
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
name|oak
operator|.
name|plugins
operator|.
name|tree
operator|.
name|TreeConstants
operator|.
name|OAK_CHILD_ORDER
import|;
end_import

begin_class
specifier|final
class|class
name|PermissionStoreEditor
implements|implements
name|AccessControlConstants
implements|,
name|PermissionConstants
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
name|PermissionStoreEditor
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|accessControlledPath
decl_stmt|;
specifier|private
specifier|final
name|String
name|nodeName
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|AcEntry
argument_list|>
argument_list|>
name|entries
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|permissionRoot
decl_stmt|;
specifier|private
specifier|final
name|PrivilegeBitsProvider
name|bitsProvider
decl_stmt|;
name|PermissionStoreEditor
parameter_list|(
annotation|@
name|NotNull
name|String
name|aclPath
parameter_list|,
annotation|@
name|NotNull
name|String
name|name
parameter_list|,
annotation|@
name|NotNull
name|NodeState
name|node
parameter_list|,
annotation|@
name|NotNull
name|NodeBuilder
name|permissionRoot
parameter_list|,
annotation|@
name|NotNull
name|TypePredicate
name|isACE
parameter_list|,
annotation|@
name|NotNull
name|TypePredicate
name|isGrantACE
parameter_list|,
annotation|@
name|NotNull
name|PrivilegeBitsProvider
name|bitsProvider
parameter_list|,
annotation|@
name|NotNull
name|RestrictionProvider
name|restrictionProvider
parameter_list|,
annotation|@
name|NotNull
name|TreeProvider
name|treeProvider
parameter_list|)
block|{
name|this
operator|.
name|permissionRoot
operator|=
name|permissionRoot
expr_stmt|;
name|this
operator|.
name|bitsProvider
operator|=
name|bitsProvider
expr_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|REP_REPO_POLICY
argument_list|)
condition|)
block|{
name|accessControlledPath
operator|=
literal|""
expr_stmt|;
block|}
else|else
block|{
name|accessControlledPath
operator|=
name|aclPath
operator|.
name|isEmpty
argument_list|()
condition|?
literal|"/"
else|:
name|aclPath
expr_stmt|;
block|}
name|nodeName
operator|=
name|PermissionUtil
operator|.
name|getEntryName
argument_list|(
name|accessControlledPath
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|orderedChildNames
init|=
name|newLinkedHashSet
argument_list|(
name|node
operator|.
name|getNames
argument_list|(
name|OAK_CHILD_ORDER
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|n
init|=
name|orderedChildNames
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|getChildNodeCount
argument_list|(
name|n
operator|+
literal|1
argument_list|)
operator|>
name|n
condition|)
block|{
name|addAll
argument_list|(
name|orderedChildNames
argument_list|,
name|node
operator|.
name|getChildNodeNames
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|int
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|childName
range|:
name|orderedChildNames
control|)
block|{
name|NodeState
name|ace
init|=
name|node
operator|.
name|getChildNode
argument_list|(
name|childName
argument_list|)
decl_stmt|;
if|if
condition|(
name|isACE
operator|.
name|apply
argument_list|(
name|ace
argument_list|)
condition|)
block|{
name|boolean
name|isAllow
init|=
name|isGrantACE
operator|.
name|apply
argument_list|(
name|ace
argument_list|)
decl_stmt|;
name|PrivilegeBits
name|privilegeBits
init|=
name|bitsProvider
operator|.
name|getBits
argument_list|(
name|ace
operator|.
name|getNames
argument_list|(
name|REP_PRIVILEGES
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
init|=
name|restrictionProvider
operator|.
name|readRestrictions
argument_list|(
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|accessControlledPath
argument_list|)
argument_list|,
name|treeProvider
operator|.
name|createReadOnlyTree
argument_list|(
name|ace
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|principalName
init|=
name|Text
operator|.
name|escapeIllegalJcrChars
argument_list|(
name|ace
operator|.
name|getString
argument_list|(
name|REP_PRINCIPAL_NAME
argument_list|)
argument_list|)
decl_stmt|;
name|AcEntry
name|entry
init|=
operator|new
name|AcEntry
argument_list|(
name|principalName
argument_list|,
name|index
argument_list|,
name|isAllow
argument_list|,
name|privilegeBits
argument_list|,
name|restrictions
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AcEntry
argument_list|>
name|list
init|=
name|entries
operator|.
name|computeIfAbsent
argument_list|(
name|principalName
argument_list|,
name|k
lambda|->
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|index
operator|++
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|NotNull
name|String
name|getPath
parameter_list|()
block|{
return|return
name|accessControlledPath
return|;
block|}
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|entries
operator|.
name|isEmpty
argument_list|()
return|;
block|}
name|void
name|removePermissionEntries
parameter_list|(
annotation|@
name|NotNull
name|PermissionStoreEditor
name|otherEditor
parameter_list|)
block|{
name|entries
operator|.
name|keySet
argument_list|()
operator|.
name|removeAll
argument_list|(
name|otherEditor
operator|.
name|entries
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|void
name|removePermissionEntries
parameter_list|()
block|{
for|for
control|(
name|String
name|principalName
range|:
name|entries
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|permissionRoot
operator|.
name|hasChildNode
argument_list|(
name|principalName
argument_list|)
condition|)
block|{
name|NodeBuilder
name|principalRoot
init|=
name|permissionRoot
operator|.
name|getChildNode
argument_list|(
name|principalName
argument_list|)
decl_stmt|;
name|boolean
name|removed
init|=
literal|false
decl_stmt|;
comment|// find the ACL node that for this path and principal
name|NodeBuilder
name|parent
init|=
name|principalRoot
operator|.
name|getChildNode
argument_list|(
name|nodeName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|parent
operator|.
name|exists
argument_list|()
condition|)
block|{
continue|continue;
block|}
comment|// check if the node is the correct one
if|if
condition|(
name|PermissionUtil
operator|.
name|checkACLPath
argument_list|(
name|parent
argument_list|,
name|accessControlledPath
argument_list|)
condition|)
block|{
comment|// remove and reconnect child nodes
name|NodeBuilder
name|newParent
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|childName
range|:
name|parent
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
if|if
condition|(
name|childName
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|!=
literal|'c'
condition|)
block|{
continue|continue;
block|}
name|NodeBuilder
name|child
init|=
name|parent
operator|.
name|getChildNode
argument_list|(
name|childName
argument_list|)
decl_stmt|;
if|if
condition|(
name|newParent
operator|==
literal|null
condition|)
block|{
name|newParent
operator|=
name|child
expr_stmt|;
block|}
else|else
block|{
name|newParent
operator|.
name|setChildNode
argument_list|(
name|childName
argument_list|,
name|child
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|newParent
operator|!=
literal|null
condition|)
block|{
comment|// replace the 'parent', which got removed
name|principalRoot
operator|.
name|setChildNode
argument_list|(
name|nodeName
argument_list|,
name|newParent
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|removed
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|removed
operator|=
name|parent
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// check if any of the child nodes match
for|for
control|(
name|String
name|childName
range|:
name|parent
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
if|if
condition|(
name|childName
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|!=
literal|'c'
condition|)
block|{
continue|continue;
block|}
name|NodeBuilder
name|child
init|=
name|parent
operator|.
name|getChildNode
argument_list|(
name|childName
argument_list|)
decl_stmt|;
if|if
condition|(
name|PermissionUtil
operator|.
name|checkACLPath
argument_list|(
name|child
argument_list|,
name|accessControlledPath
argument_list|)
condition|)
block|{
name|removed
operator|=
name|child
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|removed
condition|)
block|{
name|updateNumEntries
argument_list|(
name|principalName
argument_list|,
name|principalRoot
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Unable to remove permission entry {}: Principal root missing."
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|void
name|updatePermissionEntries
parameter_list|()
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|AcEntry
argument_list|>
argument_list|>
name|entry
range|:
name|entries
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|principalName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|NodeBuilder
name|principalRoot
init|=
name|permissionRoot
operator|.
name|child
argument_list|(
name|principalName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|principalRoot
operator|.
name|hasProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
condition|)
block|{
name|principalRoot
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
name|NodeBuilder
name|parent
init|=
name|principalRoot
operator|.
name|child
argument_list|(
name|nodeName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|parent
operator|.
name|hasProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
condition|)
block|{
name|parent
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
comment|// check if current parent already has the correct path
if|if
condition|(
name|parent
operator|.
name|hasProperty
argument_list|(
name|REP_ACCESS_CONTROLLED_PATH
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|PermissionUtil
operator|.
name|checkACLPath
argument_list|(
name|parent
argument_list|,
name|accessControlledPath
argument_list|)
condition|)
block|{
comment|// hash collision, find a new child
name|NodeBuilder
name|child
init|=
literal|null
decl_stmt|;
name|int
name|idx
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|childName
range|:
name|parent
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
if|if
condition|(
name|childName
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|!=
literal|'c'
condition|)
block|{
continue|continue;
block|}
name|child
operator|=
name|parent
operator|.
name|getChildNode
argument_list|(
name|childName
argument_list|)
expr_stmt|;
if|if
condition|(
name|PermissionUtil
operator|.
name|checkACLPath
argument_list|(
name|child
argument_list|,
name|accessControlledPath
argument_list|)
condition|)
block|{
break|break;
block|}
name|child
operator|=
literal|null
expr_stmt|;
name|idx
operator|++
expr_stmt|;
block|}
while|while
condition|(
name|child
operator|==
literal|null
condition|)
block|{
name|String
name|name
init|=
literal|'c'
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|idx
operator|++
argument_list|)
decl_stmt|;
name|child
operator|=
name|parent
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|child
operator|.
name|exists
argument_list|()
condition|)
block|{
name|child
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|child
operator|=
name|parent
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|child
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
block|}
name|parent
operator|=
name|child
expr_stmt|;
name|parent
operator|.
name|setProperty
argument_list|(
name|REP_ACCESS_CONTROLLED_PATH
argument_list|,
name|accessControlledPath
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// new parent
name|parent
operator|.
name|setProperty
argument_list|(
name|REP_ACCESS_CONTROLLED_PATH
argument_list|,
name|accessControlledPath
argument_list|)
expr_stmt|;
block|}
name|updateEntries
argument_list|(
name|parent
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|parent
operator|.
name|isNew
argument_list|()
condition|)
block|{
name|updateNumEntries
argument_list|(
name|principalName
argument_list|,
name|principalRoot
argument_list|,
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|updateEntries
parameter_list|(
annotation|@
name|NotNull
name|NodeBuilder
name|parent
parameter_list|,
annotation|@
name|NotNull
name|List
argument_list|<
name|AcEntry
argument_list|>
name|list
parameter_list|)
block|{
comment|// remove old entries
for|for
control|(
name|String
name|childName
range|:
name|parent
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
if|if
condition|(
name|childName
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|!=
literal|'c'
condition|)
block|{
name|parent
operator|.
name|getChildNode
argument_list|(
name|childName
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
for|for
control|(
name|AcEntry
name|ace
range|:
name|list
control|)
block|{
name|ace
operator|.
name|writeToPermissionStore
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|updateNumEntries
parameter_list|(
annotation|@
name|NotNull
name|String
name|principalName
parameter_list|,
annotation|@
name|NotNull
name|NodeBuilder
name|principalRoot
parameter_list|,
name|int
name|cnt
parameter_list|)
block|{
name|PropertyState
name|ps
init|=
name|principalRoot
operator|.
name|getProperty
argument_list|(
name|REP_NUM_PERMISSIONS
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps
operator|==
literal|null
operator|&&
operator|!
name|principalRoot
operator|.
name|isNew
argument_list|()
condition|)
block|{
comment|// existing principal root that doesn't have the rep:numEntries set
return|return;
block|}
name|long
name|numEntries
init|=
operator|(
operator|(
name|ps
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
operator|)
operator|+
name|cnt
decl_stmt|;
if|if
condition|(
name|numEntries
operator|<
literal|0
condition|)
block|{
comment|// numEntries unexpectedly turned negative
name|log
operator|.
name|error
argument_list|(
literal|"NumEntries counter for principal '{}' turned negative -> removing 'rep:numPermissions' property."
argument_list|,
name|principalName
argument_list|)
expr_stmt|;
name|principalRoot
operator|.
name|removeProperty
argument_list|(
name|REP_NUM_PERMISSIONS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|principalRoot
operator|.
name|setProperty
argument_list|(
name|REP_NUM_PERMISSIONS
argument_list|,
name|numEntries
argument_list|,
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|AcEntry
extends|extends
name|ValidationEntry
block|{
name|AcEntry
parameter_list|(
annotation|@
name|NotNull
name|String
name|principalName
parameter_list|,
name|int
name|index
parameter_list|,
name|boolean
name|isAllow
parameter_list|,
annotation|@
name|NotNull
name|PrivilegeBits
name|privilegeBits
parameter_list|,
annotation|@
name|NotNull
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
parameter_list|)
block|{
name|super
argument_list|(
name|principalName
argument_list|,
name|privilegeBits
argument_list|,
name|isAllow
argument_list|,
name|restrictions
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|writeToPermissionStore
parameter_list|(
annotation|@
name|NotNull
name|NodeBuilder
name|parent
parameter_list|)
block|{
name|NodeBuilder
name|n
init|=
name|parent
operator|.
name|child
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|index
argument_list|)
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_REP_PERMISSIONS
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
name|REP_IS_ALLOW
argument_list|,
name|isAllow
argument_list|)
operator|.
name|setProperty
argument_list|(
name|getPrivilegeBitsProperty
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Restriction
name|restriction
range|:
name|restrictions
control|)
block|{
name|n
operator|.
name|setProperty
argument_list|(
name|restriction
operator|.
name|getProperty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|NotNull
specifier|private
name|PropertyState
name|getPrivilegeBitsProperty
parameter_list|()
block|{
return|return
name|JcrAllUtil
operator|.
name|asPropertyState
argument_list|(
name|REP_PRIVILEGE_BITS
argument_list|,
name|privilegeBits
argument_list|,
name|bitsProvider
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

