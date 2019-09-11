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
name|elasticsearch
operator|.
name|ElasticsearchIndexCoordinateFactory
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
name|FieldNames
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
name|IndexDefinition
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
name|spi
operator|.
name|editor
operator|.
name|FulltextIndexWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|delete
operator|.
name|DeleteRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|delete
operator|.
name|DeleteResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|index
operator|.
name|IndexRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|index
operator|.
name|IndexResponse
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
name|RestHighLevelClient
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
name|indices
operator|.
name|CreateIndexRequest
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
name|indices
operator|.
name|CreateIndexResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|Strings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Settings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentType
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
name|NotNull
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
import|import static
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
name|index
operator|.
name|ElasticsearchDocument
operator|.
name|pathToId
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|WriteRequest
operator|.
name|RefreshPolicy
operator|.
name|IMMEDIATE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|WriteRequest
operator|.
name|RefreshPolicy
operator|.
name|NONE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|ToXContent
operator|.
name|EMPTY_PARAMS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentFactory
operator|.
name|jsonBuilder
import|;
end_import

begin_class
specifier|public
class|class
name|ElasticsearchIndexWriter
implements|implements
name|FulltextIndexWriter
argument_list|<
name|ElasticsearchDocument
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ElasticsearchIndexWriter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ElasticsearchIndexCoordinate
name|esIndexCoord
decl_stmt|;
specifier|private
specifier|final
name|RestHighLevelClient
name|client
decl_stmt|;
specifier|private
name|boolean
name|shouldProvisionIndex
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|isAsync
decl_stmt|;
comment|// TODO: use bulk API - https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-docs-bulk-processor.html
name|ElasticsearchIndexWriter
parameter_list|(
annotation|@
name|NotNull
name|IndexDefinition
name|indexDefinition
parameter_list|,
name|ElasticsearchIndexCoordinateFactory
name|esIndexCoordFactory
parameter_list|)
block|{
name|esIndexCoord
operator|=
name|esIndexCoordFactory
operator|.
name|getElasticsearchIndexCoordinate
argument_list|(
name|indexDefinition
argument_list|)
expr_stmt|;
name|client
operator|=
name|esIndexCoord
operator|.
name|getClient
argument_list|()
expr_stmt|;
comment|// TODO: ES indexing put another bit delay before docs appear in search.
comment|// For test without "async" indexing, we can use following hack BUT those where we
comment|// would setup async, we'd need to find another way.
name|isAsync
operator|=
name|indexDefinition
operator|.
name|getDefinitionNodeState
argument_list|()
operator|.
name|getProperty
argument_list|(
literal|"async"
argument_list|)
operator|!=
literal|null
expr_stmt|;
name|shouldProvisionIndex
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|updateDocument
parameter_list|(
name|String
name|path
parameter_list|,
name|ElasticsearchDocument
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|provisionIndex
argument_list|()
expr_stmt|;
name|IndexRequest
name|request
init|=
operator|new
name|IndexRequest
argument_list|(
name|esIndexCoord
operator|.
name|getEsIndexName
argument_list|()
argument_list|)
operator|.
name|id
argument_list|(
name|pathToId
argument_list|(
name|path
argument_list|)
argument_list|)
comment|// immediate refresh would slow indexing response such that next
comment|// search would see the effect of this indexed doc. Must only get
comment|// enabled in tests (hopefully there are no non-async indexes in real life)
operator|.
name|setRefreshPolicy
argument_list|(
name|isAsync
condition|?
name|NONE
else|:
name|IMMEDIATE
argument_list|)
operator|.
name|source
argument_list|(
name|doc
operator|.
name|build
argument_list|()
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
decl_stmt|;
name|IndexResponse
name|response
init|=
name|client
operator|.
name|index
argument_list|(
name|request
argument_list|,
name|RequestOptions
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"update {} - {}. Response: {}"
argument_list|,
name|path
argument_list|,
name|doc
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|deleteDocuments
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|provisionIndex
argument_list|()
expr_stmt|;
name|DeleteRequest
name|request
init|=
operator|new
name|DeleteRequest
argument_list|(
name|esIndexCoord
operator|.
name|getEsIndexName
argument_list|()
argument_list|)
operator|.
name|id
argument_list|(
name|pathToId
argument_list|(
name|path
argument_list|)
argument_list|)
comment|// immediate refresh would slow indexing response such that next
comment|// search would see the effect of this indexed doc. Must only get
comment|// enabled in tests (hopefully there are no non-async indexes in real life)
operator|.
name|setRefreshPolicy
argument_list|(
name|isAsync
condition|?
name|NONE
else|:
name|IMMEDIATE
argument_list|)
decl_stmt|;
name|DeleteResponse
name|response
init|=
name|client
operator|.
name|delete
argument_list|(
name|request
argument_list|,
name|RequestOptions
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"delete {}. Response: {}"
argument_list|,
name|path
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|close
parameter_list|(
name|long
name|timestamp
parameter_list|)
throws|throws
name|IOException
block|{
name|provisionIndex
argument_list|()
expr_stmt|;
comment|// TODO : track index updates and return accordingly
comment|// TODO : if/when we do async push, this is where to wait for those ops to complete
return|return
literal|false
return|;
block|}
comment|/**      * This method<b>won't</b> immediately provision index. But, provision would be done<b>before</b>      * any updates are sent to the index      */
name|void
name|setProvisioningRequired
parameter_list|()
block|{
name|shouldProvisionIndex
operator|=
literal|true
expr_stmt|;
block|}
specifier|private
name|void
name|provisionIndex
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|shouldProvisionIndex
condition|)
block|{
return|return;
block|}
try|try
block|{
name|CreateIndexRequest
name|request
init|=
operator|new
name|CreateIndexRequest
argument_list|(
name|esIndexCoord
operator|.
name|getEsIndexName
argument_list|()
argument_list|)
decl_stmt|;
comment|// provision settings
name|request
operator|.
name|settings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"analysis.analyzer.ancestor_analyzer.type"
argument_list|,
literal|"custom"
argument_list|)
operator|.
name|put
argument_list|(
literal|"analysis.analyzer.ancestor_analyzer.tokenizer"
argument_list|,
literal|"path_hierarchy"
argument_list|)
argument_list|)
expr_stmt|;
comment|// provision mappings
name|XContentBuilder
name|mappingBuilder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
decl_stmt|;
name|mappingBuilder
operator|.
name|startObject
argument_list|()
expr_stmt|;
block|{
name|mappingBuilder
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
expr_stmt|;
block|{
name|mappingBuilder
operator|.
name|startObject
argument_list|(
name|FieldNames
operator|.
name|ANCESTORS
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"text"
argument_list|)
operator|.
name|field
argument_list|(
literal|"analyzer"
argument_list|,
literal|"ancestor_analyzer"
argument_list|)
operator|.
name|field
argument_list|(
literal|"search_analyzer"
argument_list|,
literal|"keyword"
argument_list|)
operator|.
name|field
argument_list|(
literal|"search_quote_analyzer"
argument_list|,
literal|"keyword"
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|mappingBuilder
operator|.
name|startObject
argument_list|(
name|FieldNames
operator|.
name|PATH_DEPTH
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"integer"
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|mappingBuilder
operator|.
name|startObject
argument_list|(
name|FieldNames
operator|.
name|SUGGEST
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"completion"
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|mappingBuilder
operator|.
name|startObject
argument_list|(
name|FieldNames
operator|.
name|NOT_NULL_PROPS
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"keyword"
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|mappingBuilder
operator|.
name|startObject
argument_list|(
name|FieldNames
operator|.
name|NULL_PROPS
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"keyword"
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|mappingBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|mappingBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|request
operator|.
name|mapping
argument_list|(
name|mappingBuilder
argument_list|)
expr_stmt|;
name|String
name|requestMsg
init|=
name|Strings
operator|.
name|toString
argument_list|(
name|request
operator|.
name|toXContent
argument_list|(
name|jsonBuilder
argument_list|()
argument_list|,
name|EMPTY_PARAMS
argument_list|)
argument_list|)
decl_stmt|;
name|CreateIndexResponse
name|response
init|=
name|client
operator|.
name|indices
argument_list|()
operator|.
name|create
argument_list|(
name|request
argument_list|,
name|RequestOptions
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Updated settings {}. Response acknowledged: {}"
argument_list|,
name|requestMsg
argument_list|,
name|response
operator|.
name|isAcknowledged
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|shouldProvisionIndex
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

