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
name|authorization
operator|.
name|permission
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
name|NoSuchElementException
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
comment|/**  * Base class for PermissionEntry iterators.  */
end_comment

begin_class
specifier|abstract
class|class
name|AbstractEntryIterator
implements|implements
name|Iterator
argument_list|<
name|PermissionEntry
argument_list|>
block|{
comment|// the ordered permission entries at a given path in the hierarchy
specifier|protected
name|Iterator
argument_list|<
name|PermissionEntry
argument_list|>
name|nextEntries
decl_stmt|;
comment|// the next permission entry
specifier|protected
name|PermissionEntry
name|next
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
name|next
operator|==
literal|null
operator|&&
name|nextEntries
operator|==
literal|null
condition|)
block|{
comment|// lazy initialization
name|nextEntries
operator|=
name|Iterators
operator|.
name|emptyIterator
argument_list|()
expr_stmt|;
name|seekNext
argument_list|()
expr_stmt|;
block|}
return|return
name|next
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|PermissionEntry
name|next
parameter_list|()
block|{
if|if
condition|(
name|next
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
name|PermissionEntry
name|pe
init|=
name|next
decl_stmt|;
name|seekNext
argument_list|()
expr_stmt|;
return|return
name|pe
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
annotation|@
name|CheckForNull
specifier|protected
specifier|abstract
name|void
name|seekNext
parameter_list|()
function_decl|;
block|}
end_class

end_unit

