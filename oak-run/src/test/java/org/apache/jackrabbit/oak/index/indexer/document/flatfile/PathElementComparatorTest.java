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
name|index
operator|.
name|indexer
operator|.
name|document
operator|.
name|flatfile
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singleton
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

begin_class
specifier|public
class|class
name|PathElementComparatorTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|sortPathsParentChild
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|sorted
init|=
name|TestUtils
operator|.
name|sortPaths
argument_list|(
name|asList
argument_list|(
literal|"/a"
argument_list|,
literal|"/a b"
argument_list|,
literal|"/a/bw"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|asList
argument_list|(
literal|"/a"
argument_list|,
literal|"/a/bw"
argument_list|,
literal|"/a b"
argument_list|)
argument_list|,
name|sorted
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|sort2
parameter_list|()
block|{
name|assertSorted
argument_list|(
name|asList
argument_list|(
literal|"/a"
argument_list|,
literal|"/a/b"
argument_list|,
literal|"/a/b/c"
argument_list|,
literal|"/d"
argument_list|,
literal|"/e/f"
argument_list|,
literal|"/g"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSorted
argument_list|(
name|asList
argument_list|(
literal|"/"
argument_list|,
literal|"/a"
argument_list|,
literal|"/a/b"
argument_list|,
literal|"/a/b/c"
argument_list|,
literal|"/d"
argument_list|,
literal|"/e/f"
argument_list|,
literal|"/g"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSorted
argument_list|(
name|asList
argument_list|(
literal|"/"
argument_list|,
literal|"/a"
argument_list|,
literal|"/a/b"
argument_list|,
literal|"/a/b/b"
argument_list|,
literal|"/a/b/c"
argument_list|,
literal|"/d"
argument_list|,
literal|"/e/f"
argument_list|,
literal|"/g"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSorted
argument_list|(
name|asList
argument_list|(
literal|"/"
argument_list|,
literal|"/a"
argument_list|,
literal|"/a/b"
argument_list|,
literal|"/a/b/bc"
argument_list|,
literal|"/a/b/c"
argument_list|,
literal|"/d"
argument_list|,
literal|"/e/f"
argument_list|,
literal|"/g"
argument_list|)
argument_list|)
expr_stmt|;
comment|//Duplicates
name|assertSorted
argument_list|(
name|asList
argument_list|(
literal|"/"
argument_list|,
literal|"/a"
argument_list|,
literal|"/a"
argument_list|,
literal|"/a/b"
argument_list|,
literal|"/a/b/c"
argument_list|,
literal|"/d"
argument_list|,
literal|"/e/f"
argument_list|,
literal|"/g"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|preferredElements
parameter_list|()
block|{
name|PathElementComparator
name|c
init|=
operator|new
name|PathElementComparator
argument_list|(
name|singleton
argument_list|(
literal|"jcr:content"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|asList
argument_list|(
literal|"/a"
argument_list|,
literal|"/a/jcr:content"
argument_list|,
literal|"/a/b"
argument_list|)
argument_list|,
name|TestUtils
operator|.
name|sortPaths
argument_list|(
name|asList
argument_list|(
literal|"/a/jcr:content"
argument_list|,
literal|"/a/b"
argument_list|,
literal|"/a"
argument_list|)
argument_list|,
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|assertSorted
argument_list|(
name|asList
argument_list|(
literal|"/a"
argument_list|,
literal|"/a/jcr:content"
argument_list|,
literal|"/a/b"
argument_list|)
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|assertSorted
argument_list|(
name|asList
argument_list|(
literal|"/a"
argument_list|,
literal|"/a/jcr:content"
argument_list|,
literal|"/a/b"
argument_list|,
literal|"/a/b/c"
argument_list|,
literal|"/d"
argument_list|,
literal|"/e/f"
argument_list|,
literal|"/g"
argument_list|)
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertSorted
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|sorted
parameter_list|)
block|{
name|assertSorted
argument_list|(
name|sorted
argument_list|,
operator|new
name|PathElementComparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertSorted
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|sorted
parameter_list|,
name|Comparator
argument_list|<
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|>
name|comparator
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|copy
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|sorted
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|copy
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|sortedNew
init|=
name|TestUtils
operator|.
name|sortPaths
argument_list|(
name|copy
argument_list|,
name|comparator
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|sorted
argument_list|,
name|sortedNew
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

