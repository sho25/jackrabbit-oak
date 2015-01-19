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
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|configuration
operator|.
name|SolrServerConfigurationDefaults
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
name|impl
operator|.
name|HttpSolrServer
import|;
end_import

begin_class
specifier|public
class|class
name|DefaultSolrServerProvider
implements|implements
name|SolrServerProvider
block|{
specifier|private
name|SolrServer
name|solrServer
decl_stmt|;
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
name|SolrServer
name|getSolrServer
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|solrServer
operator|==
literal|null
condition|)
block|{
name|initializeSolrServer
argument_list|()
expr_stmt|;
block|}
return|return
name|solrServer
return|;
block|}
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
name|SolrServer
name|getIndexingSolrServer
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|getSolrServer
argument_list|()
return|;
block|}
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
name|SolrServer
name|getSearchingSolrServer
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|getSolrServer
argument_list|()
return|;
block|}
specifier|private
name|void
name|initializeSolrServer
parameter_list|()
block|{
name|String
name|url
init|=
name|SolrServerConfigurationDefaults
operator|.
name|LOCAL_BASE_URL
operator|+
literal|':'
operator|+
name|SolrServerConfigurationDefaults
operator|.
name|HTTP_PORT
operator|+
name|SolrServerConfigurationDefaults
operator|.
name|CONTEXT
operator|+
literal|'/'
operator|+
name|SolrServerConfigurationDefaults
operator|.
name|CORE_NAME
decl_stmt|;
name|solrServer
operator|=
operator|new
name|HttpSolrServer
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

