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
name|api
operator|.
name|jmx
operator|.
name|QueryEngineSettingsMBean
import|;
end_import

begin_comment
comment|/**  * Settings of the query engine.  */
end_comment

begin_class
specifier|public
class|class
name|QueryEngineSettings
implements|implements
name|QueryEngineSettingsMBean
block|{
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_QUERY_LIMIT_IN_MEMORY
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"oak.queryLimitInMemory"
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_QUERY_LIMIT_READS
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"oak.queryLimitReads"
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|DEFAULT_FULL_TEXT_COMPARISON_WITHOUT_INDEX
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"oak.queryFullTextComparisonWithoutIndex"
argument_list|)
decl_stmt|;
specifier|private
name|long
name|limitInMemory
init|=
name|DEFAULT_QUERY_LIMIT_IN_MEMORY
decl_stmt|;
specifier|private
name|long
name|limitReads
init|=
name|DEFAULT_QUERY_LIMIT_READS
decl_stmt|;
specifier|private
name|boolean
name|fullTextComparisonWithoutIndex
init|=
name|DEFAULT_FULL_TEXT_COMPARISON_WITHOUT_INDEX
decl_stmt|;
comment|/**      * Get the limit on how many nodes a query may read at most into memory, for      * "order by" and "distinct" queries. If this limit is exceeded, the query      * throws an exception.      *       * @return the limit      */
annotation|@
name|Override
specifier|public
name|long
name|getLimitInMemory
parameter_list|()
block|{
return|return
name|limitInMemory
return|;
block|}
comment|/**      * Change the limit.      *       * @param limitInMemory the new limit      */
annotation|@
name|Override
specifier|public
name|void
name|setLimitInMemory
parameter_list|(
name|long
name|limitInMemory
parameter_list|)
block|{
name|this
operator|.
name|limitInMemory
operator|=
name|limitInMemory
expr_stmt|;
block|}
comment|/**      * Get the limit on how many nodes a query may read at most (raw read      * operations, including skipped nodes). If this limit is exceeded, the      * query throws an exception.      *       * @return the limit      */
annotation|@
name|Override
specifier|public
name|long
name|getLimitReads
parameter_list|()
block|{
return|return
name|limitReads
return|;
block|}
comment|/**      * Change the limit.      *       * @param limitReads the new limit      */
annotation|@
name|Override
specifier|public
name|void
name|setLimitReads
parameter_list|(
name|long
name|limitReads
parameter_list|)
block|{
name|this
operator|.
name|limitReads
operator|=
name|limitReads
expr_stmt|;
block|}
specifier|public
name|void
name|setFullTextComparisonWithoutIndex
parameter_list|(
name|boolean
name|fullTextComparisonWithoutIndex
parameter_list|)
block|{
name|this
operator|.
name|fullTextComparisonWithoutIndex
operator|=
name|fullTextComparisonWithoutIndex
expr_stmt|;
block|}
specifier|public
name|boolean
name|getFullTextComparisonWithoutIndex
parameter_list|()
block|{
return|return
name|fullTextComparisonWithoutIndex
return|;
block|}
block|}
end_class

end_unit

