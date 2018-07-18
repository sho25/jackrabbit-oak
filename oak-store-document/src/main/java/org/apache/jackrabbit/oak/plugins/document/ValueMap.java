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
name|document
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|List
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
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|document
operator|.
name|util
operator|.
name|MergeSortedIterators
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
name|base
operator|.
name|Objects
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
specifier|static
specifier|final
name|SortedMap
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
name|EMPTY
init|=
name|Collections
operator|.
name|unmodifiableSortedMap
argument_list|(
operator|new
name|TreeMap
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
argument_list|(
name|StableRevisionComparator
operator|.
name|REVERSE
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|NotNull
specifier|static
name|Map
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
name|create
parameter_list|(
annotation|@
name|NotNull
specifier|final
name|NodeDocument
name|doc
parameter_list|,
annotation|@
name|NotNull
specifier|final
name|String
name|property
parameter_list|)
block|{
specifier|final
name|SortedMap
argument_list|<
name|Revision
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
name|Revision
argument_list|,
name|String
argument_list|>
argument_list|>
name|entrySet
init|=
operator|new
name|AbstractSet
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
argument_list|>
name|iterator
parameter_list|()
block|{
specifier|final
name|Comparator
argument_list|<
name|?
super|super
name|Revision
argument_list|>
name|c
init|=
name|map
operator|.
name|comparator
argument_list|()
decl_stmt|;
specifier|final
name|Iterator
argument_list|<
name|NodeDocument
argument_list|>
name|docs
decl_stmt|;
if|if
condition|(
name|map
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|docs
operator|=
name|doc
operator|.
name|getPreviousDocs
argument_list|(
name|property
argument_list|,
literal|null
argument_list|)
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// merge sort local map into maps of previous documents
name|List
argument_list|<
name|Iterator
argument_list|<
name|NodeDocument
argument_list|>
argument_list|>
name|iterators
init|=
operator|new
name|ArrayList
argument_list|<
name|Iterator
argument_list|<
name|NodeDocument
argument_list|>
argument_list|>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|iterators
operator|.
name|add
argument_list|(
name|Iterators
operator|.
name|singletonIterator
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|iterators
operator|.
name|add
argument_list|(
name|doc
operator|.
name|getPreviousDocs
argument_list|(
name|property
argument_list|,
literal|null
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|docs
operator|=
name|Iterators
operator|.
name|mergeSorted
argument_list|(
name|iterators
argument_list|,
operator|new
name|Comparator
argument_list|<
name|NodeDocument
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|NodeDocument
name|o1
parameter_list|,
name|NodeDocument
name|o2
parameter_list|)
block|{
name|Revision
name|r1
init|=
name|getFirstRevision
argument_list|(
name|o1
argument_list|)
decl_stmt|;
name|Revision
name|r2
init|=
name|getFirstRevision
argument_list|(
name|o2
argument_list|)
decl_stmt|;
return|return
name|c
operator|.
name|compare
argument_list|(
name|r1
argument_list|,
name|r2
argument_list|)
return|;
block|}
specifier|private
name|Revision
name|getFirstRevision
parameter_list|(
name|NodeDocument
name|d
parameter_list|)
block|{
name|Map
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
name|values
decl_stmt|;
if|if
condition|(
name|Objects
operator|.
name|equal
argument_list|(
name|d
operator|.
name|getId
argument_list|()
argument_list|,
name|doc
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
comment|// return local map for main document
name|values
operator|=
name|d
operator|.
name|getLocalMap
argument_list|(
name|property
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|values
operator|=
name|d
operator|.
name|getValueMap
argument_list|(
name|property
argument_list|)
expr_stmt|;
block|}
return|return
name|values
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|MergeSortedIterators
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|(
operator|new
name|Comparator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Map
operator|.
name|Entry
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
name|o1
parameter_list|,
name|Map
operator|.
name|Entry
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
name|o2
parameter_list|)
block|{
return|return
name|c
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|getKey
argument_list|()
argument_list|,
name|o2
operator|.
name|getKey
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
argument_list|>
name|nextIterator
parameter_list|()
block|{
name|NodeDocument
name|d
init|=
name|docs
operator|.
name|hasNext
argument_list|()
condition|?
name|docs
operator|.
name|next
argument_list|()
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|d
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Map
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
name|values
decl_stmt|;
if|if
condition|(
name|Objects
operator|.
name|equal
argument_list|(
name|d
operator|.
name|getId
argument_list|()
argument_list|,
name|doc
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
comment|// return local map for main document
name|values
operator|=
name|d
operator|.
name|getLocalMap
argument_list|(
name|property
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|values
operator|=
name|d
operator|.
name|getValueMap
argument_list|(
name|property
argument_list|)
expr_stmt|;
block|}
return|return
name|values
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
name|String
name|description
parameter_list|()
block|{
return|return
literal|"Revisioned values for property "
operator|+
name|doc
operator|.
name|getId
argument_list|()
operator|+
literal|"/"
operator|+
name|property
operator|+
literal|":"
return|;
block|}
block|}
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
name|property
argument_list|,
literal|null
argument_list|)
control|)
block|{
name|size
operator|+=
name|prev
operator|.
name|getValueMap
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
name|Revision
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|Revision
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
name|NotNull
specifier|public
name|Set
argument_list|<
name|Entry
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
argument_list|>
name|entrySet
parameter_list|()
block|{
return|return
name|entrySet
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
name|Revision
name|r
init|=
operator|(
name|Revision
operator|)
name|key
decl_stmt|;
comment|// first check values map of this document
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
name|r
argument_list|)
condition|)
block|{
return|return
name|map
operator|.
name|get
argument_list|(
name|r
argument_list|)
return|;
block|}
for|for
control|(
name|NodeDocument
name|prev
range|:
name|doc
operator|.
name|getPreviousDocs
argument_list|(
name|property
argument_list|,
name|r
argument_list|)
control|)
block|{
name|String
name|value
init|=
name|prev
operator|.
name|getValueMap
argument_list|(
name|property
argument_list|)
operator|.
name|get
argument_list|(
name|r
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
block|}
comment|// not found or null
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
comment|// check local map first
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|Revision
name|r
init|=
operator|(
name|Revision
operator|)
name|key
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
name|property
argument_list|,
name|r
argument_list|)
control|)
block|{
if|if
condition|(
name|prev
operator|.
name|getValueMap
argument_list|(
name|property
argument_list|)
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

