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
operator|.
name|index
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

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
name|Random
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
name|query
operator|.
name|ast
operator|.
name|Operator
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
name|query
operator|.
name|Filter
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
name|query
operator|.
name|PropertyValue
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
name|query
operator|.
name|PropertyValues
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

begin_comment
comment|/**  * Tests the Filter class.  */
end_comment

begin_class
specifier|public
class|class
name|FilterTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|propertyRestriction
parameter_list|()
block|{
name|PropertyValue
name|one
init|=
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"1"
argument_list|)
decl_stmt|;
name|PropertyValue
name|two
init|=
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"2"
argument_list|)
decl_stmt|;
name|FilterImpl
name|f
init|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|null
operator|==
name|f
operator|.
name|getPropertyRestriction
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|LESS_OR_EQUAL
argument_list|,
name|two
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"..2]"
argument_list|,
name|f
operator|.
name|getPropertyRestriction
argument_list|(
literal|"x"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|GREATER_OR_EQUAL
argument_list|,
name|one
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[1..2]"
argument_list|,
name|f
operator|.
name|getPropertyRestriction
argument_list|(
literal|"x"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|GREATER_THAN
argument_list|,
name|one
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(1..2]"
argument_list|,
name|f
operator|.
name|getPropertyRestriction
argument_list|(
literal|"x"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|LESS_THAN
argument_list|,
name|two
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(1..2)"
argument_list|,
name|f
operator|.
name|getPropertyRestriction
argument_list|(
literal|"x"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|two
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|isAlwaysFalse
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|one
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[1..1]"
argument_list|,
name|f
operator|.
name|getPropertyRestriction
argument_list|(
literal|"x"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|one
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[1..1]"
argument_list|,
name|f
operator|.
name|getPropertyRestriction
argument_list|(
literal|"x"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|GREATER_OR_EQUAL
argument_list|,
name|one
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[1..1]"
argument_list|,
name|f
operator|.
name|getPropertyRestriction
argument_list|(
literal|"x"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|LESS_OR_EQUAL
argument_list|,
name|one
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[1..1]"
argument_list|,
name|f
operator|.
name|getPropertyRestriction
argument_list|(
literal|"x"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|GREATER_THAN
argument_list|,
name|one
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|isAlwaysFalse
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|one
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[1..1]"
argument_list|,
name|f
operator|.
name|getPropertyRestriction
argument_list|(
literal|"x"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|LESS_THAN
argument_list|,
name|one
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|isAlwaysFalse
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|NOT_EQUAL
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|".."
argument_list|,
name|f
operator|.
name|getPropertyRestriction
argument_list|(
literal|"x"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|LESS_THAN
argument_list|,
name|one
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"..1)"
argument_list|,
name|f
operator|.
name|getPropertyRestriction
argument_list|(
literal|"x"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|two
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|isAlwaysFalse
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|pathRestrictionsRandomized
parameter_list|()
throws|throws
name|Exception
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|paths
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// create paths /a, /b, /c, /a/a, /a/b, ... /c/c/c
name|paths
operator|.
name|add
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|'a'
init|;
name|i
operator|<=
literal|'c'
condition|;
name|i
operator|++
control|)
block|{
name|String
name|p1
init|=
literal|"/"
operator|+
operator|(
name|char
operator|)
name|i
decl_stmt|;
name|paths
operator|.
name|add
argument_list|(
name|p1
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|'a'
init|;
name|j
operator|<=
literal|'c'
condition|;
name|j
operator|++
control|)
block|{
name|String
name|p2
init|=
literal|"/"
operator|+
operator|(
name|char
operator|)
name|j
decl_stmt|;
name|paths
operator|.
name|add
argument_list|(
name|p1
operator|+
name|p2
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|'a'
init|;
name|k
operator|<=
literal|'c'
condition|;
name|k
operator|++
control|)
block|{
name|String
name|p3
init|=
literal|"/"
operator|+
operator|(
name|char
operator|)
name|k
decl_stmt|;
name|paths
operator|.
name|add
argument_list|(
name|p1
operator|+
name|p2
operator|+
name|p3
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|String
name|p1
init|=
name|paths
operator|.
name|get
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|paths
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|p2
init|=
name|paths
operator|.
name|get
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|paths
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Filter
operator|.
name|PathRestriction
name|r1
init|=
name|Filter
operator|.
name|PathRestriction
operator|.
name|values
argument_list|()
index|[
name|r
operator|.
name|nextInt
argument_list|(
name|Filter
operator|.
name|PathRestriction
operator|.
name|values
argument_list|()
operator|.
name|length
argument_list|)
index|]
decl_stmt|;
name|Filter
operator|.
name|PathRestriction
name|r2
init|=
name|Filter
operator|.
name|PathRestriction
operator|.
name|values
argument_list|()
index|[
name|r
operator|.
name|nextInt
argument_list|(
name|Filter
operator|.
name|PathRestriction
operator|.
name|values
argument_list|()
operator|.
name|length
argument_list|)
index|]
decl_stmt|;
name|FilterImpl
name|f1
init|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|f1
operator|.
name|restrictPath
argument_list|(
name|p1
argument_list|,
name|r1
argument_list|)
expr_stmt|;
name|FilterImpl
name|f2
init|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|f2
operator|.
name|restrictPath
argument_list|(
name|p2
argument_list|,
name|r2
argument_list|)
expr_stmt|;
name|FilterImpl
name|fc
init|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|fc
operator|.
name|restrictPath
argument_list|(
name|p1
argument_list|,
name|r1
argument_list|)
expr_stmt|;
name|fc
operator|.
name|restrictPath
argument_list|(
name|p2
argument_list|,
name|r2
argument_list|)
expr_stmt|;
name|int
name|tooMany
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|paths
control|)
block|{
name|boolean
name|expected
init|=
name|f1
operator|.
name|testPath
argument_list|(
name|p
argument_list|)
operator|&&
name|f2
operator|.
name|testPath
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|boolean
name|got
init|=
name|fc
operator|.
name|testPath
argument_list|(
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|expected
operator|==
name|got
condition|)
block|{
comment|// good
block|}
elseif|else
if|if
condition|(
name|expected
operator|&&
operator|!
name|got
condition|)
block|{
name|fc
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fc
operator|.
name|restrictPath
argument_list|(
name|p1
argument_list|,
name|r1
argument_list|)
expr_stmt|;
name|fc
operator|.
name|restrictPath
argument_list|(
name|p2
argument_list|,
name|r2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"not matched: "
operator|+
name|p1
operator|+
literal|"/"
operator|+
name|r1
operator|.
name|name
argument_list|()
operator|+
literal|"&& "
operator|+
name|p2
operator|+
literal|"/"
operator|+
name|r2
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// not great, but not a problem
name|tooMany
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|tooMany
operator|>
literal|3
condition|)
block|{
name|fail
argument_list|(
literal|"too many matches: "
operator|+
name|p1
operator|+
literal|"/"
operator|+
name|r1
operator|.
name|name
argument_list|()
operator|+
literal|"&& "
operator|+
name|p2
operator|+
literal|"/"
operator|+
name|r2
operator|.
name|name
argument_list|()
operator|+
literal|" superfluous: "
operator|+
name|tooMany
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|pathRestrictions
parameter_list|()
throws|throws
name|Exception
block|{
name|FilterImpl
name|f
init|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"/"
argument_list|,
name|f
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Filter
operator|.
name|PathRestriction
operator|.
name|ALL_CHILDREN
argument_list|,
name|f
operator|.
name|getPathRestriction
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|ALL_CHILDREN
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test2"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|ALL_CHILDREN
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|isAlwaysFalse
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|ALL_CHILDREN
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test"
argument_list|,
name|f
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Filter
operator|.
name|PathRestriction
operator|.
name|ALL_CHILDREN
argument_list|,
name|f
operator|.
name|getPathRestriction
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test/x"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|DIRECT_CHILDREN
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test/x"
argument_list|,
name|f
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Filter
operator|.
name|PathRestriction
operator|.
name|DIRECT_CHILDREN
argument_list|,
name|f
operator|.
name|getPathRestriction
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test/x/y"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|PARENT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test/x/y"
argument_list|,
name|f
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Filter
operator|.
name|PathRestriction
operator|.
name|PARENT
argument_list|,
name|f
operator|.
name|getPathRestriction
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|DIRECT_CHILDREN
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test/x/y"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|PARENT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test/x/y"
argument_list|,
name|f
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Filter
operator|.
name|PathRestriction
operator|.
name|PARENT
argument_list|,
name|f
operator|.
name|getPathRestriction
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test/y"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|DIRECT_CHILDREN
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|isAlwaysFalse
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test/x/y"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|PARENT
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test/x"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|EXACT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test/x"
argument_list|,
name|f
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Filter
operator|.
name|PathRestriction
operator|.
name|EXACT
argument_list|,
name|f
operator|.
name|getPathRestriction
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test/y"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|EXACT
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|isAlwaysFalse
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|ALL_CHILDREN
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|PARENT
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|isAlwaysFalse
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test/x"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|PARENT
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|ALL_CHILDREN
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test/x"
argument_list|,
name|f
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Filter
operator|.
name|PathRestriction
operator|.
name|PARENT
argument_list|,
name|f
operator|.
name|getPathRestriction
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test/x"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|ALL_CHILDREN
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|isAlwaysFalse
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|ALL_CHILDREN
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|EXACT
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|isAlwaysFalse
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|DIRECT_CHILDREN
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test/x"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|EXACT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test/x"
argument_list|,
name|f
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Filter
operator|.
name|PathRestriction
operator|.
name|EXACT
argument_list|,
name|f
operator|.
name|getPathRestriction
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|DIRECT_CHILDREN
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test/x/y"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|EXACT
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|isAlwaysFalse
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test/x"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|PARENT
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|ALL_CHILDREN
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test/x"
argument_list|,
name|f
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Filter
operator|.
name|PathRestriction
operator|.
name|PARENT
argument_list|,
name|f
operator|.
name|getPathRestriction
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test/y"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|EXACT
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|isAlwaysFalse
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|DIRECT_CHILDREN
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test"
argument_list|,
name|f
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Filter
operator|.
name|PathRestriction
operator|.
name|DIRECT_CHILDREN
argument_list|,
name|f
operator|.
name|getPathRestriction
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|ALL_CHILDREN
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test"
argument_list|,
name|f
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Filter
operator|.
name|PathRestriction
operator|.
name|DIRECT_CHILDREN
argument_list|,
name|f
operator|.
name|getPathRestriction
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|ALL_CHILDREN
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test"
argument_list|,
name|f
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Filter
operator|.
name|PathRestriction
operator|.
name|DIRECT_CHILDREN
argument_list|,
name|f
operator|.
name|getPathRestriction
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test/x/y"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|PARENT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test/x/y"
argument_list|,
name|f
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Filter
operator|.
name|PathRestriction
operator|.
name|PARENT
argument_list|,
name|f
operator|.
name|getPathRestriction
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test2"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|ALL_CHILDREN
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|isAlwaysFalse
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test/x"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|EXACT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test/x"
argument_list|,
name|f
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Filter
operator|.
name|PathRestriction
operator|.
name|EXACT
argument_list|,
name|f
operator|.
name|getPathRestriction
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|ALL_CHILDREN
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|DIRECT_CHILDREN
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test/x/y"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|PARENT
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test/y"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|DIRECT_CHILDREN
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|isAlwaysFalse
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test/x/y"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|PARENT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test/x/y"
argument_list|,
name|f
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Filter
operator|.
name|PathRestriction
operator|.
name|PARENT
argument_list|,
name|f
operator|.
name|getPathRestriction
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test/x"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|PARENT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test/x"
argument_list|,
name|f
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Filter
operator|.
name|PathRestriction
operator|.
name|PARENT
argument_list|,
name|f
operator|.
name|getPathRestriction
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|ALL_CHILDREN
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test/x"
argument_list|,
name|f
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Filter
operator|.
name|PathRestriction
operator|.
name|PARENT
argument_list|,
name|f
operator|.
name|getPathRestriction
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|DIRECT_CHILDREN
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test/x"
argument_list|,
name|f
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Filter
operator|.
name|PathRestriction
operator|.
name|PARENT
argument_list|,
name|f
operator|.
name|getPathRestriction
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test/x"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|PARENT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test/x"
argument_list|,
name|f
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Filter
operator|.
name|PathRestriction
operator|.
name|PARENT
argument_list|,
name|f
operator|.
name|getPathRestriction
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|PARENT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test"
argument_list|,
name|f
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Filter
operator|.
name|PathRestriction
operator|.
name|PARENT
argument_list|,
name|f
operator|.
name|getPathRestriction
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|restrictPath
argument_list|(
literal|"/test2"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|EXACT
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|isAlwaysFalse
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

