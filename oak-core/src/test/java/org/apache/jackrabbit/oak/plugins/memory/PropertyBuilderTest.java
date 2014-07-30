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
name|junit
operator|.
name|Assert
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
name|PropertyBuilderTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testStringProperty
parameter_list|()
block|{
name|PropertyBuilder
name|builder
init|=
name|PropertyBuilder
operator|.
name|scalar
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
name|Assert
operator|.
name|assertEquals
argument_list|(
name|StringPropertyState
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
name|builder
operator|.
name|setArray
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|MultiStringPropertyState
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
argument_list|()
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
name|builder
init|=
name|PropertyBuilder
operator|.
name|scalar
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
name|Assert
operator|.
name|assertEquals
argument_list|(
name|LongPropertyState
operator|.
name|createLongProperty
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
name|builder
operator|.
name|setArray
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|MultiLongPropertyState
operator|.
name|createLongProperty
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
argument_list|()
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
name|builder
init|=
name|PropertyBuilder
operator|.
name|array
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
name|MultiStringPropertyState
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
name|builder
operator|.
name|setScalar
argument_list|()
expr_stmt|;
try|try
block|{
name|builder
operator|.
name|getPropertyState
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expected
parameter_list|)
block|{
comment|// success
block|}
name|builder
operator|.
name|removeValue
argument_list|(
literal|"one"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StringPropertyState
operator|.
name|stringProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"two"
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
name|testDateProperty
parameter_list|()
block|{
name|PropertyBuilder
name|builder
init|=
name|PropertyBuilder
operator|.
name|array
argument_list|(
name|Type
operator|.
name|DATE
argument_list|)
decl_stmt|;
name|String
name|date1
init|=
literal|"1970-01-01T00:00:00.000Z"
decl_stmt|;
name|String
name|date2
init|=
literal|"1971-01-01T00:00:00.000Z"
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
name|date1
argument_list|)
operator|.
name|addValue
argument_list|(
name|date2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|MultiGenericPropertyState
operator|.
name|dateProperty
argument_list|(
literal|"foo"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|date1
argument_list|,
name|date2
argument_list|)
argument_list|)
argument_list|,
name|builder
operator|.
name|getPropertyState
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setScalar
argument_list|()
expr_stmt|;
try|try
block|{
name|builder
operator|.
name|getPropertyState
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expected
parameter_list|)
block|{         }
name|builder
operator|.
name|removeValue
argument_list|(
name|date1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|GenericPropertyState
operator|.
name|dateProperty
argument_list|(
literal|"foo"
argument_list|,
name|date2
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
name|LongPropertyState
operator|.
name|createLongProperty
argument_list|(
literal|"foo"
argument_list|,
literal|42L
argument_list|)
decl_stmt|;
name|PropertyBuilder
name|builder
init|=
name|PropertyBuilder
operator|.
name|scalar
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
name|StringPropertyState
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
name|StringPropertyState
operator|.
name|stringProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"42"
argument_list|)
decl_stmt|;
name|PropertyBuilder
name|builder
init|=
name|PropertyBuilder
operator|.
name|scalar
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
name|LongPropertyState
operator|.
name|createLongProperty
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAssignFromDate
parameter_list|()
block|{
name|String
name|date
init|=
literal|"1970-01-01T00:00:00.000Z"
decl_stmt|;
name|PropertyState
name|source
init|=
name|GenericPropertyState
operator|.
name|dateProperty
argument_list|(
literal|"foo"
argument_list|,
name|date
argument_list|)
decl_stmt|;
name|PropertyBuilder
name|builder
init|=
name|PropertyBuilder
operator|.
name|scalar
argument_list|(
name|Type
operator|.
name|DATE
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
name|StringPropertyState
operator|.
name|stringProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
decl_stmt|;
name|PropertyBuilder
name|builder
init|=
name|PropertyBuilder
operator|.
name|scalar
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
name|MultiLongPropertyState
operator|.
name|createLongProperty
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
name|builder
init|=
name|PropertyBuilder
operator|.
name|scalar
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
name|MultiStringPropertyState
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
name|MultiStringPropertyState
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
name|builder
init|=
name|PropertyBuilder
operator|.
name|scalar
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
name|MultiLongPropertyState
operator|.
name|createLongProperty
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
name|testAssignFromDates
parameter_list|()
block|{
name|String
name|date1
init|=
literal|"1970-01-01T00:00:00.000Z"
decl_stmt|;
name|String
name|date2
init|=
literal|"1971-01-01T00:00:00.000Z"
decl_stmt|;
name|PropertyState
name|source
init|=
name|MultiGenericPropertyState
operator|.
name|dateProperty
argument_list|(
literal|"foo"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|date1
argument_list|,
name|date2
argument_list|)
argument_list|)
decl_stmt|;
name|PropertyBuilder
name|builder
init|=
name|PropertyBuilder
operator|.
name|scalar
argument_list|(
name|Type
operator|.
name|DATE
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
name|MultiStringPropertyState
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
name|builder
init|=
name|PropertyBuilder
operator|.
name|scalar
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
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

