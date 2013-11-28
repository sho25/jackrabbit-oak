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
name|query
operator|.
name|PropertyValues
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
name|util
operator|.
name|AbstractLazyIterator
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

begin_comment
comment|/**  * {@code MembershipProvider} implementation storing group membership information  * with the {@code Tree} associated with a given {@link org.apache.jackrabbit.api.security.user.Group}.  *  * As of Oak the {@code MembershipProvider} automatically chooses an appropriate storage structure  * depending on the number of group members. If the number of members is low they are stored as  * {@link javax.jcr.PropertyType#WEAKREFERENCE} in the {@link #REP_MEMBERS} multi value property. This is similar to  * Jackrabbit 2.x.  *  * If the number of members is high the {@code MembershipProvider} will create an intermediate node list to reduce the  * size of the multi value properties below a {@link #REP_MEMBERS_LIST} node. The provider will maintain a number of  * sub nodes of type {@link #NT_REP_MEMBER_REFERENCES} that again store the member references in a {@link #REP_MEMBERS}  * property.  *  * Note that the writing of the members is done in {@link MembershipWriter} so that the logic can be re-used by the  * migration code.  *  * The current implementation uses a fixed threshold value of {@link #getMembershipSizeThreshold()} before creating  * {@link #NT_REP_MEMBER_REFERENCES} sub nodes.  *  * Example Group with few members (irrelevant properties excluded):  *<xmp>      {          "jcr:primaryType": "rep:Group",          "rep:principalName": "contributor",          "rep:members": [              "429bbd5b-46a6-3c3d-808b-5fd4219d5c4d",              "ca58c408-fe06-357e-953c-2d23ffe1e096",              "3ebb1c04-76dd-317e-a9ee-5164182bc390",              "d3c827d3-4db2-30cc-9c41-0ed8117dbaff",              "f5777a0b-a933-3b4d-9405-613d8bc39cc7",              "fdd1547a-b19a-3154-90da-1eae8c2c3504",              "65c3084e-abfc-3719-8223-72c6cb9a3d6f"          ]      }  *</xmp>  *  * Example Group with many members (irrelevant properties excluded):  *<xmp>      {          "jcr:primaryType": "rep:Group",          "rep:principalName": "employees",          "rep:membersList": {              "jcr:primaryType": "rep:MemberReferencesList",              "0": {                  "jcr:primaryType": "rep:MemberReferences",                  "rep:members": [                      "429bbd5b-46a6-3c3d-808b-5fd4219d5c4d",                      "ca58c408-fe06-357e-953c-2d23ffe1e096",                      ...                  ]              },              ...              "341": {                  "jcr:primaryType": "rep:MemberReferences",                  "rep:members": [                      "fdd1547a-b19a-3154-90da-1eae8c2c3504",                      "65c3084e-abfc-3719-8223-72c6cb9a3d6f",                      ...                  ]              }          }      }  *</xmp>  */
end_comment

begin_class
class|class
name|MembershipProvider
extends|extends
name|AuthorizableBaseProvider
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
name|MembershipProvider
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|MembershipWriter
name|writer
init|=
operator|new
name|MembershipWriter
argument_list|()
decl_stmt|;
comment|/**      * Creates a new membership provider      * @param root the current root      * @param config the security configuration      */
name|MembershipProvider
parameter_list|(
name|Root
name|root
parameter_list|,
name|ConfigurationParameters
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|root
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the size of the membership property threshold. This is currently only useful for testing.      * @return the size of the membership property threshold.      */
name|int
name|getMembershipSizeThreshold
parameter_list|()
block|{
return|return
name|writer
operator|.
name|getMembershipSizeThreshold
argument_list|()
return|;
block|}
comment|/**      * Sets the size of the membership property threshold. This is currently only useful for testing.      * @param membershipSizeThreshold the size of the membership property threshold      */
name|void
name|setMembershipSizeThreshold
parameter_list|(
name|int
name|membershipSizeThreshold
parameter_list|)
block|{
name|writer
operator|.
name|setMembershipSizeThreshold
argument_list|(
name|membershipSizeThreshold
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns an iterator over all membership paths of the given authorizable.      *      * @param authorizableTree the authorizable tree      * @param includeInherited {@code true} to include inherited memberships      * @return an iterator over all membership paths.      */
annotation|@
name|Nonnull
name|Iterator
argument_list|<
name|String
argument_list|>
name|getMembership
parameter_list|(
name|Tree
name|authorizableTree
parameter_list|,
specifier|final
name|boolean
name|includeInherited
parameter_list|)
block|{
return|return
name|getMembership
argument_list|(
name|authorizableTree
argument_list|,
name|includeInherited
argument_list|,
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Returns an iterator over all membership paths of the given authorizable.      *      * @param authorizableTree the authorizable tree      * @param includeInherited {@code true} to include inherited memberships      * @param processedPaths helper set that contains the processed paths      * @return an iterator over all membership paths.      */
annotation|@
name|Nonnull
specifier|private
name|Iterator
argument_list|<
name|String
argument_list|>
name|getMembership
parameter_list|(
name|Tree
name|authorizableTree
parameter_list|,
specifier|final
name|boolean
name|includeInherited
parameter_list|,
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|processedPaths
parameter_list|)
block|{
specifier|final
name|Iterable
argument_list|<
name|String
argument_list|>
name|refPaths
init|=
name|identifierManager
operator|.
name|getReferences
argument_list|(
literal|true
argument_list|,
name|authorizableTree
argument_list|,
name|REP_MEMBERS
argument_list|,
name|NT_REP_MEMBER_REFERENCES
argument_list|)
decl_stmt|;
return|return
operator|new
name|AbstractLazyIterator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|String
argument_list|>
name|references
init|=
name|refPaths
operator|.
name|iterator
argument_list|()
decl_stmt|;
specifier|private
name|Iterator
argument_list|<
name|String
argument_list|>
name|parent
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|String
name|getNext
parameter_list|()
block|{
name|String
name|next
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|next
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
comment|// if we have a parent iterator, process it first
if|if
condition|(
name|parent
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|next
operator|=
name|parent
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|parent
operator|=
literal|null
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|references
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// if not, check if we have more references to process and abort if not
break|break;
block|}
else|else
block|{
comment|// get the next rep:members property path
name|String
name|propPath
init|=
name|references
operator|.
name|next
argument_list|()
decl_stmt|;
name|int
name|index
init|=
name|propPath
operator|.
name|indexOf
argument_list|(
literal|'/'
operator|+
name|REP_MEMBERS_LIST
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
name|index
operator|=
name|propPath
operator|.
name|indexOf
argument_list|(
literal|'/'
operator|+
name|REP_MEMBERS
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|index
operator|>
literal|0
condition|)
block|{
name|String
name|groupPath
init|=
name|propPath
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|processedPaths
operator|.
name|add
argument_list|(
name|groupPath
argument_list|)
condition|)
block|{
comment|// we didn't see this path before, so continue
name|next
operator|=
name|groupPath
expr_stmt|;
if|if
condition|(
name|includeInherited
condition|)
block|{
comment|// inject a parent iterator of the inherited memberships is needed
name|Tree
name|group
init|=
name|getByPath
argument_list|(
name|groupPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|UserUtil
operator|.
name|isType
argument_list|(
name|group
argument_list|,
name|AuthorizableType
operator|.
name|GROUP
argument_list|)
condition|)
block|{
name|parent
operator|=
name|getMembership
argument_list|(
name|group
argument_list|,
literal|true
argument_list|,
name|processedPaths
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Not a membership reference property "
operator|+
name|propPath
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|next
return|;
block|}
block|}
return|;
block|}
comment|/**      * Returns an iterator over all member paths of the given group.      *      * @param groupTree the group tree      * @param authorizableType type of authorizables to filter.      * @param includeInherited {@code true} to include inherited members      * @return an iterator over all member paths      */
annotation|@
name|Nonnull
name|Iterator
argument_list|<
name|String
argument_list|>
name|getMembers
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|groupTree
parameter_list|,
annotation|@
name|Nonnull
name|AuthorizableType
name|authorizableType
parameter_list|,
name|boolean
name|includeInherited
parameter_list|)
block|{
return|return
name|getMembers
argument_list|(
name|groupTree
argument_list|,
name|authorizableType
argument_list|,
name|includeInherited
argument_list|,
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Returns an iterator over all member paths of the given group.      *      * @param groupTree the group tree      * @param authorizableType type of authorizables to filter.      * @param includeInherited {@code true} to include inherited members      * @param processedRefs helper set that contains the references that are already processed.      * @return an iterator over all member paths      */
annotation|@
name|Nonnull
specifier|private
name|Iterator
argument_list|<
name|String
argument_list|>
name|getMembers
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|Tree
name|groupTree
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|AuthorizableType
name|authorizableType
parameter_list|,
specifier|final
name|boolean
name|includeInherited
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|processedRefs
parameter_list|)
block|{
return|return
operator|new
name|AbstractLazyIterator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|private
name|MemberReferenceIterator
name|references
init|=
operator|new
name|MemberReferenceIterator
argument_list|(
name|groupTree
argument_list|,
name|processedRefs
argument_list|)
decl_stmt|;
specifier|private
name|Iterator
argument_list|<
name|String
argument_list|>
name|parent
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|String
name|getNext
parameter_list|()
block|{
name|String
name|next
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|next
operator|==
literal|null
condition|)
block|{
comment|// process parent iterators first
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|parent
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|next
operator|=
name|parent
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|parent
operator|=
literal|null
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|references
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// if there are no more values left, reset the iterator
break|break;
block|}
else|else
block|{
name|String
name|value
init|=
name|references
operator|.
name|next
argument_list|()
decl_stmt|;
name|next
operator|=
name|identifierManager
operator|.
name|getPath
argument_list|(
name|PropertyValues
operator|.
name|newWeakReference
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
comment|// filter by authorizable type, and/or get inherited members
if|if
condition|(
name|next
operator|!=
literal|null
operator|&&
operator|(
name|includeInherited
operator|||
name|authorizableType
operator|!=
name|AuthorizableType
operator|.
name|AUTHORIZABLE
operator|)
condition|)
block|{
name|Tree
name|auth
init|=
name|getByPath
argument_list|(
name|next
argument_list|)
decl_stmt|;
name|AuthorizableType
name|type
init|=
name|UserUtil
operator|.
name|getType
argument_list|(
name|auth
argument_list|)
decl_stmt|;
if|if
condition|(
name|includeInherited
operator|&&
name|type
operator|==
name|AuthorizableType
operator|.
name|GROUP
condition|)
block|{
name|parent
operator|=
name|getMembers
argument_list|(
name|auth
argument_list|,
name|authorizableType
argument_list|,
literal|true
argument_list|,
name|processedRefs
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|authorizableType
operator|!=
name|AuthorizableType
operator|.
name|AUTHORIZABLE
operator|&&
name|type
operator|!=
name|authorizableType
condition|)
block|{
name|next
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|next
return|;
block|}
block|}
return|;
block|}
comment|/**      * Returns {@code true} if the given {@code groupTree} contains a member with the given {@code authorizableTree}      *      * @param groupTree  The new member to be tested for cyclic membership.      * @param authorizableTree The authorizable to check      * @param includeInherited {@code true} to also check inherited members      *      * @return true if the group has given member.      */
name|boolean
name|isMember
parameter_list|(
name|Tree
name|groupTree
parameter_list|,
name|Tree
name|authorizableTree
parameter_list|,
name|boolean
name|includeInherited
parameter_list|)
block|{
return|return
name|isMember
argument_list|(
name|groupTree
argument_list|,
name|getContentID
argument_list|(
name|authorizableTree
argument_list|)
argument_list|,
name|includeInherited
argument_list|)
return|;
block|}
comment|/**      * Returns {@code true} if the given {@code groupTree} contains a member with the given {@code contentId}      *      * @param groupTree  The new member to be tested for cyclic membership.      * @param contentId The content ID of the group.      * @param includeInherited {@code true} to also check inherited members      *      * @return true if the group has given member.      */
name|boolean
name|isMember
parameter_list|(
name|Tree
name|groupTree
parameter_list|,
name|String
name|contentId
parameter_list|,
name|boolean
name|includeInherited
parameter_list|)
block|{
if|if
condition|(
name|includeInherited
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|refs
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|getMembers
argument_list|(
name|groupTree
argument_list|,
name|AuthorizableType
operator|.
name|AUTHORIZABLE
argument_list|,
name|includeInherited
argument_list|,
name|refs
argument_list|)
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|refs
operator|.
name|contains
argument_list|(
name|contentId
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
name|MemberReferenceIterator
name|refs
init|=
operator|new
name|MemberReferenceIterator
argument_list|(
name|groupTree
argument_list|,
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|refs
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
name|contentId
operator|.
name|equals
argument_list|(
name|refs
operator|.
name|next
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
return|return
literal|false
return|;
block|}
comment|/**      * Adds a new member to the given {@code groupTree}.      * @param groupTree the group to add the member to      * @param newMemberTree the tree of the new member      * @return {@code true} if the member was added      * @throws RepositoryException if an error occurs      */
name|boolean
name|addMember
parameter_list|(
name|Tree
name|groupTree
parameter_list|,
name|Tree
name|newMemberTree
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|writer
operator|.
name|addMember
argument_list|(
name|groupTree
argument_list|,
name|getContentID
argument_list|(
name|newMemberTree
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Adds a new member to the given {@code groupTree}.      * @param groupTree the group to add the member to      * @param memberContentId the id of the new member      * @return {@code true} if the member was added      * @throws RepositoryException if an error occurs      */
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
return|return
name|writer
operator|.
name|addMember
argument_list|(
name|groupTree
argument_list|,
name|memberContentId
argument_list|)
return|;
block|}
comment|/**      * Removes the member from the given group.      *      * @param groupTree group to remove the member from      * @param memberTree member to remove      * @return {@code true} if the member was removed.      */
name|boolean
name|removeMember
parameter_list|(
name|Tree
name|groupTree
parameter_list|,
name|Tree
name|memberTree
parameter_list|)
block|{
if|if
condition|(
name|writer
operator|.
name|removeMember
argument_list|(
name|groupTree
argument_list|,
name|getContentID
argument_list|(
name|memberTree
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Authorizable {} was not member of {}"
argument_list|,
name|memberTree
operator|.
name|getName
argument_list|()
argument_list|,
name|groupTree
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
comment|/**      * Iterator that provides member references based on the rep:members properties of a underlying tree iterator.      */
specifier|private
class|class
name|MemberReferenceIterator
extends|extends
name|AbstractLazyIterator
argument_list|<
name|String
argument_list|>
block|{
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|processedRefs
decl_stmt|;
specifier|private
specifier|final
name|Iterator
argument_list|<
name|Tree
argument_list|>
name|trees
decl_stmt|;
specifier|private
name|Iterator
argument_list|<
name|String
argument_list|>
name|propertyValues
decl_stmt|;
specifier|private
name|MemberReferenceIterator
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|groupTree
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|String
argument_list|>
name|processedRefs
parameter_list|)
block|{
name|this
operator|.
name|processedRefs
operator|=
name|processedRefs
expr_stmt|;
name|this
operator|.
name|trees
operator|=
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
name|groupTree
operator|.
name|getChild
argument_list|(
name|REP_MEMBERS_LIST
argument_list|)
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getNext
parameter_list|()
block|{
name|String
name|next
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|next
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|propertyValues
operator|==
literal|null
condition|)
block|{
comment|// check if there are more trees that can provide a rep:members property
if|if
condition|(
operator|!
name|trees
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// if not, we're done
break|break;
block|}
name|PropertyState
name|property
init|=
name|trees
operator|.
name|next
argument_list|()
operator|.
name|getProperty
argument_list|(
name|REP_MEMBERS
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
block|{
name|propertyValues
operator|=
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|propertyValues
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// if there are no more values left, reset the iterator
name|propertyValues
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|String
name|value
init|=
name|propertyValues
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|processedRefs
operator|.
name|add
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|next
operator|=
name|value
expr_stmt|;
block|}
block|}
block|}
return|return
name|next
return|;
block|}
block|}
block|}
end_class

end_unit

