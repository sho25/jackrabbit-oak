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
package|;
end_package

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
name|InitialContentHelper
operator|.
name|INITIAL_CONTENT
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
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Result
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
name|ResultRow
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
name|core
operator|.
name|ImmutableRoot
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
name|NativeQueryTest
block|{
specifier|private
specifier|final
name|ImmutableRoot
name|ROOT
init|=
operator|new
name|ImmutableRoot
argument_list|(
name|INITIAL_CONTENT
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|QueryEngineImpl
name|QUERY_ENGINE
init|=
operator|(
name|QueryEngineImpl
operator|)
name|ROOT
operator|.
name|getQueryEngine
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|SQL2Parser
name|p
init|=
name|SQL2ParserTest
operator|.
name|createTestSQL2Parser
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|dontTraverseForSuggest
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|sql
init|=
literal|"select [rep:suggest()] from [nt:base] where suggest('test')"
decl_stmt|;
name|assertDontTraverseFor
argument_list|(
name|sql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|dontTraverseForSpellcheck
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|sql
init|=
literal|"select [rep:spellcheck()] from [nt:base] where spellcheck('test')"
decl_stmt|;
name|assertDontTraverseFor
argument_list|(
name|sql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|dontTraverseForNative
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|sql
init|=
literal|"select [jcr:path] from [nt:base] where native('solr', 'name:(Hello OR World)')"
decl_stmt|;
name|assertDontTraverseFor
argument_list|(
name|sql
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|dontTraverseForSimilar
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|sql
init|=
literal|"select [rep:similar()] from [nt:base] where similar(., '/test/a')"
decl_stmt|;
name|assertDontTraverseFor
argument_list|(
name|sql
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertDontTraverseFor
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|ParseException
block|{
name|QueryImpl
name|query
init|=
operator|(
name|QueryImpl
operator|)
name|p
operator|.
name|parse
argument_list|(
name|sql
argument_list|)
decl_stmt|;
name|query
operator|.
name|setExecutionContext
argument_list|(
name|QUERY_ENGINE
operator|.
name|getExecutionContext
argument_list|()
argument_list|)
expr_stmt|;
name|Result
name|result
init|=
name|query
operator|.
name|executeQuery
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|?
extends|extends
name|ResultRow
argument_list|>
name|it
init|=
name|result
operator|.
name|getRows
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Zero results expected"
argument_list|,
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|query
operator|=
operator|(
name|QueryImpl
operator|)
name|p
operator|.
name|parse
argument_list|(
literal|"measure "
operator|+
name|sql
argument_list|)
expr_stmt|;
name|query
operator|.
name|setExecutionContext
argument_list|(
name|QUERY_ENGINE
operator|.
name|getExecutionContext
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|query
operator|.
name|executeQuery
argument_list|()
expr_stmt|;
name|it
operator|=
name|result
operator|.
name|getRows
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ResultRow
name|row
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|selector
init|=
name|row
operator|.
name|getValue
argument_list|(
literal|"selector"
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"nt:base"
operator|.
name|equals
argument_list|(
name|selector
argument_list|)
condition|)
block|{
name|long
name|scanCount
init|=
name|row
operator|.
name|getValue
argument_list|(
literal|"scanCount"
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
decl_stmt|;
comment|// we expect that no was scanned that's it
comment|// - no traversal of the whole respository
name|assertEquals
argument_list|(
literal|"Repository's scan count doesn't match"
argument_list|,
literal|0
argument_list|,
name|scanCount
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

