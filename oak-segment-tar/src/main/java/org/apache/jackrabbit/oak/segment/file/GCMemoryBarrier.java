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
import|import static
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
operator|.
name|getMemoryMXBean
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
operator|.
name|getMemoryPoolMXBeans
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|MemoryType
operator|.
name|HEAP
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
name|segment
operator|.
name|file
operator|.
name|PrintableBytes
operator|.
name|newPrintableBytes
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

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
name|lang
operator|.
name|management
operator|.
name|MemoryNotificationInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|MemoryPoolMXBean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|MemoryUsage
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
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ListenerNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|Notification
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|NotificationEmitter
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|NotificationListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
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
name|compaction
operator|.
name|SegmentGCOptions
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
comment|/**  * Responsible for raising the low memory flag whenever the available memory  * falls under a specified threshold. Uses {@link MemoryPoolMXBean} to register  * for memory related notifications.  */
end_comment

begin_class
specifier|public
class|class
name|GCMemoryBarrier
implements|implements
name|Closeable
block|{
comment|// TODO possibly add a min value to the percentage, ie. skip gc if available
comment|// heap drops under 2GB
annotation|@
name|NotNull
specifier|private
specifier|final
name|AtomicBoolean
name|sufficientMemory
decl_stmt|;
annotation|@
name|NotNull
specifier|private
specifier|final
name|GCListener
name|gcListener
decl_stmt|;
annotation|@
name|NotNull
specifier|private
specifier|final
name|SegmentGCOptions
name|gcOptions
decl_stmt|;
specifier|private
specifier|final
name|NotificationEmitter
name|emitter
decl_stmt|;
specifier|private
specifier|final
name|MemoryListener
name|listener
decl_stmt|;
specifier|public
name|GCMemoryBarrier
parameter_list|(
annotation|@
name|NotNull
name|AtomicBoolean
name|sufficientMemory
parameter_list|,
annotation|@
name|NotNull
name|GCListener
name|gcListener
parameter_list|,
annotation|@
name|NotNull
name|SegmentGCOptions
name|gcOptions
parameter_list|)
block|{
name|this
operator|.
name|sufficientMemory
operator|=
name|sufficientMemory
expr_stmt|;
name|this
operator|.
name|gcListener
operator|=
name|gcListener
expr_stmt|;
name|this
operator|.
name|gcOptions
operator|=
name|gcOptions
expr_stmt|;
name|MemoryPoolMXBean
name|pool
init|=
literal|null
decl_stmt|;
name|int
name|percentage
init|=
name|gcOptions
operator|.
name|getMemoryThreshold
argument_list|()
decl_stmt|;
if|if
condition|(
name|percentage
operator|>
literal|0
condition|)
block|{
name|pool
operator|=
name|getMemoryPool
argument_list|()
expr_stmt|;
if|if
condition|(
name|pool
operator|==
literal|null
condition|)
block|{
name|gcListener
operator|.
name|warn
argument_list|(
literal|"unable to setup monitoring of available memory."
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|pool
operator|!=
literal|null
condition|)
block|{
name|emitter
operator|=
operator|(
name|NotificationEmitter
operator|)
name|getMemoryMXBean
argument_list|()
expr_stmt|;
name|listener
operator|=
operator|new
name|MemoryListener
argument_list|()
expr_stmt|;
name|emitter
operator|.
name|addNotificationListener
argument_list|(
name|listener
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|MemoryUsage
name|usage
init|=
name|pool
operator|.
name|getCollectionUsage
argument_list|()
decl_stmt|;
name|long
name|maxMemory
init|=
name|usage
operator|.
name|getMax
argument_list|()
decl_stmt|;
name|long
name|required
init|=
name|maxMemory
operator|*
name|percentage
operator|/
literal|100
decl_stmt|;
name|gcListener
operator|.
name|info
argument_list|(
literal|"setting up a listener to cancel compaction if available memory on pool '{}' drops below {} / {}%."
argument_list|,
name|pool
operator|.
name|getName
argument_list|()
argument_list|,
name|newPrintableBytes
argument_list|(
name|required
argument_list|)
argument_list|,
name|percentage
argument_list|)
expr_stmt|;
name|long
name|warningThreshold
init|=
name|maxMemory
operator|-
name|required
decl_stmt|;
name|long
name|current
init|=
name|pool
operator|.
name|getCollectionUsageThreshold
argument_list|()
decl_stmt|;
if|if
condition|(
name|current
operator|>
literal|0
condition|)
block|{
name|warningThreshold
operator|=
name|Math
operator|.
name|min
argument_list|(
name|warningThreshold
argument_list|,
name|current
argument_list|)
expr_stmt|;
block|}
name|pool
operator|.
name|setCollectionUsageThreshold
argument_list|(
name|warningThreshold
argument_list|)
expr_stmt|;
name|checkMemory
argument_list|(
name|usage
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|emitter
operator|=
literal|null
expr_stmt|;
name|listener
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|sufficientMemory
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|MemoryPoolMXBean
name|getMemoryPool
parameter_list|()
block|{
name|long
name|maxSize
init|=
literal|0
decl_stmt|;
name|MemoryPoolMXBean
name|maxPool
init|=
literal|null
decl_stmt|;
for|for
control|(
name|MemoryPoolMXBean
name|pool
range|:
name|getMemoryPoolMXBeans
argument_list|()
control|)
block|{
if|if
condition|(
name|HEAP
operator|==
name|pool
operator|.
name|getType
argument_list|()
operator|&&
name|pool
operator|.
name|isCollectionUsageThresholdSupported
argument_list|()
condition|)
block|{
comment|// Get usage after a GC, which is more stable, if available
name|long
name|poolSize
init|=
name|pool
operator|.
name|getCollectionUsage
argument_list|()
operator|.
name|getMax
argument_list|()
decl_stmt|;
comment|// Keep the pool with biggest size, by default it should be Old Gen Space
if|if
condition|(
name|poolSize
operator|>
name|maxSize
condition|)
block|{
name|maxPool
operator|=
name|pool
expr_stmt|;
block|}
block|}
block|}
return|return
name|maxPool
return|;
block|}
specifier|private
name|void
name|checkMemory
parameter_list|(
name|MemoryUsage
name|usage
parameter_list|)
block|{
name|int
name|percentage
init|=
name|gcOptions
operator|.
name|getMemoryThreshold
argument_list|()
decl_stmt|;
name|long
name|maxMemory
init|=
name|usage
operator|.
name|getMax
argument_list|()
decl_stmt|;
name|long
name|usedMemory
init|=
name|usage
operator|.
name|getUsed
argument_list|()
decl_stmt|;
name|long
name|avail
init|=
name|maxMemory
operator|-
name|usedMemory
decl_stmt|;
name|long
name|required
init|=
name|maxMemory
operator|*
name|percentage
operator|/
literal|100
decl_stmt|;
if|if
condition|(
name|avail
operator|<=
name|required
condition|)
block|{
name|gcListener
operator|.
name|warn
argument_list|(
literal|"canceling compaction because available memory level {} is too low, expecting at least {}"
argument_list|,
name|newPrintableBytes
argument_list|(
name|avail
argument_list|)
argument_list|,
name|newPrintableBytes
argument_list|(
name|required
argument_list|)
argument_list|)
expr_stmt|;
name|sufficientMemory
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|gcListener
operator|.
name|info
argument_list|(
literal|"available memory level {} is good, expecting at least {}"
argument_list|,
name|newPrintableBytes
argument_list|(
name|avail
argument_list|)
argument_list|,
name|newPrintableBytes
argument_list|(
name|required
argument_list|)
argument_list|)
expr_stmt|;
name|sufficientMemory
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|emitter
operator|!=
literal|null
operator|&&
name|listener
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|emitter
operator|.
name|removeNotificationListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ListenerNotFoundException
name|e
parameter_list|)
block|{
comment|//
block|}
block|}
block|}
specifier|private
class|class
name|MemoryListener
implements|implements
name|NotificationListener
block|{
annotation|@
name|Override
specifier|public
name|void
name|handleNotification
parameter_list|(
name|Notification
name|notification
parameter_list|,
name|Object
name|handback
parameter_list|)
block|{
if|if
condition|(
name|notification
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|MemoryNotificationInfo
operator|.
name|MEMORY_COLLECTION_THRESHOLD_EXCEEDED
argument_list|)
condition|)
block|{
if|if
condition|(
name|sufficientMemory
operator|.
name|get
argument_list|()
condition|)
block|{
name|CompositeData
name|cd
init|=
operator|(
name|CompositeData
operator|)
name|notification
operator|.
name|getUserData
argument_list|()
decl_stmt|;
name|MemoryNotificationInfo
name|info
init|=
name|MemoryNotificationInfo
operator|.
name|from
argument_list|(
name|cd
argument_list|)
decl_stmt|;
name|checkMemory
argument_list|(
name|info
operator|.
name|getUsage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

