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
name|authorization
operator|.
name|restriction
package|;
end_package

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
name|List
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
name|TestNameMapper
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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|AbstractAccessControlTest
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

begin_comment
comment|/**  * Tests for {@link RestrictionImpl}  */
end_comment

begin_class
specifier|public
class|class
name|RestrictionImplTest
extends|extends
name|AbstractAccessControlTest
block|{
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|String
name|value
init|=
literal|"value"
decl_stmt|;
specifier|private
name|RestrictionImpl
name|restriction
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
name|name
operator|=
name|TestNameMapper
operator|.
name|TEST_PREFIX
operator|+
literal|":defName"
expr_stmt|;
name|PropertyState
name|property
init|=
name|createProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|restriction
operator|=
operator|new
name|RestrictionImpl
argument_list|(
name|property
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|PropertyState
name|createProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetName
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|name
argument_list|,
name|restriction
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
name|testGetRequiredType
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Type
operator|.
name|NAME
argument_list|,
name|restriction
operator|.
name|getRequiredType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsMandatory
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|restriction
operator|.
name|isMandatory
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInvalid
parameter_list|()
block|{
try|try
block|{
operator|new
name|RestrictionImpl
argument_list|(
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Creating RestrictionDefinition with null name should fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
comment|// same definition
name|assertEquals
argument_list|(
name|restriction
argument_list|,
operator|new
name|RestrictionImpl
argument_list|(
name|createProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotEqual
parameter_list|()
block|{
name|List
argument_list|<
name|Restriction
argument_list|>
name|rs
init|=
operator|new
name|ArrayList
argument_list|<
name|Restriction
argument_list|>
argument_list|()
decl_stmt|;
comment|// - different type
name|rs
operator|.
name|add
argument_list|(
operator|new
name|RestrictionImpl
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// - different multi-value status
name|rs
operator|.
name|add
argument_list|(
operator|new
name|RestrictionImpl
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|name
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|value
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// - different name
name|rs
operator|.
name|add
argument_list|(
operator|new
name|RestrictionImpl
argument_list|(
name|createProperty
argument_list|(
literal|"otherName"
argument_list|,
name|value
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// - different value
name|rs
operator|.
name|add
argument_list|(
operator|new
name|RestrictionImpl
argument_list|(
name|createProperty
argument_list|(
literal|"name"
argument_list|,
literal|"otherValue"
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// - different mandatory flag
name|rs
operator|.
name|add
argument_list|(
operator|new
name|RestrictionImpl
argument_list|(
name|createProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// - different impl
name|rs
operator|.
name|add
argument_list|(
operator|new
name|Restriction
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|Type
argument_list|<
name|?
argument_list|>
name|getRequiredType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|NAME
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMandatory
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyState
name|getProperty
parameter_list|()
block|{
return|return
name|createProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
for|for
control|(
name|Restriction
name|r
range|:
name|rs
control|)
block|{
name|assertFalse
argument_list|(
name|restriction
operator|.
name|equals
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

