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
name|security
operator|.
name|authorization
operator|.
name|accesscontrol
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
comment|/**  * Tests for {@link RestrictionDefinitionImpl}.  */
end_comment

begin_class
specifier|public
class|class
name|RestrictionDefinitionImplTest
extends|extends
name|AbstractAccessControlTest
block|{
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|RestrictionDefinitionImpl
name|definition
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
literal|"test:defName"
expr_stmt|;
name|definition
operator|=
operator|new
name|RestrictionDefinitionImpl
argument_list|(
name|name
argument_list|,
name|Type
operator|.
name|NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
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
name|definition
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
name|definition
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
name|definition
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
name|RestrictionDefinitionImpl
argument_list|(
literal|null
argument_list|,
name|Type
operator|.
name|BOOLEAN
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
try|try
block|{
operator|new
name|RestrictionDefinitionImpl
argument_list|(
name|name
argument_list|,
name|Type
operator|.
name|UNDEFINED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Creating RestrictionDefinition with undefined required type should fail."
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
name|definition
argument_list|,
operator|new
name|RestrictionDefinitionImpl
argument_list|(
name|name
argument_list|,
name|Type
operator|.
name|NAME
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
name|RestrictionDefinition
argument_list|>
name|defs
init|=
operator|new
name|ArrayList
argument_list|<
name|RestrictionDefinition
argument_list|>
argument_list|()
decl_stmt|;
comment|// - different type
name|defs
operator|.
name|add
argument_list|(
operator|new
name|RestrictionDefinitionImpl
argument_list|(
name|name
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// - different name
name|defs
operator|.
name|add
argument_list|(
operator|new
name|RestrictionDefinitionImpl
argument_list|(
literal|"otherName"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// - different mandatory flag
name|defs
operator|.
name|add
argument_list|(
operator|new
name|RestrictionDefinitionImpl
argument_list|(
name|name
argument_list|,
name|Type
operator|.
name|NAME
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// - different mv flag
name|defs
operator|.
name|add
argument_list|(
operator|new
name|RestrictionDefinitionImpl
argument_list|(
name|name
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// - different impl
name|defs
operator|.
name|add
argument_list|(
operator|new
name|RestrictionDefinition
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
block|}
argument_list|)
expr_stmt|;
for|for
control|(
name|RestrictionDefinition
name|rd
range|:
name|defs
control|)
block|{
name|assertFalse
argument_list|(
name|definition
operator|.
name|equals
argument_list|(
name|rd
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

