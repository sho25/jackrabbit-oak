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
comment|/**  * {@code AbstractLazyIterator} provides basic iteration methods for a lazy loading iterator that does not support  * remove. Implementing classes only need to implement the {@link #getNext()} method which must return the next item  * in the iteration or {@code null} if the iteration as reached its end.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractLazyIterator
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
name|boolean
name|fetchNext
init|=
literal|true
decl_stmt|;
specifier|private
name|T
name|next
decl_stmt|;
specifier|protected
name|AbstractLazyIterator
parameter_list|()
block|{     }
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
name|fetchNext
condition|)
block|{
name|next
operator|=
name|getNext
argument_list|()
expr_stmt|;
name|fetchNext
operator|=
literal|false
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
name|T
name|next
parameter_list|()
block|{
if|if
condition|(
name|fetchNext
condition|)
block|{
name|next
operator|=
name|getNext
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|fetchNext
operator|=
literal|true
expr_stmt|;
block|}
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
return|return
name|next
return|;
block|}
comment|/**      * Returns the next element of this iteration or {@code null} if the iteration has finished.      * @return the next element.      */
specifier|abstract
specifier|protected
name|T
name|getNext
parameter_list|()
function_decl|;
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
end_class

end_unit

