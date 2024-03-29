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
name|spi
operator|.
name|security
operator|.
name|privilege
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|org
operator|.
name|mockito
operator|.
name|Mockito
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
name|assertNotEquals
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
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
specifier|public
class|class
name|ImmutablePrivilegeDefinitionTest
block|{
specifier|private
name|ImmutablePrivilegeDefinition
name|def
init|=
operator|new
name|ImmutablePrivilegeDefinition
argument_list|(
literal|"name"
argument_list|,
literal|true
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"aggrName"
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testGetName
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"name"
argument_list|,
name|def
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsAbstract
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|def
operator|.
name|isAbstract
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetDeclaredAggregatedNames
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"aggrName"
argument_list|)
argument_list|,
name|def
operator|.
name|getDeclaredAggregateNames
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetDeclaredAggregatedNames2
parameter_list|()
block|{
name|assertTrue
argument_list|(
operator|new
name|ImmutablePrivilegeDefinition
argument_list|(
literal|"name"
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
operator|.
name|getDeclaredAggregateNames
argument_list|()
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
name|testHashCode
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|def
operator|.
name|hashCode
argument_list|()
argument_list|,
name|def
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|def
operator|.
name|hashCode
argument_list|()
argument_list|,
operator|new
name|ImmutablePrivilegeDefinition
argument_list|(
name|def
operator|.
name|getName
argument_list|()
argument_list|,
name|def
operator|.
name|isAbstract
argument_list|()
argument_list|,
name|def
operator|.
name|getDeclaredAggregateNames
argument_list|()
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|def
argument_list|,
name|def
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|def
argument_list|,
operator|new
name|ImmutablePrivilegeDefinition
argument_list|(
name|def
operator|.
name|getName
argument_list|()
argument_list|,
name|def
operator|.
name|isAbstract
argument_list|()
argument_list|,
name|def
operator|.
name|getDeclaredAggregateNames
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotEquals
parameter_list|()
block|{
name|PrivilegeDefinition
name|otherDef
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|PrivilegeDefinition
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|otherDef
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|def
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|otherDef
operator|.
name|isAbstract
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|def
operator|.
name|isAbstract
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|otherDef
operator|.
name|getDeclaredAggregateNames
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|def
operator|.
name|getDeclaredAggregateNames
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|def
argument_list|,
name|otherDef
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|def
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|def
argument_list|,
operator|new
name|ImmutablePrivilegeDefinition
argument_list|(
literal|"othername"
argument_list|,
literal|true
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"aggrName"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|def
argument_list|,
operator|new
name|ImmutablePrivilegeDefinition
argument_list|(
literal|"name"
argument_list|,
literal|false
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"aggrName"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|def
argument_list|,
operator|new
name|ImmutablePrivilegeDefinition
argument_list|(
literal|"name"
argument_list|,
literal|true
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"anotherName"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|def
argument_list|,
operator|new
name|ImmutablePrivilegeDefinition
argument_list|(
literal|"name"
argument_list|,
literal|true
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|def
argument_list|,
operator|new
name|ImmutablePrivilegeDefinition
argument_list|(
literal|"otherName"
argument_list|,
literal|false
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"aggrName"
argument_list|,
literal|"aggrName2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testToString
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|def
operator|.
name|toString
argument_list|()
argument_list|,
name|def
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|def
operator|.
name|toString
argument_list|()
argument_list|,
operator|new
name|ImmutablePrivilegeDefinition
argument_list|(
name|def
operator|.
name|getName
argument_list|()
argument_list|,
name|def
operator|.
name|isAbstract
argument_list|()
argument_list|,
name|def
operator|.
name|getDeclaredAggregateNames
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|def
operator|.
name|toString
argument_list|()
argument_list|,
operator|new
name|ImmutablePrivilegeDefinition
argument_list|(
name|def
operator|.
name|getName
argument_list|()
argument_list|,
name|def
operator|.
name|isAbstract
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

