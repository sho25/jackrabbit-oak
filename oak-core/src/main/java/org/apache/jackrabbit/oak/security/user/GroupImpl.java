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
name|Enumeration
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|Iterators
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
name|principal
operator|.
name|EveryonePrincipal
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
name|Tree
name|tree
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|tree
operator|==
literal|null
operator|||
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
name|authorizableImpl
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Attempt to create circular group membership."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
name|isDeclaredMember
argument_list|(
name|authorizable
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Authorizable {} is already declared member of {}"
argument_list|,
name|memberID
argument_list|,
name|getID
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
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
return|;
block|}
comment|/**      * Returns {@code true} if the given {@code newMember} is a Group      * and contains {@code this} Group as declared or inherited member.      *      * @param newMember The new member to be tested for cyclic membership.      * @return true if the 'newMember' is a group and 'this' is an declared or      * inherited member of it.      */
specifier|private
name|boolean
name|isCyclicMembership
parameter_list|(
name|AuthorizableImpl
name|newMember
parameter_list|)
block|{
if|if
condition|(
name|newMember
operator|.
name|isGroup
argument_list|()
condition|)
block|{
name|MembershipProvider
name|mProvider
init|=
name|getMembershipProvider
argument_list|()
decl_stmt|;
name|String
name|contentId
init|=
name|mProvider
operator|.
name|getContentID
argument_list|(
name|getTree
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|mProvider
operator|.
name|isMember
argument_list|(
name|newMember
operator|.
name|getTree
argument_list|()
argument_list|,
name|contentId
argument_list|,
literal|true
argument_list|)
condition|)
block|{
comment|// found cyclic group membership
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
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
if|if
condition|(
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
name|memberTree
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
return|return
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
return|;
block|}
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
name|AuthorizableType
operator|.
name|AUTHORIZABLE
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
argument_list|,
name|includeInherited
argument_list|)
return|;
block|}
block|}
comment|/**      * Principal representation of this group instance.      */
specifier|private
class|class
name|GroupPrincipal
extends|extends
name|TreeBasedPrincipal
implements|implements
name|java
operator|.
name|security
operator|.
name|acl
operator|.
name|Group
block|{
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
specifier|public
name|boolean
name|addMember
parameter_list|(
name|Principal
name|principal
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|removeMember
parameter_list|(
name|Principal
name|principal
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMember
parameter_list|(
name|Principal
name|principal
parameter_list|)
block|{
name|boolean
name|isMember
init|=
literal|false
decl_stmt|;
try|try
block|{
comment|// shortcut for everyone group -> avoid collecting all members
comment|// as all users and groups are member of everyone.
if|if
condition|(
name|isEveryone
argument_list|()
condition|)
block|{
name|isMember
operator|=
operator|!
name|EveryonePrincipal
operator|.
name|NAME
operator|.
name|equals
argument_list|(
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Authorizable
name|a
init|=
name|getUserManager
argument_list|()
operator|.
name|getAuthorizable
argument_list|(
name|principal
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|!=
literal|null
condition|)
block|{
name|isMember
operator|=
name|GroupImpl
operator|.
name|this
operator|.
name|isMember
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
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
name|warn
argument_list|(
literal|"Failed to determine group membership"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// principal doesn't represent a known authorizable or an error occurred.
return|return
name|isMember
return|;
block|}
annotation|@
name|Override
specifier|public
name|Enumeration
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|members
parameter_list|()
block|{
specifier|final
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|members
decl_stmt|;
try|try
block|{
name|members
operator|=
name|GroupImpl
operator|.
name|this
operator|.
name|getMembers
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
comment|// should not occur.
name|String
name|msg
init|=
literal|"Unable to retrieve Group members: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
name|Iterator
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
name|Iterators
operator|.
name|transform
argument_list|(
name|members
argument_list|,
operator|new
name|Function
argument_list|<
name|Authorizable
argument_list|,
name|Principal
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Principal
name|apply
parameter_list|(
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
literal|null
return|;
block|}
try|try
block|{
return|return
name|authorizable
operator|.
name|getPrincipal
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"Internal error while retrieving principal: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|Iterators
operator|.
name|asEnumeration
argument_list|(
name|Iterators
operator|.
name|filter
argument_list|(
name|principals
argument_list|,
name|Predicates
operator|.
expr|<
name|Object
operator|>
name|notNull
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

