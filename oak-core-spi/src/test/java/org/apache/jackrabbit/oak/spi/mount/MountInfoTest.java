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
name|mount
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
name|assertTrue
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
name|MountInfo
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
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_class
specifier|public
class|class
name|MountInfoTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testIsMounted
parameter_list|()
throws|throws
name|Exception
block|{
name|MountInfo
name|md
init|=
operator|new
name|MountInfo
argument_list|(
literal|"foo"
argument_list|,
literal|false
argument_list|,
name|of
argument_list|(
literal|"/x/y"
argument_list|)
argument_list|,
name|of
argument_list|(
literal|"/a"
argument_list|,
literal|"/b"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|md
operator|.
name|isMounted
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|md
operator|.
name|isMounted
argument_list|(
literal|"/b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|md
operator|.
name|isMounted
argument_list|(
literal|"/b/c/d"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"dynamic mount path not recognized"
argument_list|,
name|md
operator|.
name|isMounted
argument_list|(
literal|"/x/y/oak:mount-foo/a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"dynamic mount path not recognized"
argument_list|,
name|md
operator|.
name|isMounted
argument_list|(
literal|"/x/y/z/oak:mount-foo/a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|md
operator|.
name|isMounted
argument_list|(
literal|"/x/y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|md
operator|.
name|isMounted
argument_list|(
literal|"/x/y/foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|md
operator|.
name|isMounted
argument_list|(
literal|"/d/c/oak:mount-foo/a"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsUnder
parameter_list|()
block|{
name|MountInfo
name|md
init|=
operator|new
name|MountInfo
argument_list|(
literal|"foo"
argument_list|,
literal|false
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptyList
argument_list|()
argument_list|,
name|of
argument_list|(
literal|"/apps"
argument_list|,
literal|"/etc/config"
argument_list|,
literal|"/content/my/site"
argument_list|,
literal|"/var"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|md
operator|.
name|isUnder
argument_list|(
literal|"/etc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|md
operator|.
name|isUnder
argument_list|(
literal|"/content"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|md
operator|.
name|isUnder
argument_list|(
literal|"/content/my"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|md
operator|.
name|isUnder
argument_list|(
literal|"/content/my/site"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|md
operator|.
name|isUnder
argument_list|(
literal|"/libs"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|md
operator|.
name|isUnder
argument_list|(
literal|"/tmp"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsDirectlyUnder
parameter_list|()
block|{
name|MountInfo
name|md
init|=
operator|new
name|MountInfo
argument_list|(
literal|"foo"
argument_list|,
literal|false
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptyList
argument_list|()
argument_list|,
name|of
argument_list|(
literal|"/apps"
argument_list|,
literal|"/etc/my/config"
argument_list|,
literal|"/var"
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|md
operator|.
name|isDirectlyUnder
argument_list|(
literal|"/etc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|md
operator|.
name|isDirectlyUnder
argument_list|(
literal|"/etc/my"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|md
operator|.
name|isDirectlyUnder
argument_list|(
literal|"/etc/my/config"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|md
operator|.
name|isDirectlyUnder
argument_list|(
literal|"/libs"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
