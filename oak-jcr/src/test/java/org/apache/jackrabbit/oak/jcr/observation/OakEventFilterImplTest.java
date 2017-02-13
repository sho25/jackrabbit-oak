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
name|jcr
operator|.
name|observation
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|OakEventFilterImplTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testAddAncestorPaths
parameter_list|()
throws|throws
name|Exception
block|{
comment|// parent of / doesnt exist, hence no ancestor path. "" will anyway resolve to "/" in FilterBuilder.getSubTrees()
name|assertMatches
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"/*"
block|}
argument_list|,
literal|"/*"
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"/**"
block|}
argument_list|,
literal|"/**"
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"/a"
block|}
argument_list|,
literal|"/a"
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"/a"
block|,
literal|"/a/b"
block|}
argument_list|,
literal|"/a/b"
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"/a"
block|,
literal|"/a/*"
block|}
argument_list|,
literal|"/a/*"
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"/a"
block|,
literal|"/a/**"
block|}
argument_list|,
literal|"/a/**"
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"/a"
block|,
literal|"/a/b"
block|,
literal|"/a/b/c"
block|}
argument_list|,
literal|"/a/b/c"
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"/a"
block|,
literal|"/a/b"
block|,
literal|"/a/b/*"
block|}
argument_list|,
literal|"/a/b/*"
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"/a"
block|,
literal|"/a/b"
block|,
literal|"/a/b/**"
block|}
argument_list|,
literal|"/a/b/**"
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"/a"
block|,
literal|"/a/b"
block|,
literal|"/a/b/**"
block|}
argument_list|,
literal|"/a/b/**/d"
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"/a"
block|,
literal|"/a/b"
block|,
literal|"/a/b/**"
block|}
argument_list|,
literal|"/a/b/**/d/**"
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"/a"
block|,
literal|"/a/b"
block|,
literal|"/a/b/**"
block|}
argument_list|,
literal|"/a/b/**/d/**/f"
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"/a"
block|,
literal|"/a/b"
block|,
literal|"/a/b/c"
block|,
literal|"/a/b/c/d"
block|}
argument_list|,
literal|"/a/b/c/d"
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"/a"
block|,
literal|"/a/b"
block|,
literal|"/a/b/c"
block|,
literal|"/a/b/c/*"
block|}
argument_list|,
literal|"/a/b/c/*"
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"/a"
block|,
literal|"/a/b"
block|,
literal|"/a/b/c"
block|,
literal|"/a/b/c/**"
block|}
argument_list|,
literal|"/a/b/c/**"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertMatches
parameter_list|(
name|String
index|[]
name|expectedPaths
parameter_list|,
name|String
name|globPath
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|ancestorPaths
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|OakEventFilterImpl
operator|.
name|addAncestorPaths
argument_list|(
name|ancestorPaths
argument_list|,
name|globPath
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|expected
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|expectedPaths
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|ancestorPaths
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

