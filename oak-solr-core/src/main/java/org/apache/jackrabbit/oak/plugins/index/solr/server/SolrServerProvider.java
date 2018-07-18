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
name|server
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|SolrClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Provider of {@link org.apache.solr.client.solrj.SolrClient}s instances  */
end_comment

begin_interface
specifier|public
interface|interface
name|SolrServerProvider
extends|extends
name|Closeable
block|{
comment|/**      * provides an already initialized {@link org.apache.solr.client.solrj.SolrClient} to be used for either searching or      * indexing, or both.      *      * @return a {@link org.apache.solr.client.solrj.SolrClient} instance      * @throws Exception if anything goes wrong while initializing the {@link org.apache.solr.client.solrj.SolrClient}      */
annotation|@
name|Nullable
name|SolrClient
name|getSolrServer
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * provides an already initialized {@link org.apache.solr.client.solrj.SolrClient} specifically configured to be      * used for indexing.      *      * @return a {@link org.apache.solr.client.solrj.SolrClient} instance      * @throws Exception if anything goes wrong while initializing the {@link org.apache.solr.client.solrj.SolrClient}      */
annotation|@
name|Nullable
name|SolrClient
name|getIndexingSolrServer
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * provides an already initialized {@link org.apache.solr.client.solrj.SolrClient} specifically configured to be      * used for searching.      *      * @return a {@link org.apache.solr.client.solrj.SolrClient} instance      * @throws Exception if anything goes wrong while initializing the {@link org.apache.solr.client.solrj.SolrClient}      */
annotation|@
name|Nullable
name|SolrClient
name|getSearchingSolrServer
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

