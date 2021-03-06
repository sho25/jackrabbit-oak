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
name|plugins
operator|.
name|index
operator|.
name|importer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|api
operator|.
name|Type
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
name|index
operator|.
name|IndexConstants
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
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
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
operator|.
name|ASYNC_PROPERTY_NAME
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
name|plugins
operator|.
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|AsyncLaneSwitcherTest
block|{
specifier|private
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|switchNone
parameter_list|()
throws|throws
name|Exception
block|{
name|AsyncLaneSwitcher
operator|.
name|switchLane
argument_list|(
name|builder
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|PropertyState
name|previous
init|=
name|builder
operator|.
name|getProperty
argument_list|(
name|AsyncLaneSwitcher
operator|.
name|ASYNC_PREVIOUS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|previous
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|AsyncLaneSwitcher
operator|.
name|ASYNC_PREVIOUS_NONE
argument_list|,
name|previous
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|switchSingleAsync
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|ASYNC_PROPERTY_NAME
argument_list|,
literal|"async"
argument_list|)
expr_stmt|;
name|AsyncLaneSwitcher
operator|.
name|switchLane
argument_list|(
name|builder
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|PropertyState
name|previous
init|=
name|builder
operator|.
name|getProperty
argument_list|(
name|AsyncLaneSwitcher
operator|.
name|ASYNC_PREVIOUS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|previous
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"async"
argument_list|,
name|previous
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|switchAsyncArray
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|ASYNC_PROPERTY_NAME
argument_list|,
name|asList
argument_list|(
literal|"async"
argument_list|,
literal|"nrt"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|AsyncLaneSwitcher
operator|.
name|switchLane
argument_list|(
name|builder
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|PropertyState
name|previous
init|=
name|builder
operator|.
name|getProperty
argument_list|(
name|AsyncLaneSwitcher
operator|.
name|ASYNC_PREVIOUS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|previous
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asList
argument_list|(
literal|"async"
argument_list|,
literal|"nrt"
argument_list|)
argument_list|,
name|previous
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|multipleSwitch
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|ASYNC_PROPERTY_NAME
argument_list|,
literal|"async"
argument_list|)
expr_stmt|;
name|AsyncLaneSwitcher
operator|.
name|switchLane
argument_list|(
name|builder
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|AsyncLaneSwitcher
operator|.
name|switchLane
argument_list|(
name|builder
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|PropertyState
name|previous
init|=
name|builder
operator|.
name|getProperty
argument_list|(
name|AsyncLaneSwitcher
operator|.
name|ASYNC_PREVIOUS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|previous
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"async"
argument_list|,
name|previous
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|revert
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|ASYNC_PROPERTY_NAME
argument_list|,
literal|"async"
argument_list|)
expr_stmt|;
name|AsyncLaneSwitcher
operator|.
name|switchLane
argument_list|(
name|builder
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|builder
operator|.
name|getProperty
argument_list|(
name|AsyncLaneSwitcher
operator|.
name|ASYNC_PREVIOUS
argument_list|)
argument_list|)
expr_stmt|;
name|AsyncLaneSwitcher
operator|.
name|revertSwitch
argument_list|(
name|builder
argument_list|,
literal|"/fooIndex"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|builder
operator|.
name|getProperty
argument_list|(
name|AsyncLaneSwitcher
operator|.
name|ASYNC_PREVIOUS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"async"
argument_list|,
name|builder
operator|.
name|getString
argument_list|(
name|ASYNC_PROPERTY_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|revert_Sync
parameter_list|()
throws|throws
name|Exception
block|{
name|AsyncLaneSwitcher
operator|.
name|switchLane
argument_list|(
name|builder
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|AsyncLaneSwitcher
operator|.
name|revertSwitch
argument_list|(
name|builder
argument_list|,
literal|"/fooIndex"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|builder
operator|.
name|getProperty
argument_list|(
name|AsyncLaneSwitcher
operator|.
name|ASYNC_PREVIOUS
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|builder
operator|.
name|getProperty
argument_list|(
name|ASYNC_PROPERTY_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|switchAndRevertMulti
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|ASYNC_PROPERTY_NAME
argument_list|,
name|asList
argument_list|(
literal|"async"
argument_list|,
literal|"nrt"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|AsyncLaneSwitcher
operator|.
name|switchLane
argument_list|(
name|builder
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|AsyncLaneSwitcher
operator|.
name|revertSwitch
argument_list|(
name|builder
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

