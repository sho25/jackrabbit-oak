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
name|document
operator|.
name|bundlor
package|;
end_package

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
name|CompositeMatcherTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|empty
parameter_list|()
throws|throws
name|Exception
block|{
name|Matcher
name|m
init|=
name|CompositeMatcher
operator|.
name|compose
argument_list|(
name|Collections
operator|.
expr|<
name|Matcher
operator|>
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|m
operator|.
name|isMatch
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|multiWithFailing
parameter_list|()
throws|throws
name|Exception
block|{
name|CompositeMatcher
operator|.
name|compose
argument_list|(
name|asList
argument_list|(
operator|new
name|Include
argument_list|(
literal|"x"
argument_list|)
operator|.
name|createMatcher
argument_list|()
argument_list|,
name|Matcher
operator|.
name|NON_MATCHING
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|multi
parameter_list|()
throws|throws
name|Exception
block|{
name|Matcher
name|m
init|=
name|CompositeMatcher
operator|.
name|compose
argument_list|(
name|asList
argument_list|(
operator|new
name|Include
argument_list|(
literal|"x/z"
argument_list|)
operator|.
name|createMatcher
argument_list|()
argument_list|,
operator|new
name|Include
argument_list|(
literal|"x/y"
argument_list|)
operator|.
name|createMatcher
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Matcher
name|m2
init|=
name|m
operator|.
name|next
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|m2
operator|.
name|isMatch
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"x"
argument_list|,
name|m2
operator|.
name|getMatchedPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|m2
operator|.
name|depth
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|m
operator|.
name|next
argument_list|(
literal|"a"
argument_list|)
operator|.
name|isMatch
argument_list|()
argument_list|)
expr_stmt|;
name|Matcher
name|m3
init|=
name|m2
operator|.
name|next
argument_list|(
literal|"y"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|m3
operator|.
name|isMatch
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"x/y"
argument_list|,
name|m3
operator|.
name|getMatchedPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|m3
operator|.
name|depth
argument_list|()
argument_list|)
expr_stmt|;
name|Matcher
name|m4
init|=
name|m3
operator|.
name|next
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|m4
operator|.
name|isMatch
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|matchChildren
parameter_list|()
throws|throws
name|Exception
block|{
comment|//Hypothetical case. First pattern is redundant
name|Matcher
name|m
init|=
name|CompositeMatcher
operator|.
name|compose
argument_list|(
name|asList
argument_list|(
operator|new
name|Include
argument_list|(
literal|"x/z"
argument_list|)
operator|.
name|createMatcher
argument_list|()
argument_list|,
operator|new
name|Include
argument_list|(
literal|"x/*"
argument_list|)
operator|.
name|createMatcher
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|m
operator|.
name|matchesAllChildren
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|m
operator|.
name|next
argument_list|(
literal|"x"
argument_list|)
operator|.
name|matchesAllChildren
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

