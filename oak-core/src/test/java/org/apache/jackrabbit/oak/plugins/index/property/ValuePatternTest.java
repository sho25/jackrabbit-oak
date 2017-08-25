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
name|property
operator|.
name|ValuePatternUtil
operator|.
name|getLongestPrefix
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
name|Collections
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
name|PropertyState
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
name|plugins
operator|.
name|memory
operator|.
name|EmptyNodeState
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
name|plugins
operator|.
name|memory
operator|.
name|MemoryNodeBuilder
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
name|plugins
operator|.
name|memory
operator|.
name|PropertyStates
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
name|plugins
operator|.
name|memory
operator|.
name|PropertyValues
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
name|query
operator|.
name|index
operator|.
name|FilterImpl
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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_class
specifier|public
class|class
name|ValuePatternTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|getStringsBuilder
parameter_list|()
block|{
name|NodeBuilder
name|b
init|=
operator|new
name|MemoryNodeBuilder
argument_list|(
name|EmptyNodeState
operator|.
name|EMPTY_NODE
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|ValuePattern
operator|.
name|getStrings
argument_list|(
name|b
argument_list|,
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
literal|"x"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[]"
argument_list|,
name|ValuePattern
operator|.
name|getStrings
argument_list|(
name|b
argument_list|,
literal|"x"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
literal|"x"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[test]"
argument_list|,
name|ValuePattern
operator|.
name|getStrings
argument_list|(
name|b
argument_list|,
literal|"x"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|PropertyState
name|ps
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"x"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"hello"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
decl_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
name|ps
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[hello]"
argument_list|,
name|ValuePattern
operator|.
name|getStrings
argument_list|(
name|b
argument_list|,
literal|"x"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ps
operator|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"x"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|()
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
name|ps
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[]"
argument_list|,
name|ValuePattern
operator|.
name|getStrings
argument_list|(
name|b
argument_list|,
literal|"x"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ps
operator|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"x"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
name|ps
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[a, b]"
argument_list|,
name|ValuePattern
operator|.
name|getStrings
argument_list|(
name|b
argument_list|,
literal|"x"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getStringsState
parameter_list|()
block|{
name|NodeBuilder
name|b
init|=
operator|new
name|MemoryNodeBuilder
argument_list|(
name|EmptyNodeState
operator|.
name|EMPTY_NODE
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|ValuePattern
operator|.
name|getStrings
argument_list|(
name|b
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
literal|"x"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[]"
argument_list|,
name|ValuePattern
operator|.
name|getStrings
argument_list|(
name|b
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"x"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
literal|"x"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[test]"
argument_list|,
name|ValuePattern
operator|.
name|getStrings
argument_list|(
name|b
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"x"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|PropertyState
name|ps
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"x"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"hello"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
decl_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
name|ps
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[hello]"
argument_list|,
name|ValuePattern
operator|.
name|getStrings
argument_list|(
name|b
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"x"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ps
operator|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"x"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|()
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
name|ps
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[]"
argument_list|,
name|ValuePattern
operator|.
name|getStrings
argument_list|(
name|b
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"x"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ps
operator|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"x"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
name|ps
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[a, b]"
argument_list|,
name|ValuePattern
operator|.
name|getStrings
argument_list|(
name|b
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"x"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|empty
parameter_list|()
block|{
name|ValuePattern
name|vp
init|=
operator|new
name|ValuePattern
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matches
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matches
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|(
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
literal|"x"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|(
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
literal|"x"
argument_list|,
literal|"y"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matchesPrefix
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matchesPrefix
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matchesPrefix
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|regex
parameter_list|()
block|{
name|ValuePattern
name|vp
init|=
operator|new
name|ValuePattern
argument_list|(
literal|"x.*"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matches
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matches
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|vp
operator|.
name|matches
argument_list|(
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|(
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
literal|"x"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
literal|"y"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|(
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
literal|"x1"
argument_list|,
literal|"x2"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|(
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
literal|"x1"
argument_list|,
literal|"y2"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|vp
operator|.
name|matchesPrefix
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|vp
operator|.
name|matchesPrefix
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
comment|// unkown, as we don't do regular expression analysis
name|assertFalse
argument_list|(
name|vp
operator|.
name|matchesPrefix
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|included
parameter_list|()
block|{
name|ValuePattern
name|vp
init|=
operator|new
name|ValuePattern
argument_list|(
literal|null
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"abc"
argument_list|,
literal|"bcd"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matches
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matches
argument_list|(
literal|"abc1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matches
argument_list|(
literal|"bcd"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|vp
operator|.
name|matches
argument_list|(
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|(
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
literal|"abc0"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
literal|"c"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|(
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
literal|"abc1"
argument_list|,
literal|"bcd1"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|(
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
literal|"abc1"
argument_list|,
literal|"c2"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|vp
operator|.
name|matchesPrefix
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|vp
operator|.
name|matchesPrefix
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|vp
operator|.
name|matchesPrefix
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matchesPrefix
argument_list|(
literal|"abcdef"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|vp
operator|.
name|matchesPrefix
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|excluded
parameter_list|()
block|{
name|ValuePattern
name|vp
init|=
operator|new
name|ValuePattern
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"abc"
argument_list|,
literal|"bcd"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matches
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|vp
operator|.
name|matches
argument_list|(
literal|"abc1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|vp
operator|.
name|matches
argument_list|(
literal|"bcd"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matches
argument_list|(
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|(
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
literal|"abc0"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
literal|"c"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|(
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
literal|"abc1"
argument_list|,
literal|"bcd1"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|(
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
literal|"abc1"
argument_list|,
literal|"c2"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matchesAll
argument_list|(
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
literal|"c2"
argument_list|,
literal|"d2"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|vp
operator|.
name|matchesPrefix
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|vp
operator|.
name|matchesPrefix
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|vp
operator|.
name|matchesPrefix
argument_list|(
literal|"abcdef"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|vp
operator|.
name|matchesPrefix
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|longestPrefix
parameter_list|()
block|{
name|FilterImpl
name|filter
decl_stmt|;
name|filter
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hello"
argument_list|,
name|getLongestPrefix
argument_list|(
name|filter
argument_list|,
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|GREATER_OR_EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"hello welt"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|LESS_OR_EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"hello world"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hello w"
argument_list|,
name|getLongestPrefix
argument_list|(
name|filter
argument_list|,
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|GREATER_THAN
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"hello welt"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|LESS_THAN
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"hello world"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hello w"
argument_list|,
name|getLongestPrefix
argument_list|(
name|filter
argument_list|,
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|GREATER_THAN
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"hello welt"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|GREATER_THAN
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"hello welt!"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|LESS_THAN
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"hello world"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"hell"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hell"
argument_list|,
name|getLongestPrefix
argument_list|(
name|filter
argument_list|,
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|GREATER_THAN
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"abc"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|LESS_THAN
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"bde"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|getLongestPrefix
argument_list|(
name|filter
argument_list|,
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|GREATER_THAN
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"abc"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|GREATER_THAN
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"bcd"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|LESS_THAN
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"dce"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|getLongestPrefix
argument_list|(
name|filter
argument_list|,
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|LIKE
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"hello%"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|getLongestPrefix
argument_list|(
name|filter
argument_list|,
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|GREATER_OR_EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|getLongestPrefix
argument_list|(
name|filter
argument_list|,
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|LESS_THAN
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|getLongestPrefix
argument_list|(
name|filter
argument_list|,
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filter
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
name|assertNull
argument_list|(
name|getLongestPrefix
argument_list|(
name|filter
argument_list|,
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|GREATER_THAN
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a0"
argument_list|,
literal|"a1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|LESS_THAN
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"a2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|getLongestPrefix
argument_list|(
name|filter
argument_list|,
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|GREATER_THAN
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"a0"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"x"
argument_list|,
name|Operator
operator|.
name|LESS_THAN
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a3"
argument_list|,
literal|"a4"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|getLongestPrefix
argument_list|(
name|filter
argument_list|,
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

