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
name|plugins
operator|.
name|memory
package|;
end_package

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
name|checkState
import|;
end_import

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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_comment
comment|/**  * {@code PropertyBuilder} for building in memory {@code PropertyState} instances.  * @param<T>  */
end_comment

begin_class
specifier|public
class|class
name|PropertyBuilder
parameter_list|<
name|T
parameter_list|>
block|{
specifier|private
specifier|final
name|Type
argument_list|<
name|T
argument_list|>
name|type
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|boolean
name|isArray
decl_stmt|;
specifier|private
name|List
argument_list|<
name|T
argument_list|>
name|values
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
comment|/**      * Create a new instance for building {@code PropertyState} instances      * of the given {@code type}.      * @param type  type of the {@code PropertyState} instances to be built.      * @throws IllegalArgumentException if {@code type.isArray()} is {@code true}.      */
specifier|public
name|PropertyBuilder
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
operator|!
name|type
operator|.
name|isArray
argument_list|()
argument_list|,
literal|"type must not be array"
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|/**      * Create a new instance for building scalar {@code PropertyState} instances      * of the given {@code type}.      * @param type  type of the {@code PropertyState} instances to be built.      * @return {@code PropertyBuilder} for {@code type}      */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|PropertyBuilder
argument_list|<
name|T
argument_list|>
name|scalar
parameter_list|(
name|Type
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
return|return
operator|new
name|PropertyBuilder
argument_list|<
name|T
argument_list|>
argument_list|(
name|type
argument_list|)
return|;
block|}
comment|/**      * Create a new instance for building array {@code PropertyState} instances      * of the given {@code type}.      * @param type  type of the {@code PropertyState} instances to be built.      * @return {@code PropertyBuilder} for {@code type}      */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|PropertyBuilder
argument_list|<
name|T
argument_list|>
name|array
parameter_list|(
name|Type
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
return|return
operator|new
name|PropertyBuilder
argument_list|<
name|T
argument_list|>
argument_list|(
name|type
argument_list|)
operator|.
name|setArray
argument_list|()
return|;
block|}
comment|/**      * Create a new instance for building scalar {@code PropertyState} instances      * of the given {@code type}. The builder is initialised with the      * given {@code name}.      * Equivalent to      *<pre>      *     MemoryPropertyBuilder.create(type).setName(name);      *</pre>      * @param type  type of the {@code PropertyState} instances to be built.      * @param name  initial name      * @return {@code PropertyBuilder} for {@code type}      */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|PropertyBuilder
argument_list|<
name|T
argument_list|>
name|scalar
parameter_list|(
name|Type
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|scalar
argument_list|(
name|type
argument_list|)
operator|.
name|setName
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Create a new instance for building array {@code PropertyState} instances      * of the given {@code type}. The builder is initialised with the      * given {@code name}.      * Equivalent to      *<pre>      *     MemoryPropertyBuilder.create(type).setName(name).setArray();      *</pre>      * @param type  type of the {@code PropertyState} instances to be built.      * @param name  initial name      * @return {@code PropertyBuilder} for {@code type}      */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|PropertyBuilder
argument_list|<
name|T
argument_list|>
name|array
parameter_list|(
name|Type
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|scalar
argument_list|(
name|type
argument_list|)
operator|.
name|setName
argument_list|(
name|name
argument_list|)
operator|.
name|setArray
argument_list|()
return|;
block|}
comment|/**      * Create a new instance for building {@code PropertyState} instances      * of the given {@code type}. The builder is initialised with the name and      * the values of {@code property}.      * Equivalent to      *<pre>      *     PropertyBuilder.scalar(type).assignFrom(property);      *</pre>      *      * @param type  type of the {@code PropertyState} instances to be built.      * @param property  initial name and values      * @return {@code PropertyBuilder} for {@code type}      */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|PropertyBuilder
argument_list|<
name|T
argument_list|>
name|copy
parameter_list|(
name|Type
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
name|PropertyState
name|property
parameter_list|)
block|{
return|return
name|scalar
argument_list|(
name|type
argument_list|)
operator|.
name|assignFrom
argument_list|(
name|property
argument_list|)
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|T
name|getValue
parameter_list|()
block|{
return|return
name|values
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
annotation|@
name|NotNull
specifier|public
name|List
argument_list|<
name|T
argument_list|>
name|getValues
parameter_list|()
block|{
return|return
name|Lists
operator|.
name|newArrayList
argument_list|(
name|values
argument_list|)
return|;
block|}
specifier|public
name|T
name|getValue
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
specifier|public
name|boolean
name|hasValue
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|values
operator|.
name|contains
argument_list|(
name|value
argument_list|)
return|;
block|}
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
specifier|public
name|boolean
name|isArray
parameter_list|()
block|{
return|return
name|isArray
return|;
block|}
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|count
argument_list|()
operator|==
literal|0
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|NotNull
specifier|public
name|PropertyState
name|getPropertyState
parameter_list|()
block|{
name|checkState
argument_list|(
name|name
operator|!=
literal|null
argument_list|,
literal|"Property has no name"
argument_list|)
expr_stmt|;
name|checkState
argument_list|(
name|isArray
argument_list|()
operator|||
name|values
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|,
literal|"Property has multiple values"
argument_list|)
expr_stmt|;
if|if
condition|(
name|values
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|EmptyPropertyState
operator|.
name|emptyProperty
argument_list|(
name|name
argument_list|,
name|Type
operator|.
name|fromTag
argument_list|(
name|type
operator|.
name|tag
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|isArray
argument_list|()
condition|)
block|{
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
name|MultiStringPropertyState
operator|.
name|stringProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Iterable
argument_list|<
name|String
argument_list|>
operator|)
name|values
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|BINARY
case|:
return|return
name|MultiBinaryPropertyState
operator|.
name|binaryPropertyFromBlob
argument_list|(
name|name
argument_list|,
operator|(
name|Iterable
argument_list|<
name|Blob
argument_list|>
operator|)
name|values
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|LONG
case|:
return|return
name|MultiLongPropertyState
operator|.
name|createLongProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Iterable
argument_list|<
name|Long
argument_list|>
operator|)
name|values
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
return|return
name|MultiDoublePropertyState
operator|.
name|doubleProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Iterable
argument_list|<
name|Double
argument_list|>
operator|)
name|values
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|BOOLEAN
case|:
return|return
name|MultiBooleanPropertyState
operator|.
name|booleanProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Iterable
argument_list|<
name|Boolean
argument_list|>
operator|)
name|values
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DECIMAL
case|:
return|return
name|MultiDecimalPropertyState
operator|.
name|decimalProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Iterable
argument_list|<
name|BigDecimal
argument_list|>
operator|)
name|values
argument_list|)
return|;
default|default:
return|return
operator|new
name|MultiGenericPropertyState
argument_list|(
name|name
argument_list|,
operator|(
name|Iterable
argument_list|<
name|String
argument_list|>
operator|)
name|values
argument_list|,
name|Type
operator|.
name|fromTag
argument_list|(
name|type
operator|.
name|tag
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
block|}
block|}
else|else
block|{
name|T
name|value
init|=
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
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
name|StringPropertyState
operator|.
name|stringProperty
argument_list|(
name|name
argument_list|,
operator|(
name|String
operator|)
name|value
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|BINARY
case|:
return|return
name|BinaryPropertyState
operator|.
name|binaryProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Blob
operator|)
name|value
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|LONG
case|:
return|return
name|LongPropertyState
operator|.
name|createLongProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Long
operator|)
name|value
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
return|return
name|DoublePropertyState
operator|.
name|doubleProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Double
operator|)
name|value
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|BOOLEAN
case|:
return|return
name|BooleanPropertyState
operator|.
name|booleanProperty
argument_list|(
name|name
argument_list|,
operator|(
name|Boolean
operator|)
name|value
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DECIMAL
case|:
return|return
name|DecimalPropertyState
operator|.
name|decimalProperty
argument_list|(
name|name
argument_list|,
operator|(
name|BigDecimal
operator|)
name|value
argument_list|)
return|;
default|default:
return|return
operator|new
name|GenericPropertyState
argument_list|(
name|name
argument_list|,
operator|(
name|String
operator|)
name|value
argument_list|,
name|type
argument_list|)
return|;
block|}
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|NotNull
specifier|public
name|PropertyBuilder
argument_list|<
name|T
argument_list|>
name|assignFrom
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
if|if
condition|(
name|property
operator|!=
literal|null
condition|)
block|{
name|setName
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|property
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|isArray
operator|=
literal|true
expr_stmt|;
name|setValues
argument_list|(
operator|(
name|Iterable
argument_list|<
name|T
argument_list|>
operator|)
name|property
operator|.
name|getValue
argument_list|(
name|type
operator|.
name|getArrayType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|isArray
operator|=
literal|false
expr_stmt|;
name|setValue
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|this
return|;
block|}
annotation|@
name|NotNull
specifier|public
name|PropertyBuilder
argument_list|<
name|T
argument_list|>
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|NotNull
specifier|public
name|PropertyBuilder
argument_list|<
name|T
argument_list|>
name|setArray
parameter_list|()
block|{
name|isArray
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|NotNull
specifier|public
name|PropertyBuilder
argument_list|<
name|T
argument_list|>
name|setScalar
parameter_list|()
block|{
name|isArray
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|NotNull
specifier|public
name|PropertyBuilder
argument_list|<
name|T
argument_list|>
name|setValue
parameter_list|(
name|T
name|value
parameter_list|)
block|{
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|NotNull
specifier|public
name|PropertyBuilder
argument_list|<
name|T
argument_list|>
name|addValue
parameter_list|(
name|T
name|value
parameter_list|)
block|{
name|values
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|NotNull
specifier|public
name|PropertyBuilder
argument_list|<
name|T
argument_list|>
name|addValues
parameter_list|(
name|Iterable
argument_list|<
name|T
argument_list|>
name|values
parameter_list|)
block|{
name|Iterables
operator|.
name|addAll
argument_list|(
name|this
operator|.
name|values
argument_list|,
name|values
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|NotNull
specifier|public
name|PropertyBuilder
argument_list|<
name|T
argument_list|>
name|setValue
parameter_list|(
name|T
name|value
parameter_list|,
name|int
name|index
parameter_list|)
block|{
name|values
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|NotNull
specifier|public
name|PropertyBuilder
argument_list|<
name|T
argument_list|>
name|setValues
parameter_list|(
name|Iterable
argument_list|<
name|T
argument_list|>
name|values
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|values
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|NotNull
specifier|public
name|PropertyBuilder
argument_list|<
name|T
argument_list|>
name|removeValue
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|values
operator|.
name|remove
argument_list|(
name|index
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|NotNull
specifier|public
name|PropertyBuilder
argument_list|<
name|T
argument_list|>
name|removeValue
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
name|values
operator|.
name|remove
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

