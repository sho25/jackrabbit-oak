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

begin_comment
comment|/**  * Constants used internally in Lucene indexes.  */
end_comment

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
comment|/**      * Name of the codec to be used for indexing      */
name|String
name|CODEC_NAME
init|=
literal|"codec"
decl_stmt|;
comment|/**      * Name of the merge policy to be used while indexing      */
name|String
name|MERGE_POLICY_NAME
init|=
literal|"mergePolicy"
decl_stmt|;
comment|/**      * Boolean property to indicate that LuceneIndex is being used in testMode      * and it should participate in every test      */
name|String
name|TEST_MODE
init|=
literal|"testMode"
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
block|}
end_interface

end_unit

