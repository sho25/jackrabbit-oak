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
name|search
package|;
end_package

begin_comment
comment|/**  * Internal constants used in index definition, and index implementations.  */
end_comment

begin_interface
specifier|public
interface|interface
name|FulltextIndexConstants
block|{
enum|enum
name|IndexingMode
block|{
name|SYNC
block|,
name|NRT
block|,
name|ASYNC
block|;
specifier|public
name|String
name|asyncValueName
parameter_list|()
block|{
return|return
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|IndexingMode
name|from
parameter_list|(
name|String
name|indexingMode
parameter_list|)
block|{
return|return
name|valueOf
argument_list|(
name|indexingMode
operator|.
name|toUpperCase
argument_list|()
argument_list|)
return|;
block|}
block|}
name|String
name|INDEX_DATA_CHILD_NAME
init|=
literal|":data"
decl_stmt|;
comment|/**      * include only certain property types in the index      */
name|String
name|INCLUDE_PROPERTY_TYPES
init|=
literal|"includePropertyTypes"
decl_stmt|;
comment|/**      * exclude certain properties by name      */
name|String
name|EXCLUDE_PROPERTY_NAMES
init|=
literal|"excludePropertyNames"
decl_stmt|;
name|String
name|PERSISTENCE_NAME
init|=
literal|"persistence"
decl_stmt|;
name|String
name|PERSISTENCE_OAK
init|=
literal|"repository"
decl_stmt|;
name|String
name|TEST_MODE
init|=
literal|"testMode"
decl_stmt|;
name|String
name|PERSISTENCE_FILE
init|=
literal|"file"
decl_stmt|;
name|String
name|PERSISTENCE_PATH
init|=
literal|"path"
decl_stmt|;
comment|/**      * Experimental flag to control storage behavior: 'null' or 'true' means the content is stored      */
name|String
name|EXPERIMENTAL_STORAGE
init|=
literal|"oak.experimental.storage"
decl_stmt|;
comment|/**      * Determines if full text indexing is enabled for this index definition.      * Default is true      */
name|String
name|FULL_TEXT_ENABLED
init|=
literal|"fulltextEnabled"
decl_stmt|;
comment|/**      * Only include properties with name in this set. If this property is defined      * then {@code excludePropertyNames} would be ignored      */
name|String
name|INCLUDE_PROPERTY_NAMES
init|=
literal|"includePropertyNames"
decl_stmt|;
comment|/**      * Type of the property being indexed defined as part of property definition      * under the given index definition. Refer to {@link javax.jcr.PropertyType}      * contants for the possible values      */
name|String
name|PROP_TYPE
init|=
literal|"type"
decl_stmt|;
comment|/**      * Defines properties which would be used for ordering. If range queries are to      * be performed with same property then it must be part of include list also      */
name|String
name|ORDERED_PROP_NAMES
init|=
literal|"orderedProps"
decl_stmt|;
comment|/**      * Size in bytes used for splitting the index files when storing them in NodeStore      */
name|String
name|BLOB_SIZE
init|=
literal|"blobSize"
decl_stmt|;
comment|/**      * Native function name associated with this index definition. Any query can      * use this as the function name to ensure that this index gets used for invoking      * the index      */
name|String
name|FUNC_NAME
init|=
literal|"functionName"
decl_stmt|;
comment|/**      * Child node name under which property details are provided      */
name|String
name|PROP_NODE
init|=
literal|"properties"
decl_stmt|;
name|String
name|INDEX_RULES
init|=
literal|"indexRules"
decl_stmt|;
comment|/**      * Field boost factor      */
name|String
name|FIELD_BOOST
init|=
literal|"boost"
decl_stmt|;
comment|/**      * Property name defined explicitly. Mostly used in case of relative property names      */
name|String
name|PROP_NAME
init|=
literal|"name"
decl_stmt|;
name|String
name|PROP_IS_REGEX
init|=
literal|"isRegexp"
decl_stmt|;
name|String
name|PROP_INDEX
init|=
literal|"index"
decl_stmt|;
name|String
name|PROP_USE_IN_EXCERPT
init|=
literal|"useInExcerpt"
decl_stmt|;
name|String
name|EXCERPT_NODE_FIELD_NAME
init|=
literal|"."
decl_stmt|;
name|String
name|PROP_NODE_SCOPE_INDEX
init|=
literal|"nodeScopeIndex"
decl_stmt|;
name|String
name|PROP_PROPERTY_INDEX
init|=
literal|"propertyIndex"
decl_stmt|;
name|String
name|PROP_ANALYZED
init|=
literal|"analyzed"
decl_stmt|;
name|String
name|RULE_INHERITED
init|=
literal|"inherited"
decl_stmt|;
name|String
name|PROP_ORDERED
init|=
literal|"ordered"
decl_stmt|;
name|String
name|PROP_SCORER_PROVIDER
init|=
literal|"scorerProviderName"
decl_stmt|;
name|String
name|PROP_WEIGHT
init|=
literal|"weight"
decl_stmt|;
comment|/**      * Boolean property in property definition to mark sync properties      */
name|String
name|PROP_SYNC
init|=
literal|"sync"
decl_stmt|;
comment|/**      * Boolean property in property definition to mark unique properties      */
name|String
name|PROP_UNIQUE
init|=
literal|"unique"
decl_stmt|;
name|String
name|EVALUATE_PATH_RESTRICTION
init|=
literal|"evaluatePathRestrictions"
decl_stmt|;
comment|/**      * Experimental config to restrict which property type gets indexed at      * property definition level. Mostly index rule level #INCLUDE_PROPERTY_TYPES      * should be sufficient      */
name|String
name|PROP_INCLUDED_TYPE
init|=
literal|"oak.experimental.includePropertyTypes"
decl_stmt|;
comment|/**      * Regex to allow inclusion of all immediate properties of the node      */
name|String
name|REGEX_ALL_PROPS
init|=
literal|"^[^\\/]*$"
decl_stmt|;
comment|/**      * Node name storing the aggregate rules      */
name|String
name|AGGREGATES
init|=
literal|"aggregates"
decl_stmt|;
name|String
name|AGG_PRIMARY_TYPE
init|=
literal|"primaryType"
decl_stmt|;
comment|/**      * Name of property which stores the aggregate include pattern like<code>jcr:content/metadata</code>      */
name|String
name|AGG_PATH
init|=
literal|"path"
decl_stmt|;
comment|/**      * Limit for maximum number of reaggregates allowed. For example if there is an aggregate of nt:folder      * and it also includes nt:folder then aggregation would traverse down untill this limit is hit      */
name|String
name|AGG_RECURSIVE_LIMIT
init|=
literal|"reaggregateLimit"
decl_stmt|;
comment|/**      * Boolean property indicating that separate fulltext field should be created for      * node represented by this pattern      */
name|String
name|AGG_RELATIVE_NODE
init|=
literal|"relativeNode"
decl_stmt|;
name|String
name|COST_PER_ENTRY
init|=
literal|"costPerEntry"
decl_stmt|;
name|String
name|COST_PER_EXECUTION
init|=
literal|"costPerExecution"
decl_stmt|;
comment|/**      * Config node which include Tika related configuration      * Its value should match {@link FieldNames#NODE_NAME}      */
name|String
name|TIKA
init|=
literal|"tika"
decl_stmt|;
comment|/**      * nt:file node under 'tika' node which refers to the config xml file      */
name|String
name|TIKA_CONFIG
init|=
literal|"config.xml"
decl_stmt|;
name|String
name|TIKA_MAX_EXTRACT_LENGTH
init|=
literal|"maxExtractLength"
decl_stmt|;
comment|/**      *  Config node under tika which defines mime type mappings      */
name|String
name|TIKA_MIME_TYPES
init|=
literal|"mimeTypes"
decl_stmt|;
comment|/**      * Property name within the mime type structure which defines a mime type mapping      */
name|String
name|TIKA_MAPPED_TYPE
init|=
literal|"mappedType"
decl_stmt|;
comment|/**      * The maximum number of terms that will be indexed for a single field in a      * document.  This limits the amount of memory required for indexing, so that      * collections with very large files will not crash the indexing process by      * running out of memory.      *<p>      * Note that this effectively truncates large documents, excluding from the      * index terms that occur further in the document.  If you know your source      * documents are large, be sure to set this value high enough to accommodate      * the expected size.  If you set it to Integer.MAX_VALUE, then the only limit      * is your memory, but you should anticipate an OutOfMemoryError.      *<p>      * By default, no more than 10,000 terms will be indexed for a field.      */
name|String
name|MAX_FIELD_LENGTH
init|=
literal|"maxFieldLength"
decl_stmt|;
comment|/**      * whether use this property values for suggestions      */
name|String
name|PROP_USE_IN_SUGGEST
init|=
literal|"useInSuggest"
decl_stmt|;
comment|/**      * subnode holding configuration for suggestions      */
name|String
name|SUGGESTION_CONFIG
init|=
literal|"suggestion"
decl_stmt|;
comment|/**      * update frequency of the suggester in minutes      */
name|String
name|SUGGEST_UPDATE_FREQUENCY_MINUTES
init|=
literal|"suggestUpdateFrequencyMinutes"
decl_stmt|;
comment|/**      * whether use this property values for spellchecking      */
name|String
name|PROP_USE_IN_SPELLCHECK
init|=
literal|"useInSpellcheck"
decl_stmt|;
comment|/**      * whether use this property values for similarity      */
name|String
name|PROP_USE_IN_SIMILARITY
init|=
literal|"useInSimilarity"
decl_stmt|;
comment|/**      * whether feature vector similarity search should rerank based on feature values      */
name|String
name|PROP_SIMILARITY_RERANK
init|=
literal|"similarityRerank"
decl_stmt|;
comment|/**      * Property definition config indicating that null check support should be      * enabled for this property      */
name|String
name|PROP_NULL_CHECK_ENABLED
init|=
literal|"nullCheckEnabled"
decl_stmt|;
comment|/**      * Property definition config indicating that this property would be used with      * 'IS NOT NULL' constraint      */
name|String
name|PROP_NOT_NULL_CHECK_ENABLED
init|=
literal|"notNullCheckEnabled"
decl_stmt|;
comment|/**      * IndexRule level config to indicate that Node name should also be index      * to support fn:name() queries      */
name|String
name|INDEX_NODE_NAME
init|=
literal|"indexNodeName"
decl_stmt|;
comment|/**      * Property definition name to indicate indexing node name      */
name|String
name|PROPDEF_PROP_NODE_NAME
init|=
literal|":nodeName"
decl_stmt|;
comment|/**      * Optional subnode holding configuration for facets.      */
name|String
name|FACETS
init|=
literal|"facets"
decl_stmt|;
comment|/**      * Optional property to set the suggest field to be analyzed and therefore allow more fine      * grained and flexible suggestions.      */
name|String
name|SUGGEST_ANALYZED
init|=
literal|"suggestAnalyzed"
decl_stmt|;
comment|/**      * Integer property indicating that the index should be      * used in compat mode to specific version      */
name|String
name|COMPAT_MODE
init|=
literal|"compatVersion"
decl_stmt|;
comment|/**      * Optional (index definition) property indicating whether facets should be ACL checked.      * Default is true      */
name|String
name|PROP_SECURE_FACETS
init|=
literal|"secure"
decl_stmt|;
name|String
name|PROP_SECURE_FACETS_VALUE_INSECURE
init|=
literal|"insecure"
decl_stmt|;
name|String
name|PROP_SECURE_FACETS_VALUE_STATISTICAL
init|=
literal|"statistical"
decl_stmt|;
name|String
name|PROP_SECURE_FACETS_VALUE_SECURE
init|=
literal|"secure"
decl_stmt|;
name|String
name|PROP_SECURE_FACETS_VALUE_JVM_PARAM
init|=
literal|"oak.facets.secure"
decl_stmt|;
name|String
name|STATISTICAL_FACET_SAMPLE_SIZE_JVM_PARAM
init|=
literal|"oak.facet.statistical.sampleSize"
decl_stmt|;
name|String
name|PROP_STATISTICAL_FACET_SAMPLE_SIZE
init|=
literal|"sampleSize"
decl_stmt|;
name|int
name|STATISTICAL_FACET_SAMPLE_SIZE_DEFAULT
init|=
literal|1000
decl_stmt|;
comment|/**      * Optional (index definition) property indicating max number of facets that will be retrieved      * in query      * Default is {@link IndexDefinition#DEFAULT_FACET_COUNT}      */
name|String
name|PROP_FACETS_TOP_CHILDREN
init|=
literal|"topChildren"
decl_stmt|;
comment|/**      * Optional (property definition) property indicating whether facets should be created      * for this property      */
name|String
name|PROP_FACETS
init|=
literal|"facets"
decl_stmt|;
comment|/**      * Boolean property indicate that property should not be included in aggregation      */
name|String
name|PROP_EXCLUDE_FROM_AGGREGATE
init|=
literal|"excludeFromAggregation"
decl_stmt|;
comment|/**      * String property: the function to index, for function-based index      */
name|String
name|PROP_FUNCTION
init|=
literal|"function"
decl_stmt|;
comment|/**      * Boolean property which signal FulltextIndexEditor to refresh the stored index definition      */
name|String
name|PROP_REFRESH_DEFN
init|=
literal|"refresh"
decl_stmt|;
comment|/**      * Long property that keep seed for random number generation. One example usage of this is      * to randomly sample query results to statistically check for ACLs to extrapolate facet      * counts      */
name|String
name|PROP_RANDOM_SEED
init|=
literal|"seed"
decl_stmt|;
comment|/**      * Boolean property to indicate that nodes nodetype matching indexRule name      * should be indexed      */
name|String
name|PROP_INDEX_NODE_TYPE
init|=
literal|"nodeTypeIndex"
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

