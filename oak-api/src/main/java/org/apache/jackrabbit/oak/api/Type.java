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
name|api
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_comment
comment|/**  * Instances of this class map Java types to {@link PropertyType property types}.  * Passing an instance of this class to {@link PropertyState#getValue(Type)} determines  * the return type of that method.  * @param<T>  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|Type
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Comparable
argument_list|<
name|Type
argument_list|<
name|?
argument_list|>
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Type
argument_list|<
name|?
argument_list|>
argument_list|>
name|TYPES
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Type
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Type
argument_list|<
name|T
argument_list|>
name|create
parameter_list|(
name|int
name|tag
parameter_list|,
name|boolean
name|array
parameter_list|,
name|String
name|string
parameter_list|)
block|{
name|Type
argument_list|<
name|T
argument_list|>
name|type
init|=
operator|new
name|Type
argument_list|<
name|T
argument_list|>
argument_list|(
name|tag
argument_list|,
name|array
argument_list|,
name|string
argument_list|)
decl_stmt|;
name|TYPES
operator|.
name|put
argument_list|(
name|string
argument_list|,
name|type
argument_list|)
expr_stmt|;
return|return
name|type
return|;
block|}
comment|/** Map {@code String} to {@link PropertyType#STRING} */
specifier|public
specifier|static
specifier|final
name|Type
argument_list|<
name|String
argument_list|>
name|STRING
init|=
name|create
argument_list|(
name|PropertyType
operator|.
name|STRING
argument_list|,
literal|false
argument_list|,
literal|"STRING"
argument_list|)
decl_stmt|;
comment|/** Map {@code Blob} to {@link PropertyType#BINARY} */
specifier|public
specifier|static
specifier|final
name|Type
argument_list|<
name|Blob
argument_list|>
name|BINARY
init|=
name|create
argument_list|(
name|PropertyType
operator|.
name|BINARY
argument_list|,
literal|false
argument_list|,
literal|"BINARY"
argument_list|)
decl_stmt|;
comment|/** Map {@code Long} to {@link PropertyType#LONG} */
specifier|public
specifier|static
specifier|final
name|Type
argument_list|<
name|Long
argument_list|>
name|LONG
init|=
name|create
argument_list|(
name|PropertyType
operator|.
name|LONG
argument_list|,
literal|false
argument_list|,
literal|"LONG"
argument_list|)
decl_stmt|;
comment|/** Map {@code Double} to {@link PropertyType#DOUBLE} */
specifier|public
specifier|static
specifier|final
name|Type
argument_list|<
name|Double
argument_list|>
name|DOUBLE
init|=
name|create
argument_list|(
name|PropertyType
operator|.
name|DOUBLE
argument_list|,
literal|false
argument_list|,
literal|"DOUBLE"
argument_list|)
decl_stmt|;
comment|/** Map {@code String} to {@link PropertyType#DATE} */
specifier|public
specifier|static
specifier|final
name|Type
argument_list|<
name|String
argument_list|>
name|DATE
init|=
name|create
argument_list|(
name|PropertyType
operator|.
name|DATE
argument_list|,
literal|false
argument_list|,
literal|"DATE"
argument_list|)
decl_stmt|;
comment|/** Map {@code Boolean} to {@link PropertyType#BOOLEAN} */
specifier|public
specifier|static
specifier|final
name|Type
argument_list|<
name|Boolean
argument_list|>
name|BOOLEAN
init|=
name|create
argument_list|(
name|PropertyType
operator|.
name|BOOLEAN
argument_list|,
literal|false
argument_list|,
literal|"BOOLEAN"
argument_list|)
decl_stmt|;
comment|/** Map {@code String} to {@link PropertyType#STRING} */
specifier|public
specifier|static
specifier|final
name|Type
argument_list|<
name|String
argument_list|>
name|NAME
init|=
name|create
argument_list|(
name|PropertyType
operator|.
name|NAME
argument_list|,
literal|false
argument_list|,
literal|"NAME"
argument_list|)
decl_stmt|;
comment|/** Map {@code String} to {@link PropertyType#PATH} */
specifier|public
specifier|static
specifier|final
name|Type
argument_list|<
name|String
argument_list|>
name|PATH
init|=
name|create
argument_list|(
name|PropertyType
operator|.
name|PATH
argument_list|,
literal|false
argument_list|,
literal|"PATH"
argument_list|)
decl_stmt|;
comment|/** Map {@code String} to {@link PropertyType#REFERENCE} */
specifier|public
specifier|static
specifier|final
name|Type
argument_list|<
name|String
argument_list|>
name|REFERENCE
init|=
name|create
argument_list|(
name|PropertyType
operator|.
name|REFERENCE
argument_list|,
literal|false
argument_list|,
literal|"REFERENCE"
argument_list|)
decl_stmt|;
comment|/** Map {@code String} to {@link PropertyType#WEAKREFERENCE} */
specifier|public
specifier|static
specifier|final
name|Type
argument_list|<
name|String
argument_list|>
name|WEAKREFERENCE
init|=
name|create
argument_list|(
name|PropertyType
operator|.
name|WEAKREFERENCE
argument_list|,
literal|false
argument_list|,
literal|"WEAKREFERENCE"
argument_list|)
decl_stmt|;
comment|/** Map {@code String} to {@link PropertyType#URI} */
specifier|public
specifier|static
specifier|final
name|Type
argument_list|<
name|String
argument_list|>
name|URI
init|=
name|create
argument_list|(
name|PropertyType
operator|.
name|URI
argument_list|,
literal|false
argument_list|,
literal|"URI"
argument_list|)
decl_stmt|;
comment|/** Map {@code BigDecimal} to {@link PropertyType#DECIMAL} */
specifier|public
specifier|static
specifier|final
name|Type
argument_list|<
name|BigDecimal
argument_list|>
name|DECIMAL
init|=
name|create
argument_list|(
name|PropertyType
operator|.
name|DECIMAL
argument_list|,
literal|false
argument_list|,
literal|"DECIMAL"
argument_list|)
decl_stmt|;
comment|/** Map {@code Iterable<String>} to array of {@link PropertyType#STRING} */
specifier|public
specifier|static
specifier|final
name|Type
argument_list|<
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|>
name|STRINGS
init|=
name|create
argument_list|(
name|PropertyType
operator|.
name|STRING
argument_list|,
literal|true
argument_list|,
literal|"STRINGS"
argument_list|)
decl_stmt|;
comment|/** Map {@code Iterable<Blob>} to array of {@link PropertyType#BINARY} */
specifier|public
specifier|static
specifier|final
name|Type
argument_list|<
name|Iterable
argument_list|<
name|Blob
argument_list|>
argument_list|>
name|BINARIES
init|=
name|create
argument_list|(
name|PropertyType
operator|.
name|BINARY
argument_list|,
literal|true
argument_list|,
literal|"BINARIES"
argument_list|)
decl_stmt|;
comment|/** Map {@code Iterable<Long>} to array of {@link PropertyType#LONG} */
specifier|public
specifier|static
specifier|final
name|Type
argument_list|<
name|Iterable
argument_list|<
name|Long
argument_list|>
argument_list|>
name|LONGS
init|=
name|create
argument_list|(
name|PropertyType
operator|.
name|LONG
argument_list|,
literal|true
argument_list|,
literal|"LONGS"
argument_list|)
decl_stmt|;
comment|/** Map {@code Iterable<Double>} to array of {@link PropertyType#DOUBLE} */
specifier|public
specifier|static
specifier|final
name|Type
argument_list|<
name|Iterable
argument_list|<
name|Double
argument_list|>
argument_list|>
name|DOUBLES
init|=
name|create
argument_list|(
name|PropertyType
operator|.
name|DOUBLE
argument_list|,
literal|true
argument_list|,
literal|"DOUBLES"
argument_list|)
decl_stmt|;
comment|/** Map {@code Iterable<String>} to array of {@link PropertyType#DATE} */
specifier|public
specifier|static
specifier|final
name|Type
argument_list|<
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|>
name|DATES
init|=
name|create
argument_list|(
name|PropertyType
operator|.
name|DATE
argument_list|,
literal|true
argument_list|,
literal|"DATES"
argument_list|)
decl_stmt|;
comment|/** Map {@code Iterable<Boolean>} to array of {@link PropertyType#BOOLEAN} */
specifier|public
specifier|static
specifier|final
name|Type
argument_list|<
name|Iterable
argument_list|<
name|Boolean
argument_list|>
argument_list|>
name|BOOLEANS
init|=
name|create
argument_list|(
name|PropertyType
operator|.
name|BOOLEAN
argument_list|,
literal|true
argument_list|,
literal|"BOOLEANS"
argument_list|)
decl_stmt|;
comment|/** Map {@code Iterable<String>} to array of {@link PropertyType#NAME} */
specifier|public
specifier|static
specifier|final
name|Type
argument_list|<
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|>
name|NAMES
init|=
name|create
argument_list|(
name|PropertyType
operator|.
name|NAME
argument_list|,
literal|true
argument_list|,
literal|"NAMES"
argument_list|)
decl_stmt|;
comment|/** Map {@code Iterable<String>} to array of {@link PropertyType#PATH} */
specifier|public
specifier|static
specifier|final
name|Type
argument_list|<
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|>
name|PATHS
init|=
name|create
argument_list|(
name|PropertyType
operator|.
name|PATH
argument_list|,
literal|true
argument_list|,
literal|"PATHS"
argument_list|)
decl_stmt|;
comment|/** Map {@code Iterable<String>} to array of {@link PropertyType#REFERENCE} */
specifier|public
specifier|static
specifier|final
name|Type
argument_list|<
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|>
name|REFERENCES
init|=
name|create
argument_list|(
name|PropertyType
operator|.
name|REFERENCE
argument_list|,
literal|true
argument_list|,
literal|"REFERENCES"
argument_list|)
decl_stmt|;
comment|/** Map {@code Iterable<String>} to array of {@link PropertyType#WEAKREFERENCE} */
specifier|public
specifier|static
specifier|final
name|Type
argument_list|<
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|>
name|WEAKREFERENCES
init|=
name|create
argument_list|(
name|PropertyType
operator|.
name|WEAKREFERENCE
argument_list|,
literal|true
argument_list|,
literal|"WEAKREFERENCES"
argument_list|)
decl_stmt|;
comment|/** Map {@code Iterable<String>} to array of {@link PropertyType#URI} */
specifier|public
specifier|static
specifier|final
name|Type
argument_list|<
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|>
name|URIS
init|=
name|create
argument_list|(
name|PropertyType
operator|.
name|URI
argument_list|,
literal|true
argument_list|,
literal|"URIS"
argument_list|)
decl_stmt|;
comment|/** Map {@code Iterable<BigDecimal>} to array of {@link PropertyType#DECIMAL} */
specifier|public
specifier|static
specifier|final
name|Type
argument_list|<
name|Iterable
argument_list|<
name|BigDecimal
argument_list|>
argument_list|>
name|DECIMALS
init|=
name|create
argument_list|(
name|PropertyType
operator|.
name|DECIMAL
argument_list|,
literal|true
argument_list|,
literal|"DECIMALS"
argument_list|)
decl_stmt|;
comment|/** The special "undefined" type, never encountered in normal values */
specifier|public
specifier|static
specifier|final
name|Type
argument_list|<
name|Void
argument_list|>
name|UNDEFINED
init|=
name|create
argument_list|(
name|PropertyType
operator|.
name|UNDEFINED
argument_list|,
literal|false
argument_list|,
literal|"UNDEFINED"
argument_list|)
decl_stmt|;
comment|/** Multi-valued "undefined" type, never encountered in normal values */
specifier|public
specifier|static
specifier|final
name|Type
argument_list|<
name|Iterable
argument_list|<
name|Void
argument_list|>
argument_list|>
name|UNDEFINEDS
init|=
name|create
argument_list|(
name|PropertyType
operator|.
name|UNDEFINED
argument_list|,
literal|true
argument_list|,
literal|"UNDEFINEDS"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|int
name|tag
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|array
decl_stmt|;
specifier|private
specifier|final
name|String
name|string
decl_stmt|;
specifier|private
name|Type
parameter_list|(
name|int
name|tag
parameter_list|,
name|boolean
name|array
parameter_list|,
name|String
name|string
parameter_list|)
block|{
name|this
operator|.
name|tag
operator|=
name|tag
expr_stmt|;
name|this
operator|.
name|array
operator|=
name|array
expr_stmt|;
name|this
operator|.
name|string
operator|=
name|string
expr_stmt|;
block|}
comment|/**      * Corresponding type tag as defined in {@link PropertyType}.      * @return  type tag      */
specifier|public
name|int
name|tag
parameter_list|()
block|{
return|return
name|tag
return|;
block|}
comment|/**      * Determine whether this is an array type      * @return  {@code true} if and only if this is an array type      */
specifier|public
name|boolean
name|isArray
parameter_list|()
block|{
return|return
name|array
return|;
block|}
comment|/**      * Corresponding {@code Type} for a given type tag and array flag.      * @param tag  type tag as defined in {@link PropertyType}.      * @param array  whether this is an array or not      * @return  {@code Type} instance      * @throws IllegalArgumentException if tag is not valid as per definition in {@link PropertyType}.      */
specifier|public
specifier|static
name|Type
argument_list|<
name|?
argument_list|>
name|fromTag
parameter_list|(
name|int
name|tag
parameter_list|,
name|boolean
name|array
parameter_list|)
block|{
switch|switch
condition|(
name|tag
condition|)
block|{
case|case
name|PropertyType
operator|.
name|STRING
case|:
return|return
name|array
condition|?
name|STRINGS
else|:
name|STRING
return|;
case|case
name|PropertyType
operator|.
name|BINARY
case|:
return|return
name|array
condition|?
name|BINARIES
else|:
name|BINARY
return|;
case|case
name|PropertyType
operator|.
name|LONG
case|:
return|return
name|array
condition|?
name|LONGS
else|:
name|LONG
return|;
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
return|return
name|array
condition|?
name|DOUBLES
else|:
name|DOUBLE
return|;
case|case
name|PropertyType
operator|.
name|DATE
case|:
return|return
name|array
condition|?
name|DATES
else|:
name|DATE
return|;
case|case
name|PropertyType
operator|.
name|BOOLEAN
case|:
return|return
name|array
condition|?
name|BOOLEANS
else|:
name|BOOLEAN
return|;
case|case
name|PropertyType
operator|.
name|NAME
case|:
return|return
name|array
condition|?
name|NAMES
else|:
name|NAME
return|;
case|case
name|PropertyType
operator|.
name|PATH
case|:
return|return
name|array
condition|?
name|PATHS
else|:
name|PATH
return|;
case|case
name|PropertyType
operator|.
name|REFERENCE
case|:
return|return
name|array
condition|?
name|REFERENCES
else|:
name|REFERENCE
return|;
case|case
name|PropertyType
operator|.
name|WEAKREFERENCE
case|:
return|return
name|array
condition|?
name|WEAKREFERENCES
else|:
name|WEAKREFERENCE
return|;
case|case
name|PropertyType
operator|.
name|URI
case|:
return|return
name|array
condition|?
name|URIS
else|:
name|URI
return|;
case|case
name|PropertyType
operator|.
name|DECIMAL
case|:
return|return
name|array
condition|?
name|DECIMALS
else|:
name|DECIMAL
return|;
case|case
name|PropertyType
operator|.
name|UNDEFINED
case|:
return|return
name|array
condition|?
name|UNDEFINEDS
else|:
name|UNDEFINED
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid type tag: "
operator|+
name|tag
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns the {@code Type} with the given string representation.      *      * @param string type string      * @return matching type      */
specifier|public
specifier|static
name|Type
argument_list|<
name|?
argument_list|>
name|fromString
parameter_list|(
name|String
name|string
parameter_list|)
block|{
name|Type
argument_list|<
name|?
argument_list|>
name|type
init|=
name|TYPES
operator|.
name|get
argument_list|(
name|string
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid type name: "
operator|+
name|string
argument_list|)
throw|;
block|}
return|return
name|type
return|;
block|}
comment|/**      * Determine the base type of array types      * @return  base type      * @throws IllegalStateException if {@code isArray} is false.      */
specifier|public
name|Type
argument_list|<
name|?
argument_list|>
name|getBaseType
parameter_list|()
block|{
name|checkState
argument_list|(
name|isArray
argument_list|()
argument_list|,
literal|"Not an array"
argument_list|)
expr_stmt|;
return|return
name|fromTag
argument_list|(
name|tag
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * Determine the array type which has this type as base type      * @return  array type with this type as base type      * @throws IllegalStateException if {@code isArray} is true.      */
specifier|public
name|Type
argument_list|<
name|?
argument_list|>
name|getArrayType
parameter_list|()
block|{
name|checkState
argument_list|(
operator|!
name|isArray
argument_list|()
argument_list|,
literal|"Not a simply type"
argument_list|)
expr_stmt|;
return|return
name|fromTag
argument_list|(
name|tag
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|//--------------------------------------------------------< Comparable>--
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
annotation|@
name|NotNull
name|Type
argument_list|<
name|?
argument_list|>
name|that
parameter_list|)
block|{
if|if
condition|(
name|tag
operator|<
name|that
operator|.
name|tag
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
if|if
condition|(
name|tag
operator|>
name|that
operator|.
name|tag
condition|)
block|{
return|return
literal|1
return|;
block|}
if|if
condition|(
operator|!
name|array
operator|&&
name|that
operator|.
name|array
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
if|if
condition|(
name|array
operator|&&
operator|!
name|that
operator|.
name|array
condition|)
block|{
return|return
literal|1
return|;
block|}
return|return
literal|0
return|;
block|}
comment|//------------------------------------------------------------< Object>--
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|string
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|result
operator|+
literal|31
operator|*
name|tag
expr_stmt|;
name|result
operator|=
name|result
operator|+
literal|31
operator|*
name|hashCode
argument_list|(
name|array
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|private
name|int
name|hashCode
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
return|return
name|value
condition|?
literal|1231
else|:
literal|1237
return|;
block|}
specifier|private
name|void
name|checkState
parameter_list|(
name|boolean
name|condition
parameter_list|,
name|String
name|message
parameter_list|)
block|{
if|if
condition|(
operator|!
name|condition
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|message
argument_list|)
throw|;
block|}
block|}
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
return|return
name|this
operator|==
name|other
return|;
block|}
block|}
end_class

end_unit

