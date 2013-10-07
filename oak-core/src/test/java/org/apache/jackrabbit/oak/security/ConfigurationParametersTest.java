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
name|security
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
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
name|HashMap
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
name|ConfigurationParameters
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
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertNull
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|fail
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
name|assertArrayEquals
import|;
end_import

begin_class
specifier|public
class|class
name|ConfigurationParametersTest
block|{
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
block|{}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{}
annotation|@
name|Test
specifier|public
name|void
name|testContains
parameter_list|()
block|{
name|ConfigurationParameters
name|params
init|=
name|ConfigurationParameters
operator|.
name|EMPTY
decl_stmt|;
name|assertFalse
argument_list|(
name|params
operator|.
name|contains
argument_list|(
literal|"some"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|params
operator|.
name|contains
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"key1"
argument_list|,
literal|"v"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"key2"
argument_list|,
literal|"v"
argument_list|)
expr_stmt|;
name|params
operator|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|params
operator|.
name|contains
argument_list|(
literal|"key1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|params
operator|.
name|contains
argument_list|(
literal|"key2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|params
operator|.
name|contains
argument_list|(
literal|"another"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|params
operator|.
name|contains
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetConfigValue
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"o1"
argument_list|,
literal|"v"
argument_list|)
expr_stmt|;
name|ConfigurationParameters
name|options
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|map
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"v"
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"o1"
argument_list|,
literal|"v2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"v2"
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"missing"
argument_list|,
literal|"v2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetNullableConfigValue
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"o1"
argument_list|,
literal|"v"
argument_list|)
expr_stmt|;
name|ConfigurationParameters
name|options
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|map
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"v"
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"o1"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"v"
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"o1"
argument_list|,
literal|null
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"v"
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"o1"
argument_list|,
literal|"v2"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"v"
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"o1"
argument_list|,
literal|"v2"
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"v2"
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"missing"
argument_list|,
literal|"v2"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"v2"
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"missing"
argument_list|,
literal|"v2"
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"missing"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"missing"
argument_list|,
literal|null
argument_list|,
name|TestObject
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefaultValue
parameter_list|()
block|{
name|TestObject
name|obj
init|=
operator|new
name|TestObject
argument_list|(
literal|"t"
argument_list|)
decl_stmt|;
name|Integer
name|int1000
init|=
literal|1000
decl_stmt|;
name|ConfigurationParameters
name|options
init|=
name|ConfigurationParameters
operator|.
name|EMPTY
decl_stmt|;
name|assertEquals
argument_list|(
name|obj
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"missing"
argument_list|,
name|obj
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|int1000
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"missing"
argument_list|,
name|int1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"missing"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"missing"
argument_list|,
literal|null
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|obj
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"missing"
argument_list|,
name|obj
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|obj
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"missing"
argument_list|,
name|obj
argument_list|,
name|TestObject
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|int1000
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"missing"
argument_list|,
name|int1000
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testArrayDefaultValue
parameter_list|()
block|{
name|TestObject
index|[]
name|testArray
init|=
operator|new
name|TestObject
index|[]
block|{
operator|new
name|TestObject
argument_list|(
literal|"t"
argument_list|)
block|}
decl_stmt|;
name|TestObject
index|[]
name|result
init|=
name|ConfigurationParameters
operator|.
name|EMPTY
operator|.
name|getConfigValue
argument_list|(
literal|"test"
argument_list|,
operator|new
name|TestObject
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|testArray
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
operator|.
name|getConfigValue
argument_list|(
literal|"test"
argument_list|,
name|testArray
argument_list|)
argument_list|)
expr_stmt|;
name|ConfigurationParameters
name|options
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"test"
argument_list|,
name|testArray
argument_list|)
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|testArray
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"test"
argument_list|,
operator|new
name|TestObject
index|[]
block|{
operator|new
name|TestObject
argument_list|(
literal|"s"
argument_list|)
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testArrayDefaultValue2
parameter_list|()
block|{
name|TestObject
index|[]
name|testArray
init|=
operator|new
name|TestObject
index|[]
block|{
operator|new
name|TestObject
argument_list|(
literal|"t"
argument_list|)
block|}
decl_stmt|;
name|TestObject
index|[]
name|result
init|=
name|ConfigurationParameters
operator|.
name|EMPTY
operator|.
name|getConfigValue
argument_list|(
literal|"test"
argument_list|,
operator|new
name|TestObject
index|[
literal|0
index|]
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|testArray
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
operator|.
name|getConfigValue
argument_list|(
literal|"test"
argument_list|,
name|testArray
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|testArray
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
operator|.
name|getConfigValue
argument_list|(
literal|"test"
argument_list|,
name|testArray
argument_list|,
name|TestObject
index|[]
operator|.
expr|class
argument_list|)
argument_list|)
expr_stmt|;
name|ConfigurationParameters
name|options
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"test"
argument_list|,
name|testArray
argument_list|)
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|testArray
argument_list|,
operator|(
name|TestObject
index|[]
operator|)
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"test"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|testArray
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"test"
argument_list|,
literal|null
argument_list|,
name|TestObject
index|[]
operator|.
expr|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|testArray
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"test"
argument_list|,
operator|new
name|TestObject
index|[]
block|{
operator|new
name|TestObject
argument_list|(
literal|"s"
argument_list|)
block|}
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|testArray
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"test"
argument_list|,
operator|new
name|TestObject
index|[]
block|{
operator|new
name|TestObject
argument_list|(
literal|"s"
argument_list|)
block|}
argument_list|,
name|TestObject
index|[]
operator|.
expr|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConversion
parameter_list|()
block|{
name|TestObject
name|testObject
init|=
operator|new
name|TestObject
argument_list|(
literal|"t"
argument_list|)
decl_stmt|;
name|Integer
name|int1000
init|=
literal|1000
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"TEST"
argument_list|,
name|testObject
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"String"
argument_list|,
literal|"1000"
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"Int2"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"Int3"
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|ConfigurationParameters
name|options
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|m
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|testObject
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"TEST"
argument_list|,
name|testObject
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"t"
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"TEST"
argument_list|,
literal|"defaultString"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|1000
operator|==
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"String"
argument_list|,
literal|10
argument_list|,
name|int
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|1000
operator|==
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"String"
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|int1000
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"String"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Long
argument_list|(
literal|1000
argument_list|)
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"String"
argument_list|,
operator|new
name|Long
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1000"
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"String"
argument_list|,
literal|"10"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|int1000
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"Int2"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1000"
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"Int2"
argument_list|,
literal|"1000"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|int1000
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"Int3"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1000"
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"Int3"
argument_list|,
literal|"1000"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConversion2
parameter_list|()
block|{
name|TestObject
name|testObject
init|=
operator|new
name|TestObject
argument_list|(
literal|"t"
argument_list|)
decl_stmt|;
name|Integer
name|int1000
init|=
operator|new
name|Integer
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"TEST"
argument_list|,
name|testObject
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"String"
argument_list|,
literal|"1000"
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"Int2"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"Int3"
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|ConfigurationParameters
name|options
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|m
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"TEST"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"TEST"
argument_list|,
literal|null
argument_list|,
name|TestObject
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testObject
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"TEST"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testObject
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"TEST"
argument_list|,
literal|null
argument_list|,
name|TestObject
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testObject
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"TEST"
argument_list|,
name|testObject
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testObject
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"TEST"
argument_list|,
name|testObject
argument_list|,
name|TestObject
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"t"
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"TEST"
argument_list|,
literal|"defaultString"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"t"
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"TEST"
argument_list|,
literal|"defaultString"
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1000"
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"String"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1000"
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"String"
argument_list|,
literal|null
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|int1000
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"String"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|10
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|int1000
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"String"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|10
argument_list|)
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Long
argument_list|(
literal|1000
argument_list|)
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"String"
argument_list|,
operator|new
name|Long
argument_list|(
literal|10
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Long
argument_list|(
literal|1000
argument_list|)
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"String"
argument_list|,
operator|new
name|Long
argument_list|(
literal|10
argument_list|)
argument_list|,
name|Long
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1000"
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"String"
argument_list|,
literal|"10"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1000"
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"String"
argument_list|,
literal|"10"
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|int1000
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"Int2"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|int1000
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"Int2"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|10
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1000"
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"Int2"
argument_list|,
literal|"1000"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1000
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"Int3"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|int1000
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"Int3"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|10
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1000"
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"Int3"
argument_list|,
literal|"1000"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImpossibleConversion
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"string"
argument_list|,
literal|"v"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"obj"
argument_list|,
operator|new
name|TestObject
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"int"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|ConfigurationParameters
name|options
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|map
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|>
name|impossible
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|impossible
operator|.
name|put
argument_list|(
literal|"string"
argument_list|,
name|TestObject
operator|.
name|class
argument_list|)
expr_stmt|;
name|impossible
operator|.
name|put
argument_list|(
literal|"string"
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
expr_stmt|;
name|impossible
operator|.
name|put
argument_list|(
literal|"string"
argument_list|,
name|Calendar
operator|.
name|class
argument_list|)
expr_stmt|;
name|impossible
operator|.
name|put
argument_list|(
literal|"obj"
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
expr_stmt|;
name|impossible
operator|.
name|put
argument_list|(
literal|"int"
argument_list|,
name|TestObject
operator|.
name|class
argument_list|)
expr_stmt|;
name|impossible
operator|.
name|put
argument_list|(
literal|"int"
argument_list|,
name|Calendar
operator|.
name|class
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|key
range|:
name|impossible
operator|.
name|keySet
argument_list|()
control|)
block|{
try|try
block|{
name|options
operator|.
name|getConfigValue
argument_list|(
name|key
argument_list|,
literal|null
argument_list|,
name|impossible
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Impossible conversion for "
operator|+
name|key
operator|+
literal|" to "
operator|+
name|impossible
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNullValue
parameter_list|()
block|{
name|ConfigurationParameters
name|options
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"test"
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"test"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value"
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"test"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
name|TestObject
name|to
init|=
operator|new
name|TestObject
argument_list|(
literal|"t"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|to
argument_list|,
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"test"
argument_list|,
name|to
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"test"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNullValue2
parameter_list|()
block|{
name|ConfigurationParameters
name|options
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"test"
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"test"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"test"
argument_list|,
literal|null
argument_list|,
name|TestObject
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"test"
argument_list|,
literal|"value"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"test"
argument_list|,
literal|"value"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"test"
argument_list|,
operator|new
name|TestObject
argument_list|(
literal|"t"
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|options
operator|.
name|getConfigValue
argument_list|(
literal|"test"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
class|class
name|TestObject
block|{
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
name|TestObject
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|name
operator|.
name|hashCode
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|object
parameter_list|)
block|{
if|if
condition|(
name|object
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|object
operator|instanceof
name|TestObject
condition|)
block|{
return|return
name|name
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|TestObject
operator|)
name|object
operator|)
operator|.
name|name
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

