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
name|plugins
operator|.
name|memory
operator|.
name|PropertyBuilder
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
import|import static
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
operator|.
name|NAME
import|;
end_import

begin_comment
comment|/**  * @see MembershipProvider to more details.  */
end_comment

begin_class
specifier|public
class|class
name|MembershipWriter
block|{
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MEMBERSHIP_THRESHOLD
init|=
literal|100
decl_stmt|;
comment|/**      * size of the membership threshold after which a new overflow node is created.      */
specifier|private
name|int
name|membershipSizeThreshold
init|=
name|DEFAULT_MEMBERSHIP_THRESHOLD
decl_stmt|;
name|void
name|setMembershipSizeThreshold
parameter_list|(
name|int
name|membershipSizeThreshold
parameter_list|)
block|{
name|this
operator|.
name|membershipSizeThreshold
operator|=
name|membershipSizeThreshold
expr_stmt|;
block|}
comment|/**      * Adds a new member to the given {@code groupTree}.      *      * @param groupTree the group to add the member to      * @param memberContentId the id of the new member      * @return {@code true} if the member was added      */
name|boolean
name|addMember
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|groupTree
parameter_list|,
annotation|@
name|NotNull
name|String
name|memberContentId
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|m
init|=
name|Maps
operator|.
name|newHashMapWithExpectedSize
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
name|memberContentId
argument_list|,
literal|"-"
argument_list|)
expr_stmt|;
return|return
name|addMembers
argument_list|(
name|groupTree
argument_list|,
name|m
argument_list|)
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/**      * Adds a new member to the given {@code groupTree}.      *      * @param groupTree the group to add the member to      * @param memberIds the ids of the new members as map of 'contentId':'memberId'      * @return the set of member IDs that was not successfully processed.      */
annotation|@
name|NotNull
name|Set
argument_list|<
name|String
argument_list|>
name|addMembers
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|groupTree
parameter_list|,
annotation|@
name|NotNull
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|memberIds
parameter_list|)
block|{
comment|// check all possible rep:members properties for the new member and also find the one with the least values
name|Tree
name|membersList
init|=
name|groupTree
operator|.
name|getChild
argument_list|(
name|UserConstants
operator|.
name|REP_MEMBERS_LIST
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Tree
argument_list|>
name|trees
init|=
name|Iterators
operator|.
name|concat
argument_list|(
name|Iterators
operator|.
name|singletonIterator
argument_list|(
name|groupTree
argument_list|)
argument_list|,
name|membersList
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|failed
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|memberIds
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|bestCount
init|=
name|membershipSizeThreshold
decl_stmt|;
name|PropertyState
name|bestProperty
init|=
literal|null
decl_stmt|;
name|Tree
name|bestTree
init|=
literal|null
decl_stmt|;
comment|// remove existing memberIds from the map and find best-matching tree
comment|// for the insertion of the new members.
while|while
condition|(
name|trees
operator|.
name|hasNext
argument_list|()
operator|&&
operator|!
name|memberIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Tree
name|t
init|=
name|trees
operator|.
name|next
argument_list|()
decl_stmt|;
name|PropertyState
name|refs
init|=
name|t
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_MEMBERS
argument_list|)
decl_stmt|;
if|if
condition|(
name|refs
operator|!=
literal|null
condition|)
block|{
name|int
name|numRefs
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|ref
range|:
name|refs
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|WEAKREFERENCES
argument_list|)
control|)
block|{
name|String
name|id
init|=
name|memberIds
operator|.
name|remove
argument_list|(
name|ref
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|failed
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|memberIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
break|break;
block|}
block|}
name|numRefs
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|numRefs
operator|<
name|bestCount
condition|)
block|{
name|bestCount
operator|=
name|numRefs
expr_stmt|;
name|bestProperty
operator|=
name|refs
expr_stmt|;
name|bestTree
operator|=
name|t
expr_stmt|;
block|}
block|}
block|}
comment|// update member content structure by starting inserting new member IDs
comment|// with the best-matching property and create new member-ref-nodes as needed.
if|if
condition|(
operator|!
name|memberIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|PropertyBuilder
argument_list|<
name|String
argument_list|>
name|propertyBuilder
decl_stmt|;
name|int
name|propCnt
decl_stmt|;
if|if
condition|(
name|bestProperty
operator|==
literal|null
condition|)
block|{
comment|// we don't have a good candidate to store the new members.
comment|// so there are no members at all or all are full
if|if
condition|(
operator|!
name|groupTree
operator|.
name|hasProperty
argument_list|(
name|UserConstants
operator|.
name|REP_MEMBERS
argument_list|)
condition|)
block|{
name|bestTree
operator|=
name|groupTree
expr_stmt|;
block|}
else|else
block|{
name|bestTree
operator|=
name|createMemberRefTree
argument_list|(
name|groupTree
argument_list|,
name|membersList
argument_list|)
expr_stmt|;
block|}
name|propertyBuilder
operator|=
name|PropertyBuilder
operator|.
name|array
argument_list|(
name|Type
operator|.
name|WEAKREFERENCE
argument_list|,
name|UserConstants
operator|.
name|REP_MEMBERS
argument_list|)
expr_stmt|;
name|propCnt
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|propertyBuilder
operator|=
name|PropertyBuilder
operator|.
name|copy
argument_list|(
name|Type
operator|.
name|WEAKREFERENCE
argument_list|,
name|bestProperty
argument_list|)
expr_stmt|;
name|propCnt
operator|=
name|bestCount
expr_stmt|;
block|}
comment|// if adding all new members to best-property would exceed the threshold
comment|// the new ids need to be distributed to different member-ref-nodes
comment|// for simplicity this is achieved by introducing new tree(s)
if|if
condition|(
operator|(
name|propCnt
operator|+
name|memberIds
operator|.
name|size
argument_list|()
operator|)
operator|>
name|membershipSizeThreshold
condition|)
block|{
while|while
condition|(
operator|!
name|memberIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|s
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|memberIds
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|propCnt
operator|<
name|membershipSizeThreshold
operator|&&
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|s
operator|.
name|add
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
name|propCnt
operator|++
expr_stmt|;
block|}
name|propertyBuilder
operator|.
name|addValues
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|bestTree
operator|.
name|setProperty
argument_list|(
name|propertyBuilder
operator|.
name|getPropertyState
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// continue filling the next (new) node + propertyBuilder pair
name|propCnt
operator|=
literal|0
expr_stmt|;
name|bestTree
operator|=
name|createMemberRefTree
argument_list|(
name|groupTree
argument_list|,
name|membersList
argument_list|)
expr_stmt|;
name|propertyBuilder
operator|=
name|PropertyBuilder
operator|.
name|array
argument_list|(
name|Type
operator|.
name|WEAKREFERENCE
argument_list|,
name|UserConstants
operator|.
name|REP_MEMBERS
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|propertyBuilder
operator|.
name|addValues
argument_list|(
name|memberIds
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|bestTree
operator|.
name|setProperty
argument_list|(
name|propertyBuilder
operator|.
name|getPropertyState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|failed
return|;
block|}
annotation|@
name|NotNull
specifier|private
specifier|static
name|Tree
name|createMemberRefTree
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|groupTree
parameter_list|,
annotation|@
name|NotNull
name|Tree
name|membersList
parameter_list|)
block|{
if|if
condition|(
operator|!
name|membersList
operator|.
name|exists
argument_list|()
condition|)
block|{
name|membersList
operator|=
name|groupTree
operator|.
name|addChild
argument_list|(
name|UserConstants
operator|.
name|REP_MEMBERS_LIST
argument_list|)
expr_stmt|;
name|membersList
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|UserConstants
operator|.
name|NT_REP_MEMBER_REFERENCES_LIST
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
block|}
name|Tree
name|refTree
init|=
name|membersList
operator|.
name|addChild
argument_list|(
name|nextRefNodeName
argument_list|(
name|membersList
argument_list|)
argument_list|)
decl_stmt|;
name|refTree
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|UserConstants
operator|.
name|NT_REP_MEMBER_REFERENCES
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
return|return
name|refTree
return|;
block|}
annotation|@
name|NotNull
specifier|private
specifier|static
name|String
name|nextRefNodeName
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|membersList
parameter_list|)
block|{
comment|// keep node names linear
name|int
name|i
init|=
literal|0
decl_stmt|;
name|String
name|name
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
decl_stmt|;
while|while
condition|(
name|membersList
operator|.
name|hasChild
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|name
operator|=
name|String
operator|.
name|valueOf
argument_list|(
operator|++
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|name
return|;
block|}
comment|/**      * Removes the member from the given group.      *      * @param groupTree group to remove the member from      * @param memberContentId member to remove      * @return {@code true} if the member was removed.      */
name|boolean
name|removeMember
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|groupTree
parameter_list|,
annotation|@
name|NotNull
name|String
name|memberContentId
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|m
init|=
name|Maps
operator|.
name|newHashMapWithExpectedSize
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
name|memberContentId
argument_list|,
literal|"-"
argument_list|)
expr_stmt|;
return|return
name|removeMembers
argument_list|(
name|groupTree
argument_list|,
name|m
argument_list|)
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/**      * Removes the members from the given group.      *      * @param groupTree group to remove the member from      * @param memberIds Map of 'contentId':'memberId' of all members that need to be removed.      * @return the set of member IDs that was not successfully processed.      */
annotation|@
name|NotNull
name|Set
argument_list|<
name|String
argument_list|>
name|removeMembers
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|groupTree
parameter_list|,
annotation|@
name|NotNull
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|memberIds
parameter_list|)
block|{
name|Tree
name|membersList
init|=
name|groupTree
operator|.
name|getChild
argument_list|(
name|UserConstants
operator|.
name|REP_MEMBERS_LIST
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Tree
argument_list|>
name|trees
init|=
name|Iterators
operator|.
name|concat
argument_list|(
name|Iterators
operator|.
name|singletonIterator
argument_list|(
name|groupTree
argument_list|)
argument_list|,
name|membersList
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|trees
operator|.
name|hasNext
argument_list|()
operator|&&
operator|!
name|memberIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Tree
name|t
init|=
name|trees
operator|.
name|next
argument_list|()
decl_stmt|;
name|PropertyState
name|refs
init|=
name|t
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_MEMBERS
argument_list|)
decl_stmt|;
if|if
condition|(
name|refs
operator|!=
literal|null
condition|)
block|{
name|PropertyBuilder
argument_list|<
name|String
argument_list|>
name|prop
init|=
name|PropertyBuilder
operator|.
name|copy
argument_list|(
name|Type
operator|.
name|WEAKREFERENCE
argument_list|,
name|refs
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|it
init|=
name|memberIds
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
operator|&&
operator|!
name|prop
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|memberContentId
init|=
name|it
operator|.
name|next
argument_list|()
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|prop
operator|.
name|hasValue
argument_list|(
name|memberContentId
argument_list|)
condition|)
block|{
name|prop
operator|.
name|removeValue
argument_list|(
name|memberContentId
argument_list|)
expr_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|prop
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|t
operator|==
name|groupTree
condition|)
block|{
name|t
operator|.
name|removeProperty
argument_list|(
name|UserConstants
operator|.
name|REP_MEMBERS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|t
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|t
operator|.
name|setProperty
argument_list|(
name|prop
operator|.
name|getPropertyState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|Sets
operator|.
name|newHashSet
argument_list|(
name|memberIds
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

