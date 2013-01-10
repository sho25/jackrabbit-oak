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
comment|/**  * GroupPredicate... TODO  */
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
name|Group
name|group
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|declaredMembersOnly
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
name|group
operator|=
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
expr_stmt|;
name|this
operator|.
name|declaredMembersOnly
operator|=
name|declaredMembersOnly
expr_stmt|;
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
if|if
condition|(
name|group
operator|!=
literal|null
operator|&&
name|authorizable
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
operator|(
name|declaredMembersOnly
operator|)
condition|?
name|group
operator|.
name|isDeclaredMember
argument_list|(
name|authorizable
argument_list|)
else|:
name|group
operator|.
name|isMember
argument_list|(
name|authorizable
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
name|debug
argument_list|(
literal|"Cannot determine group membership for {}"
argument_list|,
name|authorizable
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

