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
name|java
operator|.
name|util
operator|.
name|List
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
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|Blob
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
name|checkNotNull
import|;
end_import

begin_comment
comment|/**  * Property state that contains an empty array of values. Used as a base  * class for {@link SinglePropertyState} and {@link MultiPropertyState}.  */
end_comment

begin_class
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
specifier|private
specifier|final
name|Type
argument_list|<
name|?
argument_list|>
name|type
decl_stmt|;
specifier|public
name|EmptyPropertyState
parameter_list|(
name|String
name|name
parameter_list|,
name|Type
argument_list|<
name|?
argument_list|>
name|type
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
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
specifier|protected
specifier|static
name|Blob
name|getBlob
parameter_list|(
name|CoreValue
name|value
parameter_list|)
block|{
return|return
operator|new
name|BlobImpl
argument_list|(
name|value
argument_list|)
return|;
block|}
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
annotation|@
name|Override
annotation|@
name|Nonnull
annotation|@
name|Deprecated
specifier|public
name|CoreValue
name|getValue
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
annotation|@
name|Override
annotation|@
name|Nonnull
annotation|@
name|Deprecated
specifier|public
name|List
argument_list|<
name|CoreValue
argument_list|>
name|getValues
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
if|if
condition|(
name|type
operator|.
name|isArray
argument_list|()
condition|)
block|{
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
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Not a single valued property"
argument_list|)
throw|;
block|}
block|}
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
name|isArray
argument_list|()
operator|==
name|other
operator|.
name|isArray
argument_list|()
operator|&&
name|getValues
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getValues
argument_list|()
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
return|return
name|getName
argument_list|()
operator|+
literal|'='
operator|+
name|getValues
argument_list|()
return|;
block|}
block|}
end_class

end_unit

