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
name|javax
operator|.
name|jcr
operator|.
name|RangeIterator
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
comment|/**  * AuthorizableIterator...  */
end_comment

begin_class
specifier|final
class|class
name|AuthorizableIterator
implements|implements
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
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
name|AuthorizableIterator
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
name|authorizables
decl_stmt|;
specifier|private
specifier|final
name|long
name|size
decl_stmt|;
specifier|static
name|AuthorizableIterator
name|create
parameter_list|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|authorizableOakPaths
parameter_list|,
name|UserManagerImpl
name|userManager
parameter_list|,
name|AuthorizableType
name|authorizableType
parameter_list|)
block|{
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|it
init|=
name|Iterators
operator|.
name|transform
argument_list|(
name|authorizableOakPaths
argument_list|,
operator|new
name|PathToAuthorizable
argument_list|(
name|userManager
argument_list|,
name|authorizableType
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|size
init|=
name|getSize
argument_list|(
name|authorizableOakPaths
argument_list|)
decl_stmt|;
return|return
operator|new
name|AuthorizableIterator
argument_list|(
name|Iterators
operator|.
name|filter
argument_list|(
name|it
argument_list|,
name|Predicates
operator|.
name|notNull
argument_list|()
argument_list|)
argument_list|,
name|size
argument_list|)
return|;
block|}
specifier|private
name|AuthorizableIterator
parameter_list|(
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|authorizables
parameter_list|,
name|long
name|size
parameter_list|)
block|{
name|this
operator|.
name|authorizables
operator|=
name|authorizables
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
comment|//-----------------------------------------------------------< Iterator>---
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
name|Authorizable
name|next
parameter_list|()
block|{
return|return
name|authorizables
operator|.
name|next
argument_list|()
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
comment|//--------------------------------------------------------------------------
name|long
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|//--------------------------------------------------------------------------
specifier|private
specifier|static
name|long
name|getSize
parameter_list|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
parameter_list|)
block|{
if|if
condition|(
name|it
operator|instanceof
name|RangeIterator
condition|)
block|{
return|return
operator|(
operator|(
name|RangeIterator
operator|)
name|it
operator|)
operator|.
name|getSize
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|PathToAuthorizable
implements|implements
name|Function
argument_list|<
name|String
argument_list|,
name|Authorizable
argument_list|>
block|{
specifier|private
specifier|final
name|UserManagerImpl
name|userManager
decl_stmt|;
specifier|private
specifier|final
name|Predicate
argument_list|<
name|Authorizable
argument_list|>
name|predicate
decl_stmt|;
name|PathToAuthorizable
parameter_list|(
name|UserManagerImpl
name|userManager
parameter_list|,
name|AuthorizableType
name|type
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
name|predicate
operator|=
operator|new
name|AuthorizableTypePredicate
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Authorizable
name|apply
parameter_list|(
name|String
name|oakPath
parameter_list|)
block|{
try|try
block|{
name|Authorizable
name|a
init|=
name|userManager
operator|.
name|getAuthorizableByOakPath
argument_list|(
name|oakPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|predicate
operator|.
name|apply
argument_list|(
name|a
argument_list|)
condition|)
block|{
return|return
name|a
return|;
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
name|debug
argument_list|(
literal|"Failed to access authorizable "
operator|+
name|oakPath
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|AuthorizableTypePredicate
implements|implements
name|Predicate
argument_list|<
name|Authorizable
argument_list|>
block|{
specifier|private
specifier|final
name|AuthorizableType
name|authorizableType
decl_stmt|;
name|AuthorizableTypePredicate
parameter_list|(
name|AuthorizableType
name|authorizableType
parameter_list|)
block|{
name|this
operator|.
name|authorizableType
operator|=
name|authorizableType
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|Authorizable
name|authorizable
parameter_list|)
block|{
return|return
name|authorizableType
operator|.
name|isType
argument_list|(
name|authorizable
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

