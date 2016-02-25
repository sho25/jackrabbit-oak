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
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

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
name|SolrServerConfiguration
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

begin_comment
comment|/**  * A registry for {@link org.apache.solr.client.solrj.SolrServer}s  */
end_comment

begin_class
specifier|public
class|class
name|SolrServerRegistry
block|{
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SolrServer
argument_list|>
name|searchingServerRegistry
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|SolrServer
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SolrServer
argument_list|>
name|indexingServerRegistry
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|SolrServer
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|void
name|register
parameter_list|(
annotation|@
name|Nonnull
name|SolrServerConfiguration
name|configuration
parameter_list|,
annotation|@
name|Nonnull
name|SolrServer
name|solrServer
parameter_list|,
annotation|@
name|Nonnull
name|Strategy
name|strategy
parameter_list|)
block|{
switch|switch
condition|(
name|strategy
condition|)
block|{
case|case
name|INDEXING
case|:
synchronized|synchronized
init|(
name|indexingServerRegistry
init|)
block|{
name|indexingServerRegistry
operator|.
name|put
argument_list|(
name|configuration
operator|.
name|toString
argument_list|()
argument_list|,
name|solrServer
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|SEARCHING
case|:
synchronized|synchronized
init|(
name|searchingServerRegistry
init|)
block|{
name|searchingServerRegistry
operator|.
name|put
argument_list|(
name|configuration
operator|.
name|toString
argument_list|()
argument_list|,
name|solrServer
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
annotation|@
name|CheckForNull
specifier|public
specifier|static
name|SolrServer
name|get
parameter_list|(
annotation|@
name|Nonnull
name|SolrServerConfiguration
name|configuration
parameter_list|,
annotation|@
name|Nonnull
name|Strategy
name|strategy
parameter_list|)
block|{
switch|switch
condition|(
name|strategy
condition|)
block|{
case|case
name|INDEXING
case|:
synchronized|synchronized
init|(
name|indexingServerRegistry
init|)
block|{
return|return
name|indexingServerRegistry
operator|.
name|get
argument_list|(
name|configuration
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
case|case
name|SEARCHING
case|:
synchronized|synchronized
init|(
name|searchingServerRegistry
init|)
block|{
return|return
name|searchingServerRegistry
operator|.
name|get
argument_list|(
name|configuration
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|public
specifier|static
name|void
name|unregister
parameter_list|(
name|SolrServerConfiguration
name|configuration
parameter_list|,
annotation|@
name|Nonnull
name|Strategy
name|strategy
parameter_list|)
block|{
switch|switch
condition|(
name|strategy
condition|)
block|{
case|case
name|INDEXING
case|:
synchronized|synchronized
init|(
name|indexingServerRegistry
init|)
block|{
name|SolrServer
name|removed
init|=
name|indexingServerRegistry
operator|.
name|remove
argument_list|(
name|configuration
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|removed
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
break|break;
case|case
name|SEARCHING
case|:
synchronized|synchronized
init|(
name|searchingServerRegistry
init|)
block|{
name|SolrServer
name|removed
init|=
name|searchingServerRegistry
operator|.
name|remove
argument_list|(
name|configuration
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|removed
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
break|break;
block|}
block|}
specifier|public
enum|enum
name|Strategy
block|{
name|INDEXING
block|,
name|SEARCHING
block|}
block|}
end_class

end_unit

