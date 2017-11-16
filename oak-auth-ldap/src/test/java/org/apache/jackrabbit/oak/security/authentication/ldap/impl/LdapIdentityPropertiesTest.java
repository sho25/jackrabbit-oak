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
operator|.
name|authentication
operator|.
name|ldap
operator|.
name|impl
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
name|Maps
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

begin_class
specifier|public
class|class
name|LdapIdentityPropertiesTest
block|{
specifier|private
name|LdapIdentityProperties
name|properties
init|=
operator|new
name|LdapIdentityProperties
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|properties
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
literal|"A"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConstructorWithInitialSize
parameter_list|()
block|{
name|LdapIdentityProperties
name|props
init|=
operator|new
name|LdapIdentityProperties
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|props
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConstructorWithInitialSizeLoadFactor
parameter_list|()
block|{
name|LdapIdentityProperties
name|props
init|=
operator|new
name|LdapIdentityProperties
argument_list|(
literal|5
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|props
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConstructorWithMap
parameter_list|()
block|{
name|Map
name|m
init|=
name|Maps
operator|.
name|newLinkedHashMap
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|"v"
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"B"
argument_list|,
literal|"v2"
argument_list|)
expr_stmt|;
name|LdapIdentityProperties
name|props
init|=
operator|new
name|LdapIdentityProperties
argument_list|(
name|m
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|props
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|props
operator|.
name|get
argument_list|(
literal|"b"
argument_list|)
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"B"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testContainsKey
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|properties
operator|.
name|containsKey
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|properties
operator|.
name|containsKey
argument_list|(
literal|"A"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|properties
operator|.
name|containsKey
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testContainsKeyNull
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|properties
operator|.
name|containsKey
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPut
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|properties
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|properties
operator|.
name|containsKey
argument_list|(
literal|"A"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|properties
operator|.
name|containsKey
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
name|testPutNull
parameter_list|()
block|{
name|assertNull
argument_list|(
name|properties
operator|.
name|put
argument_list|(
literal|null
argument_list|,
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPutAll
parameter_list|()
block|{
name|Map
name|m
init|=
name|Maps
operator|.
name|newLinkedHashMap
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|"v"
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"B"
argument_list|,
literal|"v2"
argument_list|)
expr_stmt|;
name|properties
operator|.
name|putAll
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|properties
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|properties
operator|.
name|get
argument_list|(
literal|"b"
argument_list|)
argument_list|,
name|properties
operator|.
name|get
argument_list|(
literal|"B"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGet
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|properties
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|properties
operator|.
name|get
argument_list|(
literal|"A"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetNull
parameter_list|()
block|{
name|assertNull
argument_list|(
name|properties
operator|.
name|get
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemove
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"value2"
argument_list|,
name|properties
operator|.
name|remove
argument_list|(
literal|"A"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|properties
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value"
argument_list|,
name|properties
operator|.
name|remove
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|properties
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testClear
parameter_list|()
block|{
name|properties
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|properties
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|properties
operator|.
name|containsKey
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|properties
operator|.
name|containsKey
argument_list|(
literal|"A"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNonStringKey
parameter_list|()
block|{
name|properties
operator|.
name|put
argument_list|(
literal|"2"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|properties
operator|.
name|containsKey
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|properties
operator|.
name|containsKey
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

