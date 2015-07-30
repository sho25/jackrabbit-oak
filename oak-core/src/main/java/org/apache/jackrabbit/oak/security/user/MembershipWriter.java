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
name|Iterator
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
name|plugins
operator|.
name|memory
operator|.
name|PropertyBuilder
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
comment|/**      * size of the membership threshold after which a new overflow node is created.      */
specifier|private
name|int
name|membershipSizeThreshold
init|=
literal|100
decl_stmt|;
specifier|public
name|int
name|getMembershipSizeThreshold
parameter_list|()
block|{
return|return
name|membershipSizeThreshold
return|;
block|}
specifier|public
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
comment|/**      * Adds a new member to the given {@code groupTree}.      *      * @param groupTree the group to add the member to      * @param memberContentId the id of the new member      * @return {@code true} if the member was added      * @throws RepositoryException if an error occurs      */
name|boolean
name|addMember
parameter_list|(
name|Tree
name|groupTree
parameter_list|,
name|String
name|memberContentId
parameter_list|)
throws|throws
name|RepositoryException
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
while|while
condition|(
name|trees
operator|.
name|hasNext
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
if|if
condition|(
name|ref
operator|.
name|equals
argument_list|(
name|memberContentId
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
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
name|PropertyBuilder
argument_list|<
name|String
argument_list|>
name|propertyBuilder
decl_stmt|;
if|if
condition|(
name|bestProperty
operator|==
literal|null
condition|)
block|{
comment|// we don't have a good candidate to store the new member.
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
name|bestTree
operator|=
name|membersList
operator|.
name|addChild
argument_list|(
literal|"0"
argument_list|)
expr_stmt|;
block|}
else|else
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
name|bestTree
operator|=
name|membersList
operator|.
name|addChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|bestTree
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
block|}
name|propertyBuilder
operator|.
name|addValue
argument_list|(
name|memberContentId
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
return|return
literal|true
return|;
block|}
comment|/**      * Removes the member from the given group.      *      * @param groupTree group to remove the member from      * @param memberContentId member to remove      * @return {@code true} if the member was removed.      */
name|boolean
name|removeMember
parameter_list|(
name|Tree
name|groupTree
parameter_list|,
name|String
name|memberContentId
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
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Sets the given set of members to the specified group. this method is only used by the migration code.      *      * @param group node builder of group      * @param members set of content ids to set      */
specifier|public
name|void
name|setMembers
parameter_list|(
name|NodeBuilder
name|group
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|members
parameter_list|)
block|{
name|group
operator|.
name|removeProperty
argument_list|(
name|UserConstants
operator|.
name|REP_MEMBERS
argument_list|)
expr_stmt|;
if|if
condition|(
name|group
operator|.
name|hasChildNode
argument_list|(
name|UserConstants
operator|.
name|REP_MEMBERS
argument_list|)
condition|)
block|{
name|group
operator|.
name|getChildNode
argument_list|(
name|UserConstants
operator|.
name|REP_MEMBERS
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|PropertyBuilder
argument_list|<
name|String
argument_list|>
name|prop
init|=
literal|null
decl_stmt|;
name|NodeBuilder
name|refList
init|=
literal|null
decl_stmt|;
name|NodeBuilder
name|node
init|=
name|group
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|int
name|numNodes
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|ref
range|:
name|members
control|)
block|{
if|if
condition|(
name|prop
operator|==
literal|null
condition|)
block|{
name|prop
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
name|prop
operator|.
name|addValue
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|count
operator|>
name|membershipSizeThreshold
condition|)
block|{
name|node
operator|.
name|setProperty
argument_list|(
name|prop
operator|.
name|getPropertyState
argument_list|()
argument_list|)
expr_stmt|;
name|prop
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|refList
operator|==
literal|null
condition|)
block|{
comment|// create intermediate structure
name|refList
operator|=
name|group
operator|.
name|child
argument_list|(
name|UserConstants
operator|.
name|REP_MEMBERS_LIST
argument_list|)
expr_stmt|;
name|refList
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
name|node
operator|=
name|refList
operator|.
name|child
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|numNodes
operator|++
argument_list|)
argument_list|)
expr_stmt|;
name|node
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
block|}
block|}
if|if
condition|(
name|prop
operator|!=
literal|null
condition|)
block|{
name|node
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
end_class

end_unit

