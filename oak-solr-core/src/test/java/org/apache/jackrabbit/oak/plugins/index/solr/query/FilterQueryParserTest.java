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
name|index
operator|.
name|solr
operator|.
name|query
package|;
end_package

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
name|index
operator|.
name|solr
operator|.
name|configuration
operator|.
name|DefaultSolrConfiguration
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
name|index
operator|.
name|solr
operator|.
name|configuration
operator|.
name|OakSolrConfiguration
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
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrQuery
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
comment|/**  * Testcase for {@link org.apache.jackrabbit.oak.plugins.index.solr.query.FilterQueryParser}  */
end_comment

begin_class
specifier|public
class|class
name|FilterQueryParserTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testMatchAllConversionWithNoConstraints
parameter_list|()
throws|throws
name|Exception
block|{
name|Filter
name|filter
init|=
name|mock
argument_list|(
name|Filter
operator|.
name|class
argument_list|)
decl_stmt|;
name|OakSolrConfiguration
name|configuration
init|=
name|mock
argument_list|(
name|OakSolrConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
name|SolrQuery
name|solrQuery
init|=
name|FilterQueryParser
operator|.
name|getQuery
argument_list|(
name|filter
argument_list|,
literal|null
argument_list|,
name|configuration
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|solrQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"*:*"
argument_list|,
name|solrQuery
operator|.
name|getQuery
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAllChildrenQueryParsing
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|query
init|=
literal|"select [jcr:path], [jcr:score], * from [nt:hierarchy] as a where isdescendantnode(a, '/')"
decl_stmt|;
name|Filter
name|filter
init|=
name|mock
argument_list|(
name|Filter
operator|.
name|class
argument_list|)
decl_stmt|;
name|OakSolrConfiguration
name|configuration
init|=
operator|new
name|DefaultSolrConfiguration
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|useForPathRestrictions
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
name|when
argument_list|(
name|filter
operator|.
name|getQueryStatement
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|Filter
operator|.
name|PathRestriction
name|pathRestriction
init|=
name|Filter
operator|.
name|PathRestriction
operator|.
name|ALL_CHILDREN
decl_stmt|;
name|when
argument_list|(
name|filter
operator|.
name|getPathRestriction
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|pathRestriction
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|filter
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|SolrQuery
name|solrQuery
init|=
name|FilterQueryParser
operator|.
name|getQuery
argument_list|(
name|filter
argument_list|,
literal|null
argument_list|,
name|configuration
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|solrQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|configuration
operator|.
name|getFieldForPathRestriction
argument_list|(
name|pathRestriction
argument_list|)
operator|+
literal|":\\/"
argument_list|,
name|solrQuery
operator|.
name|get
argument_list|(
literal|"q"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

