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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
import|;
end_import

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
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|User
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

begin_comment
comment|/**  * Base class for {@link GroupDelegator} and {@link UserDelegator}.  */
end_comment

begin_class
specifier|abstract
class|class
name|AuthorizableDelegator
implements|implements
name|Authorizable
block|{
specifier|final
name|SessionDelegate
name|sessionDelegate
decl_stmt|;
specifier|final
name|Authorizable
name|delegate
decl_stmt|;
name|AuthorizableDelegator
parameter_list|(
annotation|@
name|NotNull
name|SessionDelegate
name|sessionDelegate
parameter_list|,
annotation|@
name|NotNull
name|Authorizable
name|delegate
parameter_list|)
block|{
name|checkArgument
argument_list|(
operator|!
operator|(
name|delegate
operator|instanceof
name|AuthorizableDelegator
operator|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|sessionDelegate
operator|=
name|sessionDelegate
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
specifier|static
name|Authorizable
name|wrap
parameter_list|(
annotation|@
name|NotNull
name|SessionDelegate
name|sessionDelegate
parameter_list|,
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
literal|null
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
return|return
name|GroupDelegator
operator|.
name|wrap
argument_list|(
name|sessionDelegate
argument_list|,
operator|(
name|Group
operator|)
name|authorizable
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|UserDelegator
operator|.
name|wrap
argument_list|(
name|sessionDelegate
argument_list|,
operator|(
name|User
operator|)
name|authorizable
argument_list|)
return|;
block|}
block|}
specifier|static
name|Authorizable
name|unwrap
parameter_list|(
annotation|@
name|NotNull
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
return|return
name|GroupDelegator
operator|.
name|unwrap
argument_list|(
operator|(
name|Group
operator|)
name|authorizable
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|UserDelegator
operator|.
name|unwrap
argument_list|(
operator|(
name|User
operator|)
name|authorizable
argument_list|)
return|;
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
name|sessionDelegate
operator|.
name|safePerform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Boolean
argument_list|>
argument_list|(
literal|"isGroup"
argument_list|)
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Boolean
name|perform
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|isGroup
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getID
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
name|String
argument_list|>
argument_list|(
literal|"getID"
argument_list|)
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|String
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|delegate
operator|.
name|getID
argument_list|()
return|;
block|}
block|}
argument_list|)
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
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Principal
argument_list|>
argument_list|(
literal|"getPrincipal"
argument_list|)
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Principal
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|delegate
operator|.
name|getPrincipal
argument_list|()
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
name|Group
argument_list|>
name|declaredMemberOf
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
name|Group
argument_list|>
argument_list|>
argument_list|(
literal|"declaredMemberOf"
argument_list|)
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Group
argument_list|>
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Iterator
argument_list|<
name|Group
argument_list|>
name|groups
init|=
name|delegate
operator|.
name|declaredMemberOf
argument_list|()
decl_stmt|;
return|return
name|Iterators
operator|.
name|transform
argument_list|(
name|groups
argument_list|,
operator|new
name|Function
argument_list|<
name|Group
argument_list|,
name|Group
argument_list|>
argument_list|()
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|Group
name|apply
parameter_list|(
annotation|@
name|Nullable
name|Group
name|group
parameter_list|)
block|{
return|return
name|GroupDelegator
operator|.
name|wrap
argument_list|(
name|sessionDelegate
argument_list|,
name|group
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
name|Group
argument_list|>
name|memberOf
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
name|Group
argument_list|>
argument_list|>
argument_list|(
literal|"memberOf"
argument_list|)
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Group
argument_list|>
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Iterator
argument_list|<
name|Group
argument_list|>
name|groups
init|=
name|delegate
operator|.
name|memberOf
argument_list|()
decl_stmt|;
return|return
name|Iterators
operator|.
name|transform
argument_list|(
name|groups
argument_list|,
operator|new
name|Function
argument_list|<
name|Group
argument_list|,
name|Group
argument_list|>
argument_list|()
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|Group
name|apply
parameter_list|(
annotation|@
name|Nullable
name|Group
name|group
parameter_list|)
block|{
return|return
name|GroupDelegator
operator|.
name|wrap
argument_list|(
name|sessionDelegate
argument_list|,
name|group
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
name|void
name|remove
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|sessionDelegate
operator|.
name|performVoid
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Void
argument_list|>
argument_list|(
literal|"remove"
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|performVoid
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|delegate
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|getPropertyNames
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
name|String
argument_list|>
argument_list|>
argument_list|(
literal|"getPropertyNames"
argument_list|)
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|delegate
operator|.
name|getPropertyNames
argument_list|()
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
name|String
argument_list|>
name|getPropertyNames
parameter_list|(
specifier|final
name|String
name|relPath
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
name|Iterator
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|(
literal|"getPropertyNames"
argument_list|)
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|delegate
operator|.
name|getPropertyNames
argument_list|(
name|relPath
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
name|hasProperty
parameter_list|(
specifier|final
name|String
name|relPath
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
literal|"hasProperty"
argument_list|)
block|{
annotation|@
name|NotNull
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
name|delegate
operator|.
name|hasProperty
argument_list|(
name|relPath
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
name|void
name|setProperty
parameter_list|(
specifier|final
name|String
name|relPath
parameter_list|,
specifier|final
name|Value
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|sessionDelegate
operator|.
name|performVoid
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Void
argument_list|>
argument_list|(
literal|"setProperty"
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|performVoid
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|delegate
operator|.
name|setProperty
argument_list|(
name|relPath
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setProperty
parameter_list|(
specifier|final
name|String
name|relPath
parameter_list|,
specifier|final
name|Value
index|[]
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|sessionDelegate
operator|.
name|performVoid
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Void
argument_list|>
argument_list|(
literal|"setProperty"
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|performVoid
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|delegate
operator|.
name|setProperty
argument_list|(
name|relPath
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Value
index|[]
name|getProperty
parameter_list|(
specifier|final
name|String
name|relPath
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
name|Value
index|[]
argument_list|>
argument_list|(
literal|"getProperty"
argument_list|)
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Value
index|[]
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|delegate
operator|.
name|getProperty
argument_list|(
name|relPath
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
name|removeProperty
parameter_list|(
specifier|final
name|String
name|relPath
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
literal|"removeProperty"
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|NotNull
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
name|delegate
operator|.
name|removeProperty
argument_list|(
name|relPath
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
name|String
name|getPath
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
name|String
argument_list|>
argument_list|(
literal|"getPath"
argument_list|)
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|String
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|delegate
operator|.
name|getPath
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
comment|//-------------------------------------------------------------< Object>---
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|other
operator|instanceof
name|AuthorizableDelegator
condition|)
block|{
name|AuthorizableDelegator
name|ad
init|=
operator|(
name|AuthorizableDelegator
operator|)
name|other
decl_stmt|;
return|return
name|delegate
operator|.
name|equals
argument_list|(
name|ad
operator|.
name|delegate
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

