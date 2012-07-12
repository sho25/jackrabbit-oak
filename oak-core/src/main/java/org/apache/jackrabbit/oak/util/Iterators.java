begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|Collection
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|collections
operator|.
name|iterators
operator|.
name|ArrayIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|collections
operator|.
name|iterators
operator|.
name|EmptyIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|collections
operator|.
name|iterators
operator|.
name|FilterIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|collections
operator|.
name|iterators
operator|.
name|IteratorChain
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|collections
operator|.
name|iterators
operator|.
name|SingletonIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|collections
operator|.
name|iterators
operator|.
name|TransformIterator
import|;
end_import

begin_comment
comment|/**  * Utility class containing type safe adapters for some of the iterators of  * commons-collections.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|Iterators
block|{
specifier|private
name|Iterators
parameter_list|()
block|{ }
comment|/**      * Returns an iterator containing the single element {@code element} of      * type {@code T}.      *      * @param<T>      * @param element      * @return      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Nonnull
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Iterator
argument_list|<
name|T
argument_list|>
name|singleton
parameter_list|(
name|T
name|element
parameter_list|)
block|{
return|return
operator|new
name|SingletonIterator
argument_list|(
name|element
argument_list|)
return|;
block|}
comment|/**      * Returns an empty iterator of type {@code T}.      *      * @param<T>      * @return      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Nonnull
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Iterator
argument_list|<
name|T
argument_list|>
name|empty
parameter_list|()
block|{
return|return
name|EmptyIterator
operator|.
name|INSTANCE
return|;
block|}
comment|/**      * Returns an iterator for the concatenation of {@code iterator1} and      * {@code iterator2}.      *      * @param<T>      * @param iterator1      * @param iterator2      * @return      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Nonnull
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Iterator
argument_list|<
name|T
argument_list|>
name|chain
parameter_list|(
name|Iterator
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|iterator1
parameter_list|,
name|Iterator
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|iterator2
parameter_list|)
block|{
return|return
operator|new
name|IteratorChain
argument_list|(
name|iterator1
argument_list|,
name|iterator2
argument_list|)
return|;
block|}
comment|/**      * Returns an iterator for the concatenation of all the given {@code iterators}.      *      * @param<T>      * @param iterators      * @return      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Nonnull
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Iterator
argument_list|<
name|T
argument_list|>
name|chain
parameter_list|(
name|Iterator
argument_list|<
name|?
extends|extends
name|T
argument_list|>
index|[]
name|iterators
parameter_list|)
block|{
return|return
operator|new
name|IteratorChain
argument_list|(
name|iterators
argument_list|)
return|;
block|}
comment|/**      * Returns an iterator for the concatenation of all the given {@code iterators}.      *      * @param<T>      * @param iterators      * @return      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Nonnull
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Iterator
argument_list|<
name|T
argument_list|>
name|chain
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|iterators
parameter_list|)
block|{
return|return
operator|new
name|IteratorChain
argument_list|(
name|iterators
argument_list|)
return|;
block|}
comment|/**      * Returns an iterator for elements of an array of {@code values}.      *      * @param<T>      * @param values  the array to iterate over.      * @param from  the index to start iterating at.      * @param to  the index to finish iterating at.      * @return      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Nonnull
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Iterator
argument_list|<
name|T
argument_list|>
name|arrayIterator
parameter_list|(
name|T
index|[]
name|values
parameter_list|,
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|)
block|{
return|return
operator|new
name|ArrayIterator
argument_list|(
name|values
argument_list|,
name|from
argument_list|,
name|to
argument_list|)
return|;
block|}
comment|/**      * Returns an iterator with elements from an original {@code iterator} where the      * given {@code predicate} matches removed.      *      * @param<T>      * @param iterator      * @param predicate      * @return      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Nonnull
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Iterator
argument_list|<
name|T
argument_list|>
name|filter
parameter_list|(
name|Iterator
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|iterator
parameter_list|,
specifier|final
name|Predicate
argument_list|<
name|?
super|super
name|T
argument_list|>
name|predicate
parameter_list|)
block|{
return|return
operator|new
name|FilterIterator
argument_list|(
name|iterator
argument_list|,
operator|new
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|collections
operator|.
name|Predicate
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|(
name|Object
name|object
parameter_list|)
block|{
return|return
name|predicate
operator|.
name|evaluate
argument_list|(
operator|(
name|T
operator|)
name|object
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
comment|/**      * Returns an iterator with elements of an original  {@code iterator} mapped by      * a {@code f}.      *      * @param<T>      * @param<R>      * @param<S>      * @param iterator      * @param f      * @return      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Nonnull
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|,
name|R
parameter_list|,
name|S
extends|extends
name|T
parameter_list|>
name|Iterator
argument_list|<
name|R
argument_list|>
name|map
parameter_list|(
name|Iterator
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|iterator
parameter_list|,
specifier|final
name|Function1
argument_list|<
name|S
argument_list|,
name|?
super|super
name|R
argument_list|>
name|f
parameter_list|)
block|{
return|return
operator|new
name|TransformIterator
argument_list|(
name|iterator
argument_list|,
operator|new
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|collections
operator|.
name|Transformer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|transform
parameter_list|(
name|Object
name|input
parameter_list|)
block|{
return|return
name|f
operator|.
name|apply
argument_list|(
operator|(
name|S
operator|)
name|input
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
comment|/**      * Spool an iterator into an iterable      * @param iterator      * @param<T>      * @return iterable containing the values from {@code iterator}      */
annotation|@
name|Nonnull
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Iterable
argument_list|<
name|T
argument_list|>
name|toIterable
parameter_list|(
specifier|final
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
parameter_list|)
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
specifier|private
name|List
argument_list|<
name|T
argument_list|>
name|copy
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
parameter_list|()
block|{
if|if
condition|(
name|copy
operator|==
literal|null
condition|)
block|{
name|copy
operator|=
name|spool
argument_list|(
name|iterator
argument_list|)
expr_stmt|;
block|}
return|return
name|copy
operator|.
name|iterator
argument_list|()
return|;
block|}
specifier|private
name|List
argument_list|<
name|T
argument_list|>
name|spool
parameter_list|(
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
parameter_list|)
block|{
name|List
argument_list|<
name|T
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|T
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
block|}
return|;
block|}
comment|/**      * Flattens an iterator of iterators into a single iterator.      * @param iterators      * @param<T>      * @return      */
annotation|@
name|Nonnull
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Iterator
argument_list|<
name|T
argument_list|>
name|flatten
parameter_list|(
specifier|final
name|Iterator
argument_list|<
name|?
extends|extends
name|Iterator
argument_list|<
name|?
extends|extends
name|T
argument_list|>
argument_list|>
name|iterators
parameter_list|)
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
specifier|private
name|Iterator
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|current
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
name|current
operator|!=
literal|null
operator|&&
name|current
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|iterators
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
do|do
block|{
name|current
operator|=
name|iterators
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
operator|!
name|current
operator|.
name|hasNext
argument_list|()
operator|&&
name|iterators
operator|.
name|hasNext
argument_list|()
condition|)
do|;
return|return
name|current
operator|.
name|hasNext
argument_list|()
return|;
block|}
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
return|return
name|current
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
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
name|current
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

