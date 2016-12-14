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
name|segment
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
name|checkElementIndex
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|newArrayListWithCapacity
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
name|collect
operator|.
name|Maps
operator|.
name|newHashMap
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyMap
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonList
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
name|BINARY
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
name|BOOLEAN
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
name|DATE
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
name|DECIMAL
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
name|DOUBLE
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
name|LONG
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
name|NAME
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
name|PATH
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
name|REFERENCE
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
name|URI
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
name|WEAKREFERENCE
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
name|memory
operator|.
name|AbstractPropertyState
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

begin_comment
comment|/**  * A property, which can read a value or list record from a segment. It  * currently doesn't cache data.  *<p>  * Depending on the property type, this is a record of type "VALUE" or a record  * of type "LIST" (for arrays).  */
end_comment

begin_class
specifier|public
class|class
name|SegmentPropertyState
extends|extends
name|Record
implements|implements
name|PropertyState
block|{
annotation|@
name|Nonnull
specifier|private
specifier|final
name|SegmentReader
name|reader
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|Type
argument_list|<
name|?
argument_list|>
name|type
decl_stmt|;
name|SegmentPropertyState
parameter_list|(
annotation|@
name|Nonnull
name|SegmentReader
name|reader
parameter_list|,
annotation|@
name|Nonnull
name|RecordId
name|id
parameter_list|,
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|Type
argument_list|<
name|?
argument_list|>
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|checkNotNull
argument_list|(
name|reader
argument_list|)
expr_stmt|;
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
name|checkNotNull
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
name|SegmentPropertyState
parameter_list|(
annotation|@
name|Nonnull
name|SegmentReader
name|reader
parameter_list|,
annotation|@
name|Nonnull
name|RecordId
name|id
parameter_list|,
annotation|@
name|Nonnull
name|PropertyTemplate
name|template
parameter_list|)
block|{
name|this
argument_list|(
name|reader
argument_list|,
name|id
argument_list|,
name|template
operator|.
name|getName
argument_list|()
argument_list|,
name|template
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|ListRecord
name|getValueList
parameter_list|(
name|Segment
name|segment
parameter_list|)
block|{
name|RecordId
name|listId
init|=
name|getRecordId
argument_list|()
decl_stmt|;
name|int
name|size
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|isArray
argument_list|()
condition|)
block|{
name|size
operator|=
name|segment
operator|.
name|readInt
argument_list|(
name|getRecordNumber
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
name|listId
operator|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|getRecordNumber
argument_list|()
argument_list|,
literal|4
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|ListRecord
argument_list|(
name|listId
argument_list|,
name|size
argument_list|)
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|RecordId
argument_list|>
name|getValueRecords
parameter_list|()
block|{
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
name|emptyMap
argument_list|()
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|RecordId
argument_list|>
name|map
init|=
name|newHashMap
argument_list|()
decl_stmt|;
name|Segment
name|segment
init|=
name|getSegment
argument_list|()
decl_stmt|;
name|ListRecord
name|values
init|=
name|getValueList
argument_list|(
name|segment
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RecordId
name|valueId
init|=
name|values
operator|.
name|getEntry
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|reader
operator|.
name|readString
argument_list|(
name|valueId
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|value
argument_list|,
name|valueId
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
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
name|Nonnull
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
name|Override
specifier|public
name|boolean
name|isArray
parameter_list|()
block|{
return|return
name|getType
argument_list|()
operator|.
name|isArray
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|count
parameter_list|()
block|{
if|if
condition|(
name|isArray
argument_list|()
condition|)
block|{
return|return
name|getSegment
argument_list|()
operator|.
name|readInt
argument_list|(
name|getRecordNumber
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|1
return|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
name|Segment
name|segment
init|=
name|getSegment
argument_list|()
decl_stmt|;
if|if
condition|(
name|isArray
argument_list|()
condition|)
block|{
name|checkState
argument_list|(
name|type
operator|.
name|isArray
argument_list|()
argument_list|)
expr_stmt|;
name|ListRecord
name|values
init|=
name|getValueList
argument_list|(
name|segment
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|emptyList
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|values
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|singletonList
argument_list|(
name|getValue
argument_list|(
name|values
operator|.
name|getEntry
argument_list|(
literal|0
argument_list|)
argument_list|,
name|type
operator|.
name|getBaseType
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
name|Type
argument_list|<
name|?
argument_list|>
name|base
init|=
name|type
operator|.
name|getBaseType
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|list
init|=
name|newArrayListWithCapacity
argument_list|(
name|values
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|RecordId
name|id
range|:
name|values
operator|.
name|getEntries
argument_list|()
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|getValue
argument_list|(
name|id
argument_list|,
name|base
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|T
operator|)
name|list
return|;
block|}
block|}
else|else
block|{
name|RecordId
name|id
init|=
name|getRecordId
argument_list|()
decl_stmt|;
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
name|singletonList
argument_list|(
name|getValue
argument_list|(
name|id
argument_list|,
name|type
operator|.
name|getBaseType
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|getValue
argument_list|(
name|id
argument_list|,
name|type
argument_list|)
return|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
name|size
argument_list|(
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
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
name|checkNotNull
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
operator|!
name|type
operator|.
name|isArray
argument_list|()
argument_list|,
literal|"Type must not be an array type"
argument_list|)
expr_stmt|;
name|Segment
name|segment
init|=
name|getSegment
argument_list|()
decl_stmt|;
name|ListRecord
name|values
init|=
name|getValueList
argument_list|(
name|segment
argument_list|)
decl_stmt|;
name|checkElementIndex
argument_list|(
name|index
argument_list|,
name|values
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|getValue
argument_list|(
name|values
operator|.
name|getEntry
argument_list|(
name|index
argument_list|)
argument_list|,
name|type
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|private
parameter_list|<
name|T
parameter_list|>
name|T
name|getValue
parameter_list|(
name|RecordId
name|id
parameter_list|,
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
operator|==
name|BINARY
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|reader
operator|.
name|readBlob
argument_list|(
name|id
argument_list|)
return|;
comment|// load binaries lazily
block|}
name|String
name|value
init|=
name|reader
operator|.
name|readString
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|STRING
operator|||
name|type
operator|==
name|URI
operator|||
name|type
operator|==
name|DATE
operator|||
name|type
operator|==
name|NAME
operator|||
name|type
operator|==
name|PATH
operator|||
name|type
operator|==
name|REFERENCE
operator|||
name|type
operator|==
name|WEAKREFERENCE
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|value
return|;
comment|// no conversion needed for string types
block|}
name|Type
argument_list|<
name|?
argument_list|>
name|base
init|=
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|base
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|base
operator|=
name|base
operator|.
name|getBaseType
argument_list|()
expr_stmt|;
block|}
name|Converter
name|converter
init|=
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|,
name|base
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|BOOLEAN
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|Boolean
operator|.
name|valueOf
argument_list|(
name|converter
operator|.
name|toBoolean
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|DECIMAL
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|converter
operator|.
name|toDecimal
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|DOUBLE
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|Double
operator|.
name|valueOf
argument_list|(
name|converter
operator|.
name|toDouble
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|LONG
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|Long
operator|.
name|valueOf
argument_list|(
name|converter
operator|.
name|toLong
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Unknown type: "
operator|+
name|type
argument_list|)
throw|;
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
name|ListRecord
name|values
init|=
name|getValueList
argument_list|(
name|getSegment
argument_list|()
argument_list|)
decl_stmt|;
name|checkElementIndex
argument_list|(
name|index
argument_list|,
name|values
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|RecordId
name|entry
init|=
name|values
operator|.
name|getEntry
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|BINARY
argument_list|)
operator|||
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|BINARIES
argument_list|)
condition|)
block|{
return|return
name|reader
operator|.
name|readBlob
argument_list|(
name|entry
argument_list|)
operator|.
name|length
argument_list|()
return|;
block|}
return|return
name|Segment
operator|.
name|readLength
argument_list|(
name|entry
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------< Object>--
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|object
parameter_list|)
block|{
comment|// optimize for common cases
if|if
condition|(
name|this
operator|==
name|object
condition|)
block|{
comment|// don't use fastEquals here due to value sharing
return|return
literal|true
return|;
block|}
if|if
condition|(
name|object
operator|instanceof
name|SegmentPropertyState
condition|)
block|{
name|SegmentPropertyState
name|that
init|=
operator|(
name|SegmentPropertyState
operator|)
name|object
decl_stmt|;
if|if
condition|(
operator|!
name|type
operator|.
name|equals
argument_list|(
name|that
operator|.
name|type
argument_list|)
operator|||
operator|!
name|name
operator|.
name|equals
argument_list|(
name|that
operator|.
name|name
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|getRecordId
argument_list|()
operator|.
name|equals
argument_list|(
name|that
operator|.
name|getRecordId
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
comment|// fall back to default equality check in AbstractPropertyState
return|return
name|object
operator|instanceof
name|PropertyState
operator|&&
name|AbstractPropertyState
operator|.
name|equal
argument_list|(
name|this
argument_list|,
operator|(
name|PropertyState
operator|)
name|object
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|AbstractPropertyState
operator|.
name|hashCode
argument_list|(
name|this
argument_list|)
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
name|AbstractPropertyState
operator|.
name|toString
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

