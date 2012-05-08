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
name|jcr
operator|.
name|security
operator|.
name|user
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|collections
operator|.
name|iterators
operator|.
name|EmptyIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|collections
operator|.
name|iterators
operator|.
name|IteratorChain
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|collections
operator|.
name|iterators
operator|.
name|SingletonIterator
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
name|flat
operator|.
name|BTreeManager
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
name|flat
operator|.
name|ItemSequence
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
name|flat
operator|.
name|PropertySequence
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
name|flat
operator|.
name|Rank
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
name|flat
operator|.
name|TreeManager
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
name|jcr
operator|.
name|SessionDelegate
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
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyIterator
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
name|Value
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|List
import|;
end_import

begin_comment
comment|/**  * MembershipManager...  */
end_comment

begin_class
class|class
name|MembershipManager
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
name|MembershipManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|UserManagerImpl
name|userManager
decl_stmt|;
specifier|private
specifier|final
name|SessionDelegate
name|sessionDelegate
decl_stmt|;
specifier|private
specifier|final
name|int
name|memberSplitSize
decl_stmt|;
specifier|private
specifier|final
name|String
name|repMembers
decl_stmt|;
name|MembershipManager
parameter_list|(
name|UserManagerImpl
name|userManager
parameter_list|,
name|int
name|memberSplitSize
parameter_list|,
name|SessionDelegate
name|sessionDelegate
parameter_list|)
block|{
name|this
operator|.
name|userManager
operator|=
name|userManager
expr_stmt|;
name|this
operator|.
name|sessionDelegate
operator|=
name|sessionDelegate
expr_stmt|;
name|this
operator|.
name|memberSplitSize
operator|=
name|memberSplitSize
expr_stmt|;
name|repMembers
operator|=
name|sessionDelegate
operator|.
name|getNamePathMapper
argument_list|()
operator|.
name|getJcrName
argument_list|(
name|AuthorizableImpl
operator|.
name|REP_MEMBERS
argument_list|)
expr_stmt|;
block|}
name|Iterator
argument_list|<
name|Group
argument_list|>
name|getMembership
parameter_list|(
name|AuthorizableImpl
name|authorizable
parameter_list|,
name|boolean
name|includeInherited
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|PropertyIterator
name|refs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|nodeID
init|=
name|authorizable
operator|.
name|getNode
argument_list|()
operator|.
name|getIdentifier
argument_list|()
decl_stmt|;
name|refs
operator|=
name|authorizable
operator|.
name|getNode
argument_list|()
operator|.
name|getWeakReferences
argument_list|(
literal|null
argument_list|)
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
name|error
argument_list|(
literal|"Failed to retrieve membership references of "
operator|+
name|authorizable
operator|.
name|getID
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// TODO retrieve by traversing
block|}
if|if
condition|(
name|refs
operator|!=
literal|null
condition|)
block|{
name|AuthorizableIterator
name|iterator
init|=
operator|new
name|AuthorizableIterator
argument_list|(
name|refs
argument_list|,
name|UserManager
operator|.
name|SEARCH_TYPE_GROUP
argument_list|,
name|userManager
argument_list|)
decl_stmt|;
if|if
condition|(
name|includeInherited
condition|)
block|{
return|return
name|getAllMembership
argument_list|(
name|iterator
argument_list|)
return|;
block|}
else|else
block|{
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
name|boolean
name|isMember
parameter_list|(
name|GroupImpl
name|group
parameter_list|,
name|AuthorizableImpl
name|authorizable
parameter_list|,
name|boolean
name|includeInherited
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|node
init|=
name|group
operator|.
name|getNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|includeInherited
condition|)
block|{
name|Iterator
argument_list|<
name|Group
argument_list|>
name|groups
init|=
name|getMembership
argument_list|(
name|authorizable
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|group
operator|.
name|getID
argument_list|()
decl_stmt|;
while|while
condition|(
name|groups
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
name|groups
operator|.
name|next
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|useMemberNode
argument_list|(
name|node
argument_list|)
condition|)
block|{
if|if
condition|(
name|node
operator|.
name|hasNode
argument_list|(
name|repMembers
argument_list|)
condition|)
block|{
comment|// TODO: fix.. testing for property name isn't correct.
name|PropertySequence
name|propertySequence
init|=
name|getPropertySequence
argument_list|(
name|node
operator|.
name|getNode
argument_list|(
name|repMembers
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|propertySequence
operator|.
name|hasItem
argument_list|(
name|authorizable
operator|.
name|getID
argument_list|()
argument_list|)
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|node
operator|.
name|hasProperty
argument_list|(
name|repMembers
argument_list|)
condition|)
block|{
name|Value
index|[]
name|members
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|repMembers
argument_list|)
operator|.
name|getValues
argument_list|()
decl_stmt|;
for|for
control|(
name|Value
name|v
range|:
name|members
control|)
block|{
if|if
condition|(
name|authorizable
operator|.
name|getNode
argument_list|()
operator|.
name|getIdentifier
argument_list|()
operator|.
name|equals
argument_list|(
name|v
operator|.
name|getString
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
block|}
block|}
comment|// no a member of the specified group
return|return
literal|false
return|;
block|}
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|getMembers
parameter_list|(
name|GroupImpl
name|group
parameter_list|,
name|int
name|authorizableType
parameter_list|,
name|boolean
name|includeInherited
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|node
init|=
name|group
operator|.
name|getNode
argument_list|()
decl_stmt|;
name|AuthorizableIterator
name|iterator
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|useMemberNode
argument_list|(
name|node
argument_list|)
condition|)
block|{
if|if
condition|(
name|node
operator|.
name|hasNode
argument_list|(
name|repMembers
argument_list|)
condition|)
block|{
name|PropertySequence
name|propertySequence
init|=
name|getPropertySequence
argument_list|(
name|node
operator|.
name|getNode
argument_list|(
name|repMembers
argument_list|)
argument_list|)
decl_stmt|;
name|iterator
operator|=
operator|new
name|AuthorizableIterator
argument_list|(
name|propertySequence
argument_list|,
name|authorizableType
argument_list|,
name|userManager
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|node
operator|.
name|hasProperty
argument_list|(
name|repMembers
argument_list|)
condition|)
block|{
name|Value
index|[]
name|members
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|repMembers
argument_list|)
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|iterator
operator|=
operator|new
name|AuthorizableIterator
argument_list|(
name|members
argument_list|,
name|authorizableType
argument_list|,
name|userManager
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|iterator
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|includeInherited
condition|)
block|{
return|return
name|getAllMembers
argument_list|(
name|iterator
argument_list|,
name|authorizableType
argument_list|)
return|;
block|}
else|else
block|{
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
name|boolean
name|addMember
parameter_list|(
name|GroupImpl
name|group
parameter_list|,
name|AuthorizableImpl
name|authorizable
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|node
init|=
name|group
operator|.
name|getNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|useMemberNode
argument_list|(
name|node
argument_list|)
condition|)
block|{
comment|// TODO: modify items on oak-api directly
block|}
else|else
block|{
name|Node
name|memberNode
init|=
name|authorizable
operator|.
name|getNode
argument_list|()
decl_stmt|;
name|Value
index|[]
name|values
decl_stmt|;
name|Value
name|toAdd
init|=
name|sessionDelegate
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|memberNode
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|hasProperty
argument_list|(
name|repMembers
argument_list|)
condition|)
block|{
name|Value
index|[]
name|old
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|repMembers
argument_list|)
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|values
operator|=
operator|new
name|Value
index|[
name|old
operator|.
name|length
operator|+
literal|1
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|old
argument_list|,
literal|0
argument_list|,
name|values
argument_list|,
literal|0
argument_list|,
name|old
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|values
operator|=
operator|new
name|Value
index|[
literal|1
index|]
expr_stmt|;
block|}
name|values
index|[
name|values
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|toAdd
expr_stmt|;
name|userManager
operator|.
name|setInternalProperty
argument_list|(
name|node
argument_list|,
name|repMembers
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
name|boolean
name|removeMember
parameter_list|(
name|GroupImpl
name|group
parameter_list|,
name|AuthorizableImpl
name|authorizable
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|node
init|=
name|group
operator|.
name|getNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|useMemberNode
argument_list|(
name|node
argument_list|)
condition|)
block|{
if|if
condition|(
name|node
operator|.
name|hasNode
argument_list|(
name|repMembers
argument_list|)
condition|)
block|{
name|Node
name|nMembers
init|=
name|node
operator|.
name|getNode
argument_list|(
name|repMembers
argument_list|)
decl_stmt|;
name|PropertySequence
name|properties
init|=
name|getPropertySequence
argument_list|(
name|nMembers
argument_list|)
decl_stmt|;
name|String
name|propName
init|=
name|authorizable
operator|.
name|getNode
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// TODO: fix.. testing for property name isn't correct.
if|if
condition|(
name|properties
operator|.
name|hasItem
argument_list|(
name|propName
argument_list|)
condition|)
block|{
name|Property
name|p
init|=
name|properties
operator|.
name|getItem
argument_list|(
name|propName
argument_list|)
decl_stmt|;
name|userManager
operator|.
name|removeInternalProperty
argument_list|(
name|p
operator|.
name|getParent
argument_list|()
argument_list|,
name|propName
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|node
operator|.
name|hasProperty
argument_list|(
name|repMembers
argument_list|)
condition|)
block|{
name|Value
name|toRemove
init|=
name|sessionDelegate
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
operator|(
name|authorizable
operator|)
operator|.
name|getNode
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Property
name|property
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|repMembers
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Value
argument_list|>
name|valList
init|=
operator|new
name|ArrayList
argument_list|<
name|Value
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|property
operator|.
name|getValues
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|valList
operator|.
name|remove
argument_list|(
name|toRemove
argument_list|)
condition|)
block|{
if|if
condition|(
name|valList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|userManager
operator|.
name|removeInternalProperty
argument_list|(
name|node
argument_list|,
name|repMembers
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Value
index|[]
name|values
init|=
name|valList
operator|.
name|toArray
argument_list|(
operator|new
name|Value
index|[
name|valList
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|userManager
operator|.
name|setInternalProperty
argument_list|(
name|node
argument_list|,
name|repMembers
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
block|}
comment|// nothing changed
name|log
operator|.
name|debug
argument_list|(
literal|"Authorizable {} was not member of {}"
argument_list|,
name|authorizable
operator|.
name|getID
argument_list|()
argument_list|,
name|group
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|//--------------------------------------------------------------------------
specifier|private
name|boolean
name|useMemberNode
parameter_list|(
name|Node
name|n
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|memberSplitSize
operator|>=
literal|4
operator|&&
operator|!
name|n
operator|.
name|hasProperty
argument_list|(
name|repMembers
argument_list|)
return|;
block|}
specifier|private
name|PropertySequence
name|getPropertySequence
parameter_list|(
name|Node
name|nMembers
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Comparator
argument_list|<
name|String
argument_list|>
name|order
init|=
name|Rank
operator|.
name|comparableComparator
argument_list|()
decl_stmt|;
name|int
name|minChildren
init|=
name|memberSplitSize
operator|/
literal|2
decl_stmt|;
name|TreeManager
name|treeManager
init|=
operator|new
name|BTreeManager
argument_list|(
name|nMembers
argument_list|,
name|minChildren
argument_list|,
name|memberSplitSize
argument_list|,
name|order
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|ItemSequence
operator|.
name|createPropertySequence
argument_list|(
name|treeManager
argument_list|)
return|;
block|}
comment|/**      * Returns an iterator of authorizables which includes all indirect members      * of the given iterator of authorizables.      *      * @param authorizables      * @param authorizableType      * @return Iterator of Authorizable objects      */
specifier|private
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|getAllMembers
parameter_list|(
specifier|final
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|authorizables
parameter_list|,
specifier|final
name|int
name|authorizableType
parameter_list|)
block|{
name|Iterator
argument_list|<
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
argument_list|>
name|inheritedMembers
init|=
operator|new
name|Iterator
argument_list|<
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|authorizables
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|next
parameter_list|()
block|{
name|Authorizable
name|next
init|=
name|authorizables
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
operator|new
name|IteratorChain
argument_list|(
operator|new
name|SingletonIterator
argument_list|(
name|next
argument_list|)
argument_list|,
name|inherited
argument_list|(
name|next
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|private
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|inherited
parameter_list|(
name|Authorizable
name|authorizable
parameter_list|)
block|{
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
name|getMembers
argument_list|(
operator|(
operator|(
name|GroupImpl
operator|)
name|authorizable
operator|)
argument_list|,
name|authorizableType
argument_list|,
literal|true
argument_list|)
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
literal|"Could not determine members of "
operator|+
name|authorizable
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|EmptyIterator
operator|.
name|INSTANCE
return|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|InheritingAuthorizableIterator
argument_list|(
name|inheritedMembers
argument_list|)
return|;
block|}
specifier|private
name|Iterator
argument_list|<
name|Group
argument_list|>
name|getAllMembership
parameter_list|(
specifier|final
name|AuthorizableIterator
name|membership
parameter_list|)
block|{
name|Iterator
argument_list|<
name|Iterator
argument_list|<
name|Group
argument_list|>
argument_list|>
name|inheritedMembership
init|=
operator|new
name|Iterator
argument_list|<
name|Iterator
argument_list|<
name|Group
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|membership
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Group
argument_list|>
name|next
parameter_list|()
block|{
name|Group
name|next
init|=
operator|(
name|Group
operator|)
name|membership
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
operator|new
name|IteratorChain
argument_list|(
operator|new
name|SingletonIterator
argument_list|(
name|next
argument_list|)
argument_list|,
name|inherited
argument_list|(
operator|(
name|AuthorizableImpl
operator|)
name|next
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|private
name|Iterator
argument_list|<
name|Group
argument_list|>
name|inherited
parameter_list|(
name|AuthorizableImpl
name|authorizable
parameter_list|)
block|{
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
name|getMembership
argument_list|(
name|authorizable
argument_list|,
literal|true
argument_list|)
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
literal|"Could not determine members of "
operator|+
name|authorizable
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|EmptyIterator
operator|.
name|INSTANCE
return|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|InheritingAuthorizableIterator
argument_list|(
name|inheritedMembership
argument_list|)
return|;
block|}
block|}
end_class

end_unit

