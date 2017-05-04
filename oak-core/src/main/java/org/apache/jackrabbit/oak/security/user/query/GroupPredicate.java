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
operator|.
name|query
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
name|CheckForNull
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
comment|/**  * Predicate used to filter authorizables based on their group membership.  */
end_comment

begin_class
class|class
name|GroupPredicate
implements|implements
name|Predicate
argument_list|<
name|Authorizable
argument_list|>
block|{
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|GroupPredicate
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|membersIterator
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|memberIds
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|GroupPredicate
parameter_list|(
name|UserManager
name|userManager
parameter_list|,
name|String
name|groupId
parameter_list|,
name|boolean
name|declaredMembersOnly
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Authorizable
name|authorizable
init|=
name|userManager
operator|.
name|getAuthorizable
argument_list|(
name|groupId
argument_list|)
decl_stmt|;
name|Group
name|group
init|=
operator|(
name|authorizable
operator|==
literal|null
operator|||
operator|!
name|authorizable
operator|.
name|isGroup
argument_list|()
operator|)
condition|?
literal|null
else|:
operator|(
name|Group
operator|)
name|authorizable
decl_stmt|;
if|if
condition|(
name|group
operator|!=
literal|null
condition|)
block|{
name|membersIterator
operator|=
operator|(
name|declaredMembersOnly
operator|)
condition|?
name|group
operator|.
name|getDeclaredMembers
argument_list|()
else|:
name|group
operator|.
name|getMembers
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|membersIterator
operator|=
name|Iterators
operator|.
name|emptyIterator
argument_list|()
expr_stmt|;
block|}
block|}
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
name|String
name|id
init|=
name|saveGetId
argument_list|(
name|authorizable
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|memberIds
operator|.
name|contains
argument_list|(
name|id
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
comment|// not contained in ids that have already been processed => look
comment|// for occurrence in the remaining iterator entries.
while|while
condition|(
name|membersIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|memberId
init|=
name|saveGetId
argument_list|(
name|membersIterator
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|memberId
operator|!=
literal|null
condition|)
block|{
name|memberIds
operator|.
name|add
argument_list|(
name|memberId
argument_list|)
expr_stmt|;
if|if
condition|(
name|memberId
operator|.
name|equals
argument_list|(
name|id
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
return|return
literal|false
return|;
block|}
annotation|@
name|CheckForNull
specifier|private
name|String
name|saveGetId
parameter_list|(
annotation|@
name|CheckForNull
name|Authorizable
name|authorizable
parameter_list|)
block|{
if|if
condition|(
name|authorizable
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
name|authorizable
operator|.
name|getID
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
name|debug
argument_list|(
literal|"Error while retrieving ID for authorizable {}"
argument_list|,
name|authorizable
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

