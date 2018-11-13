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
name|Iterator
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
name|nodetype
operator|.
name|ConstraintViolationException
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
name|Predicate
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
name|Iterators
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|commons
operator|.
name|iterator
operator|.
name|RangeIteratorAdapter
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
name|spi
operator|.
name|security
operator|.
name|user
operator|.
name|AuthorizableType
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
name|UserUtil
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
comment|/**  * GroupImpl...  */
end_comment

begin_class
class|class
name|GroupImpl
extends|extends
name|AuthorizableImpl
implements|implements
name|Group
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
name|GroupImpl
operator|.
name|class
argument_list|)
decl_stmt|;
name|GroupImpl
parameter_list|(
name|String
name|id
parameter_list|,
name|Tree
name|tree
parameter_list|,
name|UserManagerImpl
name|userManager
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|super
argument_list|(
name|id
argument_list|,
name|tree
argument_list|,
name|userManager
argument_list|)
expr_stmt|;
block|}
comment|//---------------------------------------------------< AuthorizableImpl>---
annotation|@
name|Override
name|void
name|checkValidTree
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
operator|!
name|UserUtil
operator|.
name|isType
argument_list|(
name|tree
argument_list|,
name|AuthorizableType
operator|.
name|GROUP
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid group node: node type rep:Group expected."
argument_list|)
throw|;
block|}
block|}
comment|//-------------------------------------------------------< Authorizable>---
annotation|@
name|Override
specifier|public
name|boolean
name|isGroup
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|Principal
name|getPrincipal
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
operator|new
name|GroupPrincipal
argument_list|(
name|getPrincipalName
argument_list|()
argument_list|,
name|getTree
argument_list|()
argument_list|)
return|;
block|}
comment|//--------------------------------------------------------------< Group>---
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|getDeclaredMembers
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getMembers
argument_list|(
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|getMembers
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getMembers
argument_list|(
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDeclaredMember
parameter_list|(
name|Authorizable
name|authorizable
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|isMember
argument_list|(
name|authorizable
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMember
parameter_list|(
name|Authorizable
name|authorizable
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|isMember
argument_list|(
name|authorizable
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|addMember
parameter_list|(
name|Authorizable
name|authorizable
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
operator|!
name|isValidAuthorizableImpl
argument_list|(
name|authorizable
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Invalid Authorizable: {}"
argument_list|,
name|authorizable
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|AuthorizableImpl
name|authorizableImpl
init|=
operator|(
operator|(
name|AuthorizableImpl
operator|)
name|authorizable
operator|)
decl_stmt|;
if|if
condition|(
name|isEveryone
argument_list|()
operator|||
name|authorizableImpl
operator|.
name|isEveryone
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Attempt to add member to everyone group or create membership for it."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|String
name|memberID
init|=
name|authorizable
operator|.
name|getID
argument_list|()
decl_stmt|;
if|if
condition|(
name|authorizableImpl
operator|.
name|isGroup
argument_list|()
condition|)
block|{
if|if
condition|(
name|getID
argument_list|()
operator|.
name|equals
argument_list|(
name|memberID
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
literal|"Attempt to add a group as member of itself ("
operator|+
name|getID
argument_list|()
operator|+
literal|")."
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
name|msg
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|isCyclicMembership
argument_list|(
operator|(
name|Group
operator|)
name|authorizable
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
literal|"Cyclic group membership detected for group "
operator|+
name|getID
argument_list|()
operator|+
literal|" and member "
operator|+
name|authorizable
operator|.
name|getID
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
name|boolean
name|success
init|=
name|getMembershipProvider
argument_list|()
operator|.
name|addMember
argument_list|(
name|getTree
argument_list|()
argument_list|,
name|authorizableImpl
operator|.
name|getTree
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|getUserManager
argument_list|()
operator|.
name|onGroupUpdate
argument_list|(
name|this
argument_list|,
literal|false
argument_list|,
name|authorizable
argument_list|)
expr_stmt|;
block|}
return|return
name|success
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|addMembers
parameter_list|(
annotation|@
name|NotNull
name|String
modifier|...
name|memberIds
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|updateMembers
argument_list|(
literal|false
argument_list|,
name|memberIds
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|removeMember
parameter_list|(
name|Authorizable
name|authorizable
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
operator|!
name|isValidAuthorizableImpl
argument_list|(
name|authorizable
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Invalid Authorizable: {}"
argument_list|,
name|authorizable
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|AuthorizableImpl
name|authorizableImpl
init|=
operator|(
operator|(
name|AuthorizableImpl
operator|)
name|authorizable
operator|)
decl_stmt|;
if|if
condition|(
name|isEveryone
argument_list|()
operator|||
name|authorizableImpl
operator|.
name|isEveryone
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Attempt to remove member from everyone group or remove membership for it."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
name|Tree
name|memberTree
init|=
name|authorizableImpl
operator|.
name|getTree
argument_list|()
decl_stmt|;
name|boolean
name|success
init|=
name|getMembershipProvider
argument_list|()
operator|.
name|removeMember
argument_list|(
name|getTree
argument_list|()
argument_list|,
name|memberTree
argument_list|)
decl_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|getUserManager
argument_list|()
operator|.
name|onGroupUpdate
argument_list|(
name|this
argument_list|,
literal|true
argument_list|,
name|authorizable
argument_list|)
expr_stmt|;
block|}
return|return
name|success
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|removeMembers
parameter_list|(
annotation|@
name|NotNull
name|String
modifier|...
name|memberIds
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|updateMembers
argument_list|(
literal|true
argument_list|,
name|memberIds
argument_list|)
return|;
block|}
comment|//--------------------------------------------------------------------------
comment|/**      * Internal implementation of {@link #getDeclaredMembers()} and {@link #getMembers()}.      *      * @param includeInherited Flag indicating if only the declared or all members      * should be returned.      * @return Iterator of authorizables being member of this group.      * @throws RepositoryException If an error occurs.      */
specifier|private
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|getMembers
parameter_list|(
name|boolean
name|includeInherited
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|UserManagerImpl
name|userMgr
init|=
name|getUserManager
argument_list|()
decl_stmt|;
if|if
condition|(
name|isEveryone
argument_list|()
condition|)
block|{
name|String
name|propName
init|=
name|getUserManager
argument_list|()
operator|.
name|getNamePathMapper
argument_list|()
operator|.
name|getJcrName
argument_list|(
operator|(
name|REP_PRINCIPAL_NAME
operator|)
argument_list|)
decl_stmt|;
return|return
name|Iterators
operator|.
name|filter
argument_list|(
name|userMgr
operator|.
name|findAuthorizables
argument_list|(
name|propName
argument_list|,
literal|null
argument_list|,
name|UserManager
operator|.
name|SEARCH_TYPE_AUTHORIZABLE
argument_list|)
argument_list|,
operator|new
name|Predicate
argument_list|<
name|Authorizable
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
annotation|@
name|Nullable
name|Authorizable
name|authorizable
parameter_list|)
block|{
if|if
condition|(
name|authorizable
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|authorizable
operator|.
name|isGroup
argument_list|()
condition|)
block|{
try|try
block|{
return|return
operator|!
operator|(
operator|(
name|GroupImpl
operator|)
name|authorizable
operator|)
operator|.
name|isEveryone
argument_list|()
return|;
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
literal|"Unable to evaluate if authorizable is the 'everyone' group."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
argument_list|)
return|;
block|}
else|else
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|oakPaths
init|=
name|getMembershipProvider
argument_list|()
operator|.
name|getMembers
argument_list|(
name|getTree
argument_list|()
argument_list|,
name|includeInherited
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakPaths
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|AuthorizableIterator
name|iterator
init|=
name|AuthorizableIterator
operator|.
name|create
argument_list|(
name|oakPaths
argument_list|,
name|userMgr
argument_list|,
name|AuthorizableType
operator|.
name|AUTHORIZABLE
argument_list|)
decl_stmt|;
return|return
operator|new
name|RangeIteratorAdapter
argument_list|(
name|iterator
argument_list|,
name|iterator
operator|.
name|getSize
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|RangeIteratorAdapter
operator|.
name|EMPTY
return|;
block|}
block|}
block|}
comment|/**      * Internal implementation of {@link #isDeclaredMember(Authorizable)} and {@link #isMember(Authorizable)}.      *      * @param authorizable The authorizable to test.      * @param includeInherited Flag indicating if only declared or all members      * should taken into account.      * @return {@code true} if the specified authorizable is member or declared      * member of this group; {@code false} otherwise.      * @throws RepositoryException If an error occurs.      */
specifier|private
name|boolean
name|isMember
parameter_list|(
name|Authorizable
name|authorizable
parameter_list|,
name|boolean
name|includeInherited
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
operator|!
name|isValidAuthorizableImpl
argument_list|(
name|authorizable
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getID
argument_list|()
operator|.
name|equals
argument_list|(
name|authorizable
operator|.
name|getID
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|isEveryone
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
operator|(
operator|(
name|AuthorizableImpl
operator|)
name|authorizable
operator|)
operator|.
name|isEveryone
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|Tree
name|authorizableTree
init|=
operator|(
operator|(
name|AuthorizableImpl
operator|)
name|authorizable
operator|)
operator|.
name|getTree
argument_list|()
decl_stmt|;
name|MembershipProvider
name|mgr
init|=
name|getUserManager
argument_list|()
operator|.
name|getMembershipProvider
argument_list|()
decl_stmt|;
if|if
condition|(
name|includeInherited
condition|)
block|{
return|return
name|mgr
operator|.
name|isMember
argument_list|(
name|this
operator|.
name|getTree
argument_list|()
argument_list|,
name|authorizableTree
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|mgr
operator|.
name|isDeclaredMember
argument_list|(
name|this
operator|.
name|getTree
argument_list|()
argument_list|,
name|authorizableTree
argument_list|)
return|;
block|}
block|}
block|}
comment|/**      * Internal method to add or remove members by ID.      *      * @param isRemove Boolean flag indicating if members should be added or removed.      * @param memberIds The {@code memberIds} to be added or removed.      * @return The sub-set of {@code memberIds} that could not be added/removed.      * @throws javax.jcr.nodetype.ConstraintViolationException If any of the specified      * IDs is empty string or null or if {@link org.apache.jackrabbit.oak.spi.xml.ImportBehavior#ABORT}      * is configured and an ID cannot be resolved to an existing (or accessible)      * authorizable.      * @throws javax.jcr.RepositoryException If another error occurs.      */
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|updateMembers
parameter_list|(
name|boolean
name|isRemove
parameter_list|,
annotation|@
name|NotNull
name|String
modifier|...
name|memberIds
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|failedIds
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|memberIds
argument_list|)
decl_stmt|;
name|int
name|importBehavior
init|=
name|UserUtil
operator|.
name|getImportBehavior
argument_list|(
name|getUserManager
argument_list|()
operator|.
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|isEveryone
argument_list|()
condition|)
block|{
name|String
name|msg
init|=
literal|"Attempt to add or remove from everyone group."
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
name|msg
argument_list|)
expr_stmt|;
return|return
name|failedIds
return|;
block|}
comment|// calculate the contentID for each memberId and remember ids that cannot be processed
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|updateMap
init|=
name|Maps
operator|.
name|newHashMapWithExpectedSize
argument_list|(
name|memberIds
operator|.
name|length
argument_list|)
decl_stmt|;
name|MembershipProvider
name|mp
init|=
name|getMembershipProvider
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|memberId
range|:
name|memberIds
control|)
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|memberId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
literal|"MemberId must not be null or empty."
argument_list|)
throw|;
block|}
if|if
condition|(
name|getID
argument_list|()
operator|.
name|equals
argument_list|(
name|memberId
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
literal|"Attempt to add or remove a group as member of itself ("
operator|+
name|getID
argument_list|()
operator|+
literal|")."
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
name|msg
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|ImportBehavior
operator|.
name|BESTEFFORT
operator|!=
name|importBehavior
condition|)
block|{
name|Authorizable
name|member
init|=
name|getUserManager
argument_list|()
operator|.
name|getAuthorizable
argument_list|(
name|memberId
argument_list|)
decl_stmt|;
name|String
name|msg
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|member
operator|==
literal|null
condition|)
block|{
name|msg
operator|=
literal|"Attempt to add or remove a non-existing member '"
operator|+
name|memberId
operator|+
literal|"' with ImportBehavior = "
operator|+
name|ImportBehavior
operator|.
name|nameFromValue
argument_list|(
name|importBehavior
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|member
operator|.
name|isGroup
argument_list|()
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|AuthorizableImpl
operator|)
name|member
operator|)
operator|.
name|isEveryone
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Attempt to add everyone group as member."
argument_list|)
expr_stmt|;
continue|continue;
block|}
elseif|else
if|if
condition|(
name|isCyclicMembership
argument_list|(
operator|(
name|Group
operator|)
name|member
argument_list|)
condition|)
block|{
name|msg
operator|=
literal|"Cyclic group membership detected for group "
operator|+
name|getID
argument_list|()
operator|+
literal|" and member "
operator|+
name|member
operator|.
name|getID
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|ImportBehavior
operator|.
name|ABORT
operator|==
name|importBehavior
condition|)
block|{
throw|throw
operator|new
name|ConstraintViolationException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
else|else
block|{
comment|// ImportBehavior.IGNORE is default in UserUtil.getImportBehavior
name|log
operator|.
name|debug
argument_list|(
name|msg
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
block|}
comment|// memberId can be processed -> remove from failedIds and generate contentID
name|failedIds
operator|.
name|remove
argument_list|(
name|memberId
argument_list|)
expr_stmt|;
name|updateMap
operator|.
name|put
argument_list|(
name|mp
operator|.
name|getContentID
argument_list|(
name|memberId
argument_list|)
argument_list|,
name|memberId
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|processedIds
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|updateMap
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|updateMap
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|result
decl_stmt|;
if|if
condition|(
name|isRemove
condition|)
block|{
name|result
operator|=
name|mp
operator|.
name|removeMembers
argument_list|(
name|getTree
argument_list|()
argument_list|,
name|updateMap
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|mp
operator|.
name|addMembers
argument_list|(
name|getTree
argument_list|()
argument_list|,
name|updateMap
argument_list|)
expr_stmt|;
block|}
name|failedIds
operator|.
name|addAll
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|processedIds
operator|.
name|removeAll
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
name|getUserManager
argument_list|()
operator|.
name|onGroupUpdate
argument_list|(
name|this
argument_list|,
name|isRemove
argument_list|,
literal|false
argument_list|,
name|processedIds
argument_list|,
name|failedIds
argument_list|)
expr_stmt|;
return|return
name|failedIds
return|;
block|}
specifier|private
name|boolean
name|isCyclicMembership
parameter_list|(
annotation|@
name|NotNull
name|Group
name|member
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|member
operator|.
name|isMember
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**      * Principal representation of this group instance.      */
specifier|private
specifier|final
class|class
name|GroupPrincipal
extends|extends
name|AbstractGroupPrincipal
block|{
specifier|private
name|GroupPrincipal
parameter_list|(
name|String
name|principalName
parameter_list|,
name|Tree
name|groupTree
parameter_list|)
block|{
name|super
argument_list|(
name|principalName
argument_list|,
name|groupTree
argument_list|,
name|GroupImpl
operator|.
name|this
operator|.
name|getUserManager
argument_list|()
operator|.
name|getNamePathMapper
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|UserManager
name|getUserManager
parameter_list|()
block|{
return|return
name|GroupImpl
operator|.
name|this
operator|.
name|getUserManager
argument_list|()
return|;
block|}
annotation|@
name|Override
name|boolean
name|isEveryone
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|GroupImpl
operator|.
name|this
operator|.
name|isEveryone
argument_list|()
return|;
block|}
annotation|@
name|Override
name|boolean
name|isMember
parameter_list|(
annotation|@
name|NotNull
name|Authorizable
name|authorizable
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|GroupImpl
operator|.
name|this
operator|.
name|isMember
argument_list|(
name|authorizable
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|getMembers
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|GroupImpl
operator|.
name|this
operator|.
name|getMembers
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

