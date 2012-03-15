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
name|util
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

begin_comment
comment|/**  * A {@code PagedIterator} is an iterator of several pages. A page itself is  * an iterator. The abstract {@code getPage} method is called whenever this  * iterator needs to fetch another page.<p/>  *  * Lazy flattening (e.g. with {@link org.apache.jackrabbit.oak.jcr.util.Iterators#flatten(java.util.Iterator)}  * results in an iterator which does batch reading from its back end.  *  * @param<T>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|PagedIterator
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Iterator
argument_list|<
name|Iterator
argument_list|<
name|?
extends|extends
name|T
argument_list|>
argument_list|>
block|{
specifier|private
specifier|final
name|int
name|pageSize
decl_stmt|;
specifier|private
name|long
name|pos
decl_stmt|;
specifier|private
name|Iterator
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|current
decl_stmt|;
specifier|protected
name|PagedIterator
parameter_list|(
name|int
name|pageSize
parameter_list|)
block|{
name|this
operator|.
name|pageSize
operator|=
name|pageSize
expr_stmt|;
block|}
comment|/**      * @param pos  start index      * @param size  maximal number of elements      * @return  iterator starting at index {@code pos} containing at most {@code size} elements.      */
specifier|protected
specifier|abstract
name|Iterator
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|getPage
parameter_list|(
name|long
name|pos
parameter_list|,
name|int
name|size
parameter_list|)
function_decl|;
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
operator|==
literal|null
condition|)
block|{
name|current
operator|=
name|getPage
argument_list|(
name|pos
argument_list|,
name|pageSize
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|pageSize
expr_stmt|;
block|}
return|return
name|current
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
name|?
extends|extends
name|T
argument_list|>
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
name|Iterator
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|e
init|=
name|current
decl_stmt|;
name|current
operator|=
literal|null
expr_stmt|;
return|return
name|e
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
argument_list|(
literal|"remove"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

