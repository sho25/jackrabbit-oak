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
operator|.
name|file
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
name|spi
operator|.
name|gc
operator|.
name|GCMonitor
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
comment|/**  * Monitors the compaction cycle and keeps a compacted nodes counter, in order  * to provide a best effort progress log based on extrapolating the previous  * size and node count and current size to deduce current node count.  */
end_comment

begin_class
specifier|public
class|class
name|GCNodeWriteMonitor
block|{
specifier|public
specifier|static
specifier|final
name|GCNodeWriteMonitor
name|EMPTY
init|=
operator|new
name|GCNodeWriteMonitor
argument_list|(
operator|-
literal|1
argument_list|,
name|GCMonitor
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
comment|/**      * Number of nodes the monitor will log a message, -1 to disable      */
specifier|private
specifier|final
name|long
name|gcProgressLog
decl_stmt|;
specifier|private
specifier|final
name|GCMonitor
name|gcMonitor
decl_stmt|;
comment|/**      * Start timestamp of compaction (reset at each {@code init()} call).      */
specifier|private
name|long
name|start
init|=
literal|0
decl_stmt|;
comment|/**      * Estimated nodes to compact per cycle (reset at each {@code init()} call).      */
specifier|private
name|long
name|estimated
init|=
operator|-
literal|1
decl_stmt|;
comment|/**      * Number of compacted nodes      */
specifier|private
name|long
name|nodes
decl_stmt|;
comment|/**      * Number of compacted properties      */
specifier|private
name|long
name|properties
decl_stmt|;
comment|/**      * Number of compacted binaries      */
specifier|private
name|long
name|binaries
decl_stmt|;
specifier|private
name|boolean
name|running
init|=
literal|false
decl_stmt|;
specifier|public
name|GCNodeWriteMonitor
parameter_list|(
name|long
name|gcProgressLog
parameter_list|,
annotation|@
name|NotNull
name|GCMonitor
name|gcMonitor
parameter_list|)
block|{
name|this
operator|.
name|gcProgressLog
operator|=
name|gcProgressLog
expr_stmt|;
name|this
operator|.
name|gcMonitor
operator|=
name|gcMonitor
expr_stmt|;
block|}
comment|/**      * @param prevSize      *            size from latest successful compaction      * @param prevCompactedNodes      *            number of nodes compacted during latest compaction operation      * @param currentSize      *            current repository size      */
specifier|public
specifier|synchronized
name|void
name|init
parameter_list|(
name|long
name|prevSize
parameter_list|,
name|long
name|prevCompactedNodes
parameter_list|,
name|long
name|currentSize
parameter_list|)
block|{
if|if
condition|(
name|prevCompactedNodes
operator|>
literal|0
condition|)
block|{
name|estimated
operator|=
call|(
name|long
call|)
argument_list|(
operator|(
operator|(
name|double
operator|)
name|currentSize
operator|/
name|prevSize
operator|)
operator|*
name|prevCompactedNodes
argument_list|)
expr_stmt|;
name|gcMonitor
operator|.
name|info
argument_list|(
literal|"estimated number of nodes to compact is {}, based on {} nodes compacted to {} bytes "
operator|+
literal|"on disk in previous compaction and current size of {} bytes on disk."
argument_list|,
name|estimated
argument_list|,
name|prevCompactedNodes
argument_list|,
name|prevSize
argument_list|,
name|currentSize
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|gcMonitor
operator|.
name|info
argument_list|(
literal|"unable to estimate number of nodes for compaction, missing gc history."
argument_list|)
expr_stmt|;
block|}
name|nodes
operator|=
literal|0
expr_stmt|;
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|running
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|onNode
parameter_list|()
block|{
name|nodes
operator|++
expr_stmt|;
if|if
condition|(
name|gcProgressLog
operator|>
literal|0
operator|&&
name|nodes
operator|%
name|gcProgressLog
operator|==
literal|0
condition|)
block|{
name|gcMonitor
operator|.
name|info
argument_list|(
literal|"compacted {} nodes, {} properties, {} binaries in {} ms. {}"
argument_list|,
name|nodes
argument_list|,
name|properties
argument_list|,
name|binaries
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
argument_list|,
name|getPercentageDone
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|onProperty
parameter_list|()
block|{
name|properties
operator|++
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|onBinary
parameter_list|()
block|{
name|binaries
operator|++
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|finished
parameter_list|()
block|{
name|running
operator|=
literal|false
expr_stmt|;
block|}
comment|/**      * Compacted nodes in current cycle      */
specifier|public
specifier|synchronized
name|long
name|getCompactedNodes
parameter_list|()
block|{
return|return
name|nodes
return|;
block|}
comment|/**      * Estimated nodes to compact in current cycle. Can be {@code -1} if the      * estimation could not be performed.      */
specifier|public
specifier|synchronized
name|long
name|getEstimatedTotal
parameter_list|()
block|{
return|return
name|estimated
return|;
block|}
annotation|@
name|NotNull
specifier|private
name|String
name|getPercentageDone
parameter_list|()
block|{
return|return
name|estimated
operator|>
literal|0
condition|?
name|getEstimatedPercentage
argument_list|()
operator|+
literal|"% complete."
else|:
literal|""
return|;
block|}
comment|/**      * Estimated completion percentage. Can be {@code -1} if the estimation      * could not be performed.      */
specifier|public
specifier|synchronized
name|int
name|getEstimatedPercentage
parameter_list|()
block|{
if|if
condition|(
name|estimated
operator|>
literal|0
condition|)
block|{
if|if
condition|(
operator|!
name|running
condition|)
block|{
return|return
literal|100
return|;
block|}
else|else
block|{
return|return
name|Math
operator|.
name|min
argument_list|(
call|(
name|int
call|)
argument_list|(
literal|100
operator|*
operator|(
operator|(
name|double
operator|)
name|nodes
operator|/
name|estimated
operator|)
argument_list|)
argument_list|,
literal|99
argument_list|)
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
specifier|public
specifier|synchronized
name|boolean
name|isCompactionRunning
parameter_list|()
block|{
return|return
name|running
return|;
block|}
specifier|public
name|long
name|getGcProgressLog
parameter_list|()
block|{
return|return
name|gcProgressLog
return|;
block|}
block|}
end_class

end_unit

