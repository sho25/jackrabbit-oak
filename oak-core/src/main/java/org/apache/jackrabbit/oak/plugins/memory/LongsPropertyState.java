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
name|math
operator|.
name|BigDecimal
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|List
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
name|collect
operator|.
name|Iterables
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
name|Lists
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

begin_class
specifier|public
class|class
name|LongsPropertyState
extends|extends
name|MultiPropertyState
argument_list|<
name|Long
argument_list|>
block|{
specifier|private
specifier|final
name|Type
argument_list|<
name|?
argument_list|>
name|type
decl_stmt|;
specifier|private
name|LongsPropertyState
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterable
argument_list|<
name|Long
argument_list|>
name|values
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
argument_list|,
name|values
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
specifier|public
specifier|static
name|LongsPropertyState
name|createLongsProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterable
argument_list|<
name|Long
argument_list|>
name|values
parameter_list|)
block|{
return|return
operator|new
name|LongsPropertyState
argument_list|(
name|name
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|values
argument_list|)
argument_list|,
name|Type
operator|.
name|LONGS
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|LongsPropertyState
name|createDatesPropertyFromLong
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterable
argument_list|<
name|Long
argument_list|>
name|values
parameter_list|)
block|{
return|return
operator|new
name|LongsPropertyState
argument_list|(
name|name
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|values
argument_list|)
argument_list|,
name|Type
operator|.
name|DATES
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|LongsPropertyState
name|createDatesPropertyFromCalendar
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterable
argument_list|<
name|Calendar
argument_list|>
name|values
parameter_list|)
block|{
name|List
argument_list|<
name|Long
argument_list|>
name|dates
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Calendar
name|v
range|:
name|values
control|)
block|{
name|dates
operator|.
name|add
argument_list|(
name|Conversions
operator|.
name|convert
argument_list|(
name|v
argument_list|)
operator|.
name|toLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|LongsPropertyState
argument_list|(
name|name
argument_list|,
name|dates
argument_list|,
name|Type
operator|.
name|DATES
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|LongsPropertyState
name|createDatesProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
parameter_list|)
block|{
name|List
argument_list|<
name|Calendar
argument_list|>
name|dates
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|v
range|:
name|values
control|)
block|{
name|dates
operator|.
name|add
argument_list|(
name|Conversions
operator|.
name|convert
argument_list|(
name|v
argument_list|)
operator|.
name|toCalendar
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|createDatesPropertyFromCalendar
argument_list|(
name|name
argument_list|,
name|dates
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Iterable
argument_list|<
name|BigDecimal
argument_list|>
name|getDecimals
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|values
argument_list|,
operator|new
name|Function
argument_list|<
name|Long
argument_list|,
name|BigDecimal
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|BigDecimal
name|apply
parameter_list|(
name|Long
name|value
parameter_list|)
block|{
return|return
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
operator|.
name|toDecimal
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|BigDecimal
name|getDecimal
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|Conversions
operator|.
name|convert
argument_list|(
name|values
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
operator|.
name|toDecimal
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Iterable
argument_list|<
name|Double
argument_list|>
name|getDoubles
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|values
argument_list|,
operator|new
name|Function
argument_list|<
name|Long
argument_list|,
name|Double
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Double
name|apply
parameter_list|(
name|Long
name|value
parameter_list|)
block|{
return|return
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
operator|.
name|toDouble
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Iterable
argument_list|<
name|String
argument_list|>
name|getDates
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|values
argument_list|,
operator|new
name|Function
argument_list|<
name|Long
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|Long
name|value
parameter_list|)
block|{
return|return
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
operator|.
name|toDate
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|double
name|getDouble
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|Conversions
operator|.
name|convert
argument_list|(
name|values
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
operator|.
name|toDouble
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getDate
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|Conversions
operator|.
name|convert
argument_list|(
name|values
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
operator|.
name|toDate
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Iterable
argument_list|<
name|Long
argument_list|>
name|getLongs
parameter_list|()
block|{
return|return
name|values
return|;
block|}
annotation|@
name|Override
specifier|protected
name|long
name|getLong
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|values
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Iterable
argument_list|<
name|String
argument_list|>
name|getStrings
parameter_list|()
block|{
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|DATES
condition|)
block|{
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|values
argument_list|,
operator|new
name|Function
argument_list|<
name|Long
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|Long
name|value
parameter_list|)
block|{
return|return
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
operator|.
name|toDate
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|values
argument_list|,
operator|new
name|Function
argument_list|<
name|Long
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|Long
name|value
parameter_list|)
block|{
return|return
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getString
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
operator|(
name|type
operator|==
name|Type
operator|.
name|DATES
operator|)
condition|?
name|getDate
argument_list|(
name|index
argument_list|)
else|:
name|Conversions
operator|.
name|convert
argument_list|(
name|values
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
operator|.
name|toString
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
block|}
end_class

end_unit

