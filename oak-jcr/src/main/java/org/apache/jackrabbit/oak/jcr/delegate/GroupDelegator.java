begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|delegate
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
name|oak
operator|.
name|jcr
operator|.
name|session
operator|.
name|operation
operator|.
name|SessionOperation
import|;
end_import

begin_comment
comment|/**  * This implementation of {@code Group} delegates back to a  * delegatee wrapping each call into a {@link SessionOperation} closure.  *  * @see SessionDelegate#perform(SessionOperation)  */
end_comment

begin_class
specifier|final
class|class
name|GroupDelegator
extends|extends
name|AuthorizableDelegator
implements|implements
name|Group
block|{
specifier|private
name|GroupDelegator
parameter_list|(
name|SessionDelegate
name|sessionDelegate
parameter_list|,
name|Group
name|groupDelegate
parameter_list|)
block|{
name|super
argument_list|(
name|sessionDelegate
argument_list|,
name|groupDelegate
argument_list|)
expr_stmt|;
block|}
specifier|static
name|Group
name|wrap
parameter_list|(
annotation|@
name|Nonnull
name|SessionDelegate
name|sessionDelegate
parameter_list|,
name|Group
name|group
parameter_list|)
block|{
if|if
condition|(
name|group
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|new
name|GroupDelegator
argument_list|(
name|sessionDelegate
argument_list|,
name|group
argument_list|)
return|;
block|}
block|}
annotation|@
name|Nonnull
specifier|static
name|Group
name|unwrap
parameter_list|(
annotation|@
name|Nonnull
name|Group
name|group
parameter_list|)
block|{
if|if
condition|(
name|group
operator|instanceof
name|GroupDelegator
condition|)
block|{
return|return
operator|(
operator|(
name|GroupDelegator
operator|)
name|group
operator|)
operator|.
name|getDelegate
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|group
return|;
block|}
block|}
specifier|private
name|Group
name|getDelegate
parameter_list|()
block|{
return|return
operator|(
name|Group
operator|)
name|delegate
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
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
argument_list|>
argument_list|(
literal|"getDeclaredMembers"
argument_list|)
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|authorizables
init|=
name|getDelegate
argument_list|()
operator|.
name|getDeclaredMembers
argument_list|()
decl_stmt|;
return|return
name|Iterators
operator|.
name|transform
argument_list|(
name|authorizables
argument_list|,
operator|new
name|Function
argument_list|<
name|Authorizable
argument_list|,
name|Authorizable
argument_list|>
argument_list|()
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|Authorizable
name|apply
parameter_list|(
annotation|@
name|Nullable
name|Authorizable
name|authorizable
parameter_list|)
block|{
return|return
name|wrap
argument_list|(
name|sessionDelegate
argument_list|,
name|authorizable
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
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
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
argument_list|>
argument_list|(
literal|"getMembers"
argument_list|)
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|authorizables
init|=
name|getDelegate
argument_list|()
operator|.
name|getMembers
argument_list|()
decl_stmt|;
return|return
name|Iterators
operator|.
name|transform
argument_list|(
name|authorizables
argument_list|,
operator|new
name|Function
argument_list|<
name|Authorizable
argument_list|,
name|Authorizable
argument_list|>
argument_list|()
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|Authorizable
name|apply
parameter_list|(
annotation|@
name|Nullable
name|Authorizable
name|authorizable
parameter_list|)
block|{
return|return
name|wrap
argument_list|(
name|sessionDelegate
argument_list|,
name|authorizable
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDeclaredMember
parameter_list|(
specifier|final
name|Authorizable
name|authorizable
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Boolean
argument_list|>
argument_list|(
literal|"isDeclaredMember"
argument_list|)
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Boolean
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getDelegate
argument_list|()
operator|.
name|isDeclaredMember
argument_list|(
name|unwrap
argument_list|(
name|authorizable
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMember
parameter_list|(
specifier|final
name|Authorizable
name|authorizable
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Boolean
argument_list|>
argument_list|(
literal|"isMember"
argument_list|)
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Boolean
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getDelegate
argument_list|()
operator|.
name|isMember
argument_list|(
name|unwrap
argument_list|(
name|authorizable
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|addMember
parameter_list|(
specifier|final
name|Authorizable
name|authorizable
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Boolean
argument_list|>
argument_list|(
literal|"addMember"
argument_list|)
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Boolean
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getDelegate
argument_list|()
operator|.
name|addMember
argument_list|(
name|unwrap
argument_list|(
name|authorizable
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|removeMember
parameter_list|(
specifier|final
name|Authorizable
name|authorizable
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Boolean
argument_list|>
argument_list|(
literal|"removeMember"
argument_list|)
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Boolean
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getDelegate
argument_list|()
operator|.
name|removeMember
argument_list|(
name|unwrap
argument_list|(
name|authorizable
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
end_class

end_unit

