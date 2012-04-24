begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|kernel
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
name|CoreValue
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
name|PropertyState
import|;
end_import

begin_comment
comment|/**  * Abstract base class for {@link PropertyState} implementations.  * This base class contains default implementations of the  * {@link #equals(Object)} and {@link #hashCode()} methods based on  * the implemented interface.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractPropertyState
implements|implements
name|PropertyState
block|{
comment|/**      * Checks whether the given object is equal to this one. Two property      * states are considered equal if both their names and encoded values      * match. Subclasses may override this method with a more efficient      * equality check if one is available.      *      * @param that target of the comparison      * @return {@code true} if the objects are equal, {@code false} otherwise      */
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|that
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|that
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|that
operator|instanceof
name|PropertyState
condition|)
block|{
name|PropertyState
name|other
init|=
operator|(
name|PropertyState
operator|)
name|that
decl_stmt|;
return|return
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
name|valueEquals
argument_list|(
name|other
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
specifier|private
name|boolean
name|valueEquals
parameter_list|(
name|PropertyState
name|other
parameter_list|)
block|{
if|if
condition|(
name|isArray
argument_list|()
operator|!=
name|other
operator|.
name|isArray
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|isArray
argument_list|()
condition|)
block|{
name|Iterator
argument_list|<
name|CoreValue
argument_list|>
name|iterator
init|=
name|other
operator|.
name|getValues
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|CoreValue
name|value
range|:
name|getValues
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|iterator
operator|.
name|hasNext
argument_list|()
operator|||
operator|!
name|value
operator|.
name|equals
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
operator|!
name|iterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**      * Returns a hash code that's compatible with how the      * {@link #equals(Object)} method is implemented. The current      * implementation simply returns the hash code of the property name      * since {@link PropertyState} instances are not intended for use as      * hash keys.      *      * @return hash code      */
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getName
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

