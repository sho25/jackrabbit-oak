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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
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
name|IndexUtilsTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|asyncName
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
name|IndexUtils
operator|.
name|getAsyncLaneName
argument_list|(
name|EMPTY_NODE
argument_list|,
literal|"/fooIndex"
argument_list|)
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"async"
argument_list|,
name|newArrayList
argument_list|(
literal|"async2"
argument_list|,
literal|"sync"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"async2"
argument_list|,
name|IndexUtils
operator|.
name|getAsyncLaneName
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"/fooIndex"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"async"
argument_list|,
name|newArrayList
argument_list|(
literal|"async3"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"async3"
argument_list|,
name|IndexUtils
operator|.
name|getAsyncLaneName
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"/fooIndex"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

