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
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_comment
comment|/**  * Defaults for search / indexing configurations options.  */
end_comment

begin_class
specifier|public
class|class
name|OakSolrConfigurationDefaults
block|{
specifier|public
specifier|static
specifier|final
name|String
name|PATH_FIELD_NAME
init|=
literal|"path_exact"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CHILD_FIELD_NAME
init|=
literal|"path_child"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DESC_FIELD_NAME
init|=
literal|"path_des"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ANC_FIELD_NAME
init|=
literal|"path_anc"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CATCHALL_FIELD
init|=
literal|"catch_all"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|ROWS
init|=
literal|10
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|boolean
name|PROPERTY_RESTRICTIONS
init|=
literal|false
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|boolean
name|PATH_RESTRICTIONS
init|=
literal|false
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|boolean
name|PRIMARY_TYPES
init|=
literal|false
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Collection
argument_list|<
name|String
argument_list|>
name|IGNORED_PROPERTIES
init|=
name|Collections
operator|.
name|unmodifiableCollection
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"rep:members"
argument_list|,
literal|"rep:authorizableId"
argument_list|,
literal|"jcr:uuid"
argument_list|,
literal|"rep:principalName"
argument_list|,
literal|"rep:password"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TYPE_MAPPINGS
init|=
literal|""
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_MAPPINGS
init|=
literal|""
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|boolean
name|COLLAPSE_JCR_CONTENT_NODES
init|=
literal|false
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|COLLAPSED_PATH_FIELD
init|=
literal|"path_collapsed"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PATH_DEPTH_FIELD
init|=
literal|"path_depth"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|boolean
name|COLLAPSE_JCR_CONTENT_PARENTS
init|=
literal|true
decl_stmt|;
block|}
end_class

end_unit

