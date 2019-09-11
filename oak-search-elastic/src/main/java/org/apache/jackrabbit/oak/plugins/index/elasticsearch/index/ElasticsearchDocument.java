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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|commons
operator|.
name|PathUtils
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
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLEncoder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
import|;
end_import

begin_class
specifier|public
class|class
name|ElasticsearchDocument
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
name|ElasticsearchDocument
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// id should only be useful for logging (at least as of now)
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|String
name|id
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|fulltext
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|suggest
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|notNullProps
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|nullProps
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
decl_stmt|;
name|ElasticsearchDocument
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|String
name|id
init|=
literal|null
decl_stmt|;
try|try
block|{
name|id
operator|=
name|pathToId
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Couldn't encode {} as ES id"
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|fulltext
operator|=
name|newArrayList
argument_list|()
expr_stmt|;
name|this
operator|.
name|suggest
operator|=
name|newArrayList
argument_list|()
expr_stmt|;
name|this
operator|.
name|notNullProps
operator|=
name|newArrayList
argument_list|()
expr_stmt|;
name|this
operator|.
name|nullProps
operator|=
name|newArrayList
argument_list|()
expr_stmt|;
name|this
operator|.
name|properties
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
block|}
name|void
name|addFulltext
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|fulltext
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|void
name|addFulltextRelative
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|addProperty
argument_list|(
name|FieldNames
operator|.
name|createFulltextFieldName
argument_list|(
name|path
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|void
name|addSuggest
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|suggest
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|void
name|notNullProp
parameter_list|(
name|String
name|propName
parameter_list|)
block|{
name|notNullProps
operator|.
name|add
argument_list|(
name|propName
argument_list|)
expr_stmt|;
block|}
name|void
name|nullProp
parameter_list|(
name|String
name|propName
parameter_list|)
block|{
name|nullProps
operator|.
name|add
argument_list|(
name|propName
argument_list|)
expr_stmt|;
block|}
comment|// ES for String values (that are not interpreted as date or numbers etc) would analyze in the same
comment|// field and would index a sub-field "keyword" for non-analyzed value.
comment|// ref: https://www.elastic.co/blog/strings-are-dead-long-live-strings
comment|// (interpretation of date etc: https://www.elastic.co/guide/en/elasticsearch/reference/current/dynamic-field-mapping.html)
name|void
name|addProperty
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|properties
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|void
name|indexAncestors
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|String
name|parPath
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|int
name|depth
init|=
name|PathUtils
operator|.
name|getDepth
argument_list|(
name|path
argument_list|)
decl_stmt|;
comment|// TODO: remember that mapping must be configured with
comment|// https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-pathhierarchy-tokenizer.html
name|addProperty
argument_list|(
name|FieldNames
operator|.
name|ANCESTORS
argument_list|,
name|parPath
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|FieldNames
operator|.
name|PATH_DEPTH
argument_list|,
name|depth
argument_list|)
expr_stmt|;
block|}
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
specifier|public
name|String
name|build
parameter_list|()
block|{
name|String
name|ret
init|=
literal|null
decl_stmt|;
try|try
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
if|if
condition|(
name|fulltext
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|FieldNames
operator|.
name|FULLTEXT
argument_list|,
name|fulltext
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|suggest
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|builder
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
literal|"input"
argument_list|,
name|suggest
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|notNullProps
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|FieldNames
operator|.
name|NOT_NULL_PROPS
argument_list|,
name|notNullProps
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nullProps
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|FieldNames
operator|.
name|NULL_PROPS
argument_list|,
name|nullProps
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|prop
range|:
name|properties
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|prop
operator|.
name|getKey
argument_list|()
argument_list|,
name|prop
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|ret
operator|=
name|Strings
operator|.
name|toString
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error serializing document - id: {}, properties: {}, fulltext: {}, suggest: {}, "
operator|+
literal|"notNullProps: {}, nullProps: {}"
argument_list|,
name|path
argument_list|,
name|properties
argument_list|,
name|fulltext
argument_list|,
name|suggest
argument_list|,
name|notNullProps
argument_list|,
name|nullProps
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
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
name|build
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|String
name|pathToId
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
return|return
name|URLEncoder
operator|.
name|encode
argument_list|(
name|path
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

