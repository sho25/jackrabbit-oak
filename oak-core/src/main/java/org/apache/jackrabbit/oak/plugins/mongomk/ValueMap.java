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
name|plugins
operator|.
name|mongomk
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractSet
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
comment|/**  * A value map contains the versioned values of a property. The key into this  * map is the revision when the value was set.  */
end_comment

begin_class
class|class
name|ValueMap
block|{
annotation|@
name|Nonnull
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|create
parameter_list|(
specifier|final
annotation|@
name|Nonnull
name|NodeDocument
name|doc
parameter_list|,
specifier|final
annotation|@
name|Nonnull
name|String
name|property
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
name|doc
operator|.
name|getLocalMap
argument_list|(
name|property
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|.
name|getPreviousRanges
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|map
return|;
block|}
specifier|final
name|Set
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
name|values
init|=
operator|new
name|AbstractSet
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
argument_list|()
block|{
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
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
name|iterator
parameter_list|()
block|{
return|return
name|Iterators
operator|.
name|concat
argument_list|(
name|map
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|,
name|Iterators
operator|.
name|concat
argument_list|(
operator|new
name|Iterator
argument_list|<
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
argument_list|>
argument_list|()
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|NodeDocument
argument_list|>
name|previous
init|=
name|doc
operator|.
name|getPreviousDocs
argument_list|(
literal|null
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|previous
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
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|next
parameter_list|()
block|{
return|return
name|previous
operator|.
name|next
argument_list|()
operator|.
name|getLocalMap
argument_list|(
name|property
argument_list|)
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
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
block|}
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
name|int
name|size
init|=
name|map
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeDocument
name|prev
range|:
name|doc
operator|.
name|getPreviousDocs
argument_list|(
literal|null
argument_list|)
control|)
block|{
name|size
operator|+=
name|prev
operator|.
name|getLocalMap
argument_list|(
name|property
argument_list|)
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|AbstractMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
name|doc
operator|.
name|getLocalMap
argument_list|(
name|property
argument_list|)
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Set
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|entrySet
parameter_list|()
block|{
return|return
name|values
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
comment|// first check values map of this document
name|String
name|value
init|=
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
return|return
name|value
return|;
block|}
name|Revision
name|r
init|=
name|Revision
operator|.
name|fromString
argument_list|(
name|key
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|NodeDocument
name|prev
range|:
name|doc
operator|.
name|getPreviousDocs
argument_list|(
name|r
argument_list|)
control|)
block|{
name|value
operator|=
name|prev
operator|.
name|getLocalMap
argument_list|(
name|property
argument_list|)
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
return|return
name|value
return|;
block|}
block|}
comment|// not found
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
comment|// can use get()
comment|// the values map does not have null values
return|return
name|get
argument_list|(
name|key
argument_list|)
operator|!=
literal|null
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

