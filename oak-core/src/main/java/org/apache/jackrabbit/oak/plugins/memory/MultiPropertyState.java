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
name|List
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
comment|/**  * Multi-valued property state.  */
end_comment

begin_class
specifier|public
class|class
name|MultiPropertyState
extends|extends
name|EmptyPropertyState
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|CoreValue
argument_list|>
name|values
decl_stmt|;
specifier|public
name|MultiPropertyState
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|CoreValue
argument_list|>
name|values
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|getBaseType
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|CoreValue
argument_list|>
argument_list|(
name|checkNotNull
argument_list|(
name|values
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Type
argument_list|<
name|?
argument_list|>
name|getBaseType
parameter_list|(
name|List
argument_list|<
name|CoreValue
argument_list|>
name|values
parameter_list|)
block|{
if|if
condition|(
name|values
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|STRINGS
return|;
block|}
else|else
block|{
return|return
name|Type
operator|.
name|fromTag
argument_list|(
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|,
literal|true
argument_list|)
return|;
block|}
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
return|return
name|values
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|length
argument_list|()
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
operator|!
name|type
operator|.
name|isArray
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Not a single valued property"
argument_list|)
throw|;
block|}
switch|switch
condition|(
name|type
operator|.
name|tag
argument_list|()
condition|)
block|{
case|case
name|PropertyType
operator|.
name|STRING
case|:
return|return
operator|(
name|T
operator|)
name|getStrings
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|BINARY
case|:
return|return
operator|(
name|T
operator|)
name|getBinaries
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|LONG
case|:
return|return
operator|(
name|T
operator|)
name|getLongs
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
return|return
operator|(
name|T
operator|)
name|getDoubles
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|DATE
case|:
return|return
operator|(
name|T
operator|)
name|getStrings
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|BOOLEAN
case|:
return|return
operator|(
name|T
operator|)
name|getBooleans
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|NAME
case|:
return|return
operator|(
name|T
operator|)
name|getStrings
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|PATH
case|:
return|return
operator|(
name|T
operator|)
name|getStrings
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|REFERENCE
case|:
return|return
operator|(
name|T
operator|)
name|getStrings
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|WEAKREFERENCE
case|:
return|return
operator|(
name|T
operator|)
name|getStrings
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|URI
case|:
return|return
operator|(
name|T
operator|)
name|getStrings
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|DECIMAL
case|:
return|return
operator|(
name|T
operator|)
name|getDecimals
argument_list|()
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid type:"
operator|+
name|type
argument_list|)
throw|;
block|}
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
parameter_list|,
name|int
name|index
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
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Nested arrays not supported"
argument_list|)
throw|;
block|}
switch|switch
condition|(
name|type
operator|.
name|tag
argument_list|()
condition|)
block|{
case|case
name|PropertyType
operator|.
name|STRING
case|:
return|return
operator|(
name|T
operator|)
name|values
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getString
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|BINARY
case|:
return|return
operator|(
name|T
operator|)
name|getBlob
argument_list|(
name|values
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|LONG
case|:
return|return
call|(
name|T
call|)
argument_list|(
name|Long
argument_list|)
name|values
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getLong
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
return|return
call|(
name|T
call|)
argument_list|(
name|Double
argument_list|)
name|values
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getDouble
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|DATE
case|:
return|return
operator|(
name|T
operator|)
name|values
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getString
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|BOOLEAN
case|:
return|return
call|(
name|T
call|)
argument_list|(
name|Boolean
argument_list|)
name|values
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getBoolean
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|NAME
case|:
return|return
operator|(
name|T
operator|)
name|values
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getString
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|PATH
case|:
return|return
operator|(
name|T
operator|)
name|values
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getString
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|REFERENCE
case|:
return|return
operator|(
name|T
operator|)
name|values
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getString
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|WEAKREFERENCE
case|:
return|return
operator|(
name|T
operator|)
name|values
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getString
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|URI
case|:
return|return
operator|(
name|T
operator|)
name|values
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getString
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|DECIMAL
case|:
return|return
operator|(
name|T
operator|)
name|values
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getDecimal
argument_list|()
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid type:"
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
name|values
operator|.
name|size
argument_list|()
return|;
block|}
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|getStrings
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|strings
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|CoreValue
name|value
range|:
name|values
control|)
block|{
name|strings
operator|.
name|add
argument_list|(
name|value
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|strings
return|;
block|}
specifier|private
name|List
argument_list|<
name|Long
argument_list|>
name|getLongs
parameter_list|()
block|{
name|List
argument_list|<
name|Long
argument_list|>
name|longs
init|=
operator|new
name|ArrayList
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|CoreValue
name|value
range|:
name|values
control|)
block|{
name|longs
operator|.
name|add
argument_list|(
name|value
operator|.
name|getLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|longs
return|;
block|}
specifier|private
name|List
argument_list|<
name|Double
argument_list|>
name|getDoubles
parameter_list|()
block|{
name|List
argument_list|<
name|Double
argument_list|>
name|doubles
init|=
operator|new
name|ArrayList
argument_list|<
name|Double
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|CoreValue
name|value
range|:
name|values
control|)
block|{
name|doubles
operator|.
name|add
argument_list|(
name|value
operator|.
name|getDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|doubles
return|;
block|}
specifier|private
name|List
argument_list|<
name|Boolean
argument_list|>
name|getBooleans
parameter_list|()
block|{
name|List
argument_list|<
name|Boolean
argument_list|>
name|booleans
init|=
operator|new
name|ArrayList
argument_list|<
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|CoreValue
name|value
range|:
name|values
control|)
block|{
name|booleans
operator|.
name|add
argument_list|(
name|value
operator|.
name|getBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|booleans
return|;
block|}
specifier|private
name|List
argument_list|<
name|BigDecimal
argument_list|>
name|getDecimals
parameter_list|()
block|{
name|List
argument_list|<
name|BigDecimal
argument_list|>
name|decimals
init|=
operator|new
name|ArrayList
argument_list|<
name|BigDecimal
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|CoreValue
name|value
range|:
name|values
control|)
block|{
name|decimals
operator|.
name|add
argument_list|(
name|value
operator|.
name|getDecimal
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|decimals
return|;
block|}
specifier|private
name|List
argument_list|<
name|Blob
argument_list|>
name|getBinaries
parameter_list|()
block|{
name|List
argument_list|<
name|Blob
argument_list|>
name|binaries
init|=
operator|new
name|ArrayList
argument_list|<
name|Blob
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|CoreValue
name|value
range|:
name|values
control|)
block|{
name|binaries
operator|.
name|add
argument_list|(
name|getBlob
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|binaries
return|;
block|}
block|}
end_class

end_unit

