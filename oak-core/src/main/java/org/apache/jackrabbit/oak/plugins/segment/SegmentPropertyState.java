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
name|plugins
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
class|class
name|SegmentPropertyState
extends|extends
name|AbstractPropertyState
block|{
specifier|private
specifier|final
name|PropertyTemplate
name|template
decl_stmt|;
specifier|private
specifier|final
name|SegmentReader
name|reader
decl_stmt|;
specifier|private
specifier|final
name|RecordId
name|recordId
decl_stmt|;
specifier|public
name|SegmentPropertyState
parameter_list|(
name|PropertyTemplate
name|template
parameter_list|,
name|SegmentReader
name|reader
parameter_list|,
name|RecordId
name|recordId
parameter_list|)
block|{
name|this
operator|.
name|template
operator|=
name|checkNotNull
argument_list|(
name|template
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
name|recordId
operator|=
name|checkNotNull
argument_list|(
name|recordId
argument_list|)
expr_stmt|;
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
name|template
operator|.
name|getName
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
name|template
operator|.
name|getType
argument_list|()
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
name|reader
operator|.
name|readInt
argument_list|(
name|recordId
argument_list|,
literal|0
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
if|if
condition|(
name|type
operator|.
name|isArray
argument_list|()
condition|)
block|{
specifier|final
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
return|return
operator|(
name|T
operator|)
operator|new
name|Iterable
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Object
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|private
name|int
name|index
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|index
operator|<
name|count
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|next
parameter_list|()
block|{
if|if
condition|(
name|hasNext
argument_list|()
condition|)
block|{
return|return
name|getValue
argument_list|(
name|base
argument_list|,
name|index
operator|++
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
block|}
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
return|;
block|}
block|}
return|;
block|}
else|else
block|{
return|return
name|getValue
argument_list|(
name|type
argument_list|,
literal|0
argument_list|)
return|;
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
name|reader
operator|.
name|getStore
argument_list|()
operator|.
name|readSegment
argument_list|(
name|recordId
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
name|Type
argument_list|<
name|?
argument_list|>
name|base
decl_stmt|;
name|ListRecord
name|values
decl_stmt|;
if|if
condition|(
name|isArray
argument_list|()
condition|)
block|{
name|base
operator|=
name|getType
argument_list|()
operator|.
name|getBaseType
argument_list|()
expr_stmt|;
name|int
name|size
init|=
name|segment
operator|.
name|readInt
argument_list|(
name|recordId
operator|.
name|getOffset
argument_list|()
argument_list|)
decl_stmt|;
name|RecordId
name|listId
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|recordId
operator|.
name|getOffset
argument_list|()
operator|+
literal|4
argument_list|)
decl_stmt|;
name|values
operator|=
operator|new
name|ListRecord
argument_list|(
name|listId
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|base
operator|=
name|getType
argument_list|()
expr_stmt|;
name|values
operator|=
operator|new
name|ListRecord
argument_list|(
name|recordId
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
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
name|valueId
init|=
name|values
operator|.
name|getEntry
argument_list|(
name|reader
argument_list|,
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|BINARY
condition|)
block|{
return|return
operator|(
name|T
operator|)
operator|new
name|SegmentBlob
argument_list|(
name|reader
argument_list|,
name|valueId
argument_list|)
return|;
block|}
else|else
block|{
name|String
name|value
init|=
name|segment
operator|.
name|readString
argument_list|(
name|valueId
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|STRING
operator|||
name|type
operator|==
name|Type
operator|.
name|URI
operator|||
name|type
operator|==
name|Type
operator|.
name|NAME
operator|||
name|type
operator|==
name|Type
operator|.
name|PATH
operator|||
name|type
operator|==
name|Type
operator|.
name|REFERENCE
operator|||
name|type
operator|==
name|Type
operator|.
name|WEAKREFERENCE
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|value
return|;
block|}
else|else
block|{
name|Converter
name|converter
init|=
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|base
operator|==
name|Type
operator|.
name|DATE
condition|)
block|{
name|converter
operator|=
name|Conversions
operator|.
name|convert
argument_list|(
name|converter
operator|.
name|toCalendar
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|base
operator|==
name|Type
operator|.
name|DECIMAL
condition|)
block|{
name|converter
operator|=
name|Conversions
operator|.
name|convert
argument_list|(
name|converter
operator|.
name|toDecimal
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|base
operator|==
name|Type
operator|.
name|DOUBLE
condition|)
block|{
name|converter
operator|=
name|Conversions
operator|.
name|convert
argument_list|(
name|converter
operator|.
name|toDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|base
operator|==
name|Type
operator|.
name|LONG
condition|)
block|{
name|converter
operator|=
name|Conversions
operator|.
name|convert
argument_list|(
name|converter
operator|.
name|toLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|==
name|Type
operator|.
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
name|Type
operator|.
name|DATE
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|converter
operator|.
name|toDate
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
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
name|Type
operator|.
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
name|Type
operator|.
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
decl_stmt|;
if|if
condition|(
name|isArray
argument_list|()
condition|)
block|{
name|int
name|size
init|=
name|reader
operator|.
name|readInt
argument_list|(
name|recordId
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|RecordId
name|listId
init|=
name|reader
operator|.
name|readRecordId
argument_list|(
name|recordId
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|values
operator|=
operator|new
name|ListRecord
argument_list|(
name|listId
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|values
operator|=
operator|new
name|ListRecord
argument_list|(
name|recordId
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
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
name|reader
operator|.
name|readLength
argument_list|(
name|values
operator|.
name|getEntry
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|)
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
return|return
literal|true
return|;
block|}
elseif|else
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
name|recordId
operator|.
name|equals
argument_list|(
name|that
operator|.
name|recordId
argument_list|)
operator|&&
name|template
operator|.
name|equals
argument_list|(
name|that
operator|.
name|template
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
name|super
operator|.
name|equals
argument_list|(
name|object
argument_list|)
return|;
block|}
block|}
end_class

end_unit

