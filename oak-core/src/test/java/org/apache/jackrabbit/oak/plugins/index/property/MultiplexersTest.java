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
name|property
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
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
operator|.
name|INDEX_CONTENT_NODE_NAME
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
name|property
operator|.
name|Multiplexers
operator|.
name|getIndexNodeName
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
name|property
operator|.
name|Multiplexers
operator|.
name|getNodeForMount
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
name|mount
operator|.
name|Mount
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
name|mount
operator|.
name|MountInfoProvider
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
name|mount
operator|.
name|Mounts
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

begin_class
specifier|public
class|class
name|MultiplexersTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|defaultSetup
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|INDEX_CONTENT_NODE_NAME
argument_list|,
name|getIndexNodeName
argument_list|(
name|Mounts
operator|.
name|defaultMountInfoProvider
argument_list|()
argument_list|,
literal|"/foo"
argument_list|,
name|INDEX_CONTENT_NODE_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|INDEX_CONTENT_NODE_NAME
argument_list|,
name|getNodeForMount
argument_list|(
name|Mounts
operator|.
name|defaultMount
argument_list|()
argument_list|,
name|INDEX_CONTENT_NODE_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|customNodeName
parameter_list|()
throws|throws
name|Exception
block|{
name|MountInfoProvider
name|mip
init|=
name|Mounts
operator|.
name|newBuilder
argument_list|()
operator|.
name|mount
argument_list|(
literal|"foo"
argument_list|,
literal|"/a"
argument_list|,
literal|"/b"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Mount
name|m
init|=
name|mip
operator|.
name|getMountByName
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|":index"
argument_list|,
name|getIndexNodeName
argument_list|(
name|mip
argument_list|,
literal|"/foo"
argument_list|,
name|INDEX_CONTENT_NODE_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|":index"
argument_list|,
name|getNodeForMount
argument_list|(
name|mip
operator|.
name|getDefaultMount
argument_list|()
argument_list|,
name|INDEX_CONTENT_NODE_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|":"
operator|+
name|m
operator|.
name|getPathFragmentName
argument_list|()
operator|+
literal|"-index"
argument_list|,
name|getIndexNodeName
argument_list|(
name|mip
argument_list|,
literal|"/a"
argument_list|,
name|INDEX_CONTENT_NODE_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|":"
operator|+
name|m
operator|.
name|getPathFragmentName
argument_list|()
operator|+
literal|"-index"
argument_list|,
name|getNodeForMount
argument_list|(
name|m
argument_list|,
name|INDEX_CONTENT_NODE_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

