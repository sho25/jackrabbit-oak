begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|memory
package|;
end_package

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
name|spi
operator|.
name|state
operator|.
name|PropertyBuilder
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

begin_class
specifier|public
class|class
name|MemoryPropertyBuilderTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testStringProperty
parameter_list|()
block|{
name|PropertyBuilder
argument_list|<
name|String
argument_list|>
name|builder
init|=
name|MemoryPropertyBuilder
operator|.
name|create
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setName
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|setValue
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PropertyStates
operator|.
name|stringProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
name|builder
operator|.
name|getPropertyState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PropertyStates
operator|.
name|stringProperty
argument_list|(
literal|"foo"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|getPropertyState
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLongProperty
parameter_list|()
block|{
name|PropertyBuilder
argument_list|<
name|Long
argument_list|>
name|builder
init|=
name|MemoryPropertyBuilder
operator|.
name|create
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setName
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|setValue
argument_list|(
literal|42L
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PropertyStates
operator|.
name|longProperty
argument_list|(
literal|"foo"
argument_list|,
literal|42L
argument_list|)
argument_list|,
name|builder
operator|.
name|getPropertyState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PropertyStates
operator|.
name|longProperty
argument_list|(
literal|"foo"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|42L
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|getPropertyState
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStringsProperty
parameter_list|()
block|{
name|PropertyBuilder
argument_list|<
name|String
argument_list|>
name|builder
init|=
name|MemoryPropertyBuilder
operator|.
name|create
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setName
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|addValue
argument_list|(
literal|"one"
argument_list|)
operator|.
name|addValue
argument_list|(
literal|"two"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PropertyStates
operator|.
name|stringProperty
argument_list|(
literal|"foo"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"one"
argument_list|,
literal|"two"
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|getPropertyState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAssignFromLong
parameter_list|()
block|{
name|PropertyState
name|source
init|=
name|PropertyStates
operator|.
name|longProperty
argument_list|(
literal|"foo"
argument_list|,
literal|42
argument_list|)
decl_stmt|;
name|PropertyBuilder
argument_list|<
name|String
argument_list|>
name|builder
init|=
name|MemoryPropertyBuilder
operator|.
name|create
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|builder
operator|.
name|assignFrom
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PropertyStates
operator|.
name|stringProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"42"
argument_list|)
argument_list|,
name|builder
operator|.
name|getPropertyState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAssignFromString
parameter_list|()
block|{
name|PropertyState
name|source
init|=
name|PropertyStates
operator|.
name|stringProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"42"
argument_list|)
decl_stmt|;
name|PropertyBuilder
argument_list|<
name|Long
argument_list|>
name|builder
init|=
name|MemoryPropertyBuilder
operator|.
name|create
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
decl_stmt|;
name|builder
operator|.
name|assignFrom
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PropertyStates
operator|.
name|longProperty
argument_list|(
literal|"foo"
argument_list|,
literal|42
argument_list|)
argument_list|,
name|builder
operator|.
name|getPropertyState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|NumberFormatException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testAssignFromStringNumberFormatException
parameter_list|()
block|{
name|PropertyState
name|source
init|=
name|PropertyStates
operator|.
name|stringProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
decl_stmt|;
name|PropertyBuilder
argument_list|<
name|Long
argument_list|>
name|builder
init|=
name|MemoryPropertyBuilder
operator|.
name|create
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
decl_stmt|;
name|builder
operator|.
name|assignFrom
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAssignFromLongs
parameter_list|()
block|{
name|PropertyState
name|source
init|=
name|PropertyStates
operator|.
name|longProperty
argument_list|(
literal|"foo"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|1L
argument_list|,
literal|2L
argument_list|,
literal|3L
argument_list|)
argument_list|)
decl_stmt|;
name|PropertyBuilder
argument_list|<
name|String
argument_list|>
name|builder
init|=
name|MemoryPropertyBuilder
operator|.
name|create
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|builder
operator|.
name|assignFrom
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PropertyStates
operator|.
name|stringProperty
argument_list|(
literal|"foo"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"1"
argument_list|,
literal|"2"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|getPropertyState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAssignFromStrings
parameter_list|()
block|{
name|PropertyState
name|source
init|=
name|PropertyStates
operator|.
name|stringProperty
argument_list|(
literal|"foo"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"1"
argument_list|,
literal|"2"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
decl_stmt|;
name|PropertyBuilder
argument_list|<
name|Long
argument_list|>
name|builder
init|=
name|MemoryPropertyBuilder
operator|.
name|create
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
decl_stmt|;
name|builder
operator|.
name|assignFrom
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PropertyStates
operator|.
name|longProperty
argument_list|(
literal|"foo"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|1L
argument_list|,
literal|2L
argument_list|,
literal|3L
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|getPropertyState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAssignInvariant
parameter_list|()
block|{
name|PropertyState
name|source
init|=
name|PropertyStates
operator|.
name|stringProperty
argument_list|(
literal|"source"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"1"
argument_list|,
literal|"2"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
decl_stmt|;
name|PropertyBuilder
argument_list|<
name|String
argument_list|>
name|builder
init|=
name|MemoryPropertyBuilder
operator|.
name|create
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|builder
operator|.
name|assignFrom
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|source
argument_list|,
name|builder
operator|.
name|getPropertyState
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

