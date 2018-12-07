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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|cug
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|ImmutableList
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
name|ImmutableSet
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

begin_class
specifier|public
class|class
name|SupportedPathsTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testIncludes
parameter_list|()
block|{
name|SupportedPaths
name|supportedPaths
init|=
operator|new
name|SupportedPaths
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"/content"
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|pathMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|pathMap
operator|.
name|put
argument_list|(
literal|"/content"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|pathMap
operator|.
name|put
argument_list|(
literal|"/content/a"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|pathMap
operator|.
name|put
argument_list|(
literal|"/content/a/rep:cugPolicy"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|pathMap
operator|.
name|put
argument_list|(
literal|"/content/a/b"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|pathMap
operator|.
name|put
argument_list|(
literal|"/content/a/b/c/jcr:primaryType"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|pathMap
operator|.
name|put
argument_list|(
literal|"/content/aa"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|pathMap
operator|.
name|put
argument_list|(
literal|"/content/aa/bb/cc"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|pathMap
operator|.
name|put
argument_list|(
literal|"/jcr:system"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|pathMap
operator|.
name|put
argument_list|(
literal|"/"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|pathMap
operator|.
name|put
argument_list|(
literal|"/testRoot"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|pathMap
operator|.
name|put
argument_list|(
literal|"/some/other/path"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
name|pathMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|boolean
name|expected
init|=
name|pathMap
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|path
argument_list|,
name|expected
argument_list|,
name|supportedPaths
operator|.
name|includes
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|path
argument_list|,
name|expected
argument_list|,
name|supportedPaths
operator|.
name|includes
argument_list|(
name|path
operator|+
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMayContainCug
parameter_list|()
block|{
name|SupportedPaths
name|supportedPaths
init|=
operator|new
name|SupportedPaths
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"/content/a"
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|pathMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|pathMap
operator|.
name|put
argument_list|(
literal|"/"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|pathMap
operator|.
name|put
argument_list|(
literal|"/content"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|pathMap
operator|.
name|put
argument_list|(
literal|"/jcr:system"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|pathMap
operator|.
name|put
argument_list|(
literal|"/testRoot"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|pathMap
operator|.
name|put
argument_list|(
literal|"/some/other/path"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|pathMap
operator|.
name|put
argument_list|(
literal|"/content/a"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|pathMap
operator|.
name|put
argument_list|(
literal|"/content/a/b"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
name|pathMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|boolean
name|expected
init|=
name|pathMap
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|path
argument_list|,
name|expected
argument_list|,
name|supportedPaths
operator|.
name|mayContainCug
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRootPath
parameter_list|()
block|{
name|SupportedPaths
name|supportedPaths
init|=
operator|new
name|SupportedPaths
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/"
argument_list|,
literal|"/content"
argument_list|,
literal|"/jcr:system"
argument_list|,
literal|"/testRoot"
argument_list|,
literal|"/some/other/path"
argument_list|,
literal|"/content/a"
argument_list|,
literal|"/content/a/b"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
name|assertTrue
argument_list|(
name|path
argument_list|,
name|supportedPaths
operator|.
name|includes
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|path
argument_list|,
name|supportedPaths
operator|.
name|mayContainCug
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmpty
parameter_list|()
block|{
name|SupportedPaths
name|supportedPaths
init|=
operator|new
name|SupportedPaths
argument_list|(
name|ImmutableSet
operator|.
expr|<
name|String
operator|>
name|of
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/"
argument_list|,
literal|"/content"
argument_list|,
literal|"/jcr:system"
argument_list|,
literal|"/testRoot"
argument_list|,
literal|"/some/other/path"
argument_list|,
literal|"/content/a"
argument_list|,
literal|"/content/a/b"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
name|assertFalse
argument_list|(
name|path
argument_list|,
name|supportedPaths
operator|.
name|includes
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|path
argument_list|,
name|supportedPaths
operator|.
name|mayContainCug
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

