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
name|spi
operator|.
name|state
operator|.
name|NodeState
import|;
end_import

begin_class
specifier|public
class|class
name|SegmentNodeBuilder
extends|extends
name|MemoryNodeBuilder
block|{
comment|/**      * Number of content updates that need to happen before the updates      * are automatically purged to the underlying segments.      */
specifier|private
specifier|static
specifier|final
name|int
name|UPDATE_LIMIT
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"update.limit"
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|SegmentWriter
name|writer
decl_stmt|;
specifier|private
name|long
name|updateCount
init|=
literal|0
decl_stmt|;
name|SegmentNodeBuilder
parameter_list|(
name|SegmentNodeState
name|base
parameter_list|)
block|{
name|this
argument_list|(
name|base
argument_list|,
name|base
operator|.
name|getTracker
argument_list|()
operator|.
name|getWriter
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|SegmentNodeBuilder
parameter_list|(
name|SegmentNodeState
name|base
parameter_list|,
name|SegmentWriter
name|writer
parameter_list|)
block|{
name|super
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
block|}
comment|//-------------------------------------------------< MemoryNodeBuilder>--
annotation|@
name|Override
specifier|protected
name|void
name|updated
parameter_list|()
block|{
name|updateCount
operator|++
expr_stmt|;
if|if
condition|(
name|updateCount
operator|>
name|UPDATE_LIMIT
condition|)
block|{
name|getNodeState
argument_list|()
expr_stmt|;
block|}
block|}
comment|//-------------------------------------------------------< NodeBuilder>--
annotation|@
name|Override
specifier|public
name|SegmentNodeState
name|getBaseState
parameter_list|()
block|{
comment|// guaranteed to be a SegmentNodeState
return|return
operator|(
name|SegmentNodeState
operator|)
name|super
operator|.
name|getBaseState
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|SegmentNodeState
name|getNodeState
parameter_list|()
block|{
name|NodeState
name|state
init|=
name|super
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|SegmentNodeState
name|sstate
init|=
name|writer
operator|.
name|writeNode
argument_list|(
name|state
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|!=
name|sstate
condition|)
block|{
name|set
argument_list|(
name|sstate
argument_list|)
expr_stmt|;
name|updateCount
operator|=
literal|0
expr_stmt|;
block|}
return|return
name|sstate
return|;
block|}
block|}
end_class

end_unit

