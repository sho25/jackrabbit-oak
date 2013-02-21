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
name|index
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
name|IndexHook
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
name|CommitHook
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
name|state
operator|.
name|NodeBuilder
import|;
end_import

begin_comment
comment|/**  * Provider interface for {@link SolrCommitHook}s and {@link SolrIndexHook}s  */
end_comment

begin_interface
specifier|public
interface|interface
name|SolrHookFactory
block|{
comment|/**      * create a {@link SolrIndexHook} to index data on a Solr server      *      * @param path the path the created {@link SolrIndexHook} should work on      * @param builder the {@link NodeBuilder} to get {@link org.apache.jackrabbit.oak.spi.state.NodeState}s      * @return the created {@link IndexHook}      * @throws Exception if any failres happen during the hook creation      */
specifier|public
name|IndexHook
name|createIndexHook
parameter_list|(
name|String
name|path
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * create a {@link SolrCommitHook} to index data on a Solr server      *      * @return the created {@link SolrCommitHook}      * @throws Exception if any failres happen during the hook creation      */
specifier|public
name|CommitHook
name|createCommitHook
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

