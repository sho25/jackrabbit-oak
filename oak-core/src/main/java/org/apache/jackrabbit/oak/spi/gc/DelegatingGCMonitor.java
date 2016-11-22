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
name|spi
operator|.
name|gc
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
operator|.
name|newConcurrentHashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|whiteboard
operator|.
name|Registration
import|;
end_import

begin_comment
comment|/**  * This {@link GCMonitor} implementation simply delegates all its call  * to registered monitors.  */
end_comment

begin_class
specifier|public
class|class
name|DelegatingGCMonitor
implements|implements
name|GCMonitor
block|{
specifier|private
specifier|final
name|Set
argument_list|<
name|GCMonitor
argument_list|>
name|gcMonitors
decl_stmt|;
comment|/**      * New instance with an initial set of delegates (which cannot be unregistered).      * @param gcMonitors      */
specifier|public
name|DelegatingGCMonitor
parameter_list|(
annotation|@
name|Nonnull
name|Collection
argument_list|<
name|?
extends|extends
name|GCMonitor
argument_list|>
name|gcMonitors
parameter_list|)
block|{
name|this
operator|.
name|gcMonitors
operator|=
name|newConcurrentHashSet
argument_list|()
expr_stmt|;
name|this
operator|.
name|gcMonitors
operator|.
name|addAll
argument_list|(
name|gcMonitors
argument_list|)
expr_stmt|;
block|}
comment|/**      * New instance without any delegate.      */
specifier|public
name|DelegatingGCMonitor
parameter_list|()
block|{
name|this
argument_list|(
name|Sets
operator|.
expr|<
name|GCMonitor
operator|>
name|newConcurrentHashSet
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Register a {@link GCMonitor}.      * @param gcMonitor      * @return  a {@link Registration} instance, which removes the registered      *          {@code GCMonitor} instance when called.      */
specifier|public
name|Registration
name|registerGCMonitor
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|GCMonitor
name|gcMonitor
parameter_list|)
block|{
name|gcMonitors
operator|.
name|add
argument_list|(
name|checkNotNull
argument_list|(
name|gcMonitor
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|Registration
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|unregister
parameter_list|()
block|{
name|gcMonitors
operator|.
name|remove
argument_list|(
name|gcMonitor
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|info
parameter_list|(
name|String
name|message
parameter_list|,
name|Object
modifier|...
name|arguments
parameter_list|)
block|{
for|for
control|(
name|GCMonitor
name|gcMonitor
range|:
name|gcMonitors
control|)
block|{
name|gcMonitor
operator|.
name|info
argument_list|(
name|message
argument_list|,
name|arguments
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|warn
parameter_list|(
name|String
name|message
parameter_list|,
name|Object
modifier|...
name|arguments
parameter_list|)
block|{
for|for
control|(
name|GCMonitor
name|gcMonitor
range|:
name|gcMonitors
control|)
block|{
name|gcMonitor
operator|.
name|warn
argument_list|(
name|message
argument_list|,
name|arguments
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|error
parameter_list|(
name|String
name|message
parameter_list|,
name|Exception
name|exception
parameter_list|)
block|{
for|for
control|(
name|GCMonitor
name|gcMonitor
range|:
name|gcMonitors
control|)
block|{
name|gcMonitor
operator|.
name|error
argument_list|(
name|message
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|skipped
parameter_list|(
name|String
name|reason
parameter_list|,
name|Object
modifier|...
name|arguments
parameter_list|)
block|{
for|for
control|(
name|GCMonitor
name|gcMonitor
range|:
name|gcMonitors
control|)
block|{
name|gcMonitor
operator|.
name|skipped
argument_list|(
name|reason
argument_list|,
name|arguments
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|compacted
parameter_list|()
block|{
for|for
control|(
name|GCMonitor
name|gcMonitor
range|:
name|gcMonitors
control|)
block|{
name|gcMonitor
operator|.
name|compacted
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|cleaned
parameter_list|(
name|long
name|reclaimedSize
parameter_list|,
name|long
name|currentSize
parameter_list|)
block|{
for|for
control|(
name|GCMonitor
name|gcMonitor
range|:
name|gcMonitors
control|)
block|{
name|gcMonitor
operator|.
name|cleaned
argument_list|(
name|reclaimedSize
argument_list|,
name|currentSize
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|updateStatus
parameter_list|(
name|String
name|status
parameter_list|)
block|{
for|for
control|(
name|GCMonitor
name|gcMonitor
range|:
name|gcMonitors
control|)
block|{
name|gcMonitor
operator|.
name|updateStatus
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

