begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing,  *  software distributed under the License is distributed on an  *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  *  KIND, either express or implied.  See the License for the  *  specific language governing permissions and limitations  *  under the License.  */
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
name|scalability
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|Property
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
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
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|QueryManager
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
name|RowIterator
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
name|scalability
operator|.
name|ScalabilityAbstractSuite
operator|.
name|ExecutionContext
import|;
end_import

begin_comment
comment|/**  * Abstract class which defines utility methods for processing results like   * pagination and no pagination.   *  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|PaginationEnabledSearcher
extends|extends
name|SearchScalabilityBenchmark
block|{
comment|/**      * Pagination limit for one page      */
specifier|protected
specifier|static
specifier|final
name|int
name|LIMIT
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"limit"
argument_list|,
literal|25
argument_list|)
decl_stmt|;
comment|/**      * Number of page accesses      */
specifier|protected
specifier|static
specifier|final
name|int
name|PAGES
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"pages"
argument_list|,
literal|5
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|KEYSET_VAL_PROP
init|=
literal|"keysetval"
decl_stmt|;
specifier|protected
name|void
name|processResultsOffsetPagination
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|QueryManager
name|qm
parameter_list|,
name|ExecutionContext
name|context
parameter_list|)
throws|throws
name|RepositoryException
block|{
for|for
control|(
name|int
name|page
init|=
literal|0
init|;
name|page
operator|<
name|PAGES
condition|;
name|page
operator|++
control|)
block|{
name|Query
name|query
init|=
name|getQuery
argument_list|(
name|qm
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|query
operator|.
name|setLimit
argument_list|(
name|LIMIT
argument_list|)
expr_stmt|;
name|query
operator|.
name|setOffset
argument_list|(
name|page
operator|*
name|LIMIT
argument_list|)
expr_stmt|;
name|iterate
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Node
name|iterate
parameter_list|(
name|Query
name|query
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|QueryResult
name|r
init|=
name|query
operator|.
name|execute
argument_list|()
decl_stmt|;
name|RowIterator
name|it
init|=
name|r
operator|.
name|getRows
argument_list|()
decl_stmt|;
name|Node
name|last
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|last
operator|=
name|it
operator|.
name|nextRow
argument_list|()
operator|.
name|getNode
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|last
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|last
return|;
block|}
specifier|protected
name|void
name|processResultsKeysetPagination
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|QueryManager
name|qm
parameter_list|,
name|ExecutionContext
name|context
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Calendar
name|now
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|now
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|getMap
argument_list|()
operator|.
name|put
argument_list|(
name|KEYSET_VAL_PROP
argument_list|,
name|now
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|page
init|=
literal|0
init|;
name|page
operator|<
name|PAGES
condition|;
name|page
operator|++
control|)
block|{
name|Query
name|query
init|=
name|getQuery
argument_list|(
name|qm
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|query
operator|.
name|setLimit
argument_list|(
name|LIMIT
argument_list|)
expr_stmt|;
name|Node
name|lastNode
init|=
name|iterate
argument_list|(
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastNode
operator|!=
literal|null
condition|)
block|{
name|Property
name|prop
init|=
name|lastNode
operator|.
name|getProperty
argument_list|(
name|ScalabilityNodeSuite
operator|.
name|CTX_PAGINATION_KEY_PROP
argument_list|)
decl_stmt|;
name|context
operator|.
name|getMap
argument_list|()
operator|.
name|put
argument_list|(
name|KEYSET_VAL_PROP
argument_list|,
name|prop
operator|.
name|getDate
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|context
operator|.
name|getMap
argument_list|()
operator|.
name|remove
argument_list|(
name|KEYSET_VAL_PROP
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|String
name|getOrderByClause
parameter_list|()
block|{
return|return
literal|" order by"
operator|+
literal|" @"
operator|+
name|ScalabilityNodeSuite
operator|.
name|SORT_PROP
operator|+
literal|" descending,"
operator|+
literal|" @"
operator|+
name|ScalabilityNodeSuite
operator|.
name|DATE_PROP
operator|+
literal|" descending"
return|;
block|}
block|}
end_class

end_unit

