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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
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
name|MemoryNodeStore
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
name|DefaultWhiteboard
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_class
specifier|public
class|class
name|IndexMBeanRegistrationTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|jobName
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicReference
argument_list|<
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
name|props
init|=
operator|new
name|AtomicReference
argument_list|<
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Whiteboard
name|wb
init|=
operator|new
name|DefaultWhiteboard
argument_list|()
block|{
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Registration
name|register
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
name|T
name|service
parameter_list|,
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|properties
parameter_list|)
block|{
if|if
condition|(
name|service
operator|instanceof
name|AsyncIndexUpdate
condition|)
block|{
name|props
operator|.
name|set
argument_list|(
name|properties
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|register
argument_list|(
name|type
argument_list|,
name|service
argument_list|,
name|properties
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|long
name|schedulingDelayInSecs
init|=
literal|7
decl_stmt|;
comment|// some number which is hard to default else-where
name|AsyncIndexUpdate
name|update
init|=
operator|new
name|AsyncIndexUpdate
argument_list|(
literal|"async"
argument_list|,
operator|new
name|MemoryNodeStore
argument_list|()
argument_list|,
operator|new
name|CompositeIndexEditorProvider
argument_list|()
argument_list|)
decl_stmt|;
name|IndexMBeanRegistration
name|reg
init|=
operator|new
name|IndexMBeanRegistration
argument_list|(
name|wb
argument_list|)
decl_stmt|;
name|reg
operator|.
name|registerAsyncIndexer
argument_list|(
name|update
argument_list|,
name|schedulingDelayInSecs
argument_list|)
expr_stmt|;
try|try
block|{
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|map
init|=
name|props
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|AsyncIndexUpdate
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"-async"
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"scheduler.name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|schedulingDelayInSecs
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"scheduler.period"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"scheduler.concurrent"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"LEADER"
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"scheduler.runOn"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"oak"
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"scheduler.threadPool"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|reg
operator|.
name|unregister
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

