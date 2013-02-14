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
name|checkNotNull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|annotation
operator|.
name|Nullable
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
name|plugins
operator|.
name|memory
operator|.
name|MemoryChildNodeEntry
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
name|MemoryNodeBuilder
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
name|segment
operator|.
name|MapRecord
operator|.
name|Entry
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
name|AbstractNodeState
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
name|NodeBuilder
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

begin_class
class|class
name|SegmentNodeState
extends|extends
name|AbstractNodeState
block|{
specifier|public
specifier|static
name|RecordId
name|getRecordIdIfAvailable
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|instanceof
name|SegmentNodeState
condition|)
block|{
name|SegmentNodeState
name|sstate
init|=
operator|(
name|SegmentNodeState
operator|)
name|state
decl_stmt|;
return|return
name|sstate
operator|.
name|recordId
return|;
block|}
return|return
literal|null
return|;
block|}
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
specifier|private
name|NodeTemplate
name|template
init|=
literal|null
decl_stmt|;
name|SegmentNodeState
parameter_list|(
name|SegmentReader
name|reader
parameter_list|,
name|RecordId
name|id
parameter_list|)
block|{
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
name|id
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|synchronized
name|NodeTemplate
name|getTemplate
parameter_list|()
block|{
if|if
condition|(
name|template
operator|==
literal|null
condition|)
block|{
name|template
operator|=
name|reader
operator|.
name|readTemplate
argument_list|(
name|reader
operator|.
name|readRecordId
argument_list|(
name|recordId
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|template
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getPropertyCount
parameter_list|()
block|{
return|return
name|getTemplate
argument_list|()
operator|.
name|getPropertyCount
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|PropertyState
name|getProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|getTemplate
argument_list|()
operator|.
name|getProperty
argument_list|(
name|name
argument_list|,
name|reader
argument_list|,
name|recordId
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Iterable
argument_list|<
name|PropertyState
argument_list|>
name|getProperties
parameter_list|()
block|{
return|return
name|getTemplate
argument_list|()
operator|.
name|getProperties
argument_list|(
name|reader
argument_list|,
name|recordId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getChildNodeCount
parameter_list|()
block|{
return|return
name|getTemplate
argument_list|()
operator|.
name|getChildNodeCount
argument_list|(
name|reader
argument_list|,
name|recordId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasChildNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getTemplate
argument_list|()
operator|.
name|hasChildNode
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|,
name|reader
argument_list|,
name|recordId
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|NodeState
name|getChildNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getTemplate
argument_list|()
operator|.
name|getChildNode
argument_list|(
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|,
name|reader
argument_list|,
name|recordId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|getChildNodeNames
parameter_list|()
block|{
return|return
name|getTemplate
argument_list|()
operator|.
name|getChildNodeNames
argument_list|(
name|reader
argument_list|,
name|recordId
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|ChildNodeEntry
argument_list|>
name|getChildNodeEntries
parameter_list|()
block|{
return|return
name|getTemplate
argument_list|()
operator|.
name|getChildNodeEntries
argument_list|(
name|reader
argument_list|,
name|recordId
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|NodeBuilder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|MemoryNodeBuilder
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

