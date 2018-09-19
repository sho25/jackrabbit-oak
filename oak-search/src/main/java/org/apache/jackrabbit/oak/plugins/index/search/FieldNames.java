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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Defines field names that are used internally to store data in the  * search index.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|FieldNames
block|{
comment|/**      * Private constructor.      */
specifier|private
name|FieldNames
parameter_list|()
block|{     }
comment|/**      * Name of the field that contains the {@value} property of the node.      */
specifier|public
specifier|static
specifier|final
name|String
name|PATH
init|=
literal|":path"
decl_stmt|;
comment|/**      * Name of the field that contains all the path hierarchy e.g. for /a/b/c      * it would contain /a, /a/b, /a/b/c      */
specifier|public
specifier|static
specifier|final
name|String
name|ANCESTORS
init|=
literal|":ancestors"
decl_stmt|;
comment|/**      * Name of the field which refers to the depth of path      */
specifier|public
specifier|static
specifier|final
name|String
name|PATH_DEPTH
init|=
literal|":depth"
decl_stmt|;
comment|/**      * Name of the field that contains the fulltext index.      */
specifier|public
specifier|static
specifier|final
name|String
name|FULLTEXT
init|=
literal|":fulltext"
decl_stmt|;
comment|/**      * Name of the field that contains the similarity search indexed tokens.      */
specifier|private
specifier|static
specifier|final
name|String
name|SIMILARITY_PREFIX
init|=
literal|"sim:"
decl_stmt|;
comment|/**      * Name of the field that contains the suggest index.      */
specifier|public
specifier|static
specifier|final
name|String
name|SUGGEST
init|=
literal|":suggest"
decl_stmt|;
comment|/**      * Name of the field that contains the spellcheck index.      */
specifier|public
specifier|static
specifier|final
name|String
name|SPELLCHECK
init|=
literal|":spellcheck"
decl_stmt|;
comment|/**      * Prefix for all field names that are fulltext indexed by property name.      */
specifier|public
specifier|static
specifier|final
name|String
name|ANALYZED_FIELD_PREFIX
init|=
literal|"full:"
decl_stmt|;
comment|/**      * Prefix used for storing fulltext of relative node      */
specifier|public
specifier|static
specifier|final
name|String
name|FULLTEXT_RELATIVE_NODE
init|=
literal|"fullnode:"
decl_stmt|;
comment|/**      * Name of the field that contains those property names which are not found      * (or were null) for the given      */
specifier|public
specifier|static
specifier|final
name|String
name|NULL_PROPS
init|=
literal|":nullProps"
decl_stmt|;
comment|/**      * Name of the field that contains those property names which are exist i.e. not null      * for the given NodeState      */
specifier|public
specifier|static
specifier|final
name|String
name|NOT_NULL_PROPS
init|=
literal|":notNullProps"
decl_stmt|;
comment|/**      * Name of the field that contains the node name      */
specifier|public
specifier|static
specifier|final
name|String
name|NODE_NAME
init|=
literal|":nodeName"
decl_stmt|;
comment|/**      * Suffix of the fields that contains function values      */
specifier|public
specifier|static
specifier|final
name|String
name|FUNCTION_PREFIX
init|=
literal|"function*"
decl_stmt|;
comment|/**      * Used to select only the PATH field from the lucene documents      */
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|PATH_SELECTOR
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|PATH
argument_list|)
argument_list|)
decl_stmt|;
comment|/**      * Encodes the field name such that it can be used for storing DocValue      * This is done such a field if used for both sorting and querying uses      * a different name for docvalue field      *      * @param name name to encode      * @return encoded field name      */
specifier|public
specifier|static
name|String
name|createDocValFieldName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
literal|":dv"
operator|+
name|name
return|;
block|}
specifier|public
specifier|static
name|String
name|createAnalyzedFieldName
parameter_list|(
name|String
name|pname
parameter_list|)
block|{
return|return
name|ANALYZED_FIELD_PREFIX
operator|+
name|pname
return|;
block|}
specifier|public
specifier|static
name|String
name|createFulltextFieldName
parameter_list|(
name|String
name|nodeRelativePath
parameter_list|)
block|{
if|if
condition|(
name|nodeRelativePath
operator|==
literal|null
condition|)
block|{
return|return
name|FULLTEXT
return|;
block|}
return|return
name|FULLTEXT_RELATIVE_NODE
operator|+
name|nodeRelativePath
return|;
block|}
specifier|public
specifier|static
name|String
name|createFacetFieldName
parameter_list|(
name|String
name|pname
parameter_list|)
block|{
return|return
name|pname
operator|+
literal|"_facet"
return|;
block|}
comment|/**      * @return if {@code field} represents a field property indexed data      */
specifier|public
specifier|static
name|boolean
name|isPropertyField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
operator|!
name|field
operator|.
name|startsWith
argument_list|(
name|ANALYZED_FIELD_PREFIX
argument_list|)
operator|&&
operator|!
name|field
operator|.
name|startsWith
argument_list|(
name|FULLTEXT_RELATIVE_NODE
argument_list|)
operator|&&
operator|!
name|field
operator|.
name|startsWith
argument_list|(
literal|":"
argument_list|)
operator|&&
operator|!
name|field
operator|.
name|endsWith
argument_list|(
literal|"_facet"
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|createSimilarityFieldName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|SIMILARITY_PREFIX
operator|+
name|name
return|;
block|}
block|}
end_class

end_unit

