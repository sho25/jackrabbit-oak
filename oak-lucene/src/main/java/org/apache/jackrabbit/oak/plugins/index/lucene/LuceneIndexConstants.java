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
name|lucene
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
name|api
operator|.
name|PropertyState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
operator|.
name|AbstractAnalysisFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
import|;
end_import

begin_interface
specifier|public
interface|interface
name|LuceneIndexConstants
block|{
name|String
name|TYPE_LUCENE
init|=
literal|"lucene"
decl_stmt|;
name|String
name|INDEX_DATA_CHILD_NAME
init|=
literal|":data"
decl_stmt|;
name|String
name|SUGGEST_DATA_CHILD_NAME
init|=
literal|":suggest-data"
decl_stmt|;
name|String
name|TRASH_CHILD_NAME
init|=
literal|":trash"
decl_stmt|;
name|Version
name|VERSION
init|=
name|Version
operator|.
name|LUCENE_47
decl_stmt|;
name|Analyzer
name|ANALYZER
init|=
operator|new
name|OakAnalyzer
argument_list|(
name|VERSION
argument_list|)
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
comment|/**      * Actively the data store files after this many hours.      */
name|String
name|ACTIVE_DELETE
init|=
literal|"activeDelete"
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
comment|/**      * Name of the codec to be used for indexing      */
name|String
name|CODEC_NAME
init|=
literal|"codec"
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
comment|/**      * Integer property indicating that LuceneIndex should be      * used in compat mode to specific version      */
name|String
name|COMPAT_MODE
init|=
literal|"compatVersion"
decl_stmt|;
comment|/**      * Boolean property to indicate that LuceneIndex is being used in testMode      * and it should participate in every test      */
name|String
name|TEST_MODE
init|=
literal|"testMode"
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
comment|/**      * Boolean property indicating if in-built analyzer should preserve original term      * (i.e. use      * {@link org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter#PRESERVE_ORIGINAL}      * flag)      */
name|String
name|INDEX_ORIGINAL_TERM
init|=
literal|"indexOriginalTerm"
decl_stmt|;
comment|/**      * Node name under which various analyzers are configured      */
name|String
name|ANALYZERS
init|=
literal|"analyzers"
decl_stmt|;
comment|/**      * Name of the default analyzer definition node under 'analyzers' node      */
name|String
name|ANL_DEFAULT
init|=
literal|"default"
decl_stmt|;
name|String
name|ANL_FILTERS
init|=
literal|"filters"
decl_stmt|;
name|String
name|ANL_STOPWORDS
init|=
literal|"stopwords"
decl_stmt|;
name|String
name|ANL_TOKENIZER
init|=
literal|"tokenizer"
decl_stmt|;
name|String
name|ANL_CHAR_FILTERS
init|=
literal|"charFilters"
decl_stmt|;
name|String
name|ANL_CLASS
init|=
literal|"class"
decl_stmt|;
name|String
name|ANL_NAME
init|=
literal|"name"
decl_stmt|;
name|String
name|ANL_LUCENE_MATCH_VERSION
init|=
name|AbstractAnalysisFactory
operator|.
name|LUCENE_MATCH_VERSION_PARAM
decl_stmt|;
comment|/**      * Config node which include Tika related configuration      */
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
comment|/**      * Property definition name to indicate indexing node name      * Its value should match {@link FieldNames#NODE_NAME}      */
name|String
name|PROPDEF_PROP_NODE_NAME
init|=
literal|":nodeName"
decl_stmt|;
comment|/**      * Boolean property indicating that Lucene directory content      * should be saved as part of NodeState itself as a multi value property      * to allow faster reads (OAK-2809)      */
name|String
name|SAVE_DIR_LISTING
init|=
literal|"saveDirectoryListing"
decl_stmt|;
comment|/**      * Optional  Property to store the path of index in the repository. Path at which index      * definition is defined is not known to IndexEditor. To make use of CopyOnWrite      * feature its required to know the indexPath to optimize the lookup and read of      * existing index files      *      * @deprecated With OAK-4152 no need to explicitly define indexPath property      */
annotation|@
name|Deprecated
name|String
name|INDEX_PATH
init|=
literal|"indexPath"
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
comment|/**      * Optional (index definition) property indicating whether facets should be ACL checked.      * Default is true      */
name|String
name|PROP_SECURE_FACETS
init|=
literal|"secure"
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
block|}
end_interface

end_unit

