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
name|benchmark
package|;
end_package

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
name|NodeIterator
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

begin_comment
comment|/**  * Benchmark the query performance of an ORDER BY clause when No index are involved  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|OrderedIndexQueryBaseTest
extends|extends
name|OrderedIndexBaseTest
block|{
name|Node
name|index
decl_stmt|;
comment|/**      * query to execute with the ORDER BY statement      */
specifier|public
specifier|static
specifier|final
name|String
name|QUERY_WITH_ORDER
init|=
name|String
operator|.
name|format
argument_list|(
literal|"SELECT * FROM [%s] WHERE %s IS NOT NULL ORDER BY %s"
argument_list|,
name|NODE_TYPE
argument_list|,
name|INDEXED_PROPERTY
argument_list|,
name|INDEXED_PROPERTY
argument_list|)
decl_stmt|;
comment|/**      * constant used to identify how many nodes will be fetched after the query execution      */
specifier|public
specifier|static
specifier|final
name|int
name|FETCH_NODES
init|=
literal|100
decl_stmt|;
comment|/**      * query to execute WITHOUT the ORDER BY clause      */
specifier|public
specifier|static
specifier|final
name|String
name|QUERY_WITHOUT_ORDER
init|=
name|String
operator|.
name|format
argument_list|(
literal|"SELECT * FROM [%s] WHERE %s IS NOT NULL"
argument_list|,
name|NODE_TYPE
argument_list|,
name|INDEXED_PROPERTY
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|beforeSuite
parameter_list|()
throws|throws
name|Exception
block|{
name|session
operator|=
name|loginWriter
argument_list|()
expr_stmt|;
name|dump
operator|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|DUMP_NODE
argument_list|,
name|NODE_TYPE
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|defineIndex
argument_list|()
expr_stmt|;
name|insertRandomNodes
argument_list|(
name|PRE_ADDED_NODES
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|afterSuite
parameter_list|()
throws|throws
name|Exception
block|{
name|dump
operator|.
name|remove
argument_list|()
expr_stmt|;
if|if
condition|(
name|index
operator|!=
literal|null
condition|)
block|{
name|index
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|runTest
parameter_list|()
throws|throws
name|Exception
block|{
name|QueryManager
name|qm
init|=
name|session
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getQueryManager
argument_list|()
decl_stmt|;
name|Query
name|q
init|=
name|qm
operator|.
name|createQuery
argument_list|(
name|getQuery
argument_list|()
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|)
decl_stmt|;
name|QueryResult
name|r
init|=
name|q
operator|.
name|execute
argument_list|()
decl_stmt|;
name|NodeIterator
name|nodes
init|=
name|r
operator|.
name|getNodes
argument_list|()
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|nodes
operator|.
name|hasNext
argument_list|()
operator|&&
name|counter
operator|++
operator|<
name|FETCH_NODES
condition|)
block|{
name|nodes
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
specifier|abstract
name|String
name|getQuery
parameter_list|()
function_decl|;
annotation|@
name|Override
name|boolean
name|isBatchSaving
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

