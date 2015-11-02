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
name|checkNotNull
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
operator|.
name|filter
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
operator|.
name|transform
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|AbstractMap
operator|.
name|SimpleImmutableEntry
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
name|TreeMap
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
name|AbstractIterator
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|PeekingIterator
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
name|Utils
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
comment|/**  * The revision history for a given property. The history may span multiple  * previous documents.  */
end_comment

begin_class
class|class
name|PropertyHistory
implements|implements
name|Iterable
argument_list|<
name|NodeDocument
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PropertyHistory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|NodeDocument
name|doc
decl_stmt|;
specifier|private
specifier|final
name|String
name|property
decl_stmt|;
comment|// path of the main document
specifier|private
specifier|final
name|String
name|mainPath
decl_stmt|;
specifier|public
name|PropertyHistory
parameter_list|(
annotation|@
name|Nonnull
name|NodeDocument
name|doc
parameter_list|,
annotation|@
name|Nonnull
name|String
name|property
parameter_list|)
block|{
name|this
operator|.
name|doc
operator|=
name|checkNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|this
operator|.
name|property
operator|=
name|checkNotNull
argument_list|(
name|property
argument_list|)
expr_stmt|;
name|this
operator|.
name|mainPath
operator|=
name|doc
operator|.
name|getMainPath
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|NodeDocument
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|ensureOrder
argument_list|(
name|filter
argument_list|(
name|transform
argument_list|(
name|doc
operator|.
name|getPreviousRanges
argument_list|()
operator|.
name|entrySet
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Revision
argument_list|,
name|Range
argument_list|>
argument_list|,
name|Map
operator|.
name|Entry
argument_list|<
name|Revision
argument_list|,
name|NodeDocument
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|Map
operator|.
name|Entry
argument_list|<
name|Revision
argument_list|,
name|NodeDocument
argument_list|>
name|apply
parameter_list|(
name|Map
operator|.
name|Entry
argument_list|<
name|Revision
argument_list|,
name|Range
argument_list|>
name|input
parameter_list|)
block|{
name|Revision
name|r
init|=
name|input
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|int
name|h
init|=
name|input
operator|.
name|getValue
argument_list|()
operator|.
name|height
decl_stmt|;
name|String
name|prevId
init|=
name|Utils
operator|.
name|getPreviousIdFor
argument_list|(
name|mainPath
argument_list|,
name|r
argument_list|,
name|h
argument_list|)
decl_stmt|;
name|NodeDocument
name|prev
init|=
name|doc
operator|.
name|getPreviousDocument
argument_list|(
name|prevId
argument_list|)
decl_stmt|;
if|if
condition|(
name|prev
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Document with previous revisions not found: "
operator|+
name|prevId
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
operator|new
name|SimpleImmutableEntry
argument_list|<
name|Revision
argument_list|,
name|NodeDocument
argument_list|>
argument_list|(
name|r
argument_list|,
name|prev
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|,
name|Predicates
operator|.
name|notNull
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Ensures the order of docs is correct with respect to the highest revision      * for each ValueMap for the given property.      *      * @param docs the docs to order.      * @return the docs in the correct order.      */
specifier|private
name|Iterator
argument_list|<
name|NodeDocument
argument_list|>
name|ensureOrder
parameter_list|(
specifier|final
name|Iterable
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Revision
argument_list|,
name|NodeDocument
argument_list|>
argument_list|>
name|docs
parameter_list|)
block|{
return|return
operator|new
name|AbstractIterator
argument_list|<
name|NodeDocument
argument_list|>
argument_list|()
block|{
name|PeekingIterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Revision
argument_list|,
name|NodeDocument
argument_list|>
argument_list|>
name|input
init|=
name|Iterators
operator|.
name|peekingIterator
argument_list|(
name|docs
operator|.
name|iterator
argument_list|()
argument_list|)
decl_stmt|;
name|TreeMap
argument_list|<
name|Revision
argument_list|,
name|NodeDocument
argument_list|>
name|queue
init|=
operator|new
name|TreeMap
argument_list|<
name|Revision
argument_list|,
name|NodeDocument
argument_list|>
argument_list|(
name|StableRevisionComparator
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|NodeDocument
name|computeNext
parameter_list|()
block|{
name|refillQueue
argument_list|()
expr_stmt|;
if|if
condition|(
name|queue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|endOfData
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|queue
operator|.
name|remove
argument_list|(
name|queue
operator|.
name|lastKey
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**              * Refill the queue until the highest entry in the queue is higher              * than the peeked entry from the input iterator.              */
specifier|private
name|void
name|refillQueue
parameter_list|()
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
comment|// the doc to enqueue
name|NodeDocument
name|doc
decl_stmt|;
if|if
condition|(
name|queue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|input
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|doc
operator|=
name|input
operator|.
name|next
argument_list|()
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// no more input -> done
return|return;
block|}
block|}
else|else
block|{
comment|// peek first and compare with queue
if|if
condition|(
name|input
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
name|queue
operator|.
name|comparator
argument_list|()
operator|.
name|compare
argument_list|(
name|queue
operator|.
name|lastKey
argument_list|()
argument_list|,
name|input
operator|.
name|peek
argument_list|()
operator|.
name|getKey
argument_list|()
argument_list|)
operator|<
literal|0
condition|)
block|{
name|doc
operator|=
name|input
operator|.
name|next
argument_list|()
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// top of queue rev is higher than input -> done
return|return;
block|}
block|}
else|else
block|{
comment|// no more input -> done
return|return;
block|}
block|}
comment|// check if the revision is actually in there
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
name|values
init|=
name|doc
operator|.
name|getValueMap
argument_list|(
name|property
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Revision
argument_list|>
name|revs
init|=
name|values
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|revs
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// put into queue with first (highest) revision
comment|// from value map
name|queue
operator|.
name|put
argument_list|(
name|revs
operator|.
name|next
argument_list|()
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

