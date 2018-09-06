begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
package|;
end_package

begin_comment
comment|/**  * TODO document  */
end_comment

begin_interface
specifier|public
interface|interface
name|IndexConstants
block|{
name|String
name|INDEX_DEFINITIONS_NODE_TYPE
init|=
literal|"oak:QueryIndexDefinition"
decl_stmt|;
name|String
name|INDEX_DEFINITIONS_NAME
init|=
literal|"oak:index"
decl_stmt|;
name|String
name|TYPE_PROPERTY_NAME
init|=
literal|"type"
decl_stmt|;
name|String
name|TYPE_UNKNOWN
init|=
literal|"unknown"
decl_stmt|;
name|String
name|REINDEX_PROPERTY_NAME
init|=
literal|"reindex"
decl_stmt|;
name|String
name|REINDEX_COUNT
init|=
literal|"reindexCount"
decl_stmt|;
name|String
name|REINDEX_ASYNC_PROPERTY_NAME
init|=
literal|"reindex-async"
decl_stmt|;
name|String
name|INDEXING_MODE_SYNC
init|=
literal|"sync"
decl_stmt|;
name|String
name|INDEXING_MODE_NRT
init|=
literal|"nrt"
decl_stmt|;
name|String
name|ASYNC_PROPERTY_NAME
init|=
literal|"async"
decl_stmt|;
name|String
name|ASYNC_REINDEX_VALUE
init|=
literal|"async-reindex"
decl_stmt|;
name|String
name|ENTRY_COUNT_PROPERTY_NAME
init|=
literal|"entryCount"
decl_stmt|;
name|String
name|KEY_COUNT_PROPERTY_NAME
init|=
literal|"keyCount"
decl_stmt|;
comment|/**      * The regular expression pattern of the values to be indexes.      */
name|String
name|VALUE_PATTERN
init|=
literal|"valuePattern"
decl_stmt|;
comment|/**      * A list of prefixes to be excluded from the index.      */
name|String
name|VALUE_EXCLUDED_PREFIXES
init|=
literal|"valueExcludedPrefixes"
decl_stmt|;
comment|/**      * A list of prefixes to be included from the index.      * Include has higher priority than exclude.      */
name|String
name|VALUE_INCLUDED_PREFIXES
init|=
literal|"valueIncludedPrefixes"
decl_stmt|;
comment|/**      * Marks a unique property index.      */
name|String
name|UNIQUE_PROPERTY_NAME
init|=
literal|"unique"
decl_stmt|;
comment|/**      * Defines the names of the properties that are covered by a specific      * property index definition.      */
name|String
name|PROPERTY_NAMES
init|=
literal|"propertyNames"
decl_stmt|;
comment|/**      * Defines the property name of the "declaringNodeTypes" property with      * allows to restrict a given index definition to specific node types.      */
name|String
name|DECLARING_NODE_TYPES
init|=
literal|"declaringNodeTypes"
decl_stmt|;
name|String
name|INDEX_CONTENT_NODE_NAME
init|=
literal|":index"
decl_stmt|;
comment|/**      * MVP to define the paths for which the index can be used to perform      * queries. Defaults to ['/'].      */
name|String
name|QUERY_PATHS
init|=
literal|"queryPaths"
decl_stmt|;
comment|/**      * Property name for indicating that given index is corrupt and should be excluded      * from further indexing. Its value is the date when this index was marked as      * corrupt      */
name|String
name|CORRUPT_PROPERTY_NAME
init|=
literal|"corrupt"
decl_stmt|;
comment|/**      * CommitInfo attribute name which refers to the time at which      * async index checkpoint is created i.e. time upto which repository      * state is being indexed in given indexing cycle.      *      * The time is in string for as per Type.DATE      */
name|String
name|CHECKPOINT_CREATION_TIME
init|=
literal|"indexingCheckpointTime"
decl_stmt|;
comment|/**      * The index tag hint (when using "option(index tagged x, y)", this is IN("x", "y"))      */
name|String
name|INDEX_TAG_OPTION
init|=
literal|":indexTag"
decl_stmt|;
comment|/**      * The tags property in the index definition.      */
name|String
name|INDEX_TAGS
init|=
literal|"tags"
decl_stmt|;
comment|/**      * The index name hint (when using "option(index abc)", this is "abc")      */
name|String
name|INDEX_NAME_OPTION
init|=
literal|":indexName"
decl_stmt|;
comment|/**      * Boolean property on any index node indicating that such a node should not be      * removed during reindex      */
name|String
name|REINDEX_RETAIN
init|=
literal|"retainNodeInReindex"
decl_stmt|;
comment|/**      * Index type for disabled indexes      */
name|String
name|TYPE_DISABLED
init|=
literal|"disabled"
decl_stmt|;
comment|/**      * Multi value property referring to index paths which current index supersedes      */
name|String
name|SUPERSEDED_INDEX_PATHS
init|=
literal|"supersedes"
decl_stmt|;
comment|/**      * Boolean flag indicating that old indexes need to be disabled      */
name|String
name|DISABLE_INDEXES_ON_NEXT_CYCLE
init|=
literal|":disableIndexesOnNextCycle"
decl_stmt|;
comment|/**      * The property of an index. If the given node or property exists, then the      * index is used for queries; otherwise, it is not used (returns infinite      * cost). The value is: nodes, the path. For properties, the path of the node, then '@' property.      */
name|String
name|USE_IF_EXISTS
init|=
literal|"useIfExists"
decl_stmt|;
block|}
end_interface

end_unit

