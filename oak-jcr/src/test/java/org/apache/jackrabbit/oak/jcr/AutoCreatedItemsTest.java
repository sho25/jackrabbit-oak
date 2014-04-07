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
name|jcr
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
name|assertArrayEquals
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
name|assertTrue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Value
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
comment|/**  * {@code AutoCreatedItemsTest} checks if auto-created nodes and properties  * are added correctly as defined in the node type definition.  */
end_comment

begin_class
specifier|public
class|class
name|AutoCreatedItemsTest
extends|extends
name|AbstractRepositoryTest
block|{
specifier|public
name|AutoCreatedItemsTest
parameter_list|(
name|NodeStoreFixture
name|fixture
parameter_list|)
block|{
name|super
argument_list|(
name|fixture
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|autoCreatedItems
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|s
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
operator|new
name|TestContentLoader
argument_list|()
operator|.
name|loadTestContent
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|Node
name|test
init|=
name|s
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"test"
argument_list|,
literal|"test:autoCreate"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|test
operator|.
name|hasProperty
argument_list|(
literal|"test:property"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default value"
argument_list|,
name|test
operator|.
name|getProperty
argument_list|(
literal|"test:property"
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
operator|.
name|hasProperty
argument_list|(
literal|"test:propertyMulti"
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|Value
index|[]
block|{
name|s
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
literal|"value1"
argument_list|)
block|,
name|s
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
literal|"value2"
argument_list|)
block|}
argument_list|,
name|test
operator|.
name|getProperty
argument_list|(
literal|"test:propertyMulti"
argument_list|)
operator|.
name|getValues
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
operator|.
name|hasNode
argument_list|(
literal|"test:folder"
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|folder
init|=
name|test
operator|.
name|getNode
argument_list|(
literal|"test:folder"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"nt:folder"
argument_list|,
name|folder
operator|.
name|getPrimaryNodeType
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|folder
operator|.
name|hasProperty
argument_list|(
literal|"jcr:created"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|folder
operator|.
name|hasProperty
argument_list|(
literal|"jcr:createdBy"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

