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
name|File
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
name|api
operator|.
name|Type
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
name|CommitPolicy
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
name|OakSolrConfiguration
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
name|OakSolrConfigurationProvider
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
name|SolrServerProvider
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
name|query
operator|.
name|Filter
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
name|embedded
operator|.
name|EmbeddedSolrServer
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
name|core
operator|.
name|CoreContainer
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Utility class for tests  */
end_comment

begin_class
specifier|public
class|class
name|TestUtils
implements|implements
name|SolrServerProvider
implements|,
name|OakSolrConfigurationProvider
block|{
specifier|static
specifier|final
name|String
name|SOLR_HOME_PATH
init|=
literal|"/solr"
decl_stmt|;
specifier|public
specifier|static
name|SolrServer
name|createSolrServer
parameter_list|()
block|{
name|String
name|homePath
init|=
name|SolrServerProvider
operator|.
name|class
operator|.
name|getResource
argument_list|(
name|SOLR_HOME_PATH
argument_list|)
operator|.
name|getFile
argument_list|()
decl_stmt|;
name|CoreContainer
name|coreContainer
init|=
operator|new
name|CoreContainer
argument_list|(
name|homePath
argument_list|)
decl_stmt|;
try|try
block|{
name|coreContainer
operator|.
name|load
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
operator|new
name|EmbeddedSolrServer
argument_list|(
name|coreContainer
argument_list|,
literal|"oak"
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|cleanDataDir
parameter_list|()
block|{
name|String
name|path
init|=
name|TestUtils
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/solr/oak/data"
argument_list|)
operator|.
name|getFile
argument_list|()
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
name|file
operator|.
name|delete
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|OakSolrConfiguration
name|getTestConfiguration
parameter_list|()
block|{
return|return
operator|new
name|OakSolrConfiguration
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getFieldNameFor
parameter_list|(
name|Type
argument_list|<
name|?
argument_list|>
name|propertyType
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPathField
parameter_list|()
block|{
return|return
literal|"path_exact"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getFieldForPathRestriction
parameter_list|(
name|Filter
operator|.
name|PathRestriction
name|pathRestriction
parameter_list|)
block|{
name|String
name|fieldName
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|pathRestriction
condition|)
block|{
case|case
name|ALL_CHILDREN
case|:
block|{
name|fieldName
operator|=
literal|"path_des"
expr_stmt|;
break|break;
block|}
case|case
name|DIRECT_CHILDREN
case|:
block|{
name|fieldName
operator|=
literal|"path_child"
expr_stmt|;
break|break;
block|}
case|case
name|EXACT
case|:
block|{
name|fieldName
operator|=
literal|"path_exact"
expr_stmt|;
break|break;
block|}
case|case
name|PARENT
case|:
block|{
name|fieldName
operator|=
literal|"path_anc"
expr_stmt|;
break|break;
block|}
case|case
name|NO_RESTRICTION
case|:
break|break;
default|default:
break|break;
block|}
return|return
name|fieldName
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getFieldForPropertyRestriction
parameter_list|(
name|Filter
operator|.
name|PropertyRestriction
name|propertyRestriction
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|CommitPolicy
name|getCommitPolicy
parameter_list|()
block|{
return|return
name|CommitPolicy
operator|.
name|HARD
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getCatchAllField
parameter_list|()
block|{
return|return
literal|"catch_all"
return|;
block|}
block|}
return|;
block|}
specifier|private
specifier|final
name|SolrServer
name|solrServer
init|=
name|createSolrServer
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|OakSolrConfiguration
name|configuration
init|=
name|getTestConfiguration
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|SolrServer
name|getSolrServer
parameter_list|()
block|{
return|return
name|solrServer
return|;
block|}
annotation|@
name|Override
specifier|public
name|OakSolrConfiguration
name|getConfiguration
parameter_list|()
block|{
return|return
name|configuration
return|;
block|}
block|}
end_class

end_unit

