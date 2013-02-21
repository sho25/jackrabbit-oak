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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|SolrServer
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
name|SolrServerException
import|;
end_import

begin_comment
comment|/**  * Utilities for Oak Solr integration.  */
end_comment

begin_class
specifier|public
class|class
name|OakSolrUtils
block|{
comment|/**      * Check if a given Solr instance is alive      *      * @param solrServer the {@link SolrServer} used to communicate with the Solr instance      * @return<code>true</code> if the given Solr instance is alive and responding      * @throws IOException         if any error occurs while trying to communicate with the Solr instance      * @throws SolrServerException      */
specifier|public
specifier|static
name|boolean
name|checkServerAlive
parameter_list|(
annotation|@
name|Nonnull
name|SolrServer
name|solrServer
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
return|return
name|solrServer
operator|.
name|ping
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|==
literal|0
return|;
block|}
block|}
end_class

end_unit

