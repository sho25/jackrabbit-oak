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
name|configuration
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|solr
operator|.
name|server
operator|.
name|RemoteSolrServerProvider
import|;
end_import

begin_comment
comment|/**  * {@link org.apache.jackrabbit.oak.plugins.index.solr.configuration.SolrServerConfiguration} for the {@link org.apache.jackrabbit.oak.plugins.index.solr.server.RemoteSolrServerProvider}  */
end_comment

begin_class
specifier|public
class|class
name|RemoteSolrServerConfiguration
extends|extends
name|SolrServerConfiguration
argument_list|<
name|RemoteSolrServerProvider
argument_list|>
block|{
specifier|private
specifier|final
name|String
name|solrConfDir
decl_stmt|;
specifier|private
specifier|final
name|int
name|socketTimeout
decl_stmt|;
specifier|private
specifier|final
name|int
name|connectionTimeout
decl_stmt|;
specifier|private
specifier|final
name|String
index|[]
name|solrHttpUrls
decl_stmt|;
specifier|private
specifier|final
name|String
name|solrZkHost
decl_stmt|;
specifier|private
specifier|final
name|String
name|solrCollection
decl_stmt|;
specifier|private
specifier|final
name|int
name|solrShardsNo
decl_stmt|;
specifier|private
specifier|final
name|int
name|solrReplicationFactor
decl_stmt|;
specifier|public
name|RemoteSolrServerConfiguration
parameter_list|(
name|String
name|solrZkHost
parameter_list|,
name|String
name|solrCollection
parameter_list|,
name|int
name|solrShardsNo
parameter_list|,
name|int
name|solrReplicationFactor
parameter_list|,
name|String
name|solrConfDir
parameter_list|,
name|int
name|socketTimeout
parameter_list|,
name|int
name|connectionTimeout
parameter_list|,
name|String
modifier|...
name|solrHttpUrls
parameter_list|)
block|{
name|this
operator|.
name|socketTimeout
operator|=
name|socketTimeout
expr_stmt|;
name|this
operator|.
name|connectionTimeout
operator|=
name|connectionTimeout
expr_stmt|;
name|this
operator|.
name|solrHttpUrls
operator|=
name|solrHttpUrls
expr_stmt|;
name|this
operator|.
name|solrZkHost
operator|=
name|solrZkHost
expr_stmt|;
name|this
operator|.
name|solrCollection
operator|=
name|solrCollection
expr_stmt|;
name|this
operator|.
name|solrShardsNo
operator|=
name|solrShardsNo
expr_stmt|;
name|this
operator|.
name|solrConfDir
operator|=
name|solrConfDir
expr_stmt|;
name|this
operator|.
name|solrReplicationFactor
operator|=
name|solrReplicationFactor
expr_stmt|;
block|}
specifier|public
name|String
index|[]
name|getSolrHttpUrls
parameter_list|()
block|{
return|return
name|solrHttpUrls
return|;
block|}
specifier|public
name|String
name|getSolrZkHost
parameter_list|()
block|{
return|return
name|solrZkHost
return|;
block|}
specifier|public
name|String
name|getSolrCollection
parameter_list|()
block|{
return|return
name|solrCollection
return|;
block|}
specifier|public
name|int
name|getSolrShardsNo
parameter_list|()
block|{
return|return
name|solrShardsNo
return|;
block|}
specifier|public
name|int
name|getSolrReplicationFactor
parameter_list|()
block|{
return|return
name|solrReplicationFactor
return|;
block|}
specifier|public
name|String
name|getSolrConfDir
parameter_list|()
block|{
return|return
name|solrConfDir
return|;
block|}
specifier|public
name|int
name|getSocketTimeout
parameter_list|()
block|{
return|return
name|socketTimeout
return|;
block|}
specifier|public
name|int
name|getConnectionTimeout
parameter_list|()
block|{
return|return
name|connectionTimeout
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"RemoteSolrServerConfiguration{"
operator|+
literal|"solrConfDir='"
operator|+
name|solrConfDir
operator|+
literal|'\''
operator|+
literal|", socketTimeout="
operator|+
name|socketTimeout
operator|+
literal|", connectionTimeout="
operator|+
name|connectionTimeout
operator|+
literal|", solrHttpUrls="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|solrHttpUrls
argument_list|)
operator|+
literal|", solrZkHost='"
operator|+
name|solrZkHost
operator|+
literal|'\''
operator|+
literal|", solrCollection='"
operator|+
name|solrCollection
operator|+
literal|'\''
operator|+
literal|", solrShardsNo="
operator|+
name|solrShardsNo
operator|+
literal|", solrReplicationFactor="
operator|+
name|solrReplicationFactor
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

