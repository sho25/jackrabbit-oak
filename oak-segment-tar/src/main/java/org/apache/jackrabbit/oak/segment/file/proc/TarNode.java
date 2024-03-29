begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
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
operator|.
name|file
operator|.
name|proc
package|;
end_package

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
name|memory
operator|.
name|PropertyStates
operator|.
name|createProperty
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|StreamSupport
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
name|ImmutableList
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
name|EmptyNodeState
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
name|segment
operator|.
name|file
operator|.
name|proc
operator|.
name|Proc
operator|.
name|Backend
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

begin_class
class|class
name|TarNode
extends|extends
name|AbstractNode
block|{
specifier|private
specifier|final
name|Backend
name|backend
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
name|TarNode
parameter_list|(
name|Backend
name|backend
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|backend
operator|=
name|backend
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|PropertyState
argument_list|>
name|getProperties
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|createProperty
argument_list|(
literal|"name"
argument_list|,
name|name
argument_list|)
argument_list|,
name|createProperty
argument_list|(
literal|"size"
argument_list|,
name|backend
operator|.
name|getTarSize
argument_list|(
name|name
argument_list|)
operator|.
name|orElse
argument_list|(
operator|-
literal|1L
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasChildNode
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
return|return
name|backend
operator|.
name|segmentExists
argument_list|(
name|this
operator|.
name|name
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|NodeState
name|getChildNode
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
name|backend
operator|.
name|segmentExists
argument_list|(
name|this
operator|.
name|name
argument_list|,
name|name
argument_list|)
condition|)
block|{
return|return
name|SegmentNode
operator|.
name|newSegmentNode
argument_list|(
name|backend
argument_list|,
name|name
argument_list|)
return|;
block|}
return|return
name|EmptyNodeState
operator|.
name|MISSING_NODE
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
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
name|StreamSupport
operator|.
name|stream
argument_list|(
name|backend
operator|.
name|getSegmentIds
argument_list|(
name|name
argument_list|)
operator|.
name|spliterator
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|map
argument_list|(
name|this
operator|::
name|newSegmentEntry
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
name|ChildNodeEntry
name|newSegmentEntry
parameter_list|(
name|String
name|segmentId
parameter_list|)
block|{
return|return
operator|new
name|SegmentEntry
argument_list|(
name|backend
argument_list|,
name|segmentId
argument_list|)
return|;
block|}
block|}
end_class

end_unit

