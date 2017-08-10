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
name|property
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
name|NAMES
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
name|IndexConstants
operator|.
name|PROPERTY_NAMES
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
name|IndexConstants
operator|.
name|REINDEX_PROPERTY_NAME
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
name|property
operator|.
name|PropertyIndexEditorProvider
operator|.
name|TYPE
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
name|query
operator|.
name|Query
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
name|index
operator|.
name|IndexConstants
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

begin_class
specifier|public
class|class
name|ValuePatternPropertyIndexTests
extends|extends
name|AbstractQueryTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|INDEXED_PROPERTY
init|=
literal|"indexedProperty"
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|ContentRepository
name|createRepository
parameter_list|()
block|{
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
operator|new
name|PropertyIndexProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|PropertyIndexEditorProvider
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
name|valuePattern
parameter_list|()
throws|throws
name|Exception
block|{
comment|//        valuePattern("", "* property test-index");
comment|//        valuePattern("x", "* property test-index");
name|valuePattern
argument_list|(
literal|" "
argument_list|,
literal|"* property test-index"
argument_list|)
expr_stmt|;
name|valuePattern
argument_list|(
literal|"-"
argument_list|,
literal|"* property test-index"
argument_list|)
expr_stmt|;
name|valuePattern
argument_list|(
literal|"/"
argument_list|,
literal|"* property test-index"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|valuePattern
parameter_list|(
name|String
name|middle
parameter_list|,
name|String
name|plan
parameter_list|)
throws|throws
name|Exception
block|{
name|Tree
name|index
init|=
name|super
operator|.
name|createTestIndexNode
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|,
name|TYPE
argument_list|)
decl_stmt|;
name|index
operator|.
name|setProperty
argument_list|(
name|PROPERTY_NAMES
argument_list|,
name|of
argument_list|(
name|INDEXED_PROPERTY
argument_list|)
argument_list|,
name|NAMES
argument_list|)
expr_stmt|;
name|index
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|VALUE_INCLUDED_PREFIXES
argument_list|,
literal|"hello"
operator|+
name|middle
operator|+
literal|"world"
argument_list|)
expr_stmt|;
name|index
operator|.
name|setProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
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
name|content
operator|.
name|addChild
argument_list|(
literal|"node1"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|INDEXED_PROPERTY
argument_list|,
literal|"hello"
operator|+
name|middle
operator|+
literal|"world"
argument_list|)
expr_stmt|;
name|content
operator|.
name|addChild
argument_list|(
literal|"node2"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|INDEXED_PROPERTY
argument_list|,
literal|"hello"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|String
name|statement
init|=
literal|"explain select * from [nt:base] where ["
operator|+
name|INDEXED_PROPERTY
operator|+
literal|"] = 'hello"
operator|+
name|middle
operator|+
literal|"world'"
decl_stmt|;
name|String
name|result
init|=
name|executeQuery
argument_list|(
name|statement
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|result
argument_list|,
name|result
operator|.
name|indexOf
argument_list|(
name|plan
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
