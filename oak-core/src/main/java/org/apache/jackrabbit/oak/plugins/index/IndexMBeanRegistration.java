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
name|index
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
name|spi
operator|.
name|whiteboard
operator|.
name|WhiteboardUtils
operator|.
name|registerMBean
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
name|spi
operator|.
name|whiteboard
operator|.
name|WhiteboardUtils
operator|.
name|scheduleWithFixedDelay
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|jmx
operator|.
name|IndexStatsMBean
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
name|CompositeRegistration
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
name|Lists
import|;
end_import

begin_class
specifier|public
class|class
name|IndexMBeanRegistration
implements|implements
name|Registration
block|{
specifier|private
specifier|final
name|Whiteboard
name|whiteboard
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Registration
argument_list|>
name|regs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|public
name|IndexMBeanRegistration
parameter_list|(
name|Whiteboard
name|whiteboard
parameter_list|)
block|{
name|this
operator|.
name|whiteboard
operator|=
name|whiteboard
expr_stmt|;
block|}
specifier|public
name|void
name|registerAsyncIndexer
parameter_list|(
name|AsyncIndexUpdate
name|task
parameter_list|,
name|long
name|delayInSeconds
parameter_list|)
block|{
name|task
operator|.
name|setIndexMBeanRegistration
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|regs
operator|.
name|add
argument_list|(
name|scheduleWithFixedDelay
argument_list|(
name|whiteboard
argument_list|,
name|task
argument_list|,
name|delayInSeconds
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|regs
operator|.
name|add
argument_list|(
name|registerMBean
argument_list|(
name|whiteboard
argument_list|,
name|IndexStatsMBean
operator|.
name|class
argument_list|,
name|task
operator|.
name|getIndexStats
argument_list|()
argument_list|,
name|IndexStatsMBean
operator|.
name|TYPE
argument_list|,
name|task
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Register AsyncIndexStats for execution stats update
name|regs
operator|.
name|add
argument_list|(
name|scheduleWithFixedDelay
argument_list|(
name|whiteboard
argument_list|,
name|task
operator|.
name|getIndexStats
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|unregister
parameter_list|()
block|{
operator|new
name|CompositeRegistration
argument_list|(
name|regs
argument_list|)
operator|.
name|unregister
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

