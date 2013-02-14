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
name|checkPositionIndexes
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
name|plugins
operator|.
name|segment
operator|.
name|SegmentWriter
operator|.
name|BLOCK_SIZE
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
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
name|PropertyStates
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
name|cache
operator|.
name|CacheBuilder
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
name|cache
operator|.
name|CacheLoader
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
name|cache
operator|.
name|LoadingCache
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
name|cache
operator|.
name|Weigher
import|;
end_import

begin_class
specifier|public
class|class
name|SegmentReader
block|{
specifier|private
specifier|final
name|SegmentStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|RecordId
argument_list|,
name|String
argument_list|>
name|strings
init|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|maximumWeight
argument_list|(
literal|1
operator|<<
literal|20
argument_list|)
comment|// 1 MB
operator|.
name|weigher
argument_list|(
name|newStringWeigher
argument_list|()
argument_list|)
operator|.
name|build
argument_list|(
name|newStringLoader
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|RecordId
argument_list|,
name|NodeTemplate
argument_list|>
name|templates
init|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|maximumSize
argument_list|(
literal|1000
argument_list|)
operator|.
name|build
argument_list|(
name|newTemplateLoader
argument_list|()
argument_list|)
decl_stmt|;
specifier|public
name|SegmentReader
parameter_list|(
name|SegmentStore
name|store
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
block|}
specifier|private
specifier|static
name|Weigher
argument_list|<
name|RecordId
argument_list|,
name|String
argument_list|>
name|newStringWeigher
parameter_list|()
block|{
return|return
operator|new
name|Weigher
argument_list|<
name|RecordId
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|weigh
parameter_list|(
name|RecordId
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
literal|32
operator|+
name|value
operator|.
name|length
argument_list|()
operator|*
literal|2
return|;
block|}
block|}
return|;
block|}
specifier|private
name|CacheLoader
argument_list|<
name|RecordId
argument_list|,
name|String
argument_list|>
name|newStringLoader
parameter_list|()
block|{
return|return
operator|new
name|CacheLoader
argument_list|<
name|RecordId
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|load
parameter_list|(
name|RecordId
name|key
parameter_list|)
throws|throws
name|Exception
block|{
name|SegmentStream
name|stream
init|=
name|readStream
argument_list|(
name|key
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|stream
operator|.
name|getString
argument_list|()
return|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
specifier|private
name|CacheLoader
argument_list|<
name|RecordId
argument_list|,
name|NodeTemplate
argument_list|>
name|newTemplateLoader
parameter_list|()
block|{
return|return
operator|new
name|CacheLoader
argument_list|<
name|RecordId
argument_list|,
name|NodeTemplate
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeTemplate
name|load
parameter_list|(
name|RecordId
name|key
parameter_list|)
throws|throws
name|Exception
block|{
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|key
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|offset
init|=
name|key
operator|.
name|getOffset
argument_list|()
decl_stmt|;
name|int
name|head
init|=
name|segment
operator|.
name|readInt
argument_list|(
name|offset
argument_list|)
decl_stmt|;
name|boolean
name|hasPrimaryType
init|=
operator|(
name|head
operator|&
operator|(
literal|1
operator|<<
literal|31
operator|)
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|hasMixinTypes
init|=
operator|(
name|head
operator|&
operator|(
literal|1
operator|<<
literal|30
operator|)
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|zeroChildNodes
init|=
operator|(
name|head
operator|&
operator|(
literal|1
operator|<<
literal|29
operator|)
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|manyChildNodes
init|=
operator|(
name|head
operator|&
operator|(
literal|1
operator|<<
literal|28
operator|)
operator|)
operator|!=
literal|0
decl_stmt|;
name|int
name|mixinCount
init|=
operator|(
name|head
operator|>>
literal|18
operator|)
operator|&
operator|(
operator|(
literal|1
operator|<<
literal|10
operator|)
operator|-
literal|1
operator|)
decl_stmt|;
name|int
name|propertyCount
init|=
name|head
operator|&
operator|(
operator|(
literal|1
operator|<<
literal|18
operator|)
operator|-
literal|1
operator|)
decl_stmt|;
name|offset
operator|+=
literal|4
expr_stmt|;
name|PropertyState
name|primaryType
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|hasPrimaryType
condition|)
block|{
name|RecordId
name|primaryId
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|offset
argument_list|)
decl_stmt|;
name|primaryType
operator|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
name|readString
argument_list|(
name|primaryId
argument_list|)
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|offset
operator|+=
literal|4
expr_stmt|;
block|}
name|PropertyState
name|mixinTypes
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|hasMixinTypes
condition|)
block|{
name|String
index|[]
name|mixins
init|=
operator|new
name|String
index|[
name|mixinCount
index|]
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
name|mixins
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|RecordId
name|mixinId
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|offset
argument_list|)
decl_stmt|;
name|mixins
index|[
name|i
index|]
operator|=
name|readString
argument_list|(
name|mixinId
argument_list|)
expr_stmt|;
name|offset
operator|+=
literal|4
expr_stmt|;
block|}
name|mixinTypes
operator|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"jcr:mixinTypes"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|mixins
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
expr_stmt|;
block|}
name|String
name|childName
init|=
name|NodeTemplate
operator|.
name|ZERO_CHILD_NODES
decl_stmt|;
if|if
condition|(
name|manyChildNodes
condition|)
block|{
name|childName
operator|=
name|NodeTemplate
operator|.
name|MANY_CHILD_NODES
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|zeroChildNodes
condition|)
block|{
name|RecordId
name|childNameId
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|offset
argument_list|)
decl_stmt|;
name|childName
operator|=
name|readString
argument_list|(
name|childNameId
argument_list|)
expr_stmt|;
name|offset
operator|+=
literal|4
expr_stmt|;
block|}
name|PropertyTemplate
index|[]
name|properties
init|=
operator|new
name|PropertyTemplate
index|[
name|propertyCount
index|]
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
name|properties
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|RecordId
name|propertyNameId
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|offset
argument_list|)
decl_stmt|;
name|byte
name|type
init|=
name|segment
operator|.
name|readByte
argument_list|(
name|offset
operator|+
literal|4
argument_list|)
decl_stmt|;
name|properties
index|[
name|i
index|]
operator|=
operator|new
name|PropertyTemplate
argument_list|(
name|readString
argument_list|(
name|propertyNameId
argument_list|)
argument_list|,
name|Type
operator|.
name|fromTag
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|type
argument_list|)
argument_list|,
name|type
operator|<
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|offset
operator|+=
literal|5
expr_stmt|;
block|}
return|return
operator|new
name|NodeTemplate
argument_list|(
name|primaryType
argument_list|,
name|mixinTypes
argument_list|,
name|properties
argument_list|,
name|childName
argument_list|)
return|;
block|}
block|}
return|;
block|}
specifier|public
name|NodeTemplate
name|readTemplate
parameter_list|(
name|RecordId
name|recordId
parameter_list|)
block|{
try|try
block|{
return|return
name|templates
operator|.
name|get
argument_list|(
name|recordId
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to access template record "
operator|+
name|recordId
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|String
name|readString
parameter_list|(
name|RecordId
name|recordId
parameter_list|)
block|{
try|try
block|{
return|return
name|strings
operator|.
name|get
argument_list|(
name|recordId
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to access string record "
operator|+
name|recordId
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|long
name|readLength
parameter_list|(
name|RecordId
name|recordId
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|recordId
argument_list|)
expr_stmt|;
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|recordId
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|readLength
argument_list|(
name|segment
argument_list|,
name|recordId
operator|.
name|getOffset
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|long
name|readLength
parameter_list|(
name|Segment
name|segment
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|int
name|length
init|=
name|segment
operator|.
name|readByte
argument_list|(
name|offset
operator|++
argument_list|)
operator|&
literal|0xff
decl_stmt|;
if|if
condition|(
operator|(
name|length
operator|&
literal|0x80
operator|)
operator|==
literal|0
condition|)
block|{
return|return
name|length
return|;
block|}
elseif|else
if|if
condition|(
operator|(
name|length
operator|&
literal|0x40
operator|)
operator|==
literal|0
condition|)
block|{
return|return
operator|(
operator|(
name|length
operator|&
literal|0x3f
operator|)
operator|<<
literal|8
operator||
name|segment
operator|.
name|readByte
argument_list|(
name|offset
argument_list|)
operator|&
literal|0xff
operator|)
operator|+
literal|0x80
return|;
block|}
else|else
block|{
return|return
operator|(
operator|(
operator|(
name|long
operator|)
name|length
operator|&
literal|0x3f
operator|)
operator|<<
literal|56
operator||
operator|(
call|(
name|long
call|)
argument_list|(
name|segment
operator|.
name|readByte
argument_list|(
name|offset
operator|++
argument_list|)
operator|&
literal|0xff
argument_list|)
operator|)
operator|<<
literal|48
operator||
operator|(
call|(
name|long
call|)
argument_list|(
name|segment
operator|.
name|readByte
argument_list|(
name|offset
operator|++
argument_list|)
operator|&
literal|0xff
argument_list|)
operator|)
operator|<<
literal|40
operator||
operator|(
call|(
name|long
call|)
argument_list|(
name|segment
operator|.
name|readByte
argument_list|(
name|offset
operator|++
argument_list|)
operator|&
literal|0xff
argument_list|)
operator|)
operator|<<
literal|32
operator||
operator|(
call|(
name|long
call|)
argument_list|(
name|segment
operator|.
name|readByte
argument_list|(
name|offset
operator|++
argument_list|)
operator|&
literal|0xff
argument_list|)
operator|)
operator|<<
literal|24
operator||
operator|(
call|(
name|long
call|)
argument_list|(
name|segment
operator|.
name|readByte
argument_list|(
name|offset
operator|++
argument_list|)
operator|&
literal|0xff
argument_list|)
operator|)
operator|<<
literal|16
operator||
operator|(
call|(
name|long
call|)
argument_list|(
name|segment
operator|.
name|readByte
argument_list|(
name|offset
operator|++
argument_list|)
operator|&
literal|0xff
argument_list|)
operator|)
operator|<<
literal|8
operator||
operator|(
call|(
name|long
call|)
argument_list|(
name|segment
operator|.
name|readByte
argument_list|(
name|offset
argument_list|)
operator|&
literal|0xff
argument_list|)
operator|)
operator|)
operator|+
literal|0x4080
return|;
block|}
block|}
specifier|public
name|SegmentStream
name|readStream
parameter_list|(
name|RecordId
name|recordId
parameter_list|)
block|{
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|recordId
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|offset
init|=
name|recordId
operator|.
name|getOffset
argument_list|()
decl_stmt|;
name|long
name|length
init|=
name|readLength
argument_list|(
name|segment
argument_list|,
name|offset
argument_list|)
decl_stmt|;
if|if
condition|(
name|length
operator|<
literal|0x4080
condition|)
block|{
if|if
condition|(
name|length
operator|<
literal|0x80
condition|)
block|{
name|offset
operator|+=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|offset
operator|+=
literal|2
expr_stmt|;
block|}
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|length
index|]
decl_stmt|;
name|segment
operator|.
name|readBytes
argument_list|(
name|offset
argument_list|,
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
operator|new
name|SegmentStream
argument_list|(
name|recordId
argument_list|,
name|data
argument_list|)
return|;
block|}
else|else
block|{
name|int
name|size
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|length
operator|+
name|BLOCK_SIZE
operator|-
literal|1
operator|)
operator|/
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|ListRecord
name|list
init|=
operator|new
name|ListRecord
argument_list|(
name|segment
operator|.
name|readRecordId
argument_list|(
name|offset
operator|+
literal|8
argument_list|)
argument_list|,
name|size
argument_list|)
decl_stmt|;
return|return
operator|new
name|SegmentStream
argument_list|(
name|this
argument_list|,
name|recordId
argument_list|,
name|list
argument_list|,
name|length
argument_list|)
return|;
block|}
block|}
specifier|public
name|byte
name|readByte
parameter_list|(
name|RecordId
name|recordId
parameter_list|,
name|int
name|position
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|recordId
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|position
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|recordId
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|segment
operator|.
name|readByte
argument_list|(
name|recordId
operator|.
name|getOffset
argument_list|()
operator|+
name|position
argument_list|)
return|;
block|}
specifier|public
name|int
name|readInt
parameter_list|(
name|RecordId
name|recordId
parameter_list|,
name|int
name|position
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|recordId
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|position
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|recordId
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|segment
operator|.
name|readInt
argument_list|(
name|recordId
operator|.
name|getOffset
argument_list|()
operator|+
name|position
argument_list|)
return|;
block|}
specifier|public
name|long
name|readLong
parameter_list|(
name|RecordId
name|recordId
parameter_list|,
name|int
name|position
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|recordId
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|position
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|recordId
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|segment
operator|.
name|readLong
argument_list|(
name|recordId
operator|.
name|getOffset
argument_list|()
operator|+
name|position
argument_list|)
return|;
block|}
specifier|public
name|void
name|readBytes
parameter_list|(
name|RecordId
name|recordId
parameter_list|,
name|int
name|position
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|recordId
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|position
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|checkPositionIndexes
argument_list|(
name|offset
argument_list|,
name|offset
operator|+
name|length
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
expr_stmt|;
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|recordId
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
name|segment
operator|.
name|readBytes
argument_list|(
name|recordId
operator|.
name|getOffset
argument_list|()
operator|+
name|position
argument_list|,
name|buffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RecordId
name|readRecordId
parameter_list|(
name|RecordId
name|recordId
parameter_list|,
name|int
name|position
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|recordId
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|position
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|recordId
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|segment
operator|.
name|readRecordId
argument_list|(
name|recordId
operator|.
name|getOffset
argument_list|()
operator|+
name|position
argument_list|)
return|;
block|}
specifier|public
name|ListRecord
name|readList
parameter_list|(
name|RecordId
name|recordId
parameter_list|,
name|int
name|numberOfEntries
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|recordId
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|numberOfEntries
operator|>=
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|numberOfEntries
operator|>
literal|0
condition|)
block|{
name|Segment
name|segment
init|=
name|store
operator|.
name|readSegment
argument_list|(
name|recordId
operator|.
name|getSegmentId
argument_list|()
argument_list|)
decl_stmt|;
name|RecordId
name|id
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|recordId
operator|.
name|getOffset
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|ListRecord
argument_list|(
name|id
argument_list|,
name|numberOfEntries
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|ListRecord
argument_list|(
name|recordId
argument_list|,
name|numberOfEntries
argument_list|)
return|;
block|}
block|}
specifier|public
name|BlockRecord
name|readBlock
parameter_list|(
name|RecordId
name|recordId
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|recordId
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|size
operator|>
literal|0
argument_list|)
expr_stmt|;
return|return
operator|new
name|BlockRecord
argument_list|(
name|recordId
argument_list|,
name|size
argument_list|)
return|;
block|}
block|}
end_class

end_unit

