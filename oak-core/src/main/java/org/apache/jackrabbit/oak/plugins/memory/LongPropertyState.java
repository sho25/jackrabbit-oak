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
name|Calendar
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
name|value
operator|.
name|Conversions
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
name|value
operator|.
name|Conversions
operator|.
name|Converter
import|;
end_import

begin_class
specifier|public
class|class
name|LongPropertyState
extends|extends
name|SinglePropertyState
argument_list|<
name|Long
argument_list|>
block|{
specifier|private
specifier|final
name|long
name|value
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
name|LongPropertyState
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|value
parameter_list|,
name|Type
argument_list|<
name|?
argument_list|>
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|/**      * Create a {@code PropertyState} from a long.      * @param name  The name of the property state      * @param value  The value of the property state      * @return  The new property state of type {@link Type#LONG}      */
specifier|public
specifier|static
name|PropertyState
name|createLongProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|value
parameter_list|)
block|{
return|return
operator|new
name|LongPropertyState
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|Type
operator|.
name|LONG
argument_list|)
return|;
block|}
comment|/**      * Create a {@code PropertyState} for a date value from a long.      * @param name  The name of the property state      * @param value  The value of the property state      * @return  The new property state of type {@link Type#DATE}      */
specifier|public
specifier|static
name|PropertyState
name|createDateProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|value
parameter_list|)
block|{
return|return
operator|new
name|LongPropertyState
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|Type
operator|.
name|DATE
argument_list|)
return|;
block|}
comment|/**      * Create a {@code PropertyState} for a date.      * @param name  The name of the property state      * @param value  The value of the property state      * @return  The new property state of type {@link Type#DATE}      */
specifier|public
specifier|static
name|PropertyState
name|createDateProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Calendar
name|value
parameter_list|)
block|{
return|return
operator|new
name|LongPropertyState
argument_list|(
name|name
argument_list|,
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
operator|.
name|toLong
argument_list|()
argument_list|,
name|Type
operator|.
name|DATE
argument_list|)
return|;
block|}
comment|/**      * Create a {@code PropertyState} for a date from a String.      * @param name  The name of the property state      * @param value  The value of the property state      * @return  The new property state of type {@link Type#DATE}      * @throws IllegalArgumentException if {@code value} is not a parseable to a date.      */
specifier|public
specifier|static
name|PropertyState
name|createDateProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
name|createDateProperty
argument_list|(
name|name
argument_list|,
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
operator|.
name|toCalendar
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Long
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|Converter
name|getConverter
parameter_list|()
block|{
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|DATE
condition|)
block|{
return|return
name|Conversions
operator|.
name|convert
argument_list|(
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
operator|.
name|toCalendar
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
return|;
block|}
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
block|}
end_class

end_unit

