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
name|plugins
operator|.
name|memory
package|;
end_package

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
name|jcr
operator|.
name|PropertyType
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
name|Iterables
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
name|Type
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
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
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
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_import
import|import static
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
name|Type
operator|.
name|BINARIES
import|;
end_import

begin_import
import|import static
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
name|Type
operator|.
name|STRING
import|;
end_import

begin_import
import|import static
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
name|Type
operator|.
name|STRINGS
import|;
end_import

begin_comment
comment|/**  * Abstract base class for {@link PropertyState} implementations  * providing default implementation which correspond to a property  * without any value.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|EmptyPropertyState
implements|implements
name|PropertyState
block|{
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|/**      * Create a new property state with the given {@code name}      * @param name  The name of the property state.      */
specifier|protected
name|EmptyPropertyState
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create an empty {@code PropertyState}      * @param name  The name of the property state      * @param type  The type of the property state      * @return  The new property state      */
specifier|public
specifier|static
name|PropertyState
name|emptyProperty
parameter_list|(
name|String
name|name
parameter_list|,
specifier|final
name|Type
argument_list|<
name|?
argument_list|>
name|type
parameter_list|)
block|{
if|if
condition|(
operator|!
name|type
operator|.
name|isArray
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Not an array type:"
operator|+
name|type
argument_list|)
throw|;
block|}
return|return
operator|new
name|EmptyPropertyState
argument_list|(
name|name
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Type
argument_list|<
name|?
argument_list|>
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**      * @return {@code true}      */
annotation|@
name|Override
specifier|public
name|boolean
name|isArray
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**      * @return An empty list if {@code type.isArray()} is {@code true}.      * @throws IllegalArgumentException {@code type.isArray()} is {@code false}.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|getValue
parameter_list|(
name|Type
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|type
operator|.
name|isArray
argument_list|()
argument_list|,
literal|"Type must be an array type"
argument_list|)
expr_stmt|;
return|return
operator|(
name|T
operator|)
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
comment|/**      * @throws IndexOutOfBoundsException always      */
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|getValue
parameter_list|(
name|Type
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
name|int
name|index
parameter_list|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|index
argument_list|)
argument_list|)
throw|;
block|}
comment|/**      * @throws IllegalStateException always      */
annotation|@
name|Override
specifier|public
name|long
name|size
parameter_list|()
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Not a single valued property"
argument_list|)
throw|;
block|}
comment|/**      * @throws IndexOutOfBoundsException always      */
annotation|@
name|Override
specifier|public
name|long
name|size
parameter_list|(
name|int
name|index
parameter_list|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|index
argument_list|)
argument_list|)
throw|;
block|}
comment|/**      * @return {@code 0}      */
annotation|@
name|Override
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|//------------------------------------------------------------< Object>--
comment|/**      * Checks whether the given object is equal to this one. Two property      * states are considered equal if their names and types match and      * their string representation of their values are equal.      * Subclasses may override this method with a more efficient      * equality check if one is available.      *      * @param other target of the comparison      * @return {@code true} if the objects are equal, {@code false} otherwise      */
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|other
operator|instanceof
name|PropertyState
condition|)
block|{
name|PropertyState
name|that
init|=
operator|(
name|PropertyState
operator|)
name|other
decl_stmt|;
if|if
condition|(
operator|!
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|that
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|that
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
operator|==
name|PropertyType
operator|.
name|BINARY
condition|)
block|{
return|return
name|Iterables
operator|.
name|elementsEqual
argument_list|(
name|getValue
argument_list|(
name|BINARIES
argument_list|)
argument_list|,
name|that
operator|.
name|getValue
argument_list|(
name|BINARIES
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Iterables
operator|.
name|elementsEqual
argument_list|(
name|getValue
argument_list|(
name|STRINGS
argument_list|)
argument_list|,
name|that
operator|.
name|getValue
argument_list|(
name|STRINGS
argument_list|)
argument_list|)
return|;
block|}
block|}
else|else
block|{
return|return
literal|false
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
name|name
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|isArray
argument_list|()
condition|)
block|{
return|return
name|getName
argument_list|()
operator|+
literal|'='
operator|+
name|getValue
argument_list|(
name|STRINGS
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|getName
argument_list|()
operator|+
literal|'='
operator|+
name|getValue
argument_list|(
name|STRING
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

