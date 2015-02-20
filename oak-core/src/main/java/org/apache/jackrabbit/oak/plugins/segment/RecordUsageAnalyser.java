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
name|lang
operator|.
name|System
operator|.
name|arraycopy
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|binarySearch
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
operator|.
name|byteCountToDisplaySize
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
name|plugins
operator|.
name|segment
operator|.
name|ListRecord
operator|.
name|LEVEL_SIZE
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
name|Segment
operator|.
name|MEDIUM_LIMIT
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
name|Segment
operator|.
name|RECORD_ID_BYTES
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
name|Segment
operator|.
name|SMALL_LIMIT
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
name|Template
operator|.
name|MANY_CHILD_NODES
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
name|Template
operator|.
name|ZERO_CHILD_NODES
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
name|SegmentVersion
operator|.
name|V_11
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Formatter
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
name|spi
operator|.
name|state
operator|.
name|ChildNodeEntry
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
name|spi
operator|.
name|state
operator|.
name|NodeState
import|;
end_import

begin_comment
comment|/**  * This utility breaks down space usage per record type.  * It accounts for value sharing. That is, an instance  * of this class will remember which records it has seen  * already and not count those again. Only the effective  * space taken by the records is taken into account. Slack  * space from aligning records is not accounted for.  */
end_comment

begin_class
specifier|public
class|class
name|RecordUsageAnalyser
block|{
specifier|private
name|long
name|mapSize
decl_stmt|;
comment|// leaf and branch
specifier|private
name|long
name|listSize
decl_stmt|;
comment|// list and bucket
specifier|private
name|long
name|valueSize
decl_stmt|;
comment|// inlined values
specifier|private
name|long
name|templateSize
decl_stmt|;
comment|// template
specifier|private
name|long
name|nodeSize
decl_stmt|;
comment|// node
specifier|private
name|long
name|mapCount
decl_stmt|;
specifier|private
name|long
name|listCount
decl_stmt|;
specifier|private
name|long
name|propertyCount
decl_stmt|;
specifier|private
name|long
name|smallBlobCount
decl_stmt|;
specifier|private
name|long
name|mediumBlobCount
decl_stmt|;
specifier|private
name|long
name|longBlobCount
decl_stmt|;
specifier|private
name|long
name|externalBlobCount
decl_stmt|;
specifier|private
name|long
name|smallStringCount
decl_stmt|;
specifier|private
name|long
name|mediumStringCount
decl_stmt|;
specifier|private
name|long
name|longStringCount
decl_stmt|;
specifier|private
name|long
name|templateCount
decl_stmt|;
specifier|private
name|long
name|nodeCount
decl_stmt|;
comment|/**      * @return number of bytes in {@link RecordType#LEAF leaf} and {@link RecordType#BRANCH branch}      * records.      */
specifier|public
name|long
name|getMapSize
parameter_list|()
block|{
return|return
name|mapSize
return|;
block|}
comment|/**      * @return number of bytes in {@link RecordType#LIST list} and {@link RecordType#BUCKET bucket}      * records.      */
specifier|public
name|long
name|getListSize
parameter_list|()
block|{
return|return
name|listSize
return|;
block|}
comment|/**      * @return number of bytes in inlined values (strings and blobs)      */
specifier|public
name|long
name|getValueSize
parameter_list|()
block|{
return|return
name|valueSize
return|;
block|}
comment|/**      * @return number of bytes in {@link RecordType#TEMPLATE template} records.      */
specifier|public
name|long
name|getTemplateSize
parameter_list|()
block|{
return|return
name|templateSize
return|;
block|}
comment|/**      * @return number of bytes in {@link RecordType#NODE node} records.      */
specifier|public
name|long
name|getNodeSize
parameter_list|()
block|{
return|return
name|nodeSize
return|;
block|}
comment|/**      * @return number of maps      */
specifier|public
name|long
name|getMapCount
parameter_list|()
block|{
return|return
name|mapCount
return|;
block|}
comment|/**      * @return number of lists      */
specifier|public
name|long
name|getListCount
parameter_list|()
block|{
return|return
name|listCount
return|;
block|}
comment|/**      * @return number of properties      */
specifier|public
name|long
name|getPropertyCount
parameter_list|()
block|{
return|return
name|propertyCount
return|;
block|}
comment|/**      * @return number of {@link Segment#SMALL_LIMIT small} blobs.      *      */
specifier|public
name|long
name|getSmallBlobCount
parameter_list|()
block|{
return|return
name|smallBlobCount
return|;
block|}
comment|/**      * @return number of {@link Segment#MEDIUM_LIMIT medium} blobs.      *      */
specifier|public
name|long
name|getMediumBlobCount
parameter_list|()
block|{
return|return
name|mediumBlobCount
return|;
block|}
comment|/**      * @return number of long blobs.      *      */
specifier|public
name|long
name|getLongBlobCount
parameter_list|()
block|{
return|return
name|longBlobCount
return|;
block|}
comment|/**      * @return number of external blobs.      *      */
specifier|public
name|long
name|getExternalBlobCount
parameter_list|()
block|{
return|return
name|externalBlobCount
return|;
block|}
comment|/**      * @return number of {@link Segment#SMALL_LIMIT small} strings.      *      */
specifier|public
name|long
name|getSmallStringCount
parameter_list|()
block|{
return|return
name|smallStringCount
return|;
block|}
comment|/**      * @return number of {@link Segment#MEDIUM_LIMIT medium} strings.      *      */
specifier|public
name|long
name|getMediumStringCount
parameter_list|()
block|{
return|return
name|mediumStringCount
return|;
block|}
comment|/**      * @return number of long strings.      *      */
specifier|public
name|long
name|getLongStringCount
parameter_list|()
block|{
return|return
name|longStringCount
return|;
block|}
comment|/**      * @return number of templates.      */
specifier|public
name|long
name|getTemplateCount
parameter_list|()
block|{
return|return
name|templateCount
return|;
block|}
comment|/**      * @return number of nodes.      */
specifier|public
name|long
name|getNodeCount
parameter_list|()
block|{
return|return
name|nodeCount
return|;
block|}
specifier|public
name|void
name|analyseNode
parameter_list|(
name|RecordId
name|nodeId
parameter_list|)
block|{
if|if
condition|(
name|notSeen
argument_list|(
name|nodeId
argument_list|)
condition|)
block|{
name|nodeCount
operator|++
expr_stmt|;
name|Segment
name|segment
init|=
name|nodeId
operator|.
name|getSegment
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
name|nodeId
operator|.
name|getOffset
argument_list|()
decl_stmt|;
name|RecordId
name|templateId
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|offset
argument_list|)
decl_stmt|;
name|analyseTemplate
argument_list|(
name|templateId
argument_list|)
expr_stmt|;
name|Template
name|template
init|=
name|segment
operator|.
name|readTemplate
argument_list|(
name|templateId
argument_list|)
decl_stmt|;
comment|// Recurses into child nodes in this segment
if|if
condition|(
name|template
operator|.
name|getChildName
argument_list|()
operator|==
name|MANY_CHILD_NODES
condition|)
block|{
name|RecordId
name|childMapId
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|offset
operator|+
name|RECORD_ID_BYTES
argument_list|)
decl_stmt|;
name|MapRecord
name|childMap
init|=
name|segment
operator|.
name|readMap
argument_list|(
name|childMapId
argument_list|)
decl_stmt|;
name|analyseMap
argument_list|(
name|childMapId
argument_list|,
name|childMap
argument_list|)
expr_stmt|;
for|for
control|(
name|ChildNodeEntry
name|childNodeEntry
range|:
name|childMap
operator|.
name|getEntries
argument_list|()
control|)
block|{
name|NodeState
name|child
init|=
name|childNodeEntry
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
if|if
condition|(
name|child
operator|instanceof
name|SegmentNodeState
condition|)
block|{
name|RecordId
name|childId
init|=
operator|(
operator|(
name|SegmentNodeState
operator|)
name|child
operator|)
operator|.
name|getRecordId
argument_list|()
decl_stmt|;
name|analyseNode
argument_list|(
name|childId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|template
operator|.
name|getChildName
argument_list|()
operator|!=
name|ZERO_CHILD_NODES
condition|)
block|{
name|RecordId
name|childId
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|offset
operator|+
name|RECORD_ID_BYTES
argument_list|)
decl_stmt|;
name|analyseNode
argument_list|(
name|childId
argument_list|)
expr_stmt|;
block|}
comment|// Recurse into properties
name|int
name|ids
init|=
name|template
operator|.
name|getChildName
argument_list|()
operator|==
name|ZERO_CHILD_NODES
condition|?
literal|1
else|:
literal|2
decl_stmt|;
name|nodeSize
operator|+=
name|ids
operator|*
name|RECORD_ID_BYTES
expr_stmt|;
name|PropertyTemplate
index|[]
name|propertyTemplates
init|=
name|template
operator|.
name|getPropertyTemplates
argument_list|()
decl_stmt|;
if|if
condition|(
name|segment
operator|.
name|getSegmentVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|V_11
argument_list|)
condition|)
block|{
if|if
condition|(
name|propertyTemplates
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|nodeSize
operator|+=
name|RECORD_ID_BYTES
expr_stmt|;
name|RecordId
name|id
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|offset
operator|+
name|ids
operator|*
name|RECORD_ID_BYTES
argument_list|)
decl_stmt|;
name|ListRecord
name|pIds
init|=
operator|new
name|ListRecord
argument_list|(
name|id
argument_list|,
name|propertyTemplates
operator|.
name|length
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
name|propertyTemplates
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|RecordId
name|propertyId
init|=
name|pIds
operator|.
name|getEntry
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|analyseProperty
argument_list|(
name|propertyId
argument_list|,
name|propertyTemplates
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|analyseList
argument_list|(
name|id
argument_list|,
name|propertyTemplates
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|PropertyTemplate
name|propertyTemplate
range|:
name|propertyTemplates
control|)
block|{
name|nodeSize
operator|+=
name|RECORD_ID_BYTES
expr_stmt|;
name|RecordId
name|propertyId
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|offset
operator|+
name|ids
operator|++
operator|*
name|RECORD_ID_BYTES
argument_list|)
decl_stmt|;
name|analyseProperty
argument_list|(
name|propertyId
argument_list|,
name|propertyTemplate
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"resource"
argument_list|)
name|Formatter
name|formatter
init|=
operator|new
name|Formatter
argument_list|(
name|sb
argument_list|)
decl_stmt|;
name|formatter
operator|.
name|format
argument_list|(
literal|"%s in maps (%s leaf and branch records)%n"
argument_list|,
name|byteCountToDisplaySize
argument_list|(
name|mapSize
argument_list|)
argument_list|,
name|mapCount
argument_list|)
expr_stmt|;
name|formatter
operator|.
name|format
argument_list|(
literal|"%s in lists (%s list and bucket records)%n"
argument_list|,
name|byteCountToDisplaySize
argument_list|(
name|listSize
argument_list|)
argument_list|,
name|listCount
argument_list|)
expr_stmt|;
name|formatter
operator|.
name|format
argument_list|(
literal|"%s in values (value and block records of %s properties, "
operator|+
literal|"%s/%s/%s/%s small/medium/long/external blobs, %s/%s/%s small/medium/long strings)%n"
argument_list|,
name|byteCountToDisplaySize
argument_list|(
name|valueSize
argument_list|)
argument_list|,
name|propertyCount
argument_list|,
name|smallBlobCount
argument_list|,
name|mediumBlobCount
argument_list|,
name|longBlobCount
argument_list|,
name|externalBlobCount
argument_list|,
name|smallStringCount
argument_list|,
name|mediumStringCount
argument_list|,
name|longStringCount
argument_list|)
expr_stmt|;
name|formatter
operator|.
name|format
argument_list|(
literal|"%s in templates (%s template records)%n"
argument_list|,
name|byteCountToDisplaySize
argument_list|(
name|templateSize
argument_list|)
argument_list|,
name|templateCount
argument_list|)
expr_stmt|;
name|formatter
operator|.
name|format
argument_list|(
literal|"%s in nodes (%s node records)%n"
argument_list|,
name|byteCountToDisplaySize
argument_list|(
name|nodeSize
argument_list|)
argument_list|,
name|nodeCount
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|void
name|analyseTemplate
parameter_list|(
name|RecordId
name|templateId
parameter_list|)
block|{
if|if
condition|(
name|notSeen
argument_list|(
name|templateId
argument_list|)
condition|)
block|{
name|templateCount
operator|++
expr_stmt|;
name|Segment
name|segment
init|=
name|templateId
operator|.
name|getSegment
argument_list|()
decl_stmt|;
name|int
name|size
init|=
literal|0
decl_stmt|;
name|int
name|offset
init|=
name|templateId
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
operator|+
name|size
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
name|size
operator|+=
literal|4
expr_stmt|;
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
operator|+
name|size
argument_list|)
decl_stmt|;
name|analyseString
argument_list|(
name|primaryId
argument_list|)
expr_stmt|;
name|size
operator|+=
name|Segment
operator|.
name|RECORD_ID_BYTES
expr_stmt|;
block|}
if|if
condition|(
name|hasMixinTypes
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|mixinCount
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
operator|+
name|size
argument_list|)
decl_stmt|;
name|analyseString
argument_list|(
name|mixinId
argument_list|)
expr_stmt|;
name|size
operator|+=
name|Segment
operator|.
name|RECORD_ID_BYTES
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|zeroChildNodes
operator|&&
operator|!
name|manyChildNodes
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
operator|+
name|size
argument_list|)
decl_stmt|;
name|analyseString
argument_list|(
name|childNameId
argument_list|)
expr_stmt|;
name|size
operator|+=
name|Segment
operator|.
name|RECORD_ID_BYTES
expr_stmt|;
block|}
if|if
condition|(
name|segment
operator|.
name|getSegmentVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|V_11
argument_list|)
condition|)
block|{
if|if
condition|(
name|propertyCount
operator|>
literal|0
condition|)
block|{
name|RecordId
name|listId
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|offset
operator|+
name|size
argument_list|)
decl_stmt|;
name|ListRecord
name|propertyNames
init|=
operator|new
name|ListRecord
argument_list|(
name|listId
argument_list|,
name|propertyCount
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
name|propertyCount
condition|;
name|i
operator|++
control|)
block|{
name|RecordId
name|propertyNameId
init|=
name|propertyNames
operator|.
name|getEntry
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|size
operator|+=
name|Segment
operator|.
name|RECORD_ID_BYTES
expr_stmt|;
name|size
operator|++
expr_stmt|;
comment|// type
name|analyseString
argument_list|(
name|propertyNameId
argument_list|)
expr_stmt|;
block|}
name|analyseList
argument_list|(
name|listId
argument_list|,
name|propertyCount
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|propertyCount
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
operator|+
name|size
argument_list|)
decl_stmt|;
name|size
operator|+=
name|Segment
operator|.
name|RECORD_ID_BYTES
expr_stmt|;
name|size
operator|++
expr_stmt|;
comment|// type
name|analyseString
argument_list|(
name|propertyNameId
argument_list|)
expr_stmt|;
block|}
block|}
name|templateSize
operator|+=
name|size
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|analyseMap
parameter_list|(
name|RecordId
name|mapId
parameter_list|,
name|MapRecord
name|map
parameter_list|)
block|{
if|if
condition|(
name|notSeen
argument_list|(
name|mapId
argument_list|)
condition|)
block|{
name|mapCount
operator|++
expr_stmt|;
if|if
condition|(
name|map
operator|.
name|isDiff
argument_list|()
condition|)
block|{
name|analyseDiff
argument_list|(
name|mapId
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|map
operator|.
name|isLeaf
argument_list|()
condition|)
block|{
name|analyseLeaf
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|analyseBranch
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|analyseDiff
parameter_list|(
name|RecordId
name|mapId
parameter_list|,
name|MapRecord
name|map
parameter_list|)
block|{
name|mapSize
operator|+=
literal|4
expr_stmt|;
comment|// -1
name|mapSize
operator|+=
literal|4
expr_stmt|;
comment|// hash of changed key
name|mapSize
operator|+=
name|RECORD_ID_BYTES
expr_stmt|;
comment|// key
name|mapSize
operator|+=
name|RECORD_ID_BYTES
expr_stmt|;
comment|// value
name|mapSize
operator|+=
name|RECORD_ID_BYTES
expr_stmt|;
comment|// base
name|RecordId
name|baseId
init|=
name|mapId
operator|.
name|getSegment
argument_list|()
operator|.
name|readRecordId
argument_list|(
name|mapId
operator|.
name|getOffset
argument_list|()
operator|+
literal|8
operator|+
literal|2
operator|*
name|RECORD_ID_BYTES
argument_list|)
decl_stmt|;
name|analyseMap
argument_list|(
name|baseId
argument_list|,
operator|new
name|MapRecord
argument_list|(
name|baseId
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|analyseLeaf
parameter_list|(
name|MapRecord
name|map
parameter_list|)
block|{
name|mapSize
operator|+=
literal|4
expr_stmt|;
comment|// size
name|mapSize
operator|+=
name|map
operator|.
name|size
argument_list|()
operator|*
literal|4
expr_stmt|;
comment|// key hashes
for|for
control|(
name|MapEntry
name|entry
range|:
name|map
operator|.
name|getEntries
argument_list|()
control|)
block|{
name|mapSize
operator|+=
literal|2
operator|*
name|RECORD_ID_BYTES
expr_stmt|;
comment|// key value pairs
name|analyseString
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|analyseBranch
parameter_list|(
name|MapRecord
name|map
parameter_list|)
block|{
name|mapSize
operator|+=
literal|4
expr_stmt|;
comment|// level/size
name|mapSize
operator|+=
literal|4
expr_stmt|;
comment|// bitmap
for|for
control|(
name|MapRecord
name|bucket
range|:
name|map
operator|.
name|getBuckets
argument_list|()
control|)
block|{
if|if
condition|(
name|bucket
operator|!=
literal|null
condition|)
block|{
name|mapSize
operator|+=
name|RECORD_ID_BYTES
expr_stmt|;
name|analyseMap
argument_list|(
name|bucket
operator|.
name|getRecordId
argument_list|()
argument_list|,
name|bucket
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|analyseProperty
parameter_list|(
name|RecordId
name|propertyId
parameter_list|,
name|PropertyTemplate
name|template
parameter_list|)
block|{
if|if
condition|(
operator|!
name|contains
argument_list|(
name|propertyId
argument_list|)
condition|)
block|{
name|propertyCount
operator|++
expr_stmt|;
name|Segment
name|segment
init|=
name|propertyId
operator|.
name|getSegment
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
name|propertyId
operator|.
name|getOffset
argument_list|()
decl_stmt|;
name|Type
argument_list|<
name|?
argument_list|>
name|type
init|=
name|template
operator|.
name|getType
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
name|notSeen
argument_list|(
name|propertyId
argument_list|)
expr_stmt|;
name|int
name|size
init|=
name|segment
operator|.
name|readInt
argument_list|(
name|offset
argument_list|)
decl_stmt|;
name|valueSize
operator|+=
literal|4
expr_stmt|;
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
name|RecordId
name|listId
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|offset
operator|+
literal|4
argument_list|)
decl_stmt|;
name|valueSize
operator|+=
name|RECORD_ID_BYTES
expr_stmt|;
for|for
control|(
name|RecordId
name|valueId
range|:
operator|new
name|ListRecord
argument_list|(
name|listId
argument_list|,
name|size
argument_list|)
operator|.
name|getEntries
argument_list|()
control|)
block|{
name|analyseValue
argument_list|(
name|valueId
argument_list|,
name|type
operator|.
name|getBaseType
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|analyseList
argument_list|(
name|listId
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|analyseValue
argument_list|(
name|propertyId
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|analyseValue
parameter_list|(
name|RecordId
name|valueId
parameter_list|,
name|Type
argument_list|<
name|?
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
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|==
name|BINARY
condition|)
block|{
name|analyseBlob
argument_list|(
name|valueId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|analyseString
argument_list|(
name|valueId
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|analyseBlob
parameter_list|(
name|RecordId
name|blobId
parameter_list|)
block|{
if|if
condition|(
name|notSeen
argument_list|(
name|blobId
argument_list|)
condition|)
block|{
name|Segment
name|segment
init|=
name|blobId
operator|.
name|getSegment
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
name|blobId
operator|.
name|getOffset
argument_list|()
decl_stmt|;
name|byte
name|head
init|=
name|segment
operator|.
name|readByte
argument_list|(
name|offset
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|head
operator|&
literal|0x80
operator|)
operator|==
literal|0x00
condition|)
block|{
comment|// 0xxx xxxx: small value
name|valueSize
operator|+=
operator|(
literal|1
operator|+
name|head
operator|)
expr_stmt|;
name|smallBlobCount
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|head
operator|&
literal|0xc0
operator|)
operator|==
literal|0x80
condition|)
block|{
comment|// 10xx xxxx: medium value
name|int
name|length
init|=
operator|(
name|segment
operator|.
name|readShort
argument_list|(
name|offset
argument_list|)
operator|&
literal|0x3fff
operator|)
operator|+
name|SMALL_LIMIT
decl_stmt|;
name|valueSize
operator|+=
operator|(
literal|2
operator|+
name|length
operator|)
expr_stmt|;
name|mediumBlobCount
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|head
operator|&
literal|0xe0
operator|)
operator|==
literal|0xc0
condition|)
block|{
comment|// 110x xxxx: long value
name|long
name|length
init|=
operator|(
name|segment
operator|.
name|readLong
argument_list|(
name|offset
argument_list|)
operator|&
literal|0x1fffffffffffffffL
operator|)
operator|+
name|MEDIUM_LIMIT
decl_stmt|;
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
name|RecordId
name|listId
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|offset
operator|+
literal|8
argument_list|)
decl_stmt|;
name|analyseList
argument_list|(
name|listId
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|valueSize
operator|+=
operator|(
literal|8
operator|+
name|RECORD_ID_BYTES
operator|+
name|length
operator|)
expr_stmt|;
name|longBlobCount
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|head
operator|&
literal|0xf0
operator|)
operator|==
literal|0xe0
condition|)
block|{
comment|// 1110 xxxx: external value
name|int
name|length
init|=
operator|(
name|head
operator|&
literal|0x0f
operator|)
operator|<<
literal|8
operator||
operator|(
name|segment
operator|.
name|readByte
argument_list|(
name|offset
operator|+
literal|1
argument_list|)
operator|&
literal|0xff
operator|)
decl_stmt|;
name|valueSize
operator|+=
operator|(
literal|2
operator|+
name|length
operator|)
expr_stmt|;
name|externalBlobCount
operator|++
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Unexpected value record type: %02x"
argument_list|,
name|head
operator|&
literal|0xff
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
name|void
name|analyseString
parameter_list|(
name|RecordId
name|stringId
parameter_list|)
block|{
if|if
condition|(
name|notSeen
argument_list|(
name|stringId
argument_list|)
condition|)
block|{
name|Segment
name|segment
init|=
name|stringId
operator|.
name|getSegment
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
name|stringId
operator|.
name|getOffset
argument_list|()
decl_stmt|;
name|long
name|length
init|=
name|segment
operator|.
name|readLength
argument_list|(
name|offset
argument_list|)
decl_stmt|;
if|if
condition|(
name|length
operator|<
name|Segment
operator|.
name|SMALL_LIMIT
condition|)
block|{
name|valueSize
operator|+=
operator|(
literal|1
operator|+
name|length
operator|)
expr_stmt|;
name|smallStringCount
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|length
operator|<
name|Segment
operator|.
name|MEDIUM_LIMIT
condition|)
block|{
name|valueSize
operator|+=
operator|(
literal|2
operator|+
name|length
operator|)
expr_stmt|;
name|mediumStringCount
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|length
operator|<
name|Integer
operator|.
name|MAX_VALUE
condition|)
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
name|RecordId
name|listId
init|=
name|segment
operator|.
name|readRecordId
argument_list|(
name|offset
operator|+
literal|8
argument_list|)
decl_stmt|;
name|analyseList
argument_list|(
name|listId
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|valueSize
operator|+=
operator|(
literal|8
operator|+
name|RECORD_ID_BYTES
operator|+
name|length
operator|)
expr_stmt|;
name|longStringCount
operator|++
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"String is too long: "
operator|+
name|length
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
name|void
name|analyseList
parameter_list|(
name|RecordId
name|listId
parameter_list|,
name|int
name|size
parameter_list|)
block|{
if|if
condition|(
name|notSeen
argument_list|(
name|listId
argument_list|)
condition|)
block|{
name|listCount
operator|++
expr_stmt|;
name|listSize
operator|+=
name|noOfListSlots
argument_list|(
name|size
argument_list|)
operator|*
name|RECORD_ID_BYTES
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|int
name|noOfListSlots
parameter_list|(
name|int
name|size
parameter_list|)
block|{
if|if
condition|(
name|size
operator|<=
name|LEVEL_SIZE
condition|)
block|{
return|return
name|size
return|;
block|}
else|else
block|{
name|int
name|fullBuckets
init|=
name|size
operator|/
name|LEVEL_SIZE
decl_stmt|;
if|if
condition|(
name|size
operator|%
name|LEVEL_SIZE
operator|>
literal|1
condition|)
block|{
return|return
name|size
operator|+
name|noOfListSlots
argument_list|(
name|fullBuckets
operator|+
literal|1
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|size
operator|+
name|noOfListSlots
argument_list|(
name|fullBuckets
argument_list|)
return|;
block|}
block|}
block|}
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ShortSet
argument_list|>
name|seenIds
init|=
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|notSeen
parameter_list|(
name|RecordId
name|id
parameter_list|)
block|{
name|String
name|segmentId
init|=
name|id
operator|.
name|getSegmentId
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ShortSet
name|offsets
init|=
name|seenIds
operator|.
name|get
argument_list|(
name|segmentId
argument_list|)
decl_stmt|;
if|if
condition|(
name|offsets
operator|==
literal|null
condition|)
block|{
name|offsets
operator|=
operator|new
name|ShortSet
argument_list|()
expr_stmt|;
name|seenIds
operator|.
name|put
argument_list|(
name|segmentId
argument_list|,
name|offsets
argument_list|)
expr_stmt|;
block|}
return|return
name|offsets
operator|.
name|add
argument_list|(
name|crop
argument_list|(
name|id
operator|.
name|getOffset
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|contains
parameter_list|(
name|RecordId
name|id
parameter_list|)
block|{
name|String
name|segmentId
init|=
name|id
operator|.
name|getSegmentId
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ShortSet
name|offsets
init|=
name|seenIds
operator|.
name|get
argument_list|(
name|segmentId
argument_list|)
decl_stmt|;
return|return
name|offsets
operator|!=
literal|null
operator|&&
name|offsets
operator|.
name|contains
argument_list|(
name|crop
argument_list|(
name|id
operator|.
name|getOffset
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|short
name|crop
parameter_list|(
name|int
name|value
parameter_list|)
block|{
return|return
call|(
name|short
call|)
argument_list|(
name|value
operator|>>
name|Segment
operator|.
name|RECORD_ALIGN_BITS
argument_list|)
return|;
block|}
specifier|static
class|class
name|ShortSet
block|{
name|short
index|[]
name|elements
decl_stmt|;
name|boolean
name|add
parameter_list|(
name|short
name|n
parameter_list|)
block|{
if|if
condition|(
name|elements
operator|==
literal|null
condition|)
block|{
name|elements
operator|=
operator|new
name|short
index|[
literal|1
index|]
expr_stmt|;
name|elements
index|[
literal|0
index|]
operator|=
name|n
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|int
name|k
init|=
name|binarySearch
argument_list|(
name|elements
argument_list|,
name|n
argument_list|)
decl_stmt|;
if|if
condition|(
name|k
operator|<
literal|0
condition|)
block|{
name|int
name|l
init|=
operator|-
name|k
operator|-
literal|1
decl_stmt|;
name|short
index|[]
name|e
init|=
operator|new
name|short
index|[
name|elements
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|arraycopy
argument_list|(
name|elements
argument_list|,
literal|0
argument_list|,
name|e
argument_list|,
literal|0
argument_list|,
name|l
argument_list|)
expr_stmt|;
name|e
index|[
name|l
index|]
operator|=
name|n
expr_stmt|;
name|int
name|c
init|=
name|elements
operator|.
name|length
operator|-
name|l
decl_stmt|;
if|if
condition|(
name|c
operator|>
literal|0
condition|)
block|{
name|arraycopy
argument_list|(
name|elements
argument_list|,
name|l
argument_list|,
name|e
argument_list|,
name|l
operator|+
literal|1
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
name|elements
operator|=
name|e
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
name|boolean
name|contains
parameter_list|(
name|short
name|n
parameter_list|)
block|{
return|return
name|elements
operator|!=
literal|null
operator|&&
name|binarySearch
argument_list|(
name|elements
argument_list|,
name|n
argument_list|)
operator|>=
literal|0
return|;
block|}
block|}
block|}
end_class

end_unit

