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
name|QueryEngineSettings
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
name|QueryIndex
operator|.
name|IndexPlan
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
comment|/**  * Test the advanced query index feature.  */
end_comment

begin_class
specifier|public
class|class
name|AdvancedIndexTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|builder
parameter_list|()
block|{
name|IndexPlan
operator|.
name|Builder
name|b
init|=
operator|new
name|IndexPlan
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|IndexPlan
name|plan
init|=
name|b
operator|.
name|setEstimatedEntryCount
argument_list|(
literal|10
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|plan
operator|.
name|getEstimatedEntryCount
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|setEstimatedEntryCount
argument_list|(
literal|20
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|plan
operator|.
name|getEstimatedEntryCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|copy
parameter_list|()
throws|throws
name|Exception
block|{
name|Filter
name|f
init|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|,
literal|"SELECT * FROM [nt:file]"
argument_list|,
operator|new
name|QueryEngineSettings
argument_list|()
argument_list|)
decl_stmt|;
name|IndexPlan
operator|.
name|Builder
name|b
init|=
operator|new
name|IndexPlan
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|IndexPlan
name|plan1
init|=
name|b
operator|.
name|setEstimatedEntryCount
argument_list|(
literal|10
argument_list|)
operator|.
name|setFilter
argument_list|(
name|f
argument_list|)
operator|.
name|setDelayed
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|IndexPlan
name|plan2
init|=
name|plan1
operator|.
name|copy
argument_list|()
decl_stmt|;
name|plan2
operator|.
name|setFilter
argument_list|(
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|,
literal|"SELECT * FROM [oak:Unstructured]"
argument_list|,
operator|new
name|QueryEngineSettings
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|plan1
operator|.
name|getEstimatedEntryCount
argument_list|()
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|plan2
operator|.
name|getEstimatedEntryCount
argument_list|()
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|plan1
operator|.
name|isDelayed
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|plan2
operator|.
name|isDelayed
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|plan1
operator|.
name|getFilter
argument_list|()
operator|.
name|getQueryStatement
argument_list|()
argument_list|,
literal|"SELECT * FROM [nt:file]"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|plan2
operator|.
name|getFilter
argument_list|()
operator|.
name|getQueryStatement
argument_list|()
argument_list|,
literal|"SELECT * FROM [oak:Unstructured]"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

