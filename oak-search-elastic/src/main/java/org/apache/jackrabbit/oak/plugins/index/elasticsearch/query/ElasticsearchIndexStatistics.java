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
name|elasticsearch
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
name|plugins
operator|.
name|index
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIndexCoordinate
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
name|search
operator|.
name|IndexStatistics
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|RequestOptions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|core
operator|.
name|CountRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|core
operator|.
name|CountResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryBuilders
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|builder
operator|.
name|SearchSourceBuilder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
specifier|public
class|class
name|ElasticsearchIndexStatistics
implements|implements
name|IndexStatistics
block|{
specifier|private
specifier|final
name|ElasticsearchIndexCoordinate
name|elasticsearchIndexCoordinate
decl_stmt|;
name|ElasticsearchIndexStatistics
parameter_list|(
name|ElasticsearchIndexCoordinate
name|elasticsearchIndexCoordinate
parameter_list|)
block|{
name|this
operator|.
name|elasticsearchIndexCoordinate
operator|=
name|elasticsearchIndexCoordinate
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|numDocs
parameter_list|()
block|{
name|CountRequest
name|countRequest
init|=
operator|new
name|CountRequest
argument_list|()
decl_stmt|;
name|SearchSourceBuilder
name|searchSourceBuilder
init|=
operator|new
name|SearchSourceBuilder
argument_list|()
decl_stmt|;
name|searchSourceBuilder
operator|.
name|query
argument_list|(
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
expr_stmt|;
name|countRequest
operator|.
name|source
argument_list|(
name|searchSourceBuilder
argument_list|)
expr_stmt|;
try|try
block|{
name|CountResponse
name|count
init|=
name|elasticsearchIndexCoordinate
operator|.
name|getClient
argument_list|()
operator|.
name|count
argument_list|(
name|countRequest
argument_list|,
name|RequestOptions
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
return|return
operator|(
name|int
operator|)
name|count
operator|.
name|getCount
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore failure
return|return
literal|100000
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|getDocCountFor
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|CountRequest
name|countRequest
init|=
operator|new
name|CountRequest
argument_list|()
decl_stmt|;
name|SearchSourceBuilder
name|searchSourceBuilder
init|=
operator|new
name|SearchSourceBuilder
argument_list|()
decl_stmt|;
name|searchSourceBuilder
operator|.
name|query
argument_list|(
name|QueryBuilders
operator|.
name|existsQuery
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|countRequest
operator|.
name|source
argument_list|(
name|searchSourceBuilder
argument_list|)
expr_stmt|;
try|try
block|{
name|CountResponse
name|count
init|=
name|elasticsearchIndexCoordinate
operator|.
name|getClient
argument_list|()
operator|.
name|count
argument_list|(
name|countRequest
argument_list|,
name|RequestOptions
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
return|return
operator|(
name|int
operator|)
name|count
operator|.
name|getCount
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore failure
return|return
literal|1000
return|;
block|}
block|}
block|}
end_class

end_unit

