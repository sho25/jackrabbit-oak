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
name|multiplex
package|;
end_package

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
name|Collections
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
name|ImmutableList
operator|.
name|of
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
name|assertFalse
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|SimpleMountInfoProviderTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|defaultMount
parameter_list|()
throws|throws
name|Exception
block|{
name|MountInfoProvider
name|mip
init|=
operator|new
name|SimpleMountInfoProvider
argument_list|(
name|Collections
operator|.
expr|<
name|MountInfo
operator|>
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|mip
operator|.
name|getMountByPath
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mip
operator|.
name|getMountByPath
argument_list|(
literal|"/a"
argument_list|)
operator|.
name|isDefault
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|mip
operator|.
name|hasNonDefaultMounts
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|basicMounting
parameter_list|()
throws|throws
name|Exception
block|{
name|MountInfoProvider
name|mip
init|=
name|SimpleMountInfoProvider
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
name|mount
argument_list|(
literal|"bar"
argument_list|,
literal|"/x"
argument_list|,
literal|"/y"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|mip
operator|.
name|getMountByPath
argument_list|(
literal|"/a"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|mip
operator|.
name|getMountByPath
argument_list|(
literal|"/a/x"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|mip
operator|.
name|getMountByPath
argument_list|(
literal|"/x"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mip
operator|.
name|getMountByPath
argument_list|(
literal|"/z"
argument_list|)
operator|.
name|isDefault
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mip
operator|.
name|hasNonDefaultMounts
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nonDefaultMounts
parameter_list|()
throws|throws
name|Exception
block|{
name|MountInfoProvider
name|mip
init|=
name|SimpleMountInfoProvider
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
name|mount
argument_list|(
literal|"bar"
argument_list|,
literal|"/x"
argument_list|,
literal|"/y"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|Mount
argument_list|>
name|mounts
init|=
name|mip
operator|.
name|getNonDefaultMounts
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|mounts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|mounts
operator|.
name|contains
argument_list|(
name|Mount
operator|.
name|DEFAULT
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|mip
operator|.
name|getMountByName
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|mip
operator|.
name|getMountByName
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|mip
operator|.
name|getMountByName
argument_list|(
literal|"boom"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|readOnlyMounting
parameter_list|()
throws|throws
name|Exception
block|{
name|MountInfoProvider
name|mip
init|=
name|SimpleMountInfoProvider
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
name|readOnlyMount
argument_list|(
literal|"bar"
argument_list|,
literal|"/x"
argument_list|,
literal|"/y"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|mip
operator|.
name|getMountByName
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|isReadOnly
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|mip
operator|.
name|getMountByName
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|isReadOnly
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

