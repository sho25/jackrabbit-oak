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
name|query
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|InitialContent
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
name|Oak
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
name|ContentRepository
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
name|Tree
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
name|security
operator|.
name|OpenSecurityProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|List
import|;
end_import

begin_class
specifier|public
class|class
name|PropertyInexistenceTest
extends|extends
name|AbstractQueryTest
block|{
annotation|@
name|Override
specifier|protected
name|ContentRepository
name|createRepository
parameter_list|()
block|{
return|return
operator|new
name|Oak
argument_list|()
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|InitialContent
argument_list|()
argument_list|)
operator|.
name|createContentRepository
argument_list|()
return|;
block|}
specifier|private
name|String
name|initVal
init|=
literal|null
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|initVal
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"oak.useOldInexistenceCheck"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
if|if
condition|(
name|initVal
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"oak.useOldInexistenceCheck"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"oak.useOldInexistenceCheck"
argument_list|,
name|initVal
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|inexistence
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|rootTree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|rootTree
operator|.
name|addChild
argument_list|(
literal|"x"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"y"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|String
name|query1
init|=
literal|"SELECT * FROM [nt:base] WHERE ISDESCENDANTNODE('/a') AND [z] IS NULL"
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expected1
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/a/x"
argument_list|,
literal|"/a/x/y"
argument_list|)
decl_stmt|;
name|String
name|query2
init|=
literal|"SELECT * FROM [nt:base] WHERE ISDESCENDANTNODE('/a/x') AND [z] IS NULL"
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expected2
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/a/x/y"
argument_list|)
decl_stmt|;
name|assertQuery
argument_list|(
name|query1
argument_list|,
name|expected1
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|query2
argument_list|,
name|expected2
argument_list|)
expr_stmt|;
comment|// old behavior remains same as new for non-relative constraints
name|System
operator|.
name|setProperty
argument_list|(
literal|"oak.useOldInexistenceCheck"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|query1
argument_list|,
name|expected1
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|query2
argument_list|,
name|expected2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|relativeInexistence
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|rootTree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|rootTree
operator|.
name|addChild
argument_list|(
literal|"x"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"y"
argument_list|)
expr_stmt|;
name|rootTree
operator|.
name|addChild
argument_list|(
literal|"x1"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|String
name|query1
init|=
literal|"SELECT * FROM [nt:base] WHERE ISDESCENDANTNODE('/a') AND [y/z] IS NULL"
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expected1
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/a/x"
argument_list|,
literal|"/a/x/y"
argument_list|,
literal|"/a/x1"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expectedOld1
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/a/x"
argument_list|)
decl_stmt|;
name|String
name|query2
init|=
literal|"SELECT * FROM [nt:base] WHERE ISDESCENDANTNODE('/a/x') AND [y/z] IS NULL"
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expected2
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/a/x/y"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expectedOld2
init|=
name|ImmutableList
operator|.
name|of
argument_list|()
decl_stmt|;
name|assertQuery
argument_list|(
name|query1
argument_list|,
name|expected1
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|query2
argument_list|,
name|expected2
argument_list|)
expr_stmt|;
comment|// old behavior for relative constraints differs from new
name|System
operator|.
name|setProperty
argument_list|(
literal|"oak.useOldInexistenceCheck"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|query1
argument_list|,
name|expectedOld1
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|query2
argument_list|,
name|expectedOld2
argument_list|)
expr_stmt|;
name|rootTree
operator|.
name|addChild
argument_list|(
literal|"x2"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"z"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"y"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|rootTree
operator|.
name|addChild
argument_list|(
literal|"x2"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"z1"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"y"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"z"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"oak.useOldInexistenceCheck"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|String
name|query3
init|=
literal|"SELECT * FROM [nt:base] WHERE ISDESCENDANTNODE('/a/x2') AND [y/z] IS NULL"
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expected3
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/a/x2/z"
argument_list|,
literal|"/a/x2/z1/y"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expectedOld3
init|=
name|ImmutableList
operator|.
name|of
argument_list|()
decl_stmt|;
name|assertQuery
argument_list|(
name|query3
argument_list|,
name|expected3
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"oak.useOldInexistenceCheck"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|query3
argument_list|,
name|expectedOld3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|deeperRelativeInexistence
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|rootTree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|rootTree
operator|.
name|addChild
argument_list|(
literal|"x"
argument_list|)
expr_stmt|;
name|rootTree
operator|.
name|addChild
argument_list|(
literal|"x1"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"w"
argument_list|)
expr_stmt|;
name|rootTree
operator|.
name|addChild
argument_list|(
literal|"x2"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"w"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"y"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|String
name|query
init|=
literal|"SELECT * FROM [nt:base] WHERE ISDESCENDANTNODE('/a') AND [w/y/z] IS NULL"
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/a/x"
argument_list|,
literal|"/a/x1"
argument_list|,
literal|"/a/x1/w"
argument_list|,
literal|"/a/x2"
argument_list|,
literal|"/a/x2/w"
argument_list|,
literal|"/a/x2/w/y"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expectedOld
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/a/x2"
argument_list|)
decl_stmt|;
name|assertQuery
argument_list|(
name|query
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"oak.useOldInexistenceCheck"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|query
argument_list|,
name|expectedOld
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
