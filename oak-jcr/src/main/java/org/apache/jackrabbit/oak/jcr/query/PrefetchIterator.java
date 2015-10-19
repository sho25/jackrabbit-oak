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
name|query
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|Result
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
name|api
operator|.
name|Result
operator|.
name|SizePrecision
import|;
end_import

begin_comment
comment|/**  * An iterator that pre-fetches a number of items in order to calculate the size  * of the result if possible. This iterator loads at least a number of items,  * and then tries to load some more items until the timeout is reached or the  * maximum number of entries are read.  *<p>  * Prefetching is only done when size() is called.  *   * @param<K> the iterator data type  */
end_comment

begin_class
specifier|public
class|class
name|PrefetchIterator
parameter_list|<
name|K
parameter_list|>
implements|implements
name|Iterator
argument_list|<
name|K
argument_list|>
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|K
argument_list|>
name|it
decl_stmt|;
specifier|private
specifier|final
name|long
name|minPrefetch
decl_stmt|,
name|timeout
decl_stmt|,
name|maxPrefetch
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|fastSize
decl_stmt|;
specifier|private
specifier|final
name|Result
name|fastSizeCallback
decl_stmt|;
specifier|private
name|boolean
name|prefetchDone
decl_stmt|;
specifier|private
name|Iterator
argument_list|<
name|K
argument_list|>
name|prefetchIterator
decl_stmt|;
specifier|private
name|long
name|size
decl_stmt|,
name|position
decl_stmt|;
comment|/**      * Create a new iterator.      *       * @param it the base iterator      * @param options the prefetch options to use      */
name|PrefetchIterator
parameter_list|(
name|Iterator
argument_list|<
name|K
argument_list|>
name|it
parameter_list|,
name|PrefetchOptions
name|options
parameter_list|)
block|{
name|this
operator|.
name|it
operator|=
name|it
expr_stmt|;
name|this
operator|.
name|minPrefetch
operator|=
name|options
operator|.
name|min
expr_stmt|;
name|this
operator|.
name|maxPrefetch
operator|=
name|options
operator|.
name|max
expr_stmt|;
name|this
operator|.
name|timeout
operator|=
name|options
operator|.
name|fastSize
condition|?
literal|0
else|:
name|options
operator|.
name|timeout
expr_stmt|;
name|this
operator|.
name|fastSize
operator|=
name|options
operator|.
name|fastSize
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|options
operator|.
name|size
expr_stmt|;
name|this
operator|.
name|fastSizeCallback
operator|=
name|options
operator|.
name|fastSizeCallback
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
name|prefetchIterator
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|prefetchIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
name|prefetchIterator
operator|=
literal|null
expr_stmt|;
block|}
name|boolean
name|result
init|=
name|it
operator|.
name|hasNext
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|result
condition|)
block|{
name|size
operator|=
name|position
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|K
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
if|if
condition|(
name|prefetchIterator
operator|!=
literal|null
condition|)
block|{
return|return
name|prefetchIterator
operator|.
name|next
argument_list|()
return|;
block|}
name|position
operator|++
expr_stmt|;
return|return
name|it
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
comment|/**      * Get the size if known. This call might pre-fetch data. The returned value      * is unknown if the actual size is larger than the number of pre-fetched      * elements, unless the end of the iterator has been reached already.      *       * @return the size, or -1 if unknown      */
specifier|public
name|long
name|size
parameter_list|()
block|{
if|if
condition|(
name|size
operator|!=
operator|-
literal|1
condition|)
block|{
return|return
name|size
return|;
block|}
if|if
condition|(
operator|!
name|fastSize
condition|)
block|{
if|if
condition|(
name|prefetchDone
operator|||
name|position
operator|>
name|maxPrefetch
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
name|prefetchDone
operator|=
literal|true
expr_stmt|;
name|ArrayList
argument_list|<
name|K
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|K
argument_list|>
argument_list|()
decl_stmt|;
name|long
name|end
decl_stmt|;
if|if
condition|(
name|timeout
operator|<=
literal|0
condition|)
block|{
name|end
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|long
name|nanos
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|end
operator|=
name|nanos
operator|+
name|timeout
operator|*
literal|1000
operator|*
literal|1000
expr_stmt|;
block|}
while|while
condition|(
name|position
operator|<=
name|maxPrefetch
condition|)
block|{
if|if
condition|(
name|position
operator|>
name|minPrefetch
condition|)
block|{
if|if
condition|(
name|end
operator|==
literal|0
operator|||
name|System
operator|.
name|nanoTime
argument_list|()
operator|>
name|end
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|size
operator|=
name|position
expr_stmt|;
break|break;
block|}
name|position
operator|++
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|prefetchIterator
operator|=
name|list
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|position
operator|-=
name|list
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|size
operator|==
operator|-
literal|1
operator|&&
name|fastSize
condition|)
block|{
if|if
condition|(
name|fastSizeCallback
operator|!=
literal|null
condition|)
block|{
name|size
operator|=
name|fastSizeCallback
operator|.
name|getSize
argument_list|(
name|SizePrecision
operator|.
name|EXACT
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|size
return|;
block|}
comment|/**      * The options to use for prefetching.      */
specifier|public
specifier|static
class|class
name|PrefetchOptions
block|{
comment|// uses the "simple" named-parameter pattern
comment|// see also http://stackoverflow.com/questions/1988016/named-parameter-idiom-in-java
comment|/**          * The minimum number of rows / nodes to pre-fetch.          */
name|long
name|min
init|=
literal|20
decl_stmt|;
comment|/**          * The maximum number of rows / nodes to pre-fetch.          */
name|long
name|max
init|=
literal|100
decl_stmt|;
comment|/**          * The maximum number of milliseconds to prefetch rows / nodes          * (ignored if fastSize is set).          */
name|long
name|timeout
init|=
literal|100
decl_stmt|;
comment|/**          * The size if known, or -1 if not (prefetching is only required if -1).          */
name|long
name|size
decl_stmt|;
comment|/**          * Whether or not the expected size should be read from the result.          */
name|boolean
name|fastSize
decl_stmt|;
comment|/**          * The result (optional) to get the size from, in case the fast size options is set.          */
name|Result
name|fastSizeCallback
decl_stmt|;
block|}
block|}
end_class

end_unit

