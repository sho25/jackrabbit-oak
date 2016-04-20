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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * A node builder that keeps track of the number of updates  * (set property calls and so on). If there are too many updates,  * getNodeState() is called, which will write the records to the segment,  * and that might persist the changes (if the segment is flushed).  */
end_comment

begin_class
specifier|public
class|class
name|SegmentNodeBuilder
extends|extends
name|MemoryNodeBuilder
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SegmentNodeBuilder
operator|.
name|class
argument_list|)
decl_stmt|;
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
comment|/**      * Local update counter for the root builder.      *       * The value encodes both the counter and the type of the node builder:      *<ul>      *<li>value>= {@code 0} represents a root builder (builder keeps      * counter updates)</li>      *<li>value = {@code -1} represents a child builder (value doesn't      * change, builder doesn't keep an updated counter)</li>      *</ul>      *       */
specifier|private
name|long
name|updateCount
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
name|this
operator|.
name|updateCount
operator|=
literal|0
expr_stmt|;
block|}
specifier|private
name|SegmentNodeBuilder
parameter_list|(
name|SegmentNodeBuilder
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|SegmentWriter
name|writer
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
name|this
operator|.
name|updateCount
operator|=
operator|-
literal|1
expr_stmt|;
block|}
comment|/**      * @return  {@code true} iff this builder has been acquired from a root node state.      */
name|boolean
name|isRootBuilder
parameter_list|()
block|{
return|return
name|isRoot
argument_list|()
return|;
block|}
comment|//-------------------------------------------------< MemoryNodeBuilder>--
annotation|@
name|Override
specifier|protected
name|void
name|updated
parameter_list|()
block|{
if|if
condition|(
name|isChildBuilder
argument_list|()
condition|)
block|{
name|super
operator|.
name|updated
argument_list|()
expr_stmt|;
block|}
else|else
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
block|}
specifier|private
name|boolean
name|isChildBuilder
parameter_list|()
block|{
return|return
name|updateCount
operator|<
literal|0
return|;
block|}
comment|//-------------------------------------------------------< NodeBuilder>--
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|SegmentNodeState
name|getNodeState
parameter_list|()
block|{
try|try
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
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error flushing changes"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unexpected IOException"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|MemoryNodeBuilder
name|createChildBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|SegmentNodeBuilder
argument_list|(
name|this
argument_list|,
name|name
argument_list|,
name|writer
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Blob
name|createBlob
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|SegmentNodeState
name|sns
init|=
name|getNodeState
argument_list|()
decl_stmt|;
return|return
name|sns
operator|.
name|getTracker
argument_list|()
operator|.
name|getWriter
argument_list|()
operator|.
name|writeStream
argument_list|(
name|stream
argument_list|)
return|;
block|}
block|}
end_class

end_unit

