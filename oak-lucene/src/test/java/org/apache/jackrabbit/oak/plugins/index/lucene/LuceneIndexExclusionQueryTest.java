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
name|plugins
operator|.
name|index
operator|.
name|lucene
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
operator|.
name|of
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|TYPENAME_BINARY
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|TYPENAME_STRING
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_LASTMODIFIED
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
import|;
end_import

begin_import
import|import static
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
operator|.
name|DATE
import|;
end_import

begin_import
import|import static
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
operator|.
name|STRINGS
import|;
end_import

begin_import
import|import static
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
name|lucene
operator|.
name|LuceneIndexConstants
operator|.
name|EXCLUDE_PROPERTY_NAMES
import|;
end_import

begin_import
import|import static
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
name|lucene
operator|.
name|LuceneIndexConstants
operator|.
name|INCLUDE_PROPERTY_TYPES
import|;
end_import

begin_import
import|import static
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
name|lucene
operator|.
name|LuceneIndexConstants
operator|.
name|TYPE_LUCENE
import|;
end_import

begin_import
import|import static
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
name|lucene
operator|.
name|TestUtil
operator|.
name|useV2
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
name|Oak
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
name|ContentRepository
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
name|Tree
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
name|nodetype
operator|.
name|write
operator|.
name|InitialContent
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
name|AbstractQueryTest
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
name|commit
operator|.
name|Observer
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
name|QueryIndexProvider
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
name|OpenSecurityProvider
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
comment|/**  * Tests the {@link LuceneIndexProvider} exclusion settings  */
end_comment

begin_class
specifier|public
class|class
name|LuceneIndexExclusionQueryTest
extends|extends
name|AbstractQueryTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|NOT_IN
init|=
literal|"notincluded"
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|createTestIndexNode
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|lucene
init|=
name|createTestIndexNode
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|TYPE_LUCENE
argument_list|)
decl_stmt|;
name|lucene
operator|.
name|setProperty
argument_list|(
name|INCLUDE_PROPERTY_TYPES
argument_list|,
name|of
argument_list|(
name|TYPENAME_BINARY
argument_list|,
name|TYPENAME_STRING
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
expr_stmt|;
name|lucene
operator|.
name|setProperty
argument_list|(
name|EXCLUDE_PROPERTY_NAMES
argument_list|,
name|of
argument_list|(
name|NOT_IN
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
expr_stmt|;
name|lucene
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|COMPAT_MODE
argument_list|,
name|of
argument_list|(
name|NOT_IN
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
expr_stmt|;
name|useV2
argument_list|(
name|lucene
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ContentRepository
name|createRepository
parameter_list|()
block|{
name|LowCostLuceneIndexProvider
name|provider
init|=
operator|new
name|LowCostLuceneIndexProvider
argument_list|()
decl_stmt|;
return|return
operator|new
name|Oak
argument_list|()
operator|.
name|with
argument_list|(
operator|new
name|InitialContent
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|(
name|QueryIndexProvider
operator|)
name|provider
argument_list|)
operator|.
name|with
argument_list|(
operator|(
name|Observer
operator|)
name|provider
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|LuceneIndexEditorProvider
argument_list|()
argument_list|)
operator|.
name|createContentRepository
argument_list|()
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|ignoreByType
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|content
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"content"
argument_list|)
decl_stmt|;
name|Tree
name|one
init|=
name|content
operator|.
name|addChild
argument_list|(
literal|"one"
argument_list|)
decl_stmt|;
name|one
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|one
operator|.
name|setProperty
argument_list|(
name|JCR_LASTMODIFIED
argument_list|,
literal|"2013-04-01T09:58:03.231Z"
argument_list|,
name|DATE
argument_list|)
expr_stmt|;
name|one
operator|.
name|setProperty
argument_list|(
literal|"jcr:title"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|Tree
name|two
init|=
name|content
operator|.
name|addChild
argument_list|(
literal|"two"
argument_list|)
decl_stmt|;
name|two
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|two
operator|.
name|setProperty
argument_list|(
name|JCR_LASTMODIFIED
argument_list|,
literal|"2014-04-01T09:58:03.231Z"
argument_list|,
name|DATE
argument_list|)
expr_stmt|;
name|two
operator|.
name|setProperty
argument_list|(
literal|"jcr:title"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|String
name|query
init|=
literal|"/jcr:root/content//*[jcr:contains(., 'abc' )"
operator|+
literal|" and (@"
operator|+
name|JCR_LASTMODIFIED
operator|+
literal|"> xs:dateTime('2014-04-01T08:58:03.231Z')) ]"
decl_stmt|;
name|assertQuery
argument_list|(
name|query
argument_list|,
literal|"xpath"
argument_list|,
name|of
argument_list|(
literal|"/content/two"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|ignoreByName
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|of
argument_list|(
literal|"/content/two"
argument_list|)
decl_stmt|;
name|Tree
name|content
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"content"
argument_list|)
decl_stmt|;
name|Tree
name|one
init|=
name|content
operator|.
name|addChild
argument_list|(
literal|"one"
argument_list|)
decl_stmt|;
name|one
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|one
operator|.
name|setProperty
argument_list|(
literal|"jcr:title"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|one
operator|.
name|setProperty
argument_list|(
name|NOT_IN
argument_list|,
literal|"azerty"
argument_list|)
expr_stmt|;
name|Tree
name|two
init|=
name|content
operator|.
name|addChild
argument_list|(
literal|"two"
argument_list|)
decl_stmt|;
name|two
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|two
operator|.
name|setProperty
argument_list|(
literal|"jcr:title"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|two
operator|.
name|setProperty
argument_list|(
name|NOT_IN
argument_list|,
literal|"querty"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|String
name|query
init|=
literal|"/jcr:root/content//*[jcr:contains(., 'abc' )"
operator|+
literal|" and (@"
operator|+
name|NOT_IN
operator|+
literal|" = 'querty') ]"
decl_stmt|;
name|assertQuery
argument_list|(
name|query
argument_list|,
literal|"xpath"
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

