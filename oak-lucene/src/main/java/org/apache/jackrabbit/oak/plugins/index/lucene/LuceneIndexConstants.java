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
name|String
name|INDEX_DATA_CHILD_NAME_FS
init|=
literal|"data"
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
block|}
end_interface

end_unit

