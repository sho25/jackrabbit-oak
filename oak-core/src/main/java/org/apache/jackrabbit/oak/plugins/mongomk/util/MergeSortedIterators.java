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
operator|.
name|util
package|;
end_package

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
name|NoSuchElementException
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

begin_comment
comment|/**  *<code>MergeSortedIterators</code> is a specialized implementation of a  * merge sort of already sorted iterators of some type of comparable elements.  * The input iterators must return the elements in sorted order according to  * the provided Comparator. In addition the sequence of iterators must also  * be sorted in a way that the first element of the next iterator is greater  * than the first element of the previous iterator.  *   * @param<T> the entry type  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|MergeSortedIterators
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Iterator
argument_list|<
name|T
argument_list|>
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|PeekingIterator
argument_list|<
name|T
argument_list|>
argument_list|>
name|iterators
init|=
operator|new
name|ArrayList
argument_list|<
name|PeekingIterator
argument_list|<
name|T
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Comparator
argument_list|<
name|T
argument_list|>
name|comparator
decl_stmt|;
specifier|private
name|T
name|lastPeek
decl_stmt|;
specifier|public
name|MergeSortedIterators
parameter_list|(
specifier|final
name|Comparator
argument_list|<
name|T
argument_list|>
name|comparator
parameter_list|)
block|{
name|this
operator|.
name|comparator
operator|=
name|comparator
expr_stmt|;
name|fetchNextIterator
argument_list|()
expr_stmt|;
block|}
comment|/**      * @return the next {@link Iterator} or<code>null</code> if there is none.      */
specifier|public
specifier|abstract
name|Iterator
argument_list|<
name|T
argument_list|>
name|nextIterator
parameter_list|()
function_decl|;
comment|/**      * Provides details about this iterator      */
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
comment|//----------------------------< Iterator>----------------------------------
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
operator|!
name|iterators
operator|.
name|isEmpty
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|T
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
name|PeekingIterator
argument_list|<
name|T
argument_list|>
name|it
init|=
name|iterators
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|T
name|next
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// more elements?
if|if
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|adjustFirst
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// remove from list of iterators
name|iterators
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// fetch next iterator?
if|if
condition|(
name|comparator
operator|.
name|compare
argument_list|(
name|next
argument_list|,
name|lastPeek
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|fetchNextIterator
argument_list|()
expr_stmt|;
block|}
return|return
name|next
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
comment|//----------------------------< internal>----------------------------------
specifier|private
name|void
name|fetchNextIterator
parameter_list|()
block|{
name|Iterator
argument_list|<
name|T
argument_list|>
name|it
init|=
name|nextIterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|it
operator|!=
literal|null
operator|&&
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|PeekingIterator
argument_list|<
name|T
argument_list|>
name|pIt
init|=
name|Iterators
operator|.
name|peekingIterator
argument_list|(
name|it
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|iterators
operator|.
name|isEmpty
argument_list|()
operator|&&
name|comparator
operator|.
name|compare
argument_list|(
name|pIt
operator|.
name|peek
argument_list|()
argument_list|,
name|lastPeek
argument_list|)
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|description
argument_list|()
operator|+
literal|" First element of next iterator must be greater than previous iterator"
argument_list|)
throw|;
block|}
name|lastPeek
operator|=
name|pIt
operator|.
name|peek
argument_list|()
expr_stmt|;
name|iterators
operator|.
name|add
argument_list|(
name|pIt
argument_list|)
expr_stmt|;
name|adjustLast
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|adjustFirst
parameter_list|()
block|{
comment|// shift first iterator until peeked elements are sorted again
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|+
literal|1
operator|<
name|iterators
operator|.
name|size
argument_list|()
condition|)
block|{
if|if
condition|(
name|comparator
operator|.
name|compare
argument_list|(
name|iterators
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|peek
argument_list|()
argument_list|,
name|iterators
operator|.
name|get
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|.
name|peek
argument_list|()
argument_list|)
operator|>
literal|0
condition|)
block|{
name|Collections
operator|.
name|swap
argument_list|(
name|iterators
argument_list|,
name|i
argument_list|,
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
specifier|private
name|void
name|adjustLast
parameter_list|()
block|{
comment|// shift last until sorted again
name|int
name|i
init|=
name|iterators
operator|.
name|size
argument_list|()
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|i
operator|-
literal|1
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|comparator
operator|.
name|compare
argument_list|(
name|iterators
operator|.
name|get
argument_list|(
name|i
operator|-
literal|1
argument_list|)
operator|.
name|peek
argument_list|()
argument_list|,
name|iterators
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|peek
argument_list|()
argument_list|)
operator|>
literal|0
condition|)
block|{
name|Collections
operator|.
name|swap
argument_list|(
name|iterators
argument_list|,
name|i
argument_list|,
name|i
operator|-
literal|1
argument_list|)
expr_stmt|;
name|i
operator|--
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
block|}
end_class

end_unit

