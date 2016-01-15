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
name|query
operator|.
name|facet
package|;
end_package

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
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|QueryResult
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|Row
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|RowIterator
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
name|assertNotNull
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
name|mock
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

begin_comment
comment|/**  * Tests for {@link FacetResult}  */
end_comment

begin_class
specifier|public
class|class
name|FacetResultTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testResult
parameter_list|()
throws|throws
name|Exception
block|{
name|QueryResult
name|queryResult
init|=
name|mock
argument_list|(
name|QueryResult
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|queryResult
operator|.
name|getColumnNames
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"rep:facet(text)"
block|,
literal|"jcr:path"
block|,
literal|"rep:facet(jcr:title)"
block|}
argument_list|)
expr_stmt|;
name|RowIterator
name|rows
init|=
name|mock
argument_list|(
name|RowIterator
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|rows
operator|.
name|hasNext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Row
name|row
init|=
name|mock
argument_list|(
name|Row
operator|.
name|class
argument_list|)
decl_stmt|;
name|Value
name|value
init|=
name|mock
argument_list|(
name|Value
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|value
operator|.
name|getString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"{}"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|row
operator|.
name|getValue
argument_list|(
literal|"rep:facet(text)"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|Value
name|value2
init|=
name|mock
argument_list|(
name|Value
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|value2
operator|.
name|getString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"{\"a\" : 2, \"b\" : 1}"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|row
operator|.
name|getValue
argument_list|(
literal|"rep:facet(jcr:title)"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|value2
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rows
operator|.
name|nextRow
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|row
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|queryResult
operator|.
name|getRows
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|rows
argument_list|)
expr_stmt|;
name|FacetResult
name|facetResult
init|=
operator|new
name|FacetResult
argument_list|(
name|queryResult
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|facetResult
operator|.
name|getDimensions
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|facetResult
operator|.
name|getDimensions
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|facetResult
operator|.
name|getDimensions
argument_list|()
operator|.
name|contains
argument_list|(
literal|"text"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|facetResult
operator|.
name|getDimensions
argument_list|()
operator|.
name|contains
argument_list|(
literal|"jcr:title"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|facetResult
operator|.
name|getFacets
argument_list|(
literal|"text"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|facetResult
operator|.
name|getFacets
argument_list|(
literal|"text"
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|facetResult
operator|.
name|getFacets
argument_list|(
literal|"jcr:title"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|facetResult
operator|.
name|getFacets
argument_list|(
literal|"jcr:title"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|facetResult
operator|.
name|getFacets
argument_list|(
literal|"jcr:title"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLabel
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|facetResult
operator|.
name|getFacets
argument_list|(
literal|"jcr:title"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b"
argument_list|,
name|facetResult
operator|.
name|getFacets
argument_list|(
literal|"jcr:title"
argument_list|)
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getLabel
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|facetResult
operator|.
name|getFacets
argument_list|(
literal|"jcr:title"
argument_list|)
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

