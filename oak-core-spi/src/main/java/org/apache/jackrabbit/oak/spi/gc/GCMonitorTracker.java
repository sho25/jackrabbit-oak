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
name|AbstractServiceTracker
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
name|Whiteboard
import|;
end_import

begin_comment
comment|/**  * This {@link GCMonitor} implementation tracks {@code GCMonitor} instances registered  * to the {@link Whiteboard} delegating all calls to to those.  */
end_comment

begin_class
specifier|public
class|class
name|GCMonitorTracker
extends|extends
name|AbstractServiceTracker
argument_list|<
name|GCMonitor
argument_list|>
implements|implements
name|GCMonitor
block|{
specifier|public
name|GCMonitorTracker
parameter_list|()
block|{
name|super
argument_list|(
name|GCMonitor
operator|.
name|class
argument_list|)
expr_stmt|;
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
name|getServices
argument_list|()
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
name|getServices
argument_list|()
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
name|e
parameter_list|)
block|{
for|for
control|(
name|GCMonitor
name|gcMonitor
range|:
name|getServices
argument_list|()
control|)
block|{
name|gcMonitor
operator|.
name|error
argument_list|(
name|message
argument_list|,
name|e
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
name|getServices
argument_list|()
control|)
block|{
name|gcMonitor
operator|.
name|skipped
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
name|compacted
parameter_list|()
block|{
for|for
control|(
name|GCMonitor
name|gcMonitor
range|:
name|getServices
argument_list|()
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
name|getServices
argument_list|()
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
name|getServices
argument_list|()
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
