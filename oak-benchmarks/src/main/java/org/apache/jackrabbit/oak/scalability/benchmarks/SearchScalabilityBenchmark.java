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
name|scalability
operator|.
name|benchmarks
package|;
end_package

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
name|Credentials
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
name|Repository
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
name|Session
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
name|suites
operator|.
name|ScalabilityAbstractSuite
operator|.
name|ExecutionContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Abstract class for search scalability benchmarks.  *  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SearchScalabilityBenchmark
extends|extends
name|ScalabilityBenchmark
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SearchScalabilityBenchmark
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Controls the max results retrieved after search      */
specifier|private
specifier|static
specifier|final
name|int
name|MAX_RESULTS
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"maxResults"
argument_list|,
literal|100
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|Repository
name|repository
parameter_list|,
name|Credentials
name|credentials
parameter_list|,
name|ExecutionContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|repository
operator|.
name|login
argument_list|(
name|credentials
argument_list|)
decl_stmt|;
name|QueryManager
name|qm
decl_stmt|;
try|try
block|{
name|qm
operator|=
name|session
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getQueryManager
argument_list|()
expr_stmt|;
name|search
argument_list|(
name|qm
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|search
parameter_list|(
name|QueryManager
name|qm
parameter_list|,
name|ExecutionContext
name|context
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Query
name|q
init|=
name|getQuery
argument_list|(
name|qm
argument_list|,
name|context
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
name|RowIterator
name|it
init|=
name|r
operator|.
name|getRows
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|rows
init|=
literal|0
init|;
name|it
operator|.
name|hasNext
argument_list|()
operator|&&
name|rows
operator|<
name|MAX_RESULTS
condition|;
name|rows
operator|++
control|)
block|{
name|Node
name|node
init|=
name|it
operator|.
name|nextRow
argument_list|()
operator|.
name|getNode
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
specifier|abstract
name|Query
name|getQuery
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
function_decl|;
block|}
end_class

end_unit
